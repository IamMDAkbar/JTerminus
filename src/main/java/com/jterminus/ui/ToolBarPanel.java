package com.jterminus.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Toolbar panel with shell selector, font controls, and action buttons.
 */
public class ToolBarPanel extends JPanel {

    private final JComboBox<String> shellSelector;
    private final JButton clearButton;
    private final JButton fontIncButton;
    private final JButton fontDecButton;
    private final JLabel shellIcon;

    // Callbacks
    private ActionListener onShellChange;
    private ActionListener onClear;
    private ActionListener onFontIncrease;
    private ActionListener onFontDecrease;

    public ToolBarPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 44));
        setBackground(new Color(0x181825));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x313244)));

        // Left section — shell icon + selector
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftPanel.setOpaque(false);

        shellIcon = new JLabel(createShellIconImage("CMD"));
        shellIcon.setPreferredSize(new Dimension(24, 24));
        shellIcon.setHorizontalAlignment(SwingConstants.CENTER);
        shellIcon.setForeground(new Color(0x94E2D5));
        leftPanel.add(shellIcon);

        JLabel shellLabel = new JLabel("Shell:");
        shellLabel.setForeground(new Color(0xBAC2DE));
        shellLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(shellLabel);

        shellSelector = new JComboBox<>(new String[]{"Linux", "CMD", "PowerShell", "RedHat"});
        shellSelector.setPreferredSize(new Dimension(130, 28));
        shellSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shellSelector.setFocusable(false);
        shellSelector.addActionListener(e -> {
            if (onShellChange != null) {
                onShellChange.actionPerformed(e);
            }
            updateShellIcon();
        });
        leftPanel.add(shellSelector);

        add(leftPanel, BorderLayout.WEST);

        // Center — title
        JLabel title = new JLabel("JTerminus", JLabel.CENTER);
        title.setForeground(new Color(0x6C7086));
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(title, BorderLayout.CENTER);

        // Right section — controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 6));
        rightPanel.setOpaque(false);

        fontDecButton = createToolButton(createFontIconImage("-"), "Decrease font size");
        fontDecButton.addActionListener(e -> {
            if (onFontDecrease != null) onFontDecrease.actionPerformed(e);
        });
        rightPanel.add(fontDecButton);

        fontIncButton = createToolButton(createFontIconImage("+"), "Increase font size");
        fontIncButton.addActionListener(e -> {
            if (onFontIncrease != null) onFontIncrease.actionPerformed(e);
        });
        rightPanel.add(fontIncButton);

        rightPanel.add(Box.createHorizontalStrut(8));

        clearButton = createToolButton(createClearIconImage(), "Clear terminal");
        clearButton.addActionListener(e -> {
            if (onClear != null) onClear.actionPerformed(e);
        });
        rightPanel.add(clearButton);

        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createToolButton(Icon icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(36, 28));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(0x313244));
        btn.setForeground(new Color(0xCDD6F4));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x45475A), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x45475A));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x313244));
            }
        });

        return btn;
    }

    private void updateShellIcon() {
        String shell = (String) shellSelector.getSelectedItem();
        shellIcon.setIcon(createShellIconImage(shell));
    }

    private Icon createShellIconImage(String shellName) {
        BufferedImage img = new java.awt.image.BufferedImage(24, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x94E2D5));
        
        if (shellName == null) shellName = "CMD";
        
        switch (shellName) {
            case "Linux" -> {
                // Penguin-like shape
                g2.fillOval(8, 2, 8, 8);      // head
                g2.fillPolygon(
                    new int[]{5, 19, 12},      // x
                    new int[]{10, 10, 18},     // y
                    3);                        // body
                g2.fillOval(6, 16, 4, 6);     // left foot
                g2.fillOval(14, 16, 4, 6);    // right foot
            }
            case "PowerShell" -> {
                // Lightning bolt shape
                int[] px = {12, 8, 10, 6, 14, 12, 16, 12};
                int[] py = {2, 12, 12, 18, 8, 10, 14, 20};
                g2.fillPolygon(px, py, 8);
            }
            case "RedHat" -> {
                // Hat-like shape
                g2.fillPolygon(
                    new int[]{4, 20, 18, 10},  // x
                    new int[]{14, 14, 8, 4},   // y
                    4);                        // hat top
                g2.fillRect(5, 14, 14, 8);    // hat brim
            }
            default -> {
                // CMD: Windows logo-like
                g2.fillRect(6, 6, 4, 4);      // top-left
                g2.fillRect(14, 6, 4, 4);     // top-right
                g2.fillRect(6, 14, 4, 4);     // bottom-left
                g2.fillRect(14, 14, 4, 4);    // bottom-right
            }
        }
        
        g2.dispose();
        return new ImageIcon(img);
    }

    private Icon createFontIconImage(String operation) {
        BufferedImage img = new java.awt.image.BufferedImage(24, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0xCDD6F4));
        
        // Draw "A"
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("A", 8, 16);
        
        // Draw +/- sign
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if ("+".equals(operation)) {
            g2.drawLine(18, 4, 18, 12);   // vertical
            g2.drawLine(14, 8, 22, 8);    // horizontal
        } else {
            g2.drawLine(14, 8, 22, 8);    // horizontal only
        }
        
        g2.dispose();
        return new ImageIcon(img);
    }

    private Icon createClearIconImage() {
        BufferedImage img = new java.awt.image.BufferedImage(24, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0xF38BA8));  // red color for clear
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw eraser/clear icon
        g2.drawRect(6, 8, 12, 12);  // rectangle
        g2.drawLine(6, 14, 18, 14); // horizontal divider
        
        g2.dispose();
        return new ImageIcon(img);
    }

    // --- Setters for callbacks ---

    public void setOnShellChange(ActionListener listener) {
        this.onShellChange = listener;
    }

    public void setOnClear(ActionListener listener) {
        this.onClear = listener;
    }

    public void setOnFontIncrease(ActionListener listener) {
        this.onFontIncrease = listener;
    }

    public void setOnFontDecrease(ActionListener listener) {
        this.onFontDecrease = listener;
    }

    public String getSelectedShell() {
        return (String) shellSelector.getSelectedItem();
    }

    public void setSelectedShell(String shell) {
        shellSelector.setSelectedItem(shell);
    }
}
