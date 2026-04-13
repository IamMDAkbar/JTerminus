package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Cut - extracts columns from files.
 */
public class CutCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("cut: missing file operand");
        }

        String delimiter = "\t";
        int field = 1;
        String filename = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d") && i + 1 < args.length) {
                delimiter = args[++i];
            } else if (args[i].equals("-f") && i + 1 < args.length) {
                try {
                    field = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    return CommandResult.error("cut: invalid field number");
                }
            } else {
                filename = args[i];
            }
        }

        if (filename == null) {
            return CommandResult.error("cut: no file specified");
        }

        Path filePath = cwd.resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            return CommandResult.error("cut: cannot open '" + filename + "' for reading");
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder output = new StringBuilder();

            for (String line : lines) {
                String[] parts = line.split(java.util.regex.Pattern.quote(delimiter));
                if (field > 0 && field <= parts.length) {
                    output.append(parts[field - 1]).append("\n");
                } else if (field > parts.length) {
                    // Field doesn't exist, just output empty
                    output.append("\n");
                }
            }

            return CommandResult.success(output.toString().stripTrailing());
        } catch (Exception e) {
            return CommandResult.error("cut: error processing file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "cut"; }

    @Override
    public String getDescription() { return "Extract columns from files"; }

    @Override
    public String getUsage() { return "cut [-d delimiter] [-f field] [file]"; }
}
