package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard stat card component with premium styling.
 * Features a colored accent bar at the top, subtle gradient, and modern typography.
 */
public class StatCard extends JPanel {

    private JLabel valueLabel;
    private JLabel titleLabel;
    private JLabel iconLabel;
    private Color accentColor;

    /**
     * Create a stat card.
     * 
     * @param icon emoji/unicode icon
     * @param title stat name (e.g., "Total Workouts")
     * @param value stat value (e.g., "42")
     * @param accentColor color for the accent bar and icon
     */
    public StatCard(String icon, String title, String value, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(200, 130));

        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(ThemeManager.getSecondaryTextColor());

        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(ThemeManager.getTextColor());

        // Layout
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        textPanel.add(titleRow);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLabel);

        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Card background with rounded corners
        g2.setColor(ThemeManager.getCardBackground());
        g2.fillRoundRect(0, 0, w, h, 16, 16);

        // Subtle card border
        g2.setColor(ThemeManager.getDividerColor());
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        // Top accent gradient bar (4px)
        GradientPaint accentGrad = new GradientPaint(
            0, 0, accentColor,
            w, 0, accentColor.brighter()
        );
        g2.setPaint(accentGrad);
        // Clip to rounded top
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, 16, 16, 16));
        g2.fillRect(0, 0, w, 5);
        g2.setClip(null);

        // Subtle accent glow at top
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 15));
        g2.fillRoundRect(0, 0, w, 40, 16, 16);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Update the displayed value.
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /**
     * Update the card background for theming.
     */
    public void refreshTheme() {
        valueLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setForeground(ThemeManager.getSecondaryTextColor());
        repaint();
    }
}
