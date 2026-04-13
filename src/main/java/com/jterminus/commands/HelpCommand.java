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
            // Show detailed help for a specific command
            Command cmd = commands.get(args[0].toLowerCase());
            if (cmd != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
                sb.append("║  Command Details                                          ║\n");
                sb.append("╠═══════════════════════════════════════════════════════════╣\n");
                sb.append(String.format("║  Name:        %-50s ║\n", cmd.getName()));
                sb.append(String.format("║  Description: %-50s ║\n", cmd.getDescription()));
                sb.append(String.format("║  Usage:       %-50s ║\n", cmd.getUsage()));
                sb.append("╚═══════════════════════════════════════════════════════════╝\n");
                return CommandResult.success(sb.toString());
            }
            return CommandResult.error("help: no help entry for '" + args[0] + "'");
        }

        // Show all commands with categories
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                         JTerminus Commands (v1.0.0)                                ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════════════════════════╣\n");

        // Categorize commands
        Map<String, Command> sorted = new TreeMap<>(commands);
        
        sb.append("║ FILE OPERATIONS:                                                                 ║\n");
        String[] fileOps = {"ls", "cd", "pwd", "mkdir", "rmdir", "touch", "rm", "cp", "mv", "cat", "ln"};
        for (String cmd : fileOps) {
            if (sorted.containsKey(cmd)) {
                Command c = sorted.get(cmd);
                sb.append(String.format("║   %-12s  %-57s ║%n", cmd, c.getDescription()));
            }
        }
        
        sb.append("║\n");
        sb.append("║ TEXT PROCESSING:                                                                 ║\n");
        String[] textOps = {"grep", "head", "tail", "cat", "cut", "tr", "sort", "uniq", "wc", "diff"};
        for (String cmd : textOps) {
            if (sorted.containsKey(cmd)) {
                Command c = sorted.get(cmd);
                sb.append(String.format("║   %-12s  %-57s ║%n", cmd, c.getDescription()));
            }
        }
        
        sb.append("║\n");
        sb.append("║ SYSTEM INFO:                                                                     ║\n");
        String[] sysOps = {"date", "whoami", "hostname", "which", "echo"};
        for (String cmd : sysOps) {
            if (sorted.containsKey(cmd)) {
                Command c = sorted.get(cmd);
                sb.append(String.format("║   %-12s  %-57s ║%n", cmd, c.getDescription()));
            }
        }
        
        sb.append("║\n");
        sb.append("║ UTILITIES:                                                                       ║\n");
        String[] utils = {"clear", "history", "find", "exit", "help"};
        for (String cmd : utils) {
            if (sorted.containsKey(cmd)) {
                Command c = sorted.get(cmd);
                sb.append(String.format("║   %-12s  %-57s ║%n", cmd, c.getDescription()));
            }
        }
        
        sb.append("║\n");
        sb.append("╠════════════════════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Type 'help <command>' for detailed usage information                             ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════════════════════╝");

        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() { return "help"; }

    @Override
    public String getDescription() { return "Show available commands"; }

    @Override
    public String getUsage() { return "help [command]"; }
}
