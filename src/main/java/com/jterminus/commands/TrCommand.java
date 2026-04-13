package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Translate characters in a file.
 */
public class TrCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 3) {
            return CommandResult.error("tr: missing operand");
        }

        String file = args[0];
        String from = args[1];
        String to = args[2];

        if (from.length() == 0 || to.length() == 0) {
            return CommandResult.error("tr: character set cannot be empty");
        }

        Path filePath = cwd.resolve(file).normalize();

        if (!Files.exists(filePath)) {
            return CommandResult.error("tr: cannot open '" + file + "' for reading");
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder output = new StringBuilder();

            for (String line : lines) {
                String translated = line;
                for (int i = 0; i < from.length(); i++) {
                    char fromChar = from.charAt(i);
                    char toChar = (i < to.length()) ? to.charAt(i) : to.charAt(to.length() - 1);
                    translated = translated.replace(fromChar, toChar);
                }
                output.append(translated).append("\n");
            }

            return CommandResult.success(output.toString().stripTrailing());
        } catch (Exception e) {
            return CommandResult.error("tr: error processing file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "tr"; }

    @Override
    public String getDescription() { return "Translate characters"; }

    @Override
    public String getUsage() { return "tr [file] [from] [to]"; }
}
