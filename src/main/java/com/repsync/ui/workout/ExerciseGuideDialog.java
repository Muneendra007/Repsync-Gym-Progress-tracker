package com.repsync.ui.workout;

import com.repsync.model.Exercise;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.ExerciseIllustrationPanel;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.ExerciseGuideCatalog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Interactive Biomechanical Exercise & Machine Guide Modal.
 * Displays a high-tech 2D illustration of a person performing the exercise on the machine,
 * along with anatomical muscle head explanations and step-by-step form instructions.
 */
public class ExerciseGuideDialog extends JDialog {

    public ExerciseGuideDialog(Frame owner, Exercise exercise) {
        super(owner, "Biomechanical Exercise Guide & Machine Setup", true);
        setSize(860, 600);
        setLocationRelativeTo(owner);
        setResizable(true);

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(ThemeManager.getBackground());
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- TOP HEADER: EXERCISE NAME & ANATOMICAL BADGE ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(exercise.getName());
        titleLabel.setFont(ThemeManager.FONT_HEADER);
        titleLabel.setForeground(ThemeManager.getTextColor());

        String region = exercise.getTargetRegion();
        JLabel regionBadge = new JLabel("  ANATOMICAL TARGET: " + region.toUpperCase() + "  ");
        regionBadge.setFont(ThemeManager.FONT_BOLD.deriveFont(12f));
        regionBadge.setForeground(ThemeManager.ACCENT_CYAN);
        regionBadge.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_CYAN, 30));
        regionBadge.setOpaque(true);
        regionBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(regionBadge, BorderLayout.CENTER);
        content.add(headerPanel, BorderLayout.NORTH);

        // --- CENTER SPLIT: ILLUSTRATION (LEFT) & FORM GUIDE (RIGHT) ---
        JPanel centerSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        centerSplit.setOpaque(false);

        // Left: Picture (Biomechanical Illustration Panel) + Equipment Machine Setup Box
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);

        ExerciseIllustrationPanel illusPanel = new ExerciseIllustrationPanel(
            exercise.getIllustrationType(),
            exercise.getName(),
            exercise.getTargetRegion(),
            exercise.getMachineSetup()
        );

        String textHex = ThemeManager.getHtmlTextColorHex();

        // Equipment Card
        JPanel equipCard = createInfoCard("EQUIPMENT & MACHINE SETUP GUIDE",
            "<html><div style='line-height:1.4; color:" + textHex + ";'>" +
            "<b>Equipment Required:</b> " + exercise.getMachineSetup() + "<br><br>" +
            "<b>Setup Tip:</b> Adjust seat height and bench angle so the resistance aligns directly with " +
            "the " + exercise.getTargetRegion() + " muscle fibers. Keep safety pins engaged.</div></html>",
            ThemeManager.PRIMARY_BLUE);

        leftPanel.add(illusPanel, BorderLayout.CENTER);
        leftPanel.add(equipCard, BorderLayout.SOUTH);

        // Right: Step-by-step biomechanical execution instructions
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);

        JPanel anatCard = createInfoCard("BIOMECHANICAL & ANATOMICAL SCIENCE",
            "<html><div style='line-height:1.4; color:" + textHex + ";'>" +
            "<b>Target Muscle Head:</b> " + exercise.getTargetRegion() + "<br>" +
            "<b>Why This Works:</b> By controlling the trajectory and body angle on this equipment, " +
            "you maximize motor-unit recruitment in the target muscle head while minimizing joint strain.</div></html>",
            ThemeManager.ACCENT_COLOR);

        String formText = exercise.getFormGuide();
        String formattedForm = "<html><div style='line-height:1.5; color:" + textHex + "; font-size:12px;'>" +
            formText.replace("\n", "<br><br>") +
            "</div></html>";

        JPanel formCard = createInfoCard("STEP-BY-STEP EXECUTION GUIDE",
            formattedForm,
            ThemeManager.SECONDARY_GREEN);

        rightPanel.add(anatCard, BorderLayout.NORTH);
        rightPanel.add(formCard, BorderLayout.CENTER);

        centerSplit.add(leftPanel);
        centerSplit.add(rightPanel);
        content.add(centerSplit, BorderLayout.CENTER);

        // --- BOTTOM BAR: CLOSE BUTTON ---
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);

        StyledButton closeBtn = new StyledButton("✔ Got It, Close Guide", ThemeManager.PRIMARY_BLUE);
        closeBtn.addActionListener(e -> dispose());
        bottomBar.add(closeBtn);

        content.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private JPanel createInfoCard(String title, String htmlContent, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // Top accent line
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, 4, 14, 14);
                // Border
                g2.setColor(ThemeManager.getDividerColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 15, 14, 15));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeManager.FONT_BOLD.deriveFont(12f));
        titleLbl.setForeground(accentColor);

        JLabel contentLbl = new JLabel(htmlContent);
        contentLbl.setFont(ThemeManager.FONT_REGULAR);
        contentLbl.setForeground(ThemeManager.getTextColor());

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(contentLbl, BorderLayout.CENTER);

        return card;
    }
}
