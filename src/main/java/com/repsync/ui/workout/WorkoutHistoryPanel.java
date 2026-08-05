package com.repsync.ui.workout;

import com.repsync.model.User;
import com.repsync.model.WorkoutExercise;
import com.repsync.model.WorkoutSession;
import com.repsync.service.ReportService;
import com.repsync.service.WorkoutService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Workout history panel - view past sessions in a styled table.
 * Supports search/filter and CSV export. Premium design with alternating rows.
 */
public class WorkoutHistoryPanel extends JPanel {

    private User currentUser;
    private final WorkoutService workoutService = new WorkoutService();
    private final ReportService reportService = new ReportService();
    private DefaultTableModel tableModel;
    private JTable historyTable;
    private List<WorkoutSession> loadedSessions;
    private StyledButton refreshButton;
    private StyledButton exportButton;
    private StyledButton deleteButton;

    public WorkoutHistoryPanel(User user) {
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

        JLabel title = new JLabel("📜  Workout History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel("Browse your past workout sessions, export data, or clean up old records");
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
        refreshButton.addActionListener(e -> loadHistory());

        exportButton = new StyledButton("📥 Export CSV", ThemeManager.ACCENT_GREEN);
        exportButton.setPreferredSize(new Dimension(140, 36));
        exportButton.addActionListener(e -> exportToCSV());

        deleteButton = new StyledButton("🗑 Delete", ThemeManager.ACCENT_ORANGE);
        deleteButton.setPreferredSize(new Dimension(120, 36));
        deleteButton.addActionListener(e -> deleteSelectedWorkout());

        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);
        controlPanel.add(deleteButton);

        // Top
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // History table
        String[] columns = {"Date", "Duration", "Exercises", "Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(40);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(400);
        historyTable.setBackground(ThemeManager.getCardBackground());
        historyTable.setForeground(ThemeManager.getTextColor());

        // Custom High-Contrast Table Header Renderer
        historyTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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

        // Alternating row colors
        historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(ThemeManager.withAlpha(ThemeManager.ACCENT_BLUE, 50));
                } else {
                    c.setBackground(row % 2 == 0 ? ThemeManager.getCardBackground() : ThemeManager.getTableAlternateRow());
                }
                c.setForeground(ThemeManager.getTextColor());
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadHistory();
    }

    /**
     * Load workout history from the database.
     */
    private void loadHistory() {
        if (refreshButton != null) refreshButton.setEnabled(false);
        if (exportButton != null) exportButton.setEnabled(false);
        if (deleteButton != null) deleteButton.setEnabled(false);
        tableModel.setRowCount(0);

        SwingWorker<List<WorkoutSession>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<WorkoutSession> doInBackground() throws Exception {
                return workoutService.getWorkoutHistory(currentUser.getId());
            }

            @Override
            protected void done() {
                if (refreshButton != null) refreshButton.setEnabled(true);
                if (exportButton != null) exportButton.setEnabled(true);
                if (deleteButton != null) deleteButton.setEnabled(true);
                try {
                    loadedSessions = get();
                    for (WorkoutSession session : loadedSessions) {
                        StringBuilder details = new StringBuilder();
                        for (WorkoutExercise we : session.getWorkoutExercises()) {
                            if (details.length() > 0) details.append(" | ");
                            if (we.getWeightKg() > 0) {
                                details.append(we.getExerciseName())
                                       .append(": ").append(we.getSets()).append("x").append(we.getReps())
                                       .append(" @ ").append(we.getWeightKg()).append("kg");
                            } else {
                                details.append(we.getExerciseName())
                                       .append(": ").append(we.getDurationSeconds()).append("s");
                            }
                        }

                        tableModel.addRow(new Object[]{
                            DateFormatter.formatDate(session.getSessionDate()),
                            DateFormatter.formatDuration(session.getDurationMinutes()),
                            session.getExerciseCount() + " exercises",
                            details.toString()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Failed to load history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Export workout history to CSV file.
     */
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Workout History");
        fileChooser.setSelectedFile(new java.io.File("repsync_workout_history.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            if (refreshButton != null) refreshButton.setEnabled(false);
            if (exportButton != null) exportButton.setEnabled(false);

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return reportService.exportWorkoutHistoryToCSV(currentUser.getId(), filePath);
                }

                @Override
                protected void done() {
                    if (refreshButton != null) refreshButton.setEnabled(true);
                    if (exportButton != null) exportButton.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Workout history exported to:\n" + filePath, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Failed to export. Please try again.", "Export Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Export Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * Delete the selected workout from history.
     */
    private void deleteSelectedWorkout() {
        if (historyTable == null || loadedSessions == null) return;

        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < loadedSessions.size()) {
            WorkoutSession session = loadedSessions.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this workout? This action cannot be undone.",
                "Delete Workout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (deleteButton != null) deleteButton.setEnabled(false);

                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return workoutService.deleteSession(session.getId());
                    }

                    @Override
                    protected void done() {
                        if (deleteButton != null) deleteButton.setEnabled(true);
                        try {
                            boolean success = get();
                            if (success) {
                                loadHistory();
                            } else {
                                JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Failed to delete workout.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(WorkoutHistoryPanel.this, "Error deleting workout: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a workout to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        removeAll();
        buildUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadHistory();
    }
}
