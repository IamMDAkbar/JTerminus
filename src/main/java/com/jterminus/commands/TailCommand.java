package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Displays the last N lines of a file (default 10).
 */
public class TailCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("tail: missing file operand\nUsage: tail [-n lines] <file>");
        }

        int lines = 10;
        String fileName = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n") && i + 1 < args.length) {
                try {
                    lines = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    return CommandResult.error("tail: invalid number of lines: " + args[i + 1]);
                }
            } else if (!args[i].startsWith("-")) {
                fileName = args[i];
            }
        }

        if (fileName == null) {
            return CommandResult.error("tail: missing file operand");
        }

        Path target = cwd.resolve(fileName).normalize();

        if (!Files.exists(target)) {
            return CommandResult.error("tail: '" + fileName + "': No such file or directory");
        }

        try {
            List<String> allLines = Files.readAllLines(target);
            int start = Math.max(0, allLines.size() - lines);
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < allLines.size(); i++) {
                sb.append(allLines.get(i));
                if (i < allLines.size() - 1) sb.append("\n");
            }
            return CommandResult.success(sb.toString());
        } catch (IOException e) {
            return CommandResult.error("tail: error reading file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "tail"; }

    @Override
    public String getDescription() { return "Display last lines of a file"; }

    @Override
    public String getUsage() { return "tail [-n lines] <file>"; }
}
