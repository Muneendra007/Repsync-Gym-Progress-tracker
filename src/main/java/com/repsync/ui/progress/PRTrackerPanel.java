package com.repsync.ui.progress;

import com.repsync.model.PersonalRecord;
import com.repsync.model.User;
import com.repsync.service.PRService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Personal Record (PR) tracker panel.
 * Displays all PRs in a styled table with recent PRs highlighted.
 * Premium design with section header and alternating rows.
 */
public class PRTrackerPanel extends JPanel {

    private User currentUser;
    private final PRService prService = new PRService();
    private DefaultTableModel tableModel;
    private StyledButton refreshButton;

    public PRTrackerPanel(User user) {
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

        JLabel title = new JLabel("🏆  Personal Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel("Track your all-time bests — new PRs from the last 7 days are highlighted");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controlPanel.setOpaque(false);

        refreshButton = new StyledButton("🔄 Refresh", ThemeManager.ACCENT_BLUE);
        refreshButton.setPreferredSize(new Dimension(120, 36));
        refreshButton.addActionListener(e -> loadPRs());

        controlPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // PR table
        String[] columns = {"Exercise", "Record Type", "Value", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable prTable = new JTable(tableModel);
        prTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prTable.setRowHeight(40);
        prTable.setShowGrid(false);
        prTable.setIntercellSpacing(new Dimension(0, 0));
        prTable.setBackground(ThemeManager.getCardBackground());
        prTable.setForeground(ThemeManager.getTextColor());

        // Custom High-Contrast Table Header Renderer
        prTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(ThemeManager.getTableHeaderBackground());
                c.setForeground(ThemeManager.getTableHeaderTextColor());
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                return c;
            }
        });

        // Custom renderer with alternating rows + NEW highlight
        prTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String statusVal = (String) table.getModel().getValueAt(row, 4);
                boolean isNew = statusVal != null && statusVal.contains("NEW");

                if (isSelected) {
                    c.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_BLUE, 50));
                } else if (isNew) {
                    c.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_GREEN, 20));
                } else {
                    c.setBackground(row % 2 == 0 ? ThemeManager.getCardBackground() : ThemeManager.getTableAlternateRow());
                }
                c.setForeground(ThemeManager.getTextColor());
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                // Bold the status column if NEW
                if (column == 4 && isNew) {
                    c.setForeground(ThemeManager.ACCENT_GREEN);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(prTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadPRs();
    }

    /**
     * Load PRs from the database and populate the table.
     */
    private void loadPRs() {
        if (refreshButton != null) refreshButton.setEnabled(false);
        tableModel.setRowCount(0);

        SwingWorker<List<PersonalRecord>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PersonalRecord> doInBackground() throws Exception {
                return prService.getAllPRs(currentUser.getId());
            }

            @Override
            protected void done() {
                if (refreshButton != null) refreshButton.setEnabled(true);
                try {
                    List<PersonalRecord> prs = get();
                    LocalDate oneWeekAgo = LocalDate.now().minusDays(7);

                    if (prs.isEmpty()) {
                        // Show empty state
                        tableModel.addRow(new Object[]{"No personal records yet!", "", "", "", ""});
                    }

                    for (PersonalRecord pr : prs) {
                        String status = pr.getAchievedDate().isAfter(oneWeekAgo) ? "🆕 NEW!" : "";

                        tableModel.addRow(new Object[]{
                            pr.getExerciseName(),
                            pr.getRecordType().getDisplayName(),
                            pr.getFormattedValue(),
                            DateFormatter.formatDate(pr.getAchievedDate()),
                            status
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PRTrackerPanel.this, "Failed to load PRs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        removeAll();
        buildUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadPRs();
    }
}
