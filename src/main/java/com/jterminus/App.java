package com.jterminus;

import com.formdev.flatlaf.FlatDarkLaf;
import com.jterminus.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * JTerminus — Multi-Shell CLI Terminal Application
 *
 * Entry point that configures FlatLaf dark theme and launches the main window.
 */
public class App {

    public static void main(String[] args) {
        // Configure system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set FlatLaf theme
        try {
            UIManager.put("TextPane.background", new Color(0x1E1E2E));
            UIManager.put("TextPane.foreground", new Color(0xCDD6F4));
            UIManager.put("TextPane.caretForeground", new Color(0xCDD6F4));
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.track", new Color(0x1E1E2E));
            UIManager.put("ScrollBar.thumb", new Color(0x45475A));
            UIManager.put("ComboBox.background", new Color(0x313244));
            UIManager.put("ComboBox.foreground", new Color(0xCDD6F4));
            UIManager.put("ComboBox.selectionBackground", new Color(0x45475A));
            UIManager.put("ComboBox.buttonBackground", new Color(0x313244));
            UIManager.put("MenuBar.background", new Color(0x181825));
            UIManager.put("Menu.foreground", new Color(0xCDD6F4));
            UIManager.put("MenuItem.foreground", new Color(0xCDD6F4));
            UIManager.put("MenuItem.background", new Color(0x1E1E2E));
            UIManager.put("MenuItem.selectionBackground", new Color(0x45475A));
            UIManager.put("PopupMenu.background", new Color(0x1E1E2E));
            UIManager.put("Separator.foreground", new Color(0x313244));
            UIManager.put("OptionPane.background", new Color(0x1E1E2E));
            UIManager.put("Panel.background", new Color(0x1E1E2E));
            UIManager.put("OptionPane.messageForeground", new Color(0xCDD6F4));

            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
            // Fall back to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Launch the application on the EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
