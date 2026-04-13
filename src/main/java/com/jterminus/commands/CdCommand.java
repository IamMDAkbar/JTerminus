package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Changes the current working directory.
 */
public class CdCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0 || args[0].equals("~")) {
            // Go to user home
            String home = System.getProperty("user.home");
            return CommandResult.changeDirectory(home);
        }

        String target = args[0];
        Path newDir;

        if (target.equals("-")) {
            // Could track previous directory, for now just stay
            return CommandResult.success("");
        }

        if (target.equals("..")) {
            newDir = cwd.getParent();
            if (newDir == null) {
                return CommandResult.error("cd: already at root directory");
            }
        } else {
            newDir = cwd.resolve(target).normalize();
        }

        if (!Files.exists(newDir)) {
            return CommandResult.error("cd: no such file or directory: " + target);
        }

        if (!Files.isDirectory(newDir)) {
            return CommandResult.error("cd: not a directory: " + target);
        }

        return CommandResult.changeDirectory(newDir.toAbsolutePath().toString());
    }

    @Override
    public String getName() { return "cd"; }

    @Override
    public String getDescription() { return "Change directory"; }

    @Override
    public String getUsage() { return "cd [directory]"; }
}
