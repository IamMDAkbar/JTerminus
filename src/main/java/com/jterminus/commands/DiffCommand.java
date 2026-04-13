package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Diff - compares two files line by line.
 */
public class DiffCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 2) {
            return CommandResult.error("diff: missing file operand");
        }

        String file1 = args[0];
        String file2 = args[1];

        Path path1 = cwd.resolve(file1).normalize();
        Path path2 = cwd.resolve(file2).normalize();

        if (!Files.exists(path1)) {
            return CommandResult.error("diff: cannot open '" + file1 + "' for reading");
        }
        if (!Files.exists(path2)) {
            return CommandResult.error("diff: cannot open '" + file2 + "' for reading");
        }

        try {
            List<String> lines1 = Files.readAllLines(path1);
            List<String> lines2 = Files.readAllLines(path2);

            StringBuilder output = new StringBuilder();
            int i = 0, j = 0;

            while (i < lines1.size() || j < lines2.size()) {
                if (i >= lines1.size()) {
                    output.append("> ").append(lines2.get(j)).append("\n");
                    j++;
                } else if (j >= lines2.size()) {
                    output.append("< ").append(lines1.get(i)).append("\n");
                    i++;
                } else if (lines1.get(i).equals(lines2.get(j))) {
                    i++;
                    j++;
                } else {
                    output.append("< ").append(lines1.get(i)).append("\n");
                    output.append("> ").append(lines2.get(j)).append("\n");
                    i++;
                    j++;
                }
            }

            if (output.length() == 0) {
                return CommandResult.success(""); // Files are identical
            }

            return CommandResult.success(output.toString().stripTrailing());
        } catch (Exception e) {
            return CommandResult.error("diff: error comparing files: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "diff"; }

    @Override
    public String getDescription() { return "Compare two files line by line"; }

    @Override
    public String getUsage() { return "diff [file1] [file2]"; }
}
