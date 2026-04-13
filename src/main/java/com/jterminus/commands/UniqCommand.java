package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Unique - filters out duplicate adjacent lines.
 */
public class UniqCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("uniq: missing file operand");
        }

        StringBuilder output = new StringBuilder();
        String filename = null;
        boolean countOnly = false;

        for (String arg : args) {
            if (arg.equals("-c")) {
                countOnly = true;
            } else {
                filename = arg;
            }
        }

        if (filename == null) {
            return CommandResult.error("uniq: no file specified");
        }

        Path filePath = cwd.resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            return CommandResult.error("uniq: cannot open '" + filename + "' for reading");
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            String lastLine = null;
            int count = 0;

            for (String line : lines) {
                if (lastLine == null || !lastLine.equals(line)) {
                    if (lastLine != null) {
                        if (countOnly) {
                            output.append(String.format("%7d %s%n", count, lastLine));
                        } else {
                            output.append(lastLine).append("\n");
                        }
                    }
                    lastLine = line;
                    count = 1;
                } else {
                    count++;
                }
            }

            // Don't forget last line
            if (lastLine != null) {
                if (countOnly) {
                    output.append(String.format("%7d %s%n", count, lastLine));
                } else {
                    output.append(lastLine).append("\n");
                }
            }

            return CommandResult.success(output.toString().stripTrailing());
        } catch (Exception e) {
            return CommandResult.error("uniq: error reading file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "uniq"; }

    @Override
    public String getDescription() { return "Filter out duplicate adjacent lines"; }

    @Override
    public String getUsage() { return "uniq [-c] [file]"; }
}
