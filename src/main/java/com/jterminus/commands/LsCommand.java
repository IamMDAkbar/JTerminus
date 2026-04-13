package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lists directory contents. Supports -l (long format) and -a (show hidden) flags.
 */
public class LsCommand implements Command {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd HH:mm");

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        boolean longFormat = false;
        boolean showHidden = false;
        String targetDir = null;

        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.contains("l")) longFormat = true;
                if (arg.contains("a")) showHidden = true;
            } else {
                targetDir = arg;
            }
        }

        Path dir = targetDir != null ? cwd.resolve(targetDir).normalize() : cwd;

        if (!Files.exists(dir)) {
            return CommandResult.error("ls: cannot access '" + (targetDir != null ? targetDir : ".") + "': No such file or directory");
        }

        if (!Files.isDirectory(dir)) {
            // It's a file — just show the file name
            if (longFormat) {
                try {
                    return CommandResult.success(formatLongEntry(dir));
                } catch (IOException e) {
                    return CommandResult.success(dir.getFileName().toString());
                }
            }
            return CommandResult.success(dir.getFileName().toString());
        }

        try {
            List<Path> entries = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (!showHidden && entry.getFileName().toString().startsWith(".")) {
                        continue;
                    }
                    entries.add(entry);
                }
            }

            entries.sort(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()));

            if (entries.isEmpty()) {
                return CommandResult.success("");
            }

            StringBuilder sb = new StringBuilder();

            if (longFormat) {
                sb.append(String.format("total %d%n", entries.size()));
                for (Path entry : entries) {
                    sb.append(formatLongEntry(entry)).append("\n");
                }
            } else {
                // Columnar output
                int col = 0;
                for (Path entry : entries) {
                    String name = entry.getFileName().toString();
                    if (Files.isDirectory(entry)) {
                        name = name + "/";
                    }
                    sb.append(String.format("%-24s", name));
                    col++;
                    if (col % 4 == 0) sb.append("\n");
                }
                if (col % 4 != 0) sb.append("\n");
            }

            return CommandResult.success(sb.toString().stripTrailing());
        } catch (IOException e) {
            return CommandResult.error("ls: error reading directory: " + e.getMessage());
        }
    }

    private String formatLongEntry(Path entry) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
        String type = attrs.isDirectory() ? "d" : "-";
        String perms = "rwxr-xr-x"; // Simulated permissions
        long size = attrs.size();
        String date = attrs.lastModifiedTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .format(DATE_FMT);
        String name = entry.getFileName().toString();
        if (attrs.isDirectory()) name += "/";

        return String.format("%s%s  %8d  %s  %s", type, perms, size, date, name);
    }

    @Override
    public String getName() { return "ls"; }

    @Override
    public String getDescription() { return "List directory contents"; }

    @Override
    public String getUsage() { return "ls [-la] [directory]"; }
}
