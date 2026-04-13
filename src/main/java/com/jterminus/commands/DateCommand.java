package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Displays the current date and time.
 */
public class DateCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");
        return CommandResult.success(LocalDateTime.now().format(formatter));
    }

    @Override
    public String getName() { return "date"; }

    @Override
    public String getDescription() { return "Display current date and time"; }

    @Override
    public String getUsage() { return "date"; }
}
