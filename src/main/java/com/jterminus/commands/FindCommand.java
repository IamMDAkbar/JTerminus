package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds files by name pattern.
 */
public class FindCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("find: missing arguments\nUsage: find [path] -name <pattern>");
        }

        String searchPath = ".";
        String namePattern = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-name") && i + 1 < args.length) {
                namePattern = args[i + 1];
                i++;
            } else if (!args[i].startsWith("-")) {
                searchPath = args[i];
            }
        }

        // If no -name flag, treat first arg as pattern
        if (namePattern == null) {
            namePattern = args[args.length - 1];
        }

        Path root = cwd.resolve(searchPath).normalize();
        if (!Files.exists(root)) {
            return CommandResult.error("find: '" + searchPath + "': No such file or directory");
        }

        List<String> results = new ArrayList<>();
        String globPattern = namePattern.contains("*") ? namePattern : "*" + namePattern + "*";

        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (matcher.matches(file.getFileName())) {
                        results.add(cwd.relativize(file).toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!dir.equals(root) && matcher.matches(dir.getFileName())) {
                        results.add(cwd.relativize(dir).toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            return CommandResult.error("find: error: " + e.getMessage());
        }

        if (results.isEmpty()) {
            return CommandResult.success("(no matches found)");
        }
        return CommandResult.success(String.join("\n", results));
    }

    @Override
    public String getName() { return "find"; }

    @Override
    public String getDescription() { return "Find files by name"; }

    @Override
    public String getUsage() { return "find [path] -name <pattern>"; }
}
