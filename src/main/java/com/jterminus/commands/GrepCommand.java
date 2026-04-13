package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Searches for text patterns within files.
 */
public class GrepCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 2) {
            return CommandResult.error("grep: missing operand\nUsage: grep [-i] [-r] <pattern> <file>...");
        }

        boolean ignoreCase = false;
        boolean recursive = false;
        String pattern = null;
        List<String> files = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-i")) {
                ignoreCase = true;
            } else if (arg.equals("-r") || arg.equals("-R")) {
                recursive = true;
            } else if (pattern == null) {
                pattern = arg;
            } else {
                files.add(arg);
            }
        }

        if (pattern == null || files.isEmpty()) {
            return CommandResult.error("grep: missing pattern or file");
        }

        int flags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;
        Pattern regex;
        try {
            regex = Pattern.compile(Pattern.quote(pattern), flags);
        } catch (Exception e) {
            regex = Pattern.compile(pattern, flags);
        }

        StringBuilder output = new StringBuilder();
        boolean found = false;

        for (String file : files) {
            Path target = cwd.resolve(file).normalize();

            if (!Files.exists(target)) {
                output.append("grep: '").append(file).append("': No such file or directory\n");
                continue;
            }

            if (Files.isDirectory(target) && recursive) {
                try {
                    Pattern finalRegex = regex;
                    Files.walkFileTree(target, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) {
                            searchFile(f, finalRegex, output, cwd);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    found = true;
                } catch (IOException e) {
                    output.append("grep: error: ").append(e.getMessage()).append("\n");
                }
            } else if (!Files.isDirectory(target)) {
                if (searchFile(target, regex, output, cwd)) {
                    found = true;
                }
            } else {
                output.append("grep: '").append(file).append("': Is a directory (use -r)\n");
            }
        }

        String result = output.toString().stripTrailing();
        if (!found && result.isEmpty()) {
            return CommandResult.success("(no matches found)");
        }
        return CommandResult.success(result);
    }

    private boolean searchFile(Path file, Pattern pattern, StringBuilder output, Path cwd) {
        boolean found = false;
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size(); i++) {
                if (pattern.matcher(lines.get(i)).find()) {
                    String relativePath = cwd.relativize(file).toString();
                    output.append(String.format("%s:%d: %s%n", relativePath, i + 1, lines.get(i)));
                    found = true;
                }
            }
        } catch (IOException ignored) {
            // Skip binary or unreadable files
        }
        return found;
    }

    @Override
    public String getName() { return "grep"; }

    @Override
    public String getDescription() { return "Search for patterns in files"; }

    @Override
    public String getUsage() { return "grep [-i] [-r] <pattern> <file>..."; }
}
