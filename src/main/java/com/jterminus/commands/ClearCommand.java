package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Clears the terminal screen.
 */
public class ClearCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        return CommandResult.clear();
    }

    @Override
    public String getName() { return "clear"; }

    @Override
    public String getDescription() { return "Clear the terminal screen"; }

    @Override
    public String getUsage() { return "clear"; }
}
