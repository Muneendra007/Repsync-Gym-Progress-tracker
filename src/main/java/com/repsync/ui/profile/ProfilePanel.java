package com.repsync.ui.profile;

import com.repsync.model.User;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.service.UserService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.InputValidator;

import javax.swing.*;
import java.awt.*;

/**
 * User profile panel - view and edit profile info, fitness goal, and body stats.
 * Features a visual BMI gauge and goal recommendation based on BMI.
 */
public class ProfilePanel extends JPanel {

    private User currentUser;
    private final UserService userService = new UserService();
    private JLabel statusLabel;

    // Editable fields
    private JTextField emailField;
    private JTextField ageField;
    private JComboBox<String> genderCombo;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<FitnessGoal> goalCombo;
    private StyledButton saveButton;

    public ProfilePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackground());
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
    }

    private void buildUI() {
        // Section header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("👤  My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel("Update your profile, set your fitness goal, and view your BMI assessment");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        headerPanel.add(Box.createVerticalStrut(15));

        // Content: Profile Form (left) + BMI Gauge (right)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // ─── LEFT: Profile Form Card ───
        JPanel formCard = createStyledCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("📋  Profile Information");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(ThemeManager.getTextColor());
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(15));

        // Username (read-only)
        formCard.add(createFieldRow("Username:", createReadOnlyField(currentUser.getUsername())));
        formCard.add(Box.createVerticalStrut(10));

        // Email
        emailField = new JTextField(currentUser.getEmail(), 20);
        styleInputField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(createFieldRow("Email:", emailField));
        formCard.add(Box.createVerticalStrut(10));

        // Age
        ageField = new JTextField(String.valueOf(currentUser.getAge()), 8);
        styleInputField(ageField);
        ageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(createFieldRow("Age:", ageField));
        formCard.add(Box.createVerticalStrut(10));

        // Gender
        genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        styleInputField(genderCombo);
        if (currentUser.getGender() != null) {
            genderCombo.setSelectedItem(currentUser.getGender());
        }
        formCard.add(createFieldRow("Gender:", genderCombo));
        formCard.add(Box.createVerticalStrut(10));

        // Height
        heightField = new JTextField(String.valueOf(currentUser.getHeightCm()), 8);
        styleInputField(heightField);
        heightField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(createFieldRow("Height (cm):", heightField));
        formCard.add(Box.createVerticalStrut(10));

        // Weight
        weightField = new JTextField(String.valueOf(currentUser.getWeightKg()), 8);
        styleInputField(weightField);
        weightField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(createFieldRow("Weight (kg):", weightField));
        formCard.add(Box.createVerticalStrut(10));

        // Fitness Goal
        goalCombo = new JComboBox<>(FitnessGoal.values());
        styleInputField(goalCombo);
        if (currentUser.getFitnessGoal() != null) {
            goalCombo.setSelectedItem(currentUser.getFitnessGoal());
        }
        formCard.add(createFieldRow("Fitness Goal:", goalCombo));
        formCard.add(Box.createVerticalStrut(15));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(8));

        // Save button
        saveButton = new StyledButton("✔ Save Profile", ThemeManager.ACCENT_GREEN);
        saveButton.setMaximumSize(new Dimension(200, 40));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> saveProfile());
        formCard.add(saveButton);

        // ─── RIGHT: BMI Gauge Card ───
        JPanel bmiCard = createStyledCard();
        bmiCard.setLayout(new BoxLayout(bmiCard, BoxLayout.Y_AXIS));

        JLabel bmiTitle = new JLabel("📊  BMI Assessment");
        bmiTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bmiTitle.setForeground(ThemeManager.getTextColor());
        bmiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        bmiCard.add(bmiTitle);
        bmiCard.add(Box.createVerticalStrut(15));

        // BMI Gauge visual
        double bmi = calculateBMI(currentUser.getHeightCm(), currentUser.getWeightKg());
        String bmiCategory = getBMICategory(bmi);
        Color bmiColor = getBMIColor(bmi);
        String goalRecommendation = getGoalRecommendation(bmi);

        // BMI value display
        JLabel bmiValueLabel = new JLabel(bmi > 0 ? String.format("%.1f", bmi) : "N/A");
        bmiValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        bmiValueLabel.setForeground(bmiColor);
        bmiValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bmiCatLabel = new JLabel(bmiCategory);
        bmiCatLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bmiCatLabel.setForeground(bmiColor);
        bmiCatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bmiCard.add(bmiValueLabel);
        bmiCard.add(bmiCatLabel);
        bmiCard.add(Box.createVerticalStrut(15));

        // Visual BMI bar
        JPanel bmiBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int barH = 18;
                int barY = 20;

                // Draw the gradient bar: Underweight -> Normal -> Overweight -> Obese
                int segW = w / 4;
                Color[] colors = {ThemeManager.ACCENT_CYAN, ThemeManager.ACCENT_GREEN, ThemeManager.ACCENT_ORANGE, ThemeManager.ACCENT_RED};
                for (int i = 0; i < 4; i++) {
                    g2.setColor(colors[i]);
                    if (i == 0) {
                        g2.fillRoundRect(i * segW, barY, segW, barH, 8, 8);
                    } else if (i == 3) {
                        g2.fillRoundRect(i * segW, barY, segW, barH, 8, 8);
                    } else {
                        g2.fillRect(i * segW, barY, segW, barH);
                    }
                }

                // Labels
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String[] labels = {"<18.5", "18.5-24.9", "25-29.9", "30+"};
                String[] names = {"Underweight", "Normal", "Overweight", "Obese"};
                for (int i = 0; i < 4; i++) {
                    g2.setColor(ThemeManager.getSecondaryTextColor());
                    int labelX = i * segW + segW / 2 - g2.getFontMetrics().stringWidth(names[i]) / 2;
                    g2.drawString(names[i], labelX, barY - 4);
                    g2.drawString(labels[i], i * segW + segW / 2 - g2.getFontMetrics().stringWidth(labels[i]) / 2, barY + barH + 14);
                }

                // Pointer for current BMI
                if (bmi > 0 && bmi < 50) {
                    double normalized = Math.min(Math.max((bmi - 14) / (40 - 14), 0), 1.0);
                    int pointerX = (int) (normalized * w);
                    g2.setColor(ThemeManager.getTextColor());

                    // Draw triangle pointer
                    int[] xPoints = {pointerX - 6, pointerX + 6, pointerX};
                    int[] yPoints = {barY + barH + 18, barY + barH + 18, barY + barH + 2};
                    g2.fillPolygon(xPoints, yPoints, 3);
                }

                g2.dispose();
            }
        };
        bmiBar.setOpaque(false);
        bmiBar.setPreferredSize(new Dimension(0, 60));
        bmiBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        bmiBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        bmiCard.add(bmiBar);
        bmiCard.add(Box.createVerticalStrut(20));

        // Goal recommendation
        JLabel recTitle = new JLabel("🎯  Recommended Goal");
        recTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        recTitle.setForeground(ThemeManager.getTextColor());
        recTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recText = new JLabel("<html><p style='width:250px'>" + goalRecommendation + "</p></html>");
        recText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recText.setForeground(ThemeManager.getSecondaryTextColor());
        recText.setAlignmentX(Component.LEFT_ALIGNMENT);

        bmiCard.add(recTitle);
        bmiCard.add(Box.createVerticalStrut(6));
        bmiCard.add(recText);

        contentPanel.add(formCard);
        contentPanel.add(bmiCard);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Create a styled card panel.
     */
    private JPanel createStyledCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(60, 60, 80, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return card;
    }

    /**
     * Create a field row with label + input side by side.
     */
    private JPanel createFieldRow(String labelText, Component field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ThemeManager.getSecondaryTextColor());
        label.setPreferredSize(new Dimension(120, 30));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    /**
     * Create a read-only display field.
     */
    private JLabel createReadOnlyField(String text) {
        JLabel field = new JLabel(text);
        field.setFont(new Font("Segoe UI", Font.BOLD, 14));
        field.setForeground(ThemeManager.getTextColor());
        return field;
    }

    /**
     * Save the updated profile.
     */
    private void saveProfile() {
        currentUser.setEmail(emailField.getText());
        currentUser.setAge(InputValidator.parseIntSafe(ageField.getText()));
        currentUser.setGender((String) genderCombo.getSelectedItem());
        currentUser.setHeightCm(InputValidator.parseDoubleSafe(heightField.getText()));
        currentUser.setWeightKg(InputValidator.parseDoubleSafe(weightField.getText()));
        currentUser.setFitnessGoal((FitnessGoal) goalCombo.getSelectedItem());

        saveButton.setEnabled(false);
        statusLabel.setForeground(ThemeManager.getSecondaryTextColor());
        statusLabel.setText("Saving...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userService.updateProfile(currentUser);
            }

            @Override
            protected void done() {
                saveButton.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        statusLabel.setForeground(ThemeManager.ACCENT_GREEN);
                        statusLabel.setText("✅ Profile saved successfully!");
                        // Rebuild to refresh BMI gauge
                        removeAll();
                        buildUI();
                        revalidate();
                        repaint();
                    } else {
                        statusLabel.setForeground(ThemeManager.ACCENT_RED);
                        statusLabel.setText("❌ Failed to save profile.");
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(ThemeManager.ACCENT_RED);
                    statusLabel.setText("❌ Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Calculate BMI from height (cm) and weight (kg).
     */
    private double calculateBMI(double heightCm, double weightKg) {
        if (heightCm <= 0 || weightKg <= 0) return 0;
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Get the BMI category string.
     */
    private String getBMICategory(double bmi) {
        if (bmi <= 0) return "Enter your height and weight";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal Weight";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    /**
     * Get the color for a BMI value.
     */
    private Color getBMIColor(double bmi) {
        if (bmi <= 0) return ThemeManager.getSecondaryTextColor();
        if (bmi < 18.5) return ThemeManager.ACCENT_CYAN;
        if (bmi < 25) return ThemeManager.ACCENT_GREEN;
        if (bmi < 30) return ThemeManager.ACCENT_ORANGE;
        return ThemeManager.ACCENT_RED;
    }

    /**
     * Get a goal recommendation based on BMI.
     */
    private String getGoalRecommendation(double bmi) {
        if (bmi <= 0) return "Update your height and weight to get a personalized recommendation.";
        if (bmi < 18.5) return "Based on your BMI, we recommend focusing on <b>Muscle Gain</b> with a caloric surplus to build lean mass and reach a healthy weight.";
        if (bmi < 25) return "Great! Your BMI is in the healthy range. You can focus on <b>Build Strength</b> or <b>Muscle Gain</b> to optimize your physique.";
        if (bmi < 30) return "Consider starting with <b>Fat Loss</b> to reduce body fat, combined with strength training to maintain muscle mass.";
        return "We recommend prioritizing <b>Fat Loss</b> with a combination of circuit training and cardio. Consult a healthcare provider for personalized advice.";
    }

    private void styleInputField(JComponent field) {
        field.setBackground(ThemeManager.getInputBackground());
        field.setForeground(ThemeManager.getInputTextColor());
        field.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (field instanceof JTextField tf) {
            tf.setCaretColor(ThemeManager.ACCENT_BLUE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
        }
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
