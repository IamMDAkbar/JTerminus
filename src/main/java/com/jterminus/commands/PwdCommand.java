package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Prints the current working directory.
 */
public class PwdCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        return CommandResult.success(cwd.toAbsolutePath().toString());
    }

    @Override
    public String getName() { return "pwd"; }

    @Override
    public String getDescription() { return "Print working directory"; }

    @Override
    public String getUsage() { return "pwd"; }
}
