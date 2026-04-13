package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * Copies files and directories. Supports -r for recursive copy.
 */
public class CpCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 2) {
            return CommandResult.error("cp: missing operand\nUsage: cp [-r] <source> <destination>");
        }

        boolean recursive = false;
        String src = null;
        String dst = null;

        for (String arg : args) {
            if (arg.equals("-r") || arg.equals("-R")) {
                recursive = true;
            } else if (src == null) {
                src = arg;
            } else {
                dst = arg;
            }
        }

        if (src == null || dst == null) {
            return CommandResult.error("cp: missing source or destination");
        }

        Path source = cwd.resolve(src).normalize();
        Path destination = cwd.resolve(dst).normalize();

        if (!Files.exists(source)) {
            return CommandResult.error("cp: '" + src + "': No such file or directory");
        }

        try {
            if (Files.isDirectory(source)) {
                if (!recursive) {
                    return CommandResult.error("cp: '" + src + "' is a directory (use -r)");
                }
                copyDirectoryRecursive(source, destination);
            } else {
                if (Files.isDirectory(destination)) {
                    destination = destination.resolve(source.getFileName());
                }
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return CommandResult.success("Copied: " + src + " -> " + dst);
        } catch (IOException e) {
            return CommandResult.error("cp: error: " + e.getMessage());
        }
    }

    private void copyDirectoryRecursive(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = destination.resolve(source.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path targetFile = destination.resolve(source.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    @Override
    public String getName() { return "cp"; }

    @Override
    public String getDescription() { return "Copy files or directories"; }

    @Override
    public String getUsage() { return "cp [-r] <source> <destination>"; }
}
