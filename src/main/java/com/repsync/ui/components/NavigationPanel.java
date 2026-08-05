package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sidebar navigation panel with clickable menu items, icons, section dividers,
 * and a user avatar area. Uses custom painting for smooth hover transitions
 * and an active indicator bar.
 */
public class NavigationPanel extends JPanel {

    private final Map<String, NavItem> menuItems = new LinkedHashMap<>();
    private String activeItem = "";
    private Runnable onNavigate;
    private JLabel userInitialsLabel;
    private JLabel userNameLabel;
    private boolean isAdmin = false;

    // Menu items: key -> icon -> display text -> tooltip -> section (null = same section as previous)
    private static final String[][] MENU_ITEMS = {
        {"dashboard",        "⚡",  "Dashboard",         "View your stats and recent activity"},
        {"workout_planner",  "📋",  "Workout Planner",   "Generate a workout plan for your goal"},
        {"workout_logger",   "🏋",  "Log Workout",       "Record a new workout session"},
        {"workout_history",  "📜",  "Workout History",   "Browse your past workout sessions"},
        {"---",              "",    "",                  ""},  // Divider
        {"progress",         "📈",  "Progress",          "Track weight and workout frequency"},
        {"pr_tracker",       "🏆",  "PR Tracker",        "View your personal records"},
        {"---2",             "",    "",                  ""},  // Divider
        {"profile",          "👤",  "Profile",           "Edit your profile and fitness goal"},
        {"exercises",        "⚙",   "Manage Exercises",  "Add or remove exercises (Admin)"},
    };

    public NavigationPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Add menu items
        for (String[] item : MENU_ITEMS) {
            if ("exercises".equals(item[0]) && !isAdmin) {
                continue;
            }
            if (item[0].startsWith("---")) {
                add(createDivider());
            } else {
                addMenuItem(item[0], item[1], item[2], item[3]);
            }
        }

        // Push theme toggle to bottom
        add(Box.createVerticalGlue());

        // Theme toggle button at bottom
        NavItem themeToggle = new NavItem("theme", "🌓", "Toggle Theme", "Switch between dark and light mode");
        add(themeToggle);
        add(Box.createVerticalStrut(10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Sidebar background
        g2.setColor(ThemeManager.getSidebarBackground());
        g2.fillRect(0, 0, w, h);

        // Right edge subtle line
        g2.setColor(ThemeManager.getDividerColor());
        g2.fillRect(w - 1, 0, 1, h);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Set the user info displayed in the sidebar header.
     */
    public void setUserInfo(String username) {
        // Remove old header if exists and rebuild
        removeAll();

        // App logo section
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        logoPanel.setMaximumSize(new Dimension(240, 70));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logo = new JLabel("RepSync");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(ThemeManager.ACCENT_BLUE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Track · Train · Transform");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagline.setForeground(ThemeManager.getSecondaryTextColor());
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(tagline);
        add(logoPanel);

        // User avatar section
        if (username != null && !username.isEmpty()) {
            add(Box.createVerticalStrut(5));
            JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
            avatarPanel.setOpaque(false);
            avatarPanel.setMaximumSize(new Dimension(240, 50));
            avatarPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            // Avatar circle with initials
            String initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
            JPanel avatarCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient circle
                    GradientPaint gp = new GradientPaint(
                        0, 0, ThemeManager.ACCENT_BLUE,
                        getWidth(), getHeight(), ThemeManager.ACCENT_PURPLE
                    );
                    g2.setPaint(gp);
                    g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                    
                    // Initials text
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(initials)) / 2;
                    int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(initials, textX, textY);
                    
                    g2.dispose();
                }
            };
            avatarCircle.setPreferredSize(new Dimension(36, 36));
            avatarCircle.setOpaque(false);

            userNameLabel = new JLabel(username);
            userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            userNameLabel.setForeground(ThemeManager.getTextColor());

            avatarPanel.add(avatarCircle);
            avatarPanel.add(userNameLabel);
            if (this.isAdmin) {
                JLabel badge = new JLabel(" ADMIN ");
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
                badge.setForeground(Color.WHITE);
                badge.setBackground(ThemeManager.ACCENT_PURPLE);
                badge.setOpaque(true);
                badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                avatarPanel.add(badge);
            }
            add(avatarPanel);
        }

        add(Box.createVerticalStrut(5));
        add(createDivider());

        // Re-add menu items
        for (String[] item : MENU_ITEMS) {
            if ("exercises".equals(item[0]) && !isAdmin) {
                continue;
            }
            if (item[0].startsWith("---")) {
                add(createDivider());
            } else {
                addMenuItem(item[0], item[1], item[2], item[3]);
            }
        }

        // Push theme toggle to bottom
        add(Box.createVerticalGlue());

        // Theme toggle
        NavItem themeToggle = new NavItem("theme", "🌓", "Toggle Theme", "Switch between dark and light mode");
        add(themeToggle);
        add(Box.createVerticalStrut(10));

        revalidate();
        repaint();
    }

