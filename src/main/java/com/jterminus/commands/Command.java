package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Interface for all built-in commands.
 */
public interface Command {

    /**
     * Execute the command with the given arguments.
     *
     * @param args the command arguments (excluding the command name itself)
     * @param cwd  the current working directory
     * @return the result of the command execution
     */
    CommandResult execute(String[] args, Path cwd);

    /**
     * Get the primary name of this command.
     */
    String getName();

    /**
     * Get a short description of what this command does.
     */
    String getDescription();

    /**
     * Get usage string for this command.
     */
    String getUsage();
}
