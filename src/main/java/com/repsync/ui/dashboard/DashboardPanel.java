package com.repsync.ui.dashboard;

import com.repsync.model.BodyProgress;
import com.repsync.model.User;
import com.repsync.model.WorkoutSession;
import com.repsync.service.ProgressService;
import com.repsync.service.WorkoutService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StatCard;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Main dashboard panel - shows quick stats, quick-action buttons,
 * recent activity, and a daily tip. Premium design with gradient welcome card.
 */
public class DashboardPanel extends JPanel {

    private User currentUser;
    private final WorkoutService workoutService = new WorkoutService();
    private final ProgressService progressService = new ProgressService();
    private Consumer<String> onNavigate;

    private static final String[] MOTIVATIONAL_QUOTES = {
        "\u201CThe only bad workout is the one that didn\u2019t happen.\u201D",
        "\u201CStrength does not come from the body. It comes from the will.\u201D",
        "\u201CPush yourself, because no one else is going to do it for you.\u201D",
        "\u201CSuccess isn\u2019t always about greatness. It\u2019s about consistency.\u201D",
        "\u201CDon\u2019t stop when you\u2019re tired. Stop when you\u2019re done.\u201D",
        "\u201CYour body can stand almost anything. It\u2019s your mind you have to convince.\u201D",
        "\u201CThe pain you feel today will be the strength you feel tomorrow.\u201D",
        "\u201CIt never gets easier. You just get stronger.\u201D",
        "\u201COne more rep. One more set. One step closer.\u201D",
        "\u201CChampions aren\u2019t made in the gyms. They are made from something deep inside them.\u201D"
    };

