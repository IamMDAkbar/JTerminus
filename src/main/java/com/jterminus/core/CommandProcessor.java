package com.jterminus.core;

import com.jterminus.commands.*;
import com.jterminus.shell.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Central command processor that parses input, resolves commands
 * via the current shell adapter, and executes them.
 */
public class CommandProcessor {

    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final CommandHistory history;
    private ShellAdapter shellAdapter;
    private Path currentDirectory;
    private final String username;

    public CommandProcessor() {
        this.history = new CommandHistory();
        this.username = System.getProperty("user.name");
        this.currentDirectory = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        this.shellAdapter = new LinuxShellAdapter();

        registerCommands();
    }

    /**
     * Register all built-in commands.
     */
    private void registerCommands() {
        registerCommand(new MkdirCommand());
        registerCommand(new RmdirCommand());
        registerCommand(new LsCommand());
        registerCommand(new CdCommand());
        registerCommand(new TouchCommand());
        registerCommand(new RmCommand());
        registerCommand(new CpCommand());
        registerCommand(new MvCommand());
        registerCommand(new CatCommand());
        registerCommand(new EchoCommand());
        registerCommand(new PwdCommand());
        registerCommand(new ClearCommand());
        registerCommand(new HistoryCommand(history));
        registerCommand(new GrepCommand());
        registerCommand(new FindCommand());
        registerCommand(new HeadCommand());
        registerCommand(new TailCommand());
        registerCommand(new DateCommand());
        registerCommand(new WhoamiCommand());
        registerCommand(new HostnameCommand());
        registerCommand(new ExitCommand());
        
        // Additional Linux commands
        registerCommand(new WcCommand());
        registerCommand(new SortCommand());
        registerCommand(new UniqCommand());
        registerCommand(new DiffCommand());
        registerCommand(new WhichCommand());
        registerCommand(new TrCommand());
        registerCommand(new CutCommand());
        registerCommand(new LnCommand());
        registerCommand(new TeeCommand());

        // Help command needs reference to command map
        commands.put("help", new HelpCommand(commands));
    }

    private void registerCommand(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    /**
     * Process a raw input string and return the result.
     */
    public CommandResult processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return CommandResult.success("");
        }

        String trimmed = input.trim();
        String[] parts = parseCommandLine(trimmed);

        if (parts.length == 0) {
            return CommandResult.success("");
        }

        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        // Handle special aliases
        if (commandName.equals("ll") && shellAdapter.resolveCommand("ll").equals("ls")) {
            // ll = ls -l
            String[] newArgs = new String[args.length + 1];
            newArgs[0] = "-l";
            System.arraycopy(args, 0, newArgs, 1, args.length);
            args = newArgs;
            commandName = "ls";
        } else if (commandName.equals("la") && shellAdapter.resolveCommand("la").equals("ls")) {
            // la = ls -la
            String[] newArgs = new String[args.length + 1];
            newArgs[0] = "-la";
            System.arraycopy(args, 0, newArgs, 1, args.length);
            args = newArgs;
            commandName = "ls";
        } else {
            // Resolve via shell adapter
            commandName = shellAdapter.resolveCommand(commandName);
        }

        // Find and execute the command
        Command command = commands.get(commandName);
        if (command == null) {
            // Try external command
            command = new ExternalCommand(parts[0]);
        }

        CommandResult result = command.execute(args, currentDirectory);

        // Handle directory change
        if (result.getNewDirectory() != null) {
            currentDirectory = Paths.get(result.getNewDirectory()).toAbsolutePath();
        }

        // Store in history
        history.add(trimmed, shellAdapter.getShellName(),
                result.getOutput(), result.isError());

        return result;
    }

    /**
     * Parse a command line string into parts, respecting quoted strings.
     */
    private String[] parseCommandLine(String input) {
        List<String> parts = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                parts.add(matcher.group(2));
            } else {
                parts.add(matcher.group(3));
            }
        }

        return parts.toArray(new String[0]);
    }

    // --- Getters and setters ---

    public ShellAdapter getShellAdapter() {
        return shellAdapter;
    }

    public void setShellAdapter(ShellAdapter adapter) {
        this.shellAdapter = adapter;
    }

    public void switchShell(String shellName) {
        switch (shellName.toLowerCase()) {
            case "linux" -> shellAdapter = new LinuxShellAdapter();
            case "cmd" -> shellAdapter = new CmdShellAdapter();
            case "powershell" -> shellAdapter = new PowerShellAdapter();
            case "redhat" -> shellAdapter = new RedHatShellAdapter();
        }
    }

    public String getPrompt() {
        return shellAdapter.getPrompt(currentDirectory, username);
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public CommandHistory getHistory() {
        return history;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
}
