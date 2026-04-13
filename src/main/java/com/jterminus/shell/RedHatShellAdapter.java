package com.jterminus.shell;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Red Hat Linux shell adapter.
 * Prompt: [user@jterminus path]$
 */
public class RedHatShellAdapter implements ShellAdapter {

    @Override
    public String getShellName() {
        return "RedHat";
    }

    @Override
    public String getPrompt(Path cwd, String username) {
        String home = System.getProperty("user.home");
        String path = cwd.toAbsolutePath().toString();

        // Replace home directory with ~
        if (path.startsWith(home)) {
            path = "~" + path.substring(home.length());
        }
        path = path.replace("\\", "/");

        // Get just the last directory name for the prompt
        String dirName = cwd.getFileName() != null ? cwd.getFileName().toString() : "/";
        if (path.equals("~")) dirName = "~";

        return "[" + username + "@jterminus " + dirName + "]$ ";
    }

    @Override
    public Map<String, String> getCommandAliases() {
        Map<String, String> aliases = new LinkedHashMap<>();
        // RedHat Linux commands (same as Linux plus some extras)
        aliases.put("ls", "ls");
        aliases.put("ll", "ls"); // ll = ls -l
        aliases.put("la", "ls"); // la = ls -la
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
        aliases.put("logout", "exit");
        return aliases;
    }

    @Override
    public String formatPath(Path path) {
        return path.toAbsolutePath().toString().replace("\\", "/");
    }

    @Override
    public String getWelcomeMessage() {
        return "Red Hat Enterprise Linux [JTerminus v1.0.0]\n" +
               "Kernel: JTerminus Custom Kernel\n" +
               "Type 'help' for a list of available commands.\n";
    }
}
