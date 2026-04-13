package com.jterminus.commands;

import com.jterminus.core.CommandHistory;
import com.jterminus.core.CommandResult;

import java.nio.file.Path;

/**
 * Displays command history.
 */
public class HistoryCommand implements Command {

    private final CommandHistory history;

    public HistoryCommand(CommandHistory history) {
        this.history = history;
    }

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length > 0 && args[0].equals("-c")) {
            history.clear();
            return CommandResult.success("History cleared.");
        }
        return CommandResult.success(history.getFormattedHistory());
    }

    @Override
    public String getName() { return "history"; }

    @Override
    public String getDescription() { return "Show command history"; }

    @Override
    public String getUsage() { return "history [-c]"; }
}
