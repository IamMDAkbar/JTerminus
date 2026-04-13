package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Displays available commands and their descriptions.
 */
public class HelpCommand implements Command {

    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length > 0) {
            // Show help for a specific command
            Command cmd = commands.get(args[0].toLowerCase());
            if (cmd != null) {
                return CommandResult.success(
                        "  " + cmd.getName() + " - " + cmd.getDescription() + "\n" +
                        "  Usage: " + cmd.getUsage()
                );
            }
            return CommandResult.error("help: no help entry for '" + args[0] + "'");
        }

        // Show all commands
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    JTerminus Commands                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        Map<String, Command> sorted = new TreeMap<>(commands);
        for (Map.Entry<String, Command> entry : sorted.entrySet()) {
            sb.append(String.format("║  %-14s  %-42s  ║%n",
                    entry.getKey(), entry.getValue().getDescription()));
        }

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Type 'help <command>' for detailed usage info              ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝");

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "help"; }

    @Override
    public String getDescription() { return "Show available commands"; }

    @Override
    public String getUsage() { return "help [command]"; }
}
