package com.repsync.ui;

import com.repsync.model.User;
import com.repsync.ui.admin.ExerciseManagerPanel;
import com.repsync.ui.auth.ChangePasswordPanel;
import com.repsync.ui.auth.LoginPanel;
import com.repsync.ui.auth.RegisterPanel;
import com.repsync.ui.components.NavigationPanel;
import com.repsync.ui.dashboard.DashboardPanel;
import com.repsync.ui.profile.ProfilePanel;
import com.repsync.ui.progress.PRTrackerPanel;
import com.repsync.ui.progress.ProgressPanel;
import com.repsync.ui.workout.WorkoutHistoryPanel;
import com.repsync.ui.workout.WorkoutLoggerPanel;
import com.repsync.ui.workout.WorkoutPlannerPanel;
import com.repsync.model.Exercise;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

/**
 * Main application frame - the root window.
 * Uses CardLayout to switch between auth screens and main app.
 * Has a sidebar navigation and content area.
 */
public class MainFrame extends JFrame {

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    // Auth panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    // App panels (created after login)
    private NavigationPanel navigationPanel;
    private CardLayout contentCardLayout;
    private JPanel contentCardPanel;

    private DashboardPanel dashboardPanel;
    private WorkoutPlannerPanel plannerPanel;
    private WorkoutLoggerPanel loggerPanel;
    private WorkoutHistoryPanel historyPanel;
    private ProgressPanel progressPanel;
    private PRTrackerPanel prTrackerPanel;
    private ProfilePanel profilePanel;
    private ExerciseManagerPanel exerciseManagerPanel;
    private ChangePasswordPanel changePasswordPanel;
    private JLabel greetingLabel;
    private JButton changePassButton;

    private User currentUser;

    public MainFrame() {
        setTitle("RepSync – Smart Gym & Fitness Progress Tracker");
        setSize(1280, 780);
        setMinimumSize(new Dimension(960, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main card layout: switches between "auth" and "app"
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);

        // Auth section
        JPanel authSection = new JPanel(new CardLayout());
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();

        // Wire up auth callbacks
        loginPanel.setOnLoginSuccess(this::onLogin);
        loginPanel.setOnShowRegister(() -> ((CardLayout) authSection.getLayout()).show(authSection, "register"));

        registerPanel.setOnRegisterSuccess(this::onLogin);
        registerPanel.setOnShowLogin(() -> ((CardLayout) authSection.getLayout()).show(authSection, "login"));

        authSection.add(loginPanel, "login");
        authSection.add(registerPanel, "register");

        // App section (placeholder - built on login)
        JPanel appSection = new JPanel(new BorderLayout());

        mainCardPanel.add(authSection, "auth");
        mainCardPanel.add(appSection, "app");

        setContentPane(mainCardPanel);
        mainCardLayout.show(mainCardPanel, "auth");
    }

    /**
     * Called when login/registration succeeds.
     * Builds the main app UI and switches to it.
     */
    private void onLogin(User user) {
        this.currentUser = user;

        // Build the main application panel
        JPanel appSection = buildAppSection();

        // Replace the app panel in the card layout
        mainCardPanel.add(appSection, "app");
        mainCardLayout.show(mainCardPanel, "app");

        setTitle("RepSync – " + user.getUsername() + " (" + user.getRole() + ")");
    }

    /**
     * Build the main application UI with sidebar + content area.
     */
    private JPanel buildAppSection() {
        JPanel appPanel = new JPanel(new BorderLayout());

        // Sidebar navigation
        navigationPanel = new NavigationPanel();
        navigationPanel.setAdminVisible(currentUser.isAdmin());
        navigationPanel.setUserInfo(currentUser.getUsername());

        // Content area with CardLayout
        contentCardLayout = new CardLayout();
        contentCardPanel = new JPanel(contentCardLayout);
        contentCardPanel.setBackground(ThemeManager.getBackground());

        // Create all content panels
        dashboardPanel = new DashboardPanel(currentUser);
        plannerPanel = new WorkoutPlannerPanel(currentUser);
        loggerPanel = new WorkoutLoggerPanel(currentUser);
        historyPanel = new WorkoutHistoryPanel(currentUser);
        progressPanel = new ProgressPanel(currentUser);
        prTrackerPanel = new PRTrackerPanel(currentUser);
        profilePanel = new ProfilePanel(currentUser);
        exerciseManagerPanel = new ExerciseManagerPanel();
        changePasswordPanel = new ChangePasswordPanel(currentUser);

        // Add panels to card layout
        contentCardPanel.add(dashboardPanel, "dashboard");
        contentCardPanel.add(plannerPanel, "workout_planner");
        contentCardPanel.add(loggerPanel, "workout_logger");
        contentCardPanel.add(historyPanel, "workout_history");
        contentCardPanel.add(progressPanel, "progress");
        contentCardPanel.add(prTrackerPanel, "pr_tracker");
        contentCardPanel.add(profilePanel, "profile");
        contentCardPanel.add(exerciseManagerPanel, "exercises");
        contentCardPanel.add(changePasswordPanel, "change_password");

        // Wire up Planner to Logger bridge
        plannerPanel.setOnStartWorkout((List<Exercise> exercises) -> {
            navigationPanel.setActiveItem("workout_logger");
            contentCardLayout.show(contentCardPanel, "workout_logger");
            loggerPanel.prefillExercises(exercises);
        });

        // Wire up Dashboard quick actions
        dashboardPanel.setOnNavigate((String target) -> {
            navigationPanel.setActiveItem(target);
            contentCardLayout.show(contentCardPanel, target);
            refreshActivePanel(target);
        });

        // Wire up navigation
        navigationPanel.setOnNavigate(() -> {
            String activeItem = navigationPanel.getActiveItem();
            if ("theme".equals(activeItem)) {
                ThemeManager.toggleTheme(this);
                refreshAllPanels();
            } else {
                contentCardLayout.show(contentCardPanel, activeItem);
                refreshActivePanel(activeItem);
            }
        });

        // Start on dashboard
        navigationPanel.setActiveItem("dashboard");
        contentCardLayout.show(contentCardPanel, "dashboard");

        // Top bar with user info and logout
        JPanel topBar = createTopBar();

        appPanel.add(navigationPanel, BorderLayout.WEST);
        appPanel.add(contentCardPanel, BorderLayout.CENTER);
        appPanel.add(topBar, BorderLayout.NORTH);

        return appPanel;
    }

    /**
     * Create the top bar with user greeting, avatar, and action buttons.
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();

                // Subtle gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, ThemeManager.getSidebarBackground(),
                    w, 0, ThemeManager.getBackground()
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Bottom border line
                g2.setColor(ThemeManager.getDividerColor());
                g2.fillRect(0, h - 1, w, 1);

                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        topBar.setPreferredSize(new Dimension(0, 52));

        // Left side: Greeting
        String greeting = getTimeBasedGreeting();
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        // Small avatar circle
        JPanel miniAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, ThemeManager.ACCENT_BLUE,
                    getWidth(), getHeight(), ThemeManager.ACCENT_PURPLE
                );
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                
                String initials = currentUser.getUsername().substring(0, Math.min(2, currentUser.getUsername().length())).toUpperCase();
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(initials)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, textX, textY);
                g2.dispose();
            }
        };
        miniAvatar.setPreferredSize(new Dimension(30, 30));
        miniAvatar.setOpaque(false);

        greetingLabel = new JLabel(greeting + ", " + currentUser.getUsername() + (currentUser.isAdmin() ? " [ADMIN]" : "") + "!");
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        greetingLabel.setForeground(ThemeManager.getTextColor());

        leftPanel.add(miniAvatar);
        leftPanel.add(greetingLabel);

        // Right side: Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        changePassButton = new JButton("🔑 Password");
        changePassButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changePassButton.setForeground(ThemeManager.getTextColor());
        changePassButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePassButton.setFocusPainted(false);
        changePassButton.addActionListener(e -> {
            contentCardLayout.show(contentCardPanel, "change_password");
        });

        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setForeground(ThemeManager.ACCENT_RED);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());

        rightPanel.add(changePassButton);
        rightPanel.add(logoutButton);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Get a time-of-day based greeting.
     */
    private String getTimeBasedGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    }

