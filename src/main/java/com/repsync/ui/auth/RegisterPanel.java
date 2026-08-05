package com.repsync.ui.auth;

import com.repsync.model.User;
import com.repsync.service.AuthService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.Emblem3DRenderer;
import com.repsync.ui.components.StyledButton;
import com.repsync.ui.components.StyledTextField;
import com.repsync.util.exceptions.AuthException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * High-End 3D Glassmorphism Registration Panel for RepSync.
 * Features 3D dynamic lighting background, vector 3D barbell emblem, and glass card.
 */
public class RegisterPanel extends JPanel {

    private StyledTextField usernameField;
    private StyledTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private StyledButton registerButton;

    private final AuthService authService = new AuthService();

    private java.util.function.Consumer<User> onRegisterSuccess;
    private Runnable onShowLogin;

    public RegisterPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(12, 14, 24)); // Deep obsidian 3D background

        // --- FLOATING 3D GLASS CARD ---
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // 1. Multi-Layer 3D Ambient Drop Shadow
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(8, 12, w - 16, h - 12, 24, 24);
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(4, 6, w - 8, h - 8, 24, 24);

                // 2. Frosted Glass Body
                g2.setColor(new Color(22, 26, 44, 230));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 24, 24);

                // 3. Top Specular Glass Reflection
                GradientPaint glassGloss = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 30),
                    0, h / 3, new Color(255, 255, 255, 2)
                );
                g2.setPaint(glassGloss);
                g2.fillRoundRect(0, 0, w - 1, h / 3, 24, 24);

                // 4. 3D Neon Dual-Gradient Header Accent Bar (6px)
                GradientPaint neonGrad = new GradientPaint(
                    0, 0, ThemeManager.ACCENT_GREEN,
                    w, 0, ThemeManager.ACCENT_CYAN
                );
                g2.setPaint(neonGrad);
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, 24, 24, 24));
                g2.fillRect(0, 0, w, 6);
                g2.setClip(null);

                // 5. Metallic Glass Border
                g2.setColor(new Color(255, 255, 255, 45));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 24, 24);

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 45, 30, 45));
        card.setPreferredSize(new Dimension(440, 640));

        // --- 3D LOGO EMBLEM HEADER ---
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Emblem3DRenderer.draw3DLogo(g2, (getWidth() - 56) / 2, 0, 56);
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(350, 60));
        logoPanel.setMaximumSize(new Dimension(350, 60));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ThemeManager.getTextColor());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("  🚀 JOIN REPSYNC FITNESS PORTAL  ");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tagline.setForeground(ThemeManager.ACCENT_GREEN);
        tagline.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_GREEN, 30));
        tagline.setOpaque(true);
        tagline.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.withAlpha(ThemeManager.ACCENT_GREEN, 100), 1, true),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- INPUT FIELDS ---
        usernameField = new StyledTextField("Choose a username");
        usernameField.setMaximumSize(new Dimension(350, 38));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new StyledTextField("you@example.com");
        emailField.setMaximumSize(new Dimension(350, 38));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();

        // 3D Register Button
        registerButton = new StyledButton("Create RepSync Account", ThemeManager.ACCENT_GREEN);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setMaximumSize(new Dimension(350, 44));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> performRegister());

        // Login Link
        JLabel loginLink = new JLabel("Already have an account? Sign In");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setForeground(ThemeManager.ACCENT_CYAN);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onShowLogin != null) onShowLogin.run();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Already have an account? Sign In</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Already have an account? Sign In");
            }
        });

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeManager.ACCENT_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- ASSEMBLE CARD ---
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(4));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(tagline);
        card.add(Box.createVerticalStrut(18));

        card.add(createFieldLabel("👤  USERNAME"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));

        card.add(createFieldLabel("✉️  EMAIL ADDRESS"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));

        card.add(createFieldLabel("🔑  PASSWORD"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        card.add(createFieldLabel("🔒  CONFIRM PASSWORD"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(8));

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(14));
        card.add(loginLink);

        add(card);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Base Dark Cyber Gradient Background
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(10, 12, 22), 0, h, new Color(18, 22, 38));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // 2. Dynamic 3D Radial Glow Orbs
        RadialGradientPaint orbGreen = new RadialGradientPaint(
            w * 0.75f, h * 0.25f, 350f,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(34, 197, 94, 50),
                new Color(10, 80, 40, 15),
                new Color(0, 0, 0, 0)
            }
        );
        g2.setPaint(orbGreen);
        g2.fillRect(0, 0, w, h);

        RadialGradientPaint orbCyan = new RadialGradientPaint(
            w * 0.25f, h * 0.75f, 300f,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(6, 182, 212, 50),
                new Color(10, 70, 90, 15),
                new Color(0, 0, 0, 0)
            }
        );
        g2.setPaint(orbCyan);
        g2.fillRect(0, 0, w, h);

        // 3. Subtle 3D Grid Mesh
        g2.setColor(new Color(255, 255, 255, 6));
        g2.setStroke(new BasicStroke(1f));
        int gridSize = 45;
        for (int x = 0; x < w; x += gridSize) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += gridSize) {
            g2.drawLine(0, y, w, y);
        }

        g2.dispose();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(ThemeManager.ACCENT_CYAN);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(ThemeManager.getInputTextColor());
        field.setBackground(ThemeManager.getInputBackground());
        field.setCaretColor(ThemeManager.ACCENT_CYAN);
        field.setMaximumSize(new Dimension(350, 38));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }

    private void performRegister() {
        String username = usernameField.getActualText();
        String email = emailField.getActualText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(ThemeManager.ACCENT_RED);
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setForeground(ThemeManager.ACCENT_RED);
            statusLabel.setText("Passwords do not match!");
            return;
        }

        registerButton.setEnabled(false);
        statusLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        statusLabel.setText("Creating RepSync account...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.register(username, password, email);
            }

            @Override
            protected void done() {
                registerButton.setEnabled(true);
                try {
                    User user = get();
                    statusLabel.setForeground(ThemeManager.ACCENT_GREEN);
                    statusLabel.setText("Account created! Redirecting...");

                    if (onRegisterSuccess != null) {
                        Timer timer = new Timer(1000, e -> onRegisterSuccess.accept(user));
                        timer.setRepeats(false);
                        timer.start();
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(ThemeManager.ACCENT_RED);
                    statusLabel.setText(e.getCause() instanceof AuthException ? e.getCause().getMessage() : "An error occurred.");
                }
            }
        };
        worker.execute();
    }

    public void setOnRegisterSuccess(java.util.function.Consumer<User> callback) {
        this.onRegisterSuccess = callback;
    }

    public void setOnShowLogin(Runnable callback) {
        this.onShowLogin = callback;
    }
}
