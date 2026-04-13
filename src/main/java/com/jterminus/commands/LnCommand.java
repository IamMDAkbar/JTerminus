package com.jterminus.commands;

import com.jterminus.core.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Symbolic link creator.
 */
public class LnCommand implements Command {

    @Override
    public CommandResult execute(String[] args, Path cwd) {
        if (args.length < 2) {
            return CommandResult.error("ln: missing operand");
        }

        String target = args[0];
        String linkName = args[1];

        Path targetPath = cwd.resolve(target).normalize();
        Path linkPath = cwd.resolve(linkName).normalize();

        if (!Files.exists(targetPath)) {
            return CommandResult.error("ln: cannot access '" + target + "': No such file or directory");
        }

        try {
            Files.createSymbolicLink(linkPath, targetPath);
            return CommandResult.success("Created symbolic link: " + linkName + " -> " + target);
        } catch (UnsupportedOperationException e) {
            return CommandResult.error("ln: symbolic links not supported on this system");
        } catch (Exception e) {
            return CommandResult.error("ln: error creating link: " + e.getMessage());
        }
    }

    @Override
    public String getName() { return "ln"; }

    @Override
    public String getDescription() { return "Create symbolic links"; }

    @Override
    public String getUsage() { return "ln [target] [link-name]"; }
}
