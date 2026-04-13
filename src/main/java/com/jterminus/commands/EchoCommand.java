package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Prints text to the terminal.
 */
public class EchoCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length == 0) {
            return CommandResult.success("");
        }
        String output = String.join(" ", args);
        // Remove surrounding quotes if present
        if ((output.startsWith("\"") && output.endsWith("\"")) ||
            (output.startsWith("'") && output.endsWith("'"))) {
            output = output.substring(1, output.length() - 1);
        }
        return CommandResult.success(output);
    }

    @Override
    public String getName() { return "echo"; }

    @Override
    public String getDescription() { return "Display text"; }

    @Override
    public String getUsage() { return "echo [text]"; }
}
