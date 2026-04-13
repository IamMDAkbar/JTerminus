package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sort - sorts lines from files.
 */
public class SortCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("sort: missing file operand");
        }

        StringBuilder output = new StringBuilder();
        List<String> allLines = new ArrayList<>();
        boolean reverse = false;
        String filename = null;

        for (String arg : args) {
            if (arg.equals("-r")) {
                reverse = true;
            } else {
                filename = arg;
            }
        }

        if (filename == null) {
            return CommandResult.error("sort: no file specified");
        }

        Path filePath = cwd.resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            return CommandResult.error("sort: cannot open '" + filename + "' for reading");
        }

        try {
            allLines.addAll(Files.readAllLines(filePath));
            Collections.sort(allLines);
            
            if (reverse) {
                Collections.reverse(allLines);
            }

            for (String line : allLines) {
                output.append(line).append("\n");
            }

            return CommandResult.success(output.toString().stripTrailing());
        } catch (Exception e) {
            return CommandResult.error("sort: error reading file: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "sort"; }

    @Override
    public String getDescription() { return "Sort lines in a file"; }

    @Override
    public String getUsage() { return "sort [-r] [file]"; }
}
