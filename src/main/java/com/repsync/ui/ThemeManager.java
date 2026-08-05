package com.repsync.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Manages the application theme (Dark/Light mode).
 * Persists the user's preference between sessions.
 */
public class ThemeManager {

    private static boolean isDarkMode = true;  // Default to dark mode
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    // --- Color Constants ---
    // Dark theme colors (Obsidian Cyber)
    public static final Color DARK_BG = new Color(12, 14, 24);
    public static final Color DARK_CARD = new Color(22, 26, 42);
    public static final Color DARK_SIDEBAR = new Color(16, 18, 30);

    // Light theme colors (Clean Pearl Slate)
    public static final Color LIGHT_BG = new Color(241, 245, 249);
    public static final Color LIGHT_CARD = new Color(255, 255, 255);
    public static final Color LIGHT_SIDEBAR = new Color(255, 255, 255);

    // Accent colors (High Contrast Vibrancy)
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color ACCENT_GREEN = new Color(34, 197, 94);
    public static final Color ACCENT_RED = new Color(239, 68, 68);
    public static final Color ACCENT_ORANGE = new Color(249, 115, 22);
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    public static final Color ACCENT_CYAN = new Color(6, 182, 212);
    public static final Color ACCENT_PINK = new Color(236, 72, 153);

    // Text colors (Crisp High Contrast for Both Modes)
    public static final Color DARK_TEXT = new Color(248, 250, 252);
    public static final Color DARK_TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color LIGHT_TEXT = new Color(15, 23, 42); // Deep Obsidian Navy
    public static final Color LIGHT_TEXT_SECONDARY = new Color(30, 41, 59); // Dark Slate (No faint gray)

    // Gradient colors
    public static final Color DARK_GRADIENT_START = new Color(22, 26, 44);
    public static final Color DARK_GRADIENT_END = new Color(12, 14, 24);
    public static final Color LIGHT_GRADIENT_START = new Color(255, 255, 255);
    public static final Color LIGHT_GRADIENT_END = new Color(241, 245, 249);

    // Table alternate row colors
    public static final Color DARK_TABLE_ALT = new Color(28, 33, 52);
    public static final Color LIGHT_TABLE_ALT = new Color(248, 250, 252);

    // Divider colors
    public static final Color DARK_DIVIDER = new Color(51, 65, 85);
    public static final Color LIGHT_DIVIDER = new Color(203, 213, 225);

    // Card hover colors
    public static final Color DARK_CARD_HOVER = new Color(30, 36, 58);
    public static final Color LIGHT_CARD_HOVER = new Color(241, 245, 249);

    // Input & Table Header Specific Colors for High Contrast
    public static final Color DARK_INPUT_BG = new Color(30, 36, 56);
    public static final Color LIGHT_INPUT_BG = new Color(255, 255, 255);
    public static final Color DARK_HEADER_BG = new Color(30, 41, 59);
    public static final Color LIGHT_HEADER_BG = new Color(226, 232, 240);
    public static final Color DARK_HEADER_TEXT = new Color(56, 189, 248);
    public static final Color LIGHT_HEADER_TEXT = new Color(3, 105, 161);

    // Convenience Color & Font Aliases for UI Components
    public static final Color PRIMARY_BLUE = ACCENT_BLUE;
    public static final Color SECONDARY_GREEN = ACCENT_GREEN;
    public static final Color ACCENT_COLOR = ACCENT_BLUE;
    public static final Color BG_DARK = DARK_BG;
    public static final Color BG_CARD = DARK_CARD;
    public static final Color BORDER_COLOR = DARK_DIVIDER;
    public static final Color TEXT_WHITE = new Color(248, 250, 252);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Initialize the theme on application startup.
     * Loads saved preference.
     */
    public static void initialize() {
        isDarkMode = prefs.getBoolean("darkMode", true);
        applyTheme();
    }

    /**
     * Toggle between dark and light mode.
     */
    public static void toggleTheme(JFrame frame) {
        isDarkMode = !isDarkMode;
        prefs.putBoolean("darkMode", isDarkMode);
        applyTheme();

        // Update all visible components
        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    /**
     * Apply the current theme using FlatLaf.
     */
    private static void applyTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set theme: " + e.getMessage());
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static Color getBackground() {
        return isDarkMode ? DARK_BG : LIGHT_BG;
    }

    public static Color getCardBackground() {
        return isDarkMode ? DARK_CARD : LIGHT_CARD;
    }

    public static Color getSidebarBackground() {
        return isDarkMode ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }

    public static Color getTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color getSecondaryTextColor() {
        return isDarkMode ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static Color getInputBackground() {
        return isDarkMode ? DARK_INPUT_BG : LIGHT_INPUT_BG;
    }

    public static Color getInputTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color getTableHeaderBackground() {
        return isDarkMode ? DARK_HEADER_BG : LIGHT_HEADER_BG;
    }

    public static Color getTableHeaderTextColor() {
        return isDarkMode ? DARK_HEADER_TEXT : LIGHT_HEADER_TEXT;
    }

    public static Color getGradientStart() {
        return isDarkMode ? DARK_GRADIENT_START : LIGHT_GRADIENT_START;
    }

    public static Color getGradientEnd() {
        return isDarkMode ? DARK_GRADIENT_END : LIGHT_GRADIENT_END;
    }

    public static Color getTableAlternateRow() {
        return isDarkMode ? DARK_TABLE_ALT : LIGHT_TABLE_ALT;
    }

    public static Color getDividerColor() {
        return isDarkMode ? DARK_DIVIDER : LIGHT_DIVIDER;
    }

    public static Color getBorderColor() {
        return getDividerColor();
    }

    public static Color getCardHoverBackground() {
        return isDarkMode ? DARK_CARD_HOVER : LIGHT_CARD_HOVER;
    }

    /**
     * Helper to get HTML hex color string for primary text.
     */
    public static String getHtmlTextColorHex() {
        Color c = getTextColor();
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * Helper to get HTML hex color string for secondary text.
     */
    public static String getHtmlSecondaryTextColorHex() {
        Color c = getSecondaryTextColor();
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * Get a semi-transparent version of an accent color for glow effects.
     */
    public static Color getAccentGlow(Color accent) {
        return new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40);
    }

    /**
     * Get a color with modified alpha for overlays and effects.
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(255, Math.max(0, alpha)));
    }
}

