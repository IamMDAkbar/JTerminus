package com.jterminus.shell;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Windows CMD shell adapter.
 * Prompt: C:\path>
 */
public class CmdShellAdapter implements ShellAdapter {

    @Override
    public String getShellName() {
        return "CMD";
    }

    @Override
    public String getPrompt(Path cwd, String username) {
        String path = cwd.toAbsolutePath().toString();
        return path + ">";
    }

    @Override
    public Map<String, String> getCommandAliases() {
        Map<String, String> aliases = new LinkedHashMap<>();
        // CMD command mappings
        aliases.put("dir", "ls");
        aliases.put("cd", "cd");
        aliases.put("chdir", "cd");
        aliases.put("md", "mkdir");
        aliases.put("mkdir", "mkdir");
        aliases.put("rd", "rmdir");
        aliases.put("rmdir", "rmdir");
        aliases.put("del", "rm");
        aliases.put("erase", "rm");
        aliases.put("copy", "cp");
        aliases.put("xcopy", "cp");
        aliases.put("move", "mv");
        aliases.put("ren", "mv");
        aliases.put("rename", "mv");
        aliases.put("type", "cat");
        aliases.put("echo", "echo");
        aliases.put("cls", "clear");
        aliases.put("date", "date");
        aliases.put("time", "date");
        aliases.put("whoami", "whoami");
        aliases.put("hostname", "hostname");
        aliases.put("findstr", "grep");
        aliases.put("where", "find");
        aliases.put("history", "history");
        aliases.put("help", "help");
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
        return "Microsoft Windows [JTerminus v1.0.0]\n" +
               "(c) JTerminus. All rights reserved.\n\n" +
               "Type 'help' for a list of available commands.\n";
    }
}
