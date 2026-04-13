package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Displays the current username.
 */
public class WhoamiCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        return CommandResult.success(System.getProperty("user.name"));
    }

    @Override
    public String getName() { return "whoami"; }

    @Override
    public String getDescription() { return "Display current username"; }

    @Override
    public String getUsage() { return "whoami"; }
}