    /**
     * Logout and return to login screen.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        currentUser = null;
        mainCardLayout.show(mainCardPanel, "auth");
        setTitle("RepSync – Smart Gym & Fitness Progress Tracker");
    }

    /**
     * Refresh the active panel when navigated to.
     */
    private void refreshActivePanel(String panelName) {
        switch (panelName) {
            case "dashboard" -> dashboardPanel.refresh();
            case "workout_planner" -> plannerPanel.refresh();
            case "workout_logger" -> loggerPanel.refresh();
            case "workout_history" -> historyPanel.refresh();
            case "progress" -> progressPanel.refresh();
            case "pr_tracker" -> prTrackerPanel.refresh();
            case "profile" -> profilePanel.refresh();
            case "exercises" -> exerciseManagerPanel.refresh();
            case "change_password" -> changePasswordPanel.refresh();
        }
    }

    /**
     * Refresh all panels after theme change.
     */
    private void refreshAllPanels() {
        if (contentCardPanel != null) {
            contentCardPanel.setBackground(ThemeManager.getBackground());
        }
        if (greetingLabel != null) {
            greetingLabel.setForeground(ThemeManager.getTextColor());
        }
        if (changePassButton != null) {
            changePassButton.setForeground(ThemeManager.getTextColor());
        }
        if (navigationPanel != null) {
            navigationPanel.refreshTheme();
        }
        if (dashboardPanel != null) dashboardPanel.refresh();
        if (plannerPanel != null) plannerPanel.refresh();
        if (loggerPanel != null) loggerPanel.refresh();
        if (historyPanel != null) historyPanel.refresh();
        if (progressPanel != null) progressPanel.refresh();
        if (prTrackerPanel != null) prTrackerPanel.refresh();
        if (profilePanel != null) profilePanel.refresh();
        if (exerciseManagerPanel != null) exerciseManagerPanel.refresh();
        if (changePasswordPanel != null) changePasswordPanel.refresh();
        repaint();
    }
}
