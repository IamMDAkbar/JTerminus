package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Exits the application.
 */
public class ExitCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        return CommandResult.exit();
    }

    @Override
    public String getName() { return "exit"; }

    @Override
    public String getDescription() { return "Exit JTerminus"; }

    @Override
    public String getUsage() { return "exit"; }
}
