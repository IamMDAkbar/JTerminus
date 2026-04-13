package com.jterminus.shell;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PowerShell adapter.
 * Prompt: PS C:\path>
 */
public class PowerShellAdapter implements ShellAdapter {

    @Override
    public String getShellName() {
        return "PowerShell";
    }

    @Override
    public String getPrompt(Path cwd, String username) {
        String path = cwd.toAbsolutePath().toString();
        return "PS " + path + "> ";
    }

    @Override
    public Map<String, String> getCommandAliases() {
        Map<String, String> aliases = new LinkedHashMap<>();
        // PowerShell cmdlets and their aliases
        aliases.put("get-childitem", "ls");
        aliases.put("gci", "ls");
        aliases.put("ls", "ls");
        aliases.put("dir", "ls");
        aliases.put("set-location", "cd");
        aliases.put("sl", "cd");
        aliases.put("cd", "cd");
        aliases.put("get-location", "pwd");
        aliases.put("gl", "pwd");
        aliases.put("pwd", "pwd");
        aliases.put("new-item", "mkdir");
        aliases.put("ni", "touch");
        aliases.put("mkdir", "mkdir");
        aliases.put("md", "mkdir");
        aliases.put("remove-item", "rm");
        aliases.put("ri", "rm");
        aliases.put("rm", "rm");
        aliases.put("del", "rm");
        aliases.put("rmdir", "rmdir");
        aliases.put("copy-item", "cp");
        aliases.put("ci", "cp");
        aliases.put("cp", "cp");
        aliases.put("copy", "cp");
        aliases.put("move-item", "mv");
        aliases.put("mi", "mv");
        aliases.put("mv", "mv");
        aliases.put("move", "mv");
        aliases.put("get-content", "cat");
        aliases.put("gc", "cat");
        aliases.put("cat", "cat");
        aliases.put("type", "cat");
        aliases.put("write-output", "echo");
        aliases.put("echo", "echo");
        aliases.put("clear-host", "clear");
        aliases.put("cls", "clear");
        aliases.put("clear", "clear");
        aliases.put("select-string", "grep");
        aliases.put("get-date", "date");
        aliases.put("whoami", "whoami");
        aliases.put("hostname", "hostname");
        aliases.put("touch", "touch");
        aliases.put("history", "history");
        aliases.put("get-history", "history");
        aliases.put("help", "help");
        aliases.put("get-help", "help");
        aliases.put("head", "head");
        aliases.put("tail", "tail");
        aliases.put("find", "find");
        aliases.put("grep", "grep");
        aliases.put("exit", "exit");
        return aliases;
    }

    @Override
    public String getPathSeparator() {
        return "\\";
    }

    @Override
    public String formatPath(Path path) {
        return path.toAbsolutePath().toString();
    }

    @Override
    public String getWelcomeMessage() {
        return "Windows PowerShell [JTerminus v1.0.0]\n" +
               "Copyright (C) JTerminus. All rights reserved.\n\n" +
               "Type 'help' for a list of available commands.\n";
    }
}
