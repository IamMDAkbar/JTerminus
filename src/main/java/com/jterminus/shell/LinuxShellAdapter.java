package com.jterminus.shell;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Linux (Bash) shell adapter.
 * Prompt: user@jterminus:~/path$
 */
public class LinuxShellAdapter implements ShellAdapter {

    @Override
    public String getShellName() {
        return "Linux";
    }

    @Override
    public String getPrompt(Path cwd, String username) {
        String home = System.getProperty("user.home");
        String path = cwd.toAbsolutePath().toString();

        // Replace home directory with ~
        if (path.startsWith(home)) {
            path = "~" + path.substring(home.length());
        }
        // Use forward slashes for Linux style
        path = path.replace("\\", "/");

        return username + "@jterminus:" + path + "$ ";
    }

    @Override
    public Map<String, String> getCommandAliases() {
        Map<String, String> aliases = new LinkedHashMap<>();
        // Linux commands map directly to canonical names
        aliases.put("ls", "ls");
        aliases.put("ll", "ls"); // ll = ls -l alias handled specially
        aliases.put("cd", "cd");
        aliases.put("pwd", "pwd");
        aliases.put("mkdir", "mkdir");
        aliases.put("rmdir", "rmdir");
        aliases.put("rm", "rm");
        aliases.put("cp", "cp");
        aliases.put("mv", "mv");
        aliases.put("cat", "cat");
        aliases.put("touch", "touch");
        aliases.put("echo", "echo");
        aliases.put("clear", "clear");
        aliases.put("history", "history");
        aliases.put("help", "help");
        aliases.put("grep", "grep");
        aliases.put("find", "find");
        aliases.put("head", "head");
        aliases.put("tail", "tail");
        aliases.put("date", "date");
        aliases.put("whoami", "whoami");
        aliases.put("hostname", "hostname");
        aliases.put("exit", "exit");
        aliases.put("quit", "exit");
        return aliases;
    }

    @Override
    public String formatPath(Path path) {
        return path.toAbsolutePath().toString().replace("\\", "/");
    }

    @Override
    public String getWelcomeMessage() {
        return "GNU/Linux Terminal [JTerminus v1.0.0]\nType 'help' for a list of available commands.\n";
    }
}
