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
 * High-End 3D Glassmorphism Login Panel for RepSync.
 * Features dynamic 3D lighting background, vector-rendered 3D barbell emblem,
 * floating glassmorphism card, password eye toggle, and 1-click quick demo login buttons.
 */
public class LoginPanel extends JPanel {

    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private StyledButton loginButton;
    private JButton eyeToggleButton;
    private boolean isPasswordVisible = false;

    private final AuthService authService = new AuthService();

    private java.util.function.Consumer<User> onLoginSuccess;
    private Runnable onShowRegister;

    public LoginPanel() {
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
                    0, 0, ThemeManager.ACCENT_BLUE,
                    w, 0, ThemeManager.ACCENT_PURPLE
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
        card.setBorder(new EmptyBorder(35, 45, 35, 45));
        card.setPreferredSize(new Dimension(440, 560));

        // --- 3D LOGO EMBLEM HEADER ---
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Emblem3DRenderer.draw3DLogo(g2, (getWidth() - 64) / 2, 0, 64);
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(350, 68));
        logoPanel.setMaximumSize(new Dimension(350, 68));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title text "REPSYNC"
        JLabel title = new JLabel("REPSYNC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ThemeManager.getTextColor());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline Pill Badge
        JLabel tagline = new JLabel("  ⚡ TRACK • TRAIN • TRANSFORM  ");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tagline.setForeground(ThemeManager.ACCENT_CYAN);
        tagline.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_CYAN, 30));
        tagline.setOpaque(true);
        tagline.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.withAlpha(ThemeManager.ACCENT_CYAN, 100), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to access your fitness dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- INPUT FIELDS ---
        JLabel usernameLabel = createFieldLabel("👤  USERNAME");
        usernameField = new StyledTextField("Enter your username");
        usernameField.setMaximumSize(new Dimension(350, 40));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = createFieldLabel("🔑  PASSWORD");

        // Password field container with eye toggle button
        JPanel passwordContainer = new JPanel(new BorderLayout(5, 0));
        passwordContainer.setOpaque(false);
        passwordContainer.setMaximumSize(new Dimension(350, 40));
        passwordContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(ThemeManager.getInputTextColor());
        passwordField.setBackground(ThemeManager.getInputBackground());
        passwordField.setCaretColor(ThemeManager.ACCENT_CYAN);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        eyeToggleButton = new JButton("👁");
        eyeToggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eyeToggleButton.setForeground(ThemeManager.getSecondaryTextColor());
        eyeToggleButton.setContentAreaFilled(false);
        eyeToggleButton.setBorderPainted(false);
        eyeToggleButton.setFocusPainted(false);
        eyeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeToggleButton.setToolTipText("Toggle password visibility");
        eyeToggleButton.addActionListener(e -> togglePasswordVisibility());

        passwordContainer.add(passwordField, BorderLayout.CENTER);
        passwordContainer.add(eyeToggleButton, BorderLayout.EAST);

        // --- 3D LOGIN BUTTON ---
        loginButton = new StyledButton("Sign In to RepSync", ThemeManager.ACCENT_BLUE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setMaximumSize(new Dimension(350, 44));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        // --- QUICK DEMO LOGIN BUTTONS ---
        JPanel quickDemoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        quickDemoPanel.setOpaque(false);
        quickDemoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel demoLbl = new JLabel("Quick Login:");
        demoLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        demoLbl.setForeground(ThemeManager.getSecondaryTextColor());

        JButton demoUserBtn = createQuickDemoButton("🚀 User Demo", "hello", "123456");
        JButton demoAdminBtn = createQuickDemoButton("👑 Admin", "admin", "admin123");

        quickDemoPanel.add(demoLbl);
        quickDemoPanel.add(demoUserBtn);
        quickDemoPanel.add(demoAdminBtn);

        // --- REGISTER LINK ---
        JLabel registerLink = new JLabel("Don't have an account? Create One Here");
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerLink.setForeground(ThemeManager.ACCENT_CYAN);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onShowRegister != null) onShowRegister.run();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Don't have an account? Create One Here</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Don't have an account? Create One Here");
            }
        });

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeManager.ACCENT_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- ASSEMBLE CARD ---
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(tagline);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));

        card.add(usernameLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));

        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordContainer);
        card.add(Box.createVerticalStrut(10));

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(14));

        card.add(quickDemoPanel);
        card.add(Box.createVerticalStrut(16));
        card.add(registerLink);

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
        RadialGradientPaint orbBlue = new RadialGradientPaint(
            w * 0.2f, h * 0.25f, 350f,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(59, 130, 246, 60),
                new Color(30, 60, 140, 15),
                new Color(0, 0, 0, 0)
            }
        );
        g2.setPaint(orbBlue);
        g2.fillRect(0, 0, w, h);

        RadialGradientPaint orbPurple = new RadialGradientPaint(
            w * 0.8f, h * 0.75f, 300f,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(139, 92, 246, 50),
                new Color(70, 30, 130, 15),
                new Color(0, 0, 0, 0)
            }
        );
        g2.setPaint(orbPurple);
        g2.fillRect(0, 0, w, h);

        // 3. Subtle 3D Depth Grid Mesh
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

    private JButton createQuickDemoButton(String title, String user, String pass) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(ThemeManager.getTextColor());
        btn.setBackground(ThemeManager.getCardBackground());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.withAlpha(ThemeManager.ACCENT_BLUE, 100), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        btn.addActionListener(e -> {
            usernameField.setText(user);
            usernameField.setForeground(ThemeManager.getInputTextColor());
            passwordField.setText(pass);
            performLogin();
        });
        return btn;
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setEchoChar((char) 0);
            eyeToggleButton.setText("🙈");
        } else {
            passwordField.setEchoChar('•');
            eyeToggleButton.setText("👁");
        }
    }

    private void performLogin() {
        String username = usernameField.getActualText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(ThemeManager.ACCENT_RED);
            statusLabel.setText("Please enter username and password.");
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        statusLabel.setText("Connecting to RepSync...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    User user = get();
                    statusLabel.setText(" ");
                    if (onLoginSuccess != null) {
                        onLoginSuccess.accept(user);
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(ThemeManager.ACCENT_RED);
                    statusLabel.setText(e.getCause() instanceof AuthException ? e.getCause().getMessage() : "Invalid credentials.");
                }
            }
        };
        worker.execute();
    }

    public void setOnLoginSuccess(java.util.function.Consumer<User> callback) {
        this.onLoginSuccess = callback;
    }

    public void setOnShowRegister(Runnable callback) {
        this.onShowRegister = callback;
    }
}
