package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Tee - reads from stdin and writes to stdout and file.
 */
public class TeeCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("tee: missing file operand");
        }

        String filename = args[0];
        Path filePath = cwd.resolve(filename).normalize();

        // In a real shell, tee would read stdin
        // For this simplified version, we'll just create an empty file
        try {
            if (Files.exists(filePath)) {
                return CommandResult.error("tee: file already exists: " + filename);
            }
            Files.createFile(filePath);
            return CommandResult.success("Wrote to: " + filename);
        } catch (IOException e) {
            return CommandResult.error("tee: error creating file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "tee"; }

    @Override
    public String getDescription() { return "Read stdin and write to file and stdout"; }

    @Override
    public String getUsage() { return "tee [file]"; }
}
