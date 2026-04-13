package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Removes empty directories.
 */
public class RmdirCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("rmdir: missing operand\nUsage: rmdir <directory>...");
        }

        StringBuilder output = new StringBuilder();
        boolean hasError = false;

        for (String arg : args) {
            Path target = cwd.resolve(arg).normalize();
            try {
                if (!Files.exists(target)) {
                    output.append("rmdir: '").append(arg).append("': No such file or directory\n");
                    hasError = true;
                    continue;
                }
                if (!Files.isDirectory(target)) {
                    output.append("rmdir: '").append(arg).append("': Not a directory\n");
                    hasError = true;
                    continue;
                }

                // Check if directory is empty
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(target)) {
                    if (ds.iterator().hasNext()) {
                        output.append("rmdir: '").append(arg).append("': Directory not empty\n");
                        hasError = true;
                        continue;
                    }
                }

                Files.delete(target);
                output.append("Removed directory: ").append(arg).append("\n");
            } catch (IOException e) {
                output.append("rmdir: error removing '").append(arg).append("': ")
                        .append(e.getMessage()).append("\n");
                hasError = true;
            }
        }

        String result = output.toString().trim();
        return hasError ? CommandResult.error(result) : CommandResult.success(result);
    }

    @Override
    public String getName() { return "rmdir"; }

    @Override
    public String getDescription() { return "Remove empty directories"; }

    @Override
    public String getUsage() { return "rmdir <directory>..."; }
}
