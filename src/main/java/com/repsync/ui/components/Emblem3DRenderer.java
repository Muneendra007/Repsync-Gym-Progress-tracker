package com.repsync.ui.components;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Renders a high-tech 3D Gym Barbell & Shield Emblem using pure Java2D vectors.
 * Ensures the logo looks stunning and 3D on any screen without missing emoji font boxes.
 */
public class Emblem3DRenderer {

    public static void draw3DLogo(Graphics2D g2, int x, int y, int size) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = x + size / 2;
        int cy = y + size / 2;

        // 1. Ambient Outer 3D Glow Ring
        RadialGradientPaint outerGlow = new RadialGradientPaint(
            cx, cy, size / 2f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(59, 130, 246, 120),
                new Color(139, 92, 246, 40),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(outerGlow);
        g.fill(new Ellipse2D.Float(x, y, size, size));

        // 2. 3D Outer Metallic Ring
        g.setStroke(new BasicStroke(2.5f));
        GradientPaint ringGrad = new GradientPaint(
            x, y, new Color(59, 130, 246),
            x + size, y + size, new Color(139, 92, 246)
        );
        g.setPaint(ringGrad);
        g.draw(new Ellipse2D.Float(x + 4, y + 4, size - 8, size - 8));

        // 3. Central Barbell Handle (Chrome Cylinder with specular highlight)
        int barH = Math.max(4, size / 10);
        int barW = (int) (size * 0.75);
        int barX = cx - barW / 2;
        int barY = cy - barH / 2;

        // Barbell shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fill(new RoundRectangle2D.Float(barX + 2, barY + 3, barW, barH, 4, 4));

        // Barbell chrome body
        GradientPaint chromeGrad = new GradientPaint(
            barX, barY, new Color(200, 210, 230),
            barX, barY + barH, new Color(100, 115, 140)
        );
        g.setPaint(chromeGrad);
        g.fill(new RoundRectangle2D.Float(barX, barY, barW, barH, 4, 4));

        // Chrome specular shine
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(barX, barY + 1, barW, Math.max(1, barH / 3));

        // 4. 3D Dumbbell Plates (Left and Right)
        int plateW = Math.max(6, size / 8);
        int plateH = (int) (size * 0.55);

        // Left Plate
        drawPlate3D(g, barX + 6, cy - plateH / 2, plateW, plateH);
        drawPlate3D(g, barX, cy - (int)(plateH * 0.8) / 2, plateW, (int)(plateH * 0.8));

        // Right Plate
        drawPlate3D(g, barX + barW - 6 - plateW, cy - plateH / 2, plateW, plateH);
        drawPlate3D(g, barX + barW - plateW, cy - (int)(plateH * 0.8) / 2, plateW, (int)(plateH * 0.8));

        // 5. Center Neon Energy Gem
        int gemSize = Math.max(8, size / 5);
        g.setColor(new Color(6, 182, 212, 180));
        g.fill(new Ellipse2D.Float(cx - gemSize / 2f, cy - gemSize / 2f, gemSize, gemSize));

        g.setColor(new Color(255, 255, 255, 240));
        g.fill(new Ellipse2D.Float(cx - gemSize / 4f, cy - gemSize / 4f, gemSize / 2f, gemSize / 2f));

        g.dispose();
    }

    private static void drawPlate3D(Graphics2D g, int x, int y, int w, int h) {
        // Plate drop shadow
        g.setColor(new Color(0, 0, 0, 120));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 2, w, h, 6, 6));

        // Plate body gradient
        GradientPaint pGrad = new GradientPaint(
            x, y, new Color(50, 55, 75),
            x + w, y + h, new Color(20, 24, 38)
        );
        g.setPaint(pGrad);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 6, 6));

        // Plate metallic rim
        GradientPaint rimGrad = new GradientPaint(
            x, y, new Color(59, 130, 246, 200),
            x, y + h, new Color(139, 92, 246, 200)
        );
        g.setPaint(rimGrad);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new RoundRectangle2D.Float(x, y, w - 1, h - 1, 6, 6));
    }
}
