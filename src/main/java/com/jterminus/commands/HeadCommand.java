package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Displays the first N lines of a file (default 10).
 */
public class HeadCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("head: missing file operand\nUsage: head [-n lines] <file>");
        }

        int lines = 10;
        String fileName = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n") && i + 1 < args.length) {
                try {
                    lines = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    return CommandResult.error("head: invalid number of lines: " + args[i + 1]);
                }
            } else if (!args[i].startsWith("-")) {
                fileName = args[i];
            }
        }

        if (fileName == null) {
            return CommandResult.error("head: missing file operand");
        }

        Path target = cwd.resolve(fileName).normalize();

        if (!Files.exists(target)) {
            return CommandResult.error("head: '" + fileName + "': No such file or directory");
        }

        try {
            List<String> allLines = Files.readAllLines(target);
            int count = Math.min(lines, allLines.size());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(allLines.get(i));
                if (i < count - 1) sb.append("\n");
            }
            return CommandResult.success(sb.toString());
        } catch (IOException e) {
            return CommandResult.error("head: error reading file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "head"; }

    @Override
    public String getDescription() { return "Display first lines of a file"; }

    @Override
    public String getUsage() { return "head [-n lines] <file>"; }
}
