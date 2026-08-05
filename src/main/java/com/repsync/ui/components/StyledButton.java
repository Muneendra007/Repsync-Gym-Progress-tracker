package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom styled button with rounded corners, hover effects, and subtle shadow.
 * Provides a modern, premium look.
 */
public class StyledButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    /**
     * Create a styled button with default accent color.
     */
    public StyledButton(String text) {
        this(text, ThemeManager.ACCENT_BLUE);
    }

    /**
     * Create a styled button with a custom color.
     */
    public StyledButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.brighter();
        this.pressColor = color.darker();

        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 40));

        // Add hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 3D Hover lift offset
        int yOffset = (isHovered && isEnabled() && !isPressed) ? -2 : (isPressed ? 1 : 0);

        // State colors
        Color baseColor = isEnabled() ? normalColor : new Color(normalColor.getRed(), normalColor.getGreen(), normalColor.getBlue(), 90);
        Color topGradColor = isHovered && isEnabled() ? baseColor.brighter() : baseColor;
        Color bottomGradColor = isHovered && isEnabled() ? baseColor : baseColor.darker();

        // 1. Ambient 3D Drop Shadow on hover
        if (isEnabled()) {
            Color glowColor = isHovered ? new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120) 
                                        : new Color(0, 0, 0, 70);
            g2.setColor(glowColor);
            g2.fillRoundRect(2, 6 + yOffset, w - 4, h - 6, 16, 16);
        }

        // 2. 3D Body Gradient Fill
        GradientPaint bodyGrad = new GradientPaint(0, yOffset, topGradColor, 0, yOffset + h - 4, bottomGradColor);
        g2.setPaint(bodyGrad);
        g2.fillRoundRect(0, yOffset, w, h - 4, 14, 14);

        // 3. Top Specular 3D Gloss Highlight Line
        if (isEnabled() && !isPressed) {
            GradientPaint gloss = new GradientPaint(
                0, yOffset, new Color(255, 255, 255, 90),
                0, yOffset + (h / 2), new Color(255, 255, 255, 10)
            );
            g2.setPaint(gloss);
            g2.fillRoundRect(1, yOffset + 1, w - 2, (h - 4) / 2, 12, 12);
        }

        // 4. Subtle 3D Inset Border
        g2.setColor(isHovered ? new Color(255, 255, 255, 140) : new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, yOffset, w - 1, h - 5, 14, 14);

        // 5. Draw Centered Text with 3D Drop Shadow
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (w - fm.stringWidth(getText())) / 2;
        int textY = ((h - 4) + fm.getAscent() - fm.getDescent()) / 2 + yOffset;

        // Text 3D shadow
        if (isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 110));
            g2.drawString(getText(), textX, textY + 1);
        }

        g2.setColor(isEnabled() ? getForeground() : new Color(255, 255, 255, 130));
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    /**
     * Set the button color.
     */
    public void setButtonColor(Color color) {
        this.normalColor = color;
        this.hoverColor = color.brighter();
        this.pressColor = color.darker();
        repaint();
    }
}
