package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * Removes files and directories. Supports -r for recursive removal.
 */
public class RmCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.error("rm: missing operand\nUsage: rm [-r] <file>...");
        }

        boolean recursive = false;
        boolean force = false;
        StringBuilder output = new StringBuilder();
        boolean hasError = false;

        for (String arg : args) {
            if (arg.equals("-r") || arg.equals("-rf") || arg.equals("-R")) {
                recursive = true;
                if (arg.contains("f")) force = true;
                continue;
            }
            if (arg.equals("-f")) {
                force = true;
                continue;
            }

            Path target = cwd.resolve(arg).normalize();

            if (!Files.exists(target)) {
                if (!force) {
                    output.append("rm: '").append(arg).append("': No such file or directory\n");
                    hasError = true;
                }
                continue;
            }

            try {
                if (Files.isDirectory(target)) {
                    if (!recursive) {
                        output.append("rm: '").append(arg).append("': Is a directory (use -r)\n");
                        hasError = true;
                        continue;
                    }
                    deleteRecursive(target);
                    output.append("Removed directory: ").append(arg).append("\n");
                } else {
                    Files.delete(target);
                    output.append("Removed: ").append(arg).append("\n");
                }
            } catch (IOException e) {
                output.append("rm: error removing '").append(arg).append("': ")
                        .append(e.getMessage()).append("\n");
                hasError = true;
            }
        }

        String result = output.toString().trim();
        return hasError ? CommandResult.error(result) : CommandResult.success(result);
    }

    private void deleteRecursive(Path root) throws IOException {
        Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    @Override
    public String getName() { return "rm"; }

    @Override
    public String getDescription() { return "Remove files or directories"; }

    @Override
    public String getUsage() { return "rm [-rf] <file>..."; }
}
