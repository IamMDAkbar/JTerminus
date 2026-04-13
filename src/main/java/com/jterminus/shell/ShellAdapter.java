package com.jterminus.shell;

import java.nio.file.Path;
import java.util.Map;

/**
 * Interface for shell adapters that define the personality of each shell mode.
 * Each adapter maps shell-specific command names to internal command names.
 */
public interface ShellAdapter {

    /**
     * Get the name of this shell (e.g., "Linux", "CMD", "PowerShell").
     */
    String getShellName();

    /**
     * Get the display prompt for this shell.
     *
     * @param cwd      the current working directory
     * @param username the current username
     * @return formatted prompt string
     */
    String getPrompt(Path cwd, String username);

    /**
     * Get the command alias mappings.
     * Maps shell-specific command names to the internal canonical command names.
     * e.g., "dir" -> "ls", "del" -> "rm", "cls" -> "clear"
     *
     * @return map of aliases
     */
    Map<String, String> getCommandAliases();

    /**
     * Resolve a shell-specific command name to the internal canonical name.
     *
     * @param shellCommand the command as typed by the user
     * @return the canonical internal command name
     */
    default String resolveCommand(String shellCommand) {
        String lower = shellCommand.toLowerCase().trim();
        Map<String, String> aliases = getCommandAliases();
        return aliases.getOrDefault(lower, lower);
    }

    /**
     * Get the path separator style for this shell.
     */
    default String getPathSeparator() {
        return "/";
    }

    /**
     * Format a path for display according to this shell's conventions.
     */
    default String formatPath(Path path) {
        return path.toAbsolutePath().toString();
    }

    /**
     * Get a welcome message for this shell.
     */
    String getWelcomeMessage();
}
