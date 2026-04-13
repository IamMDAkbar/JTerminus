package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates empty files (like Unix touch).
 */
public class TouchCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("touch: missing file operand\nUsage: touch <file>...");
        }

        StringBuilder output = new StringBuilder();
        boolean hasError = false;

        for (String arg : args) {
            Path target = cwd.resolve(arg).normalize();
            try {
                if (Files.exists(target)) {
                    // Update modification time
                    Files.setLastModifiedTime(target, java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis()));
                    output.append("Updated timestamp: ").append(arg).append("\n");
                } else {
                    // Create parent directories if needed
                    if (target.getParent() != null && !Files.exists(target.getParent())) {
                        output.append("touch: cannot create '").append(arg)
                                .append("': No such parent directory\n");
                        hasError = true;
                        continue;
                    }
                    Files.createFile(target);
                    output.append("Created file: ").append(arg).append("\n");
                }
            } catch (IOException e) {
                output.append("touch: error with '").append(arg).append("': ")
                        .append(e.getMessage()).append("\n");
                hasError = true;
            }
        }

        String result = output.toString().trim();
        return hasError ? CommandResult.error(result) : CommandResult.success(result);
    }

    @Override
    public String getName() { return "touch"; }

    @Override
    public String getDescription() { return "Create empty files or update timestamps"; }

    @Override
    public String getUsage() { return "touch <file>..."; }
}
