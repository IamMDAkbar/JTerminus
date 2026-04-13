package com.jterminus.ui;

import com.jterminus.core.CommandProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application frame.
 */
public class MainFrame extends JFrame {

    private final CommandProcessor processor;
    private final TerminalPanel terminalPanel;
    private final ToolBarPanel toolBarPanel;
    private final StatusBarPanel statusBarPanel;
    private int currentFontSize = 14;

    public MainFrame() {
        super("JTerminus — Multi-Shell Terminal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        // Set application icon color (use a simple colored square)
        try {
            setIconImage(createAppIcon());
        } catch (Exception ignored) {}

        // Create the command processor
        processor = new CommandProcessor();

        // Create UI components
        toolBarPanel = new ToolBarPanel();
        terminalPanel = new TerminalPanel(processor);
        statusBarPanel = new StatusBarPanel(processor);

        // Wire up events
        setupToolbarEvents();
        terminalPanel.setOnCommandExecuted(statusBarPanel::update);

        // Layout
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());
        add(toolBarPanel, BorderLayout.NORTH);
        add(terminalPanel, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);

        // Menu bar
        setJMenuBar(createMenuBar());

        // Focus terminal on window activation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                terminalPanel.requestFocusInWindow();
            }
        });

        // Global key bindings
        setupGlobalKeyBindings();
    }

    private Image createAppIcon() {
        // Create a simple gradient icon
        int size = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(0x94E2D5), size, size, new Color(0x89B4FA));
        g2.setPaint(gp);
        g2.fillRoundRect(4, 4, size - 8, size - 8, 16, 16);

        // Terminal prompt symbol  >_
        g2.setColor(new Color(0x1E1E2E));
        g2.setFont(new Font("Consolas", Font.BOLD, 30));
        g2.drawString(">_", 12, 44);

        g2.dispose();
        return img;
    }

    private void setupToolbarEvents() {
        toolBarPanel.setOnShellChange(e -> {
            String shell = toolBarPanel.getSelectedShell();
            terminalPanel.switchShell(shell);
        });

        toolBarPanel.setOnClear(e -> terminalPanel.clearTerminalWithConfirmation());

        toolBarPanel.setOnFontIncrease(e -> {
            if (currentFontSize < 28) {
                currentFontSize += 2;
                terminalPanel.setFontSize(currentFontSize);
            }
        });

        toolBarPanel.setOnFontDecrease(e -> {
            if (currentFontSize > 8) {
                currentFontSize -= 2;
                terminalPanel.setFontSize(currentFontSize);
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0x181825));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x313244)));

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(new Color(0xCDD6F4));

        JMenuItem clearItem = new JMenuItem("Clear Terminal");
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        clearItem.addActionListener(e -> terminalPanel.clearTerminalWithConfirmation());
        fileMenu.add(clearItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> {
            dispose();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Shell menu
        JMenu shellMenu = new JMenu("Shell");
        shellMenu.setForeground(new Color(0xCDD6F4));

        String[] shells = {"Linux", "CMD", "PowerShell", "RedHat"};
        for (String shell : shells) {
            JMenuItem item = new JMenuItem(shell);
            item.addActionListener(e -> {
                toolBarPanel.setSelectedShell(shell);
                terminalPanel.switchShell(shell);
            });
            shellMenu.add(item);
        }

        menuBar.add(shellMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(new Color(0xCDD6F4));

        JMenuItem fontInc = new JMenuItem("Increase Font Size");
        fontInc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        fontInc.addActionListener(e -> {
            if (currentFontSize < 28) {
                currentFontSize += 2;
                terminalPanel.setFontSize(currentFontSize);
            }
        });
        viewMenu.add(fontInc);

        JMenuItem fontDec = new JMenuItem("Decrease Font Size");
        fontDec.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        fontDec.addActionListener(e -> {
            if (currentFontSize > 8) {
                currentFontSize -= 2;
                terminalPanel.setFontSize(currentFontSize);
            }
        });
        viewMenu.add(fontDec);

        JMenuItem resetFont = new JMenuItem("Reset Font Size");
        resetFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        resetFont.addActionListener(e -> {
            currentFontSize = 14;
            terminalPanel.setFontSize(currentFontSize);
        });
        viewMenu.add(resetFont);

        menuBar.add(viewMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(new Color(0xCDD6F4));

        JMenuItem aboutItem = new JMenuItem("About JTerminus");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        JMenuItem shortcutsItem = new JMenuItem("Keyboard Shortcuts");
        shortcutsItem.addActionListener(e -> showShortcutsDialog());
        helpMenu.add(shortcutsItem);

        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAboutDialog() {
        String msg = """
                JTerminus v1.0.0
                Multi-Shell CLI Terminal
                
                A custom terminal emulator supporting
                Linux, CMD, PowerShell, and RedHat shells.
                
                Built with Java Swing + FlatLaf
                """;
        JOptionPane.showMessageDialog(this, msg, "About JTerminus", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showShortcutsDialog() {
        String msg = """
                Keyboard Shortcuts:
                
                Enter        — Execute command
                Up/Down      — Navigate command history
                Ctrl+C       — Cancel current input
                Ctrl+L       — Clear terminal
                Ctrl+Q       — Exit
                Ctrl+=/−     — Increase/Decrease font
                Ctrl+0       — Reset font size
                Home         — Move cursor to start of input
                """;
        JOptionPane.showMessageDialog(this, msg, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupGlobalKeyBindings() {
        // Make terminal receive focus when clicking anywhere
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                terminalPanel.requestFocusInWindow();
            }
        });
    }
}
