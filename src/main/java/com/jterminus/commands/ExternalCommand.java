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

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return CommandResult.error("Command timed out after 30 seconds");
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
