package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Fallback command that delegates to the OS shell via ProcessBuilder.
 * Used for commands not in the built-in set.
 */
public class ExternalCommand implements Command {

    private final String commandName;

    public ExternalCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        try {
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();

            // Build the full command line
            StringBuilder cmdLine = new StringBuilder(commandName);
            for (String arg : args) {
                cmdLine.append(" ").append(arg);
            }

            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", cmdLine.toString());
            } else {
                pb = new ProcessBuilder("/bin/sh", "-c", cmdLine.toString());
            }

            pb.directory(cwd.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Read output with a line cap to prevent huge memory usage
            final int MAX_LINES = 5000;
            int lineCount = 0;
            boolean truncated = false;
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (lineCount < MAX_LINES) {
                        output.append(line).append("\n");
                        lineCount++;
                    } else {
                        truncated = true;
                        // Drain remaining output to prevent blocking
                        while (reader.readLine() != null) {
                            lineCount++;
                        }
                        break;
                    }
                }
            }

            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                String partial = output.toString().stripTrailing();
                if (!partial.isEmpty()) {
                    return CommandResult.error(partial + "\n\n[Command timed out after 15 seconds]");
                }
                return CommandResult.error("Command timed out after 15 seconds");
            }

            if (truncated) {
                output.append("\n... (output truncated — showed ")
                      .append(MAX_LINES).append(" of ").append(lineCount).append(" lines)");
            }

            int exitCode = process.exitValue();
            String result = output.toString().stripTrailing();

            if (exitCode != 0) {
                return CommandResult.error(result.isEmpty() ?
                        commandName + ": command failed with exit code " + exitCode : result);
            }

            return CommandResult.success(result);
        } catch (Exception e) {
            return CommandResult.error(commandName + ": command not found or failed: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return commandName; }

    @Override
    public String getDescription() { return "External command: " + commandName; }

    @Override
    public String getUsage() { return commandName + " [args...]"; }
}
