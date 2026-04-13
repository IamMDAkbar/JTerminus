package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates directories. Supports -p flag for nested directory creation.
 */
public class MkdirCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("mkdir: missing operand\nUsage: mkdir [-p] <directory>...");
        }

        boolean createParents = false;
        StringBuilder output = new StringBuilder();
        boolean hasError = false;

        for (String arg : args) {
            if (arg.equals("-p")) {
                createParents = true;
                continue;
            }

            Path target = cwd.resolve(arg).normalize();
            try {
                if (Files.exists(target)) {
                    output.append("mkdir: cannot create directory '").append(arg)
                            .append("': File exists\n");
                    hasError = true;
                    continue;
                }

                if (createParents) {
                    Files.createDirectories(target);
                } else {
                    if (!Files.exists(target.getParent())) {
                        output.append("mkdir: cannot create directory '").append(arg)
                                .append("': No such file or directory (use -p for nested)\n");
                        hasError = true;
                        continue;
                    }
                    Files.createDirectory(target);
                }
                output.append("Created directory: ").append(arg).append("\n");
            } catch (IOException e) {
                output.append("mkdir: error creating '").append(arg).append("': ")
                        .append(e.getMessage()).append("\n");
                hasError = true;
            }
        }

        String result = output.toString().trim();
        return hasError ? CommandResult.error(result) : CommandResult.success(result);
    }

    @Override
    public String getName() { return "mkdir"; }

    @Override
    public String getDescription() { return "Create directories"; }

    @Override
    public String getUsage() { return "mkdir [-p] <directory>..."; }
}
