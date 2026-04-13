package com.jterminus.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores command execution history with timestamps.
 * Supports navigation (up/down arrows) and display.
 */
public class CommandHistory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<HistoryEntry> entries = new ArrayList<>();
    private int navigationIndex = -1;

    /**
     * Represents a single history entry.
     */
    public static class HistoryEntry {
        private final String command;
        private final String shellMode;
        private final LocalDateTime timestamp;
        private final String output;
        private final boolean wasError;

        public HistoryEntry(String command, String shellMode, String output, boolean wasError) {
            this.command = command;
            this.shellMode = shellMode;
            this.timestamp = LocalDateTime.now();
            this.output = output;
            this.wasError = wasError;
        }

        public String getCommand() { return command; }
        public String getShellMode() { return shellMode; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getOutput() { return output; }
        public boolean wasError() { return wasError; }

        @Override
        public String toString() {
            return String.format("[%s] [%s] %s", FORMATTER.format(timestamp), shellMode, command);
        }
    }

    /**
     * Adds a command to history.
     */
    public void add(String command, String shellMode, String output, boolean wasError) {
        if (command != null && !command.trim().isEmpty()) {
            entries.add(new HistoryEntry(command.trim(), shellMode, output, wasError));
            resetNavigation();
        }
    }

    /**
     * Navigate to the previous command (Up arrow).
     */
    public String navigateUp() {
        if (entries.isEmpty()) return null;

        if (navigationIndex == -1) {
            navigationIndex = entries.size() - 1;
        } else if (navigationIndex > 0) {
            navigationIndex--;
        }
        return entries.get(navigationIndex).getCommand();
    }

    /**
     * Navigate to the next command (Down arrow).
     */
    public String navigateDown() {
        if (entries.isEmpty() || navigationIndex == -1) return "";

        if (navigationIndex < entries.size() - 1) {
            navigationIndex++;
            return entries.get(navigationIndex).getCommand();
        } else {
            navigationIndex = -1;
            return "";
        }
    }

    /**
     * Reset navigation index (called after command execution).
     */
    public void resetNavigation() {
        navigationIndex = -1;
    }

    /**
     * Get all history entries.
     */
    public List<HistoryEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Get formatted history for display.
     */
    public String getFormattedHistory() {
        if (entries.isEmpty()) {
            return "No commands in history.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            HistoryEntry entry = entries.get(i);
            sb.append(String.format("  %4d  %s  %-12s  %s%n",
                    i + 1,
                    FORMATTER.format(entry.getTimestamp()),
                    entry.getShellMode(),
                    entry.getCommand()));
        }
        return sb.toString();
    }

    /**
     * Get the total number of commands executed.
     */
    public int size() {
        return entries.size();
    }

    /**
     * Clear all history.
     */
    public void clear() {
        entries.clear();
        resetNavigation();
    }
}
