package com.jterminus.ui;

import com.jterminus.core.CommandProcessor;
import com.jterminus.core.CommandResult;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The main terminal panel with JTextPane for styled output.
 * Handles keyboard input, command execution, and styled text rendering.
 */
public class TerminalPanel extends JPanel {

    private final JTextPane textPane;
    private final StyledDocument doc;
    private final CommandProcessor processor;
    private int inputStartOffset = 0;
    private boolean filterEnabled = true;
    private volatile boolean isExecuting = false;

    // Style attribute names
    private Style promptStyle;
    private Style commandStyle;
    private Style outputStyle;
    private Style errorStyle;
    private Style successStyle;
    private Style systemStyle;

    // Callback for status updates
    private Runnable onCommandExecuted;

    public TerminalPanel(CommandProcessor processor) {
        this.processor = processor;
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setBackground(new Color(0x1E1E2E));
        textPane.setCaretColor(new Color(0xCDD6F4));
        textPane.setSelectionColor(new Color(0x45475A));
        textPane.setSelectedTextColor(new Color(0xCDD6F4));
        textPane.setMargin(new Insets(10, 12, 10, 12));

        // Set monospaced font
        Font termFont = loadTerminalFont();
        textPane.setFont(termFont);

        doc = textPane.getStyledDocument();
        initStyles();

        // Document filter to prevent editing before input area
        ((AbstractDocument) doc).setDocumentFilter(new TerminalDocumentFilter());

        // Key bindings
        textPane.addKeyListener(new TerminalKeyListener());

        // Scroll pane with custom styling
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Show welcome message
        showWelcome();
    }

    private Font loadTerminalFont() {
        // Try JetBrains Mono, then Cascadia Code, then fall back to Consolas/monospaced
        String[] fontNames = {"JetBrains Mono", "Cascadia Code", "Consolas", "Courier New"};
        for (String name : fontNames) {
            Font f = new Font(name, Font.PLAIN, 14);
            if (!f.getFamily().equals("Dialog")) {
                return f;
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, 14);
    }

    private void initStyles() {
        // Prompt style — cyan/teal
        promptStyle = doc.addStyle("prompt", null);
        StyleConstants.setForeground(promptStyle, new Color(0x94E2D5));
        StyleConstants.setBold(promptStyle, true);

        // Command input style — white
        commandStyle = doc.addStyle("command", null);
        StyleConstants.setForeground(commandStyle, new Color(0xCDD6F4));

        // Output style — light gray
        outputStyle = doc.addStyle("output", null);
        StyleConstants.setForeground(outputStyle, new Color(0xBAC2DE));

        // Error style — red
        errorStyle = doc.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, new Color(0xF38BA8));

        // Success style — green
        successStyle = doc.addStyle("success", null);
        StyleConstants.setForeground(successStyle, new Color(0xA6E3A1));

        // System style — yellow/amber
        systemStyle = doc.addStyle("system", null);
        StyleConstants.setForeground(systemStyle, new Color(0xF9E2AF));
        StyleConstants.setItalic(systemStyle, true);
    }

    /**
     * Show the welcome message and initial prompt.
     */
    private void showWelcome() {
        SwingUtilities.invokeLater(() -> {
            appendText(getAsciiArt(), systemStyle);
            appendText("\n", outputStyle);
            appendText(processor.getShellAdapter().getWelcomeMessage(), systemStyle);
            appendText("\n", outputStyle);
            showPrompt();
        });
    }

    private String getAsciiArt() {
        return """
               ╔═══════════════════════════════════════════════════════╗
               ║     _  _____                   _                     ║
               ║    | ||_   _|__ _ __ _ __ ___ (_)_ __  _   _ ___     ║
               ║ _  | |  | |/ _ \\ '__| '_ ` _ \\| | '_ \\| | | / __|    ║
               ║| |_| |  | |  __/ |  | | | | | | | | | | |_| \\__ \\    ║
               ║ \\___/   |_|\\___|_|  |_| |_| |_|_|_| |_|\\__,_|___/    ║
               ║                                                       ║
               ║           Multi-Shell CLI Terminal v1.0.0              ║
               ╚═══════════════════════════════════════════════════════╝
               """;
    }

    /**
     * Show the prompt and set the input start offset.
     */
    private void showPrompt() {
        String prompt = processor.getPrompt();
        appendText(prompt, promptStyle);
        inputStartOffset = doc.getLength();
        textPane.setCaretPosition(doc.getLength());
        textPane.setCharacterAttributes(commandStyle, true);
    }

