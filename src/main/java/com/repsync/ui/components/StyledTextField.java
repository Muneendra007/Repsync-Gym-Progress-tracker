package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Custom styled text field with placeholder text, focus highlight border, and modern styling.
 */
public class StyledTextField extends JTextField {

    private String placeholder;
    private boolean showingPlaceholder = true;
    private boolean isFocused = false;

    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(250, 38));
        setCaretColor(ThemeManager.ACCENT_BLUE);

        // Placeholder behavior
        setText(placeholder);
        setForeground(ThemeManager.getSecondaryTextColor());

        // Custom focus-glow border
        setBorder(BorderFactory.createCompoundBorder(
            new FocusGlowBorder(),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                if (showingPlaceholder) {
                    setText("");
                    setForeground(ThemeManager.getTextColor());
                    showingPlaceholder = false;
                }
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(ThemeManager.getSecondaryTextColor());
                    showingPlaceholder = true;
                }
                repaint();
            }
        });
    }

    /**
     * Get the actual text (returns empty if showing placeholder).
     */
    public String getActualText() {
        if (showingPlaceholder) {
            return "";
        }
        return getText();
    }

    /**
     * Clear the field and show placeholder.
     */
    public void clearField() {
        setText(placeholder);
        setForeground(ThemeManager.getSecondaryTextColor());
        showingPlaceholder = true;
    }

    /**
     * Refresh theme colors for text field.
     */
    public void refreshTheme() {
        setBackground(ThemeManager.getInputBackground());
        if (showingPlaceholder) {
            setForeground(ThemeManager.getSecondaryTextColor());
        } else {
            setForeground(ThemeManager.getInputTextColor());
        }
        repaint();
    }

    /**
     * Custom border that renders a subtle glow when the field is focused.
     */
    private class FocusGlowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isFocused) {
                // Outer glow
                g2.setColor(ThemeManager.getAccentGlow(ThemeManager.ACCENT_BLUE));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 8, 8);

                // Inner border
                g2.setColor(ThemeManager.ACCENT_BLUE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 8, 8);
            } else {
                // Default border
                g2.setColor(ThemeManager.getDividerColor());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 8, 8);
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(3, 3, 3, 3);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = 3;
            return insets;
        }
    }
}