    private static final String[] DAILY_TIPS = {
        "💡 Tip: Drink at least 3-4 liters of water daily to stay hydrated during workouts.",
        "💡 Tip: Get 7-8 hours of sleep — muscles grow during rest, not just in the gym.",
        "💡 Tip: Warm up for 5-10 minutes before lifting to prevent injuries.",
        "💡 Tip: Track your protein intake — aim for 1.6-2.2g per kg of body weight.",
        "💡 Tip: Progressive overload is key — increase weight or reps every 1-2 weeks.",
        "💡 Tip: Don't skip leg day! Compound leg exercises boost overall growth hormones.",
        "💡 Tip: Rest 60-90s between sets for hypertrophy, 3-5 mins for strength.",
    };

    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackground());
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
    }

    /**
     * Set the navigation callback for quick-action buttons.
     */
    public void setOnNavigate(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    private void buildUI() {
        removeAll();

        JLabel loadingLabel = new JLabel("Loading Dashboard...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loadingLabel.setForeground(ThemeManager.getTextColor());
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loadingLabel, BorderLayout.CENTER);
        revalidate();
        repaint();

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                DashboardData data = new DashboardData();
                data.totalWorkouts = workoutService.getTotalWorkouts(currentUser.getId());
                data.streak = progressService.calculateWorkoutStreak(currentUser.getId());
                data.latestWeight = progressService.getLatestWeight(currentUser.getId());
                data.recentSessions = workoutService.getWorkoutHistory(currentUser.getId());
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    buildDashboardContent(data);
                } catch (Exception e) {
                    removeAll();
                    JLabel errorLabel = new JLabel("Error loading dashboard: " + e.getMessage());
                    errorLabel.setForeground(ThemeManager.ACCENT_RED);
                    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    add(errorLabel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        };
        worker.execute();
    }

    private static class DashboardData {
        int totalWorkouts;
        int streak;
        BodyProgress latestWeight;
        List<WorkoutSession> recentSessions;
    }

    private void buildDashboardContent(DashboardData data) {
        removeAll();

        // ─── SCROLLABLE CONTENT ───
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // ─── WELCOME CARD (gradient background) ───
        JPanel welcomeCard = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(59, 130, 246, 30),
                    w, h, new Color(139, 92, 246, 20)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Border
                g2.setColor(new Color(59, 130, 246, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

                // Left accent bar
                GradientPaint accent = new GradientPaint(
                    0, 0, ThemeManager.ACCENT_BLUE,
                    0, h, ThemeManager.ACCENT_PURPLE
                );
                g2.setPaint(accent);
                g2.fillRoundRect(0, 8, 5, h - 16, 4, 4);

                g2.dispose();
            }
        };
        welcomeCard.setOpaque(false);
        welcomeCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        welcomeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        welcomeCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername() + "! 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(ThemeManager.getTextColor());

        String goalText = currentUser.getFitnessGoal() != null ? currentUser.getFitnessGoal().getDisplayName() : "Not set";
        JLabel goalLabel = new JLabel("🎯 Goal: " + goalText);
        goalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        goalLabel.setForeground(ThemeManager.getSecondaryTextColor());

        // Motivational quote
        String quote = MOTIVATIONAL_QUOTES[new Random().nextInt(MOTIVATIONAL_QUOTES.length)];
        JLabel quoteLabel = new JLabel(quote);
        quoteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        quoteLabel.setForeground(ThemeManager.ACCENT_PURPLE);

        welcomeText.add(welcomeLabel);
        welcomeText.add(Box.createVerticalStrut(4));
        welcomeText.add(goalLabel);
        welcomeText.add(Box.createVerticalStrut(4));
        welcomeText.add(quoteLabel);

        welcomeCard.add(welcomeText, BorderLayout.CENTER);

        contentPanel.add(welcomeCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // ─── STAT CARDS ROW ───
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String weightStr = data.latestWeight != null ? String.format("%.1f kg", data.latestWeight.getWeightKg()) : "N/A";
        String bmiStr = data.latestWeight != null && data.latestWeight.getBmi() > 0 ? String.format("%.1f", data.latestWeight.getBmi()) : "N/A";

        statsPanel.add(new StatCard("🏋", "Total Workouts", String.valueOf(data.totalWorkouts), ThemeManager.ACCENT_BLUE));
        statsPanel.add(new StatCard("🔥", "Workout Streak", data.streak + " days", ThemeManager.ACCENT_ORANGE));
        statsPanel.add(new StatCard("⚖", "Current Weight", weightStr, ThemeManager.ACCENT_GREEN));
        statsPanel.add(new StatCard("📊", "BMI", bmiStr, ThemeManager.ACCENT_PURPLE));

        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // ─── QUICK ACTIONS ROW ───
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        quickActionsPanel.add(createQuickActionCard("🏋  Start Today's Workout", "Go to Workout Planner", ThemeManager.ACCENT_BLUE, "workout_planner"));
        quickActionsPanel.add(createQuickActionCard("⚖  Log Your Weight", "Track your body progress", ThemeManager.ACCENT_GREEN, "progress"));
        quickActionsPanel.add(createQuickActionCard("🏆  View Your PRs", "Check personal records", ThemeManager.ACCENT_ORANGE, "pr_tracker"));

        contentPanel.add(quickActionsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // ─── BOTTOM SECTION: Recent Activity + Daily Tip side by side ───
        JPanel bottomSection = new JPanel(new BorderLayout(15, 0));
        bottomSection.setOpaque(false);
        bottomSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Recent Activity
        JPanel activityPanel = createStyledCard();
        activityPanel.setLayout(new BorderLayout(10, 10));

        JLabel activityTitle = new JLabel("📋  Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setForeground(ThemeManager.getTextColor());

        // Recent workouts table
        String[] columns = {"Date", "Duration", "Exercises", "Volume (kg)"};
        int rowCount = Math.min(5, data.recentSessions.size());
        Object[][] tableData = new Object[rowCount][4];

        for (int i = 0; i < rowCount; i++) {
            WorkoutSession session = data.recentSessions.get(i);
            tableData[i] = new Object[]{
                DateFormatter.formatDate(session.getSessionDate()),
                DateFormatter.formatDuration(session.getDurationMinutes()),
                session.getExerciseCount() + " exercises",
                String.format("%.1f", session.getTotalVolume())
            };
        }

        JTable activityTable = new JTable(tableData, columns);
        activityTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activityTable.setRowHeight(36);
        activityTable.setEnabled(false);
        activityTable.setShowGrid(false);
        activityTable.setIntercellSpacing(new Dimension(0, 0));
        activityTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        activityTable.getTableHeader().setForeground(ThemeManager.getSecondaryTextColor());

        // Alternating row colors
        activityTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(ThemeManager.getCardBackground());
                } else {
                    c.setBackground(ThemeManager.getTableAlternateRow());
                }
                c.setForeground(ThemeManager.getTextColor());
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 210));

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        if (rowCount > 0) {
            activityPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel noDataLabel = new JLabel("No workouts yet! Go to Workout Planner to get started.");
            noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            noDataLabel.setForeground(ThemeManager.getSecondaryTextColor());
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            activityPanel.add(noDataLabel, BorderLayout.CENTER);
        }

        // Daily Tip card
        JPanel tipPanel = createStyledCard();
        tipPanel.setLayout(new BorderLayout(10, 10));
        tipPanel.setPreferredSize(new Dimension(280, 0));

        JLabel tipTitle = new JLabel("💡  Daily Tip");
        tipTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tipTitle.setForeground(ThemeManager.getTextColor());

        String tip = DAILY_TIPS[new Random().nextInt(DAILY_TIPS.length)];
        JLabel tipText = new JLabel("<html><p style='width:200px; line-height:1.5;'>" + tip.substring(tip.indexOf(" ") + 1) + "</p></html>");
        tipText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tipText.setForeground(ThemeManager.getSecondaryTextColor());

        tipPanel.add(tipTitle, BorderLayout.NORTH);
        tipPanel.add(tipText, BorderLayout.CENTER);

        bottomSection.add(activityPanel, BorderLayout.CENTER);
        bottomSection.add(tipPanel, BorderLayout.EAST);

        contentPanel.add(bottomSection);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(mainScroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Create a quick-action card button.
     */
    private JPanel createQuickActionCard(String title, String subtitle, Color accentColor, String target) {
        JPanel card = new JPanel(new BorderLayout()) {
            private boolean hovering = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e) { hovering = false; repaint(); }
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (onNavigate != null) onNavigate.accept(target);
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(hovering ? ThemeManager.getCardHoverBackground() : ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, w, h, 14, 14);

                // Border
                g2.setColor(hovering ? ThemeManager.withAlpha(accentColor, 60) : new Color(60, 60, 80, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

                // Left accent bar
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 10, 4, h - 20, 4, 4);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ThemeManager.getTextColor());

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(ThemeManager.getSecondaryTextColor());

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrowLabel.setForeground(accentColor);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(arrowLabel, BorderLayout.EAST);

        return card;
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
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        return card;
    }

    /**
     * Refresh the dashboard data.
     */
    public void refresh() {
        buildUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
