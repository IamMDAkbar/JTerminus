package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Which - locates a command in PATH.
 */
public class WhichCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("which: missing command operand");
        }

        String commandName = args[0];
        String pathEnv = System.getenv("PATH");

        if (pathEnv == null || pathEnv.isEmpty()) {
            return CommandResult.error("which: PATH environment variable not set");
        }

        String[] pathDirs = pathEnv.split(System.getProperty("path.separator"));

        for (String pathDir : pathDirs) {
            Path commandPath = Paths.get(pathDir).resolve(commandName);
            if (Files.exists(commandPath) && Files.isExecutable(commandPath)) {
                return CommandResult.success(commandPath.toString());
            }

            // Try with .exe extension on Windows
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                commandPath = Paths.get(pathDir).resolve(commandName + ".exe");
                if (Files.exists(commandPath) && Files.isExecutable(commandPath)) {
                    return CommandResult.success(commandPath.toString());
                }
            }
        }

        return CommandResult.error("which: command not found: " + commandName);
    }

    @Override
    public String getName() { return "which"; }

    @Override
    public String getDescription() { return "Locate a command in PATH"; }

    @Override
    public String getUsage() { return "which [command]"; }
}