    /**
     * Append styled text to the document.
     */
    private void appendText(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scroll to the bottom of the terminal.
     */
    private void scrollToBottom() {
        textPane.setCaretPosition(doc.getLength());
    }

    /**
     * Execute the current command in the input area.
     * Uses SwingWorker to run commands off the EDT, preventing UI freezes.
     */
    private void executeCurrentInput() {
        if (isExecuting) return; // Prevent double execution

        String input;
        try {
            input = doc.getText(inputStartOffset, doc.getLength() - inputStartOffset).trim();
        } catch (BadLocationException e) {
            input = "";
        }

        appendText("\n", outputStyle);

        if (input.isEmpty()) {
            showPrompt();
            return;
        }

        isExecuting = true;
        final String commandInput = input;

        // Run command execution in a background thread
        SwingWorker<CommandResult, Void> worker = new SwingWorker<>() {
            @Override
            protected CommandResult doInBackground() {
                return processor.processInput(commandInput);
            }

            @Override
            protected void done() {
                try {
                    CommandResult result = get();
                    handleCommandResult(result);
                } catch (Exception e) {
                    appendText("Error: " + e.getMessage() + "\n", errorStyle);
                    showPrompt();
                } finally {
                    isExecuting = false;
                }
            }
        };
        worker.execute();
    }

    /**
     * Handle the result of a command execution (always called on EDT).
     */
    private void handleCommandResult(CommandResult result) {
        // Handle special results
        if (result.isExit()) {
            appendText("Goodbye!\n", systemStyle);
            SwingUtilities.invokeLater(() -> {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
                System.exit(0);
            });
            return;
        }

        if (result.isClearScreen()) {
            clearTerminal();
            return;
        }

        // Display output
        String output = result.getOutput();
        if (output != null && !output.isEmpty()) {
            Style style = result.isError() ? errorStyle : outputStyle;
            appendText(output + "\n", style);
        }

        // Show next prompt
        showPrompt();
        scrollToBottom();

        // Notify listeners
        if (onCommandExecuted != null) {
            onCommandExecuted.run();
        }
    }

    /**
     * Clear the terminal and show a fresh prompt.
     */
    /**
     * Clear the terminal (internal, no confirmation).
     */
    private void doClearTerminal() {
        SwingUtilities.invokeLater(() -> {
            try {
                filterEnabled = false;
                doc.remove(0, doc.getLength());
                inputStartOffset = 0;
            } catch (BadLocationException e) {
                e.printStackTrace();
            } finally {
                filterEnabled = true;
            }
            showPrompt();
        });
    }

    /**
     * Clear the terminal without confirmation (used by clear/cls command and Ctrl+L).
     */
    public void clearTerminal() {
        doClearTerminal();
    }

    /**
     * Clear the terminal with a confirmation dialog (used by toolbar/menu).
     */
    public void clearTerminalWithConfirmation() {
        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to clear the terminal?",
                "Clear Terminal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            doClearTerminal();
        }
    }

    /**
     * Switch the shell and display the new welcome message.
     */
    public void switchShell(String shellName) {
        processor.switchShell(shellName);
        appendText("\n", outputStyle);
        appendText("Switched to " + shellName + " shell\n", systemStyle);
        appendText(processor.getShellAdapter().getWelcomeMessage(), systemStyle);
        appendText("\n", outputStyle);
        showPrompt();

        if (onCommandExecuted != null) {
            onCommandExecuted.run();
        }
    }

    /**
     * Set font size for the terminal.
     */
    public void setFontSize(int size) {
        Font current = textPane.getFont();
        textPane.setFont(current.deriveFont((float) size));

        // Update all styles
        StyleConstants.setFontSize(promptStyle, size);
        StyleConstants.setFontSize(commandStyle, size);
        StyleConstants.setFontSize(outputStyle, size);
        StyleConstants.setFontSize(errorStyle, size);
        StyleConstants.setFontSize(successStyle, size);
        StyleConstants.setFontSize(systemStyle, size);
    }

    public void setOnCommandExecuted(Runnable callback) {
        this.onCommandExecuted = callback;
    }

    public CommandProcessor getProcessor() {
        return processor;
    }

    // ==================== Key Listener ====================

    private class TerminalKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();
                executeCurrentInput();
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                e.consume();
                String prev = processor.getHistory().navigateUp();
                if (prev != null) {
                    replaceInput(prev);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                e.consume();
                String next = processor.getHistory().navigateDown();
                replaceInput(next != null ? next : "");
            } else if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
                // Ctrl+C — cancel current input
                e.consume();
                appendText("^C\n", errorStyle);
                showPrompt();
            } else if (e.getKeyCode() == KeyEvent.VK_L && e.isControlDown()) {
                // Ctrl+L — clear screen
                e.consume();
                clearTerminal();
            } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
                e.consume();
                textPane.setCaretPosition(inputStartOffset);
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (textPane.getCaretPosition() <= inputStartOffset) {
                    e.consume();
                }
            }
        }
    }

    /**
     * Replace the current input text with new text.
     */
    private void replaceInput(String newInput) {
        try {
            doc.remove(inputStartOffset, doc.getLength() - inputStartOffset);
            doc.insertString(inputStartOffset, newInput, commandStyle);
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // ==================== Document Filter ====================

    private class TerminalDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (offset >= inputStartOffset) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (!filterEnabled || offset >= inputStartOffset) {
                super.remove(fb, offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (offset >= inputStartOffset) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
