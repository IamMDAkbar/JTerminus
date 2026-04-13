package com.jterminus.core;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String output;
    private final boolean error;
    private final int exitCode;
    private final boolean clearScreen;
    private final boolean exit;
    private final String newDirectory;

    private CommandResult(Builder builder) {
        this.output = builder.output;
        this.error = builder.error;
        this.exitCode = builder.exitCode;
        this.clearScreen = builder.clearScreen;
        this.exit = builder.exit;
        this.newDirectory = builder.newDirectory;
    }

    public String getOutput() {
        return output;
    }

    public boolean isError() {
        return error;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isClearScreen() {
        return clearScreen;
    }

    public boolean isExit() {
        return exit;
    }

    public String getNewDirectory() {
        return newDirectory;
    }

    // --- Static factory methods ---

    public static CommandResult success(String output) {
        return new Builder().output(output).build();
    }

    public static CommandResult error(String output) {
        return new Builder().output(output).error(true).exitCode(1).build();
    }

    public static CommandResult clear() {
        return new Builder().clearScreen(true).build();
    }

    public static CommandResult exit() {
        return new Builder().exit(true).build();
    }

    public static CommandResult changeDirectory(String newDir) {
        return new Builder().newDirectory(newDir).build();
    }

    public static CommandResult changeDirectory(String newDir, String output) {
        return new Builder().newDirectory(newDir).output(output).build();
    }

    // --- Builder ---

    public static class Builder {
        private String output = "";
        private boolean error = false;
        private int exitCode = 0;
        private boolean clearScreen = false;
        private boolean exit = false;
        private String newDirectory = null;

        public Builder output(String output) {
            this.output = output;
            return this;
        }

        public Builder error(boolean error) {
            this.error = error;
            return this;
        }

        public Builder exitCode(int exitCode) {
            this.exitCode = exitCode;
            return this;
        }

        public Builder clearScreen(boolean clearScreen) {
            this.clearScreen = clearScreen;
            return this;
        }

        public Builder exit(boolean exit) {
            this.exit = exit;
            return this;
        }

        public Builder newDirectory(String newDirectory) {
            this.newDirectory = newDirectory;
            return this;
        }

        public CommandResult build() {
            return new CommandResult(this);
        }
    }
}
