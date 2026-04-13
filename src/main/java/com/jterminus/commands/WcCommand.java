package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Word count - counts lines, words, and characters in files.
 */
public class WcCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("wc: missing file operand");
        }

        StringBuilder output = new StringBuilder();
        int totalLines = 0, totalWords = 0, totalChars = 0;
        List<String> files = new ArrayList<>();

        for (String arg : args) {
            if (!arg.startsWith("-")) {
                files.add(arg);
            }
        }

        if (files.isEmpty()) {
            return CommandResult.error("wc: no files specified");
        }

        for (String filename : files) {
            Path filePath = cwd.resolve(filename).normalize();

            if (!Files.exists(filePath)) {
                output.append("wc: cannot open '").append(filename).append("' for reading\n");
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(filePath);
                int lineCount = lines.size();
                int wordCount = 0;
                int charCount = 0;

                for (String line : lines) {
                    charCount += line.length() + 1; // +1 for newline
                    wordCount += line.split("\\s+").length;
                }

                totalLines += lineCount;
                totalWords += wordCount;
                totalChars += charCount;

                output.append(String.format("%7d %7d %7d %s%n", lineCount, wordCount, charCount, filename));
            } catch (Exception e) {
                output.append("wc: error reading '").append(filename).append("': ").append(e.getMessage()).append("\n");
            }
        }

        if (files.size() > 1) {
            output.append(String.format("%7d %7d %7d total%n", totalLines, totalWords, totalChars));
        }

        return CommandResult.success(output.toString().stripTrailing());
    }

    @Override
    public String getName() { return "wc"; }

    @Override
    public String getDescription() { return "Count lines, words, and characters"; }

    @Override
    public String getUsage() { return "wc [file...]"; }
}
