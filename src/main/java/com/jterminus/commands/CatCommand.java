package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Displays the contents of a file.
 */
public class CatCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("cat: missing file operand\nUsage: cat <file>...");
        }

        StringBuilder output = new StringBuilder();
        boolean hasError = false;
        boolean showLineNumbers = false;

        for (String arg : args) {
            if (arg.equals("-n")) {
                showLineNumbers = true;
                continue;
            }

            Path target = cwd.resolve(arg).normalize();

            if (!Files.exists(target)) {
                output.append("cat: '").append(arg).append("': No such file or directory\n");
                hasError = true;
                continue;
            }

            if (Files.isDirectory(target)) {
                output.append("cat: '").append(arg).append("': Is a directory\n");
                hasError = true;
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(target);
                if (showLineNumbers) {
                    for (int i = 0; i < lines.size(); i++) {
                        output.append(String.format("%6d  %s%n", i + 1, lines.get(i)));
                    }
                } else {
                    for (String line : lines) {
                        output.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                output.append("cat: error reading '").append(arg).append("': ")
                        .append(e.getMessage()).append("\n");
                hasError = true;
            }
        }

        String result = output.toString().stripTrailing();
        return hasError ? CommandResult.error(result) : CommandResult.success(result);
    }

    @Override
    public String getName() { return "cat"; }

    @Override
    public String getDescription() { return "Display file contents"; }

    @Override
    public String getUsage() { return "cat [-n] <file>..."; }
}
