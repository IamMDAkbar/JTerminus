package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Moves or renames files and directories.
 */
public class MvCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 2) {
            return CommandResult.error("mv: missing operand\nUsage: mv <source> <destination>");
        }

        String src = args[0];
        String dst = args[1];

        Path source = cwd.resolve(src).normalize();
        Path destination = cwd.resolve(dst).normalize();

        if (!Files.exists(source)) {
            return CommandResult.error("mv: '" + src + "': No such file or directory");
        }

        try {
            if (Files.isDirectory(destination)) {
                destination = destination.resolve(source.getFileName());
            }
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return CommandResult.success("Moved: " + src + " -> " + dst);
        } catch (IOException e) {
            return CommandResult.error("mv: error: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "mv"; }

    @Override
    public String getDescription() { return "Move or rename files"; }

    @Override
    public String getUsage() { return "mv <source> <destination>"; }
}
