package com.jterminus.ui;

import com.jterminus.core.CommandProcessor;

import javax.swing.*;
import java.awt.*;

/**
 * Status bar showing current directory, shell mode, and command count.
 */
public class StatusBarPanel extends JPanel {

    private final JLabel cwdLabel;
    private final JLabel shellModeLabel;
    private final JLabel commandCountLabel;
    private final CommandProcessor processor;

    public StatusBarPanel(CommandProcessor processor) {
        this.processor = processor;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 28));
        setBackground(new Color(0x181825));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x313244)));

        // Left — CWD
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        leftPanel.setOpaque(false);

        JLabel folderIcon = new JLabel("📂");
        folderIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        leftPanel.add(folderIcon);

        cwdLabel = new JLabel();
        cwdLabel.setForeground(new Color(0x94E2D5));
        cwdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        leftPanel.add(cwdLabel);

        add(leftPanel, BorderLayout.WEST);

        // Center — shell mode
        shellModeLabel = new JLabel("", JLabel.CENTER);
        shellModeLabel.setForeground(new Color(0xF9E2AF));
        shellModeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        add(shellModeLabel, BorderLayout.CENTER);

        // Right — command count
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightPanel.setOpaque(false);

        commandCountLabel = new JLabel();
        commandCountLabel.setForeground(new Color(0x6C7086));
        commandCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rightPanel.add(commandCountLabel);

        add(rightPanel, BorderLayout.EAST);

        update();
    }

    /**
     * Update the status bar with current information.
     */
    public void update() {
        String cwd = processor.getShellAdapter().formatPath(processor.getCurrentDirectory());
        // Truncate if too long
        if (cwd.length() > 60) {
            cwd = "..." + cwd.substring(cwd.length() - 57);
        }
        cwdLabel.setText(cwd);
        shellModeLabel.setText("● " + processor.getShellAdapter().getShellName());
        commandCountLabel.setText("Commands: " + processor.getHistory().size());
    }
}
