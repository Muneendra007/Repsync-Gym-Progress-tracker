package com.repsync.ui.auth;

import com.repsync.model.User;
import com.repsync.service.AuthService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.exceptions.AuthException;

import javax.swing.*;
import java.awt.*;

/**
 * Change password panel.
 */
public class ChangePasswordPanel extends JPanel {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private StyledButton changeButton;

    private User currentUser;
    private final AuthService authService = new AuthService();

    public ChangePasswordPanel(User user) {
        this.currentUser = user;
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.getBackground());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ThemeManager.getCardBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80, 50), 1, true),
            BorderFactory.createEmptyBorder(35, 50, 35, 50)
        ));
        card.setPreferredSize(new Dimension(400, 400));

        JLabel title = new JLabel("🔒 Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        oldPasswordField = new JPasswordField();
        oldPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        oldPasswordField.setMaximumSize(new Dimension(300, 38));
        oldPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        oldPasswordField.setToolTipText("Enter your current password");
        oldPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120, 80), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setMaximumSize(new Dimension(300, 38));
        newPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPasswordField.setToolTipText("Enter your new password");
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120, 80), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setMaximumSize(new Dimension(300, 38));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField.setToolTipText("Re-enter your new password");
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120, 80), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        changeButton = new StyledButton("Change Password", ThemeManager.ACCENT_ORANGE);
        changeButton.setMaximumSize(new Dimension(300, 40));
        changeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeButton.addActionListener(e -> performChange());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelOld = new JLabel("Current Password:");
        labelOld.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelOld.setForeground(ThemeManager.getSecondaryTextColor());
        labelOld.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelNew = new JLabel("New Password:");
        labelNew.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelNew.setForeground(ThemeManager.getSecondaryTextColor());
        labelNew.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelConfirm = new JLabel("Confirm New Password:");
        labelConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelConfirm.setForeground(ThemeManager.getSecondaryTextColor());
        labelConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(25));
        card.add(labelOld);
        card.add(Box.createVerticalStrut(5));
        card.add(oldPasswordField);
        card.add(Box.createVerticalStrut(15));
        card.add(labelNew);
        card.add(Box.createVerticalStrut(5));
        card.add(newPasswordField);
        card.add(Box.createVerticalStrut(15));
        card.add(labelConfirm);
        card.add(Box.createVerticalStrut(5));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(changeButton);

        add(card);
    }

    private void performChange() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (!newPass.equals(confirmPass)) {
            statusLabel.setForeground(ThemeManager.ACCENT_RED);
            statusLabel.setText("New passwords do not match!");
            return;
        }

        changeButton.setEnabled(false);
        statusLabel.setForeground(ThemeManager.getSecondaryTextColor());
        statusLabel.setText("Changing password...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                authService.changePassword(currentUser.getId(), oldPass, newPass);
                return null;
            }

            @Override
            protected void done() {
                changeButton.setEnabled(true);
                try {
                    get(); // Wait for completion
                    statusLabel.setForeground(ThemeManager.ACCENT_GREEN);
                    statusLabel.setText("Password changed successfully!");
                    oldPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } catch (Exception e) {
                    statusLabel.setForeground(ThemeManager.ACCENT_RED);
                    statusLabel.setText(e.getCause() instanceof AuthException ? e.getCause().getMessage() : "An error occurred.");
                }
            }
        };
        worker.execute();
    }

    private void stylePasswordField(JPasswordField field) {
        if (field != null) {
            field.setBackground(ThemeManager.getInputBackground());
            field.setForeground(ThemeManager.getInputTextColor());
            field.setCaretColor(ThemeManager.ACCENT_ORANGE);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        stylePasswordField(oldPasswordField);
        stylePasswordField(newPasswordField);
        stylePasswordField(confirmPasswordField);
        revalidate();
        repaint();
    }
}