    /**
     * Create a horizontal divider line.
     */
    private JComponent createDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.getDividerColor());
                int y = getHeight() / 2;
                g2.fillRect(20, y, getWidth() - 40, 1);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(240, 16));
        divider.setPreferredSize(new Dimension(240, 16));
        return divider;
    }

    /**
     * Add a navigation menu item.
     */
    private void addMenuItem(String key, String icon, String label, String tooltip) {
        NavItem item = new NavItem(key, icon, label, tooltip);
        menuItems.put(key, item);
        add(item);
    }

    /**
     * Set the active (highlighted) menu item.
     */
    public void setActiveItem(String key) {
        // Reset previous active
        if (menuItems.containsKey(activeItem)) {
            menuItems.get(activeItem).setActive(false);
        }

        activeItem = key;

        // Highlight new active
        if (menuItems.containsKey(key)) {
            menuItems.get(key).setActive(true);
        }

        // Notify listener
        if (onNavigate != null) {
            onNavigate.run();
        }
    }

    /**
     * Get the currently active menu item key.
     */
    public String getActiveItem() {
        return activeItem;
    }

    /**
     * Set navigation callback.
     */
    public void setOnNavigate(Runnable onNavigate) {
        this.onNavigate = onNavigate;
    }

    /**
     * Show or hide the admin "Manage Exercises" menu item.
     */
    public void setAdminVisible(boolean visible) {
        this.isAdmin = visible;
        NavItem exercisesPanel = menuItems.get("exercises");
        if (exercisesPanel != null) {
            exercisesPanel.setVisible(visible);
        }
    }

    /**
     * Refresh colors for all navigation items on theme change.
     */
    public void refreshTheme() {
        if (userNameLabel != null) {
            userNameLabel.setForeground(ThemeManager.getTextColor());
        }
        for (NavItem item : menuItems.values()) {
            item.updateTheme();
        }
        revalidate();
        repaint();
    }

    // ──────────────────────────────────────────────
    //  Inner class: a single navigation item
    // ──────────────────────────────────────────────
    private class NavItem extends JPanel {

        private final String key;
        private final JLabel iconLabel;
        private final JLabel textLabel;

        private boolean isActive = false;
        private float hoverAlpha = 0f;           // 0 = no hover, 1 = full hover
        private static final float ALPHA_STEP = 0.15f;
        private static final int TIMER_DELAY_MS = 16; // ~60 fps
        private Timer fadeTimer;
        private boolean hovering = false;

        NavItem(String key, String icon, String label, String tooltip) {
            this.key = key;

            setLayout(new BorderLayout());
            setOpaque(false);                     // WE paint everything ourselves
            setMaximumSize(new Dimension(240, 44));
            setPreferredSize(new Dimension(240, 44));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);

            // Icon label
            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            iconLabel.setPreferredSize(new Dimension(28, 28));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, BorderLayout.WEST);

            // Text label
            textLabel = new JLabel(label);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            textLabel.setForeground(ThemeManager.getTextColor());
            textLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            add(textLabel, BorderLayout.CENTER);

            // Fade timer
            fadeTimer = new Timer(TIMER_DELAY_MS, e -> {
                if (hovering && hoverAlpha < 1f) {
                    hoverAlpha = Math.min(1f, hoverAlpha + ALPHA_STEP);
                    repaint();
                } else if (!hovering && hoverAlpha > 0f) {
                    hoverAlpha = Math.max(0f, hoverAlpha - ALPHA_STEP);
                    repaint();
                } else {
                    fadeTimer.stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    if (!fadeTimer.isRunning()) fadeTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    if (!fadeTimer.isRunning()) fadeTimer.start();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveItem(key);
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            textLabel.setForeground(active ? ThemeManager.ACCENT_BLUE : ThemeManager.getTextColor());
            textLabel.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }

        void updateTheme() {
            textLabel.setForeground(isActive ? ThemeManager.ACCENT_BLUE : ThemeManager.getTextColor());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (isActive) {
                // Active item: solid semi-transparent blue fill + left accent bar
                g2.setComposite(AlphaComposite.SrcOver.derive(0.12f));
                g2.setColor(ThemeManager.ACCENT_BLUE);
                g2.fillRoundRect(8, 0, w - 16, h, 10, 10);

                // Left accent bar (fully opaque, rounded)
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(ThemeManager.ACCENT_BLUE);
                g2.fillRoundRect(0, 6, 4, h - 12, 4, 4);

            } else if (hoverAlpha > 0f) {
                // Hover fade: translucent highlight
                g2.setComposite(AlphaComposite.SrcOver.derive(hoverAlpha * 0.07f));
                g2.setColor(ThemeManager.ACCENT_BLUE);
                g2.fillRoundRect(8, 0, w - 16, h, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
