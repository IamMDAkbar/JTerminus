package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.net.InetAddress;
import java.nio.file.Path;

/**
 * Displays the hostname.
 */
public class HostnameCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        try {
            return CommandResult.success(InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            return CommandResult.success(System.getenv("COMPUTERNAME") != null ?
                    System.getenv("COMPUTERNAME") : "unknown");
        }
    }

    @Override
    public String getName() { return "hostname"; }

    @Override
    public String getDescription() { return "Display system hostname"; }

    @Override
    public String getUsage() { return "hostname"; }
}
