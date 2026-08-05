package com.repsync.ui.workout;

import com.repsync.model.CardioExercise;
import com.repsync.model.Exercise;
import com.repsync.model.PersonalRecord;
import com.repsync.model.StrengthExercise;
import com.repsync.model.User;
import com.repsync.model.WorkoutExercise;
import com.repsync.model.WorkoutSession;
import com.repsync.service.ExerciseService;
import com.repsync.service.PRService;
import com.repsync.service.WorkoutService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.RestTimerDialog;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Workout logger panel - log a new workout session with exercises.
 * Pick exercises, enter sets/reps/weight, save the session.
 * Premium design with section headers and styled table.
 */
public class WorkoutLoggerPanel extends JPanel {

    private User currentUser;
    private final ExerciseService exerciseService = new ExerciseService();
    private final WorkoutService workoutService = new WorkoutService();
    private final PRService prService = new PRService();

    private JComboBox<Exercise> exerciseCombo;
    private JTextField setsField, repsField, weightField, durationField;
    private JTextField durationMinutesField, notesField;
    private DefaultTableModel tableModel;
    private JTable logTable;
    private List<WorkoutExercise> loggedExercises = new ArrayList<>();
    private JLabel summaryLabel;
    private StyledButton saveButton;

    public WorkoutLoggerPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackground());
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
    }

    private void buildUI() {
        removeAll();
        JLabel loadingLabel = new JLabel("Loading Exercises...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loadingLabel.setForeground(ThemeManager.getTextColor());
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loadingLabel, BorderLayout.CENTER);
        revalidate();
        repaint();

        SwingWorker<List<Exercise>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Exercise> doInBackground() throws Exception {
                return exerciseService.getAllExercises();
            }

            @Override
            protected void done() {
                try {
                    List<Exercise> allExercises = get();
                    buildMainUI(allExercises);
                } catch (Exception e) {
                    removeAll();
                    JLabel errorLabel = new JLabel("Error loading exercises: " + e.getMessage());
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

    private void buildMainUI(List<Exercise> allExercises) {
        removeAll();

        // Section header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Log Workout Session");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel("Add exercises, set reps & weight, then save your session");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);

        // Session info panel
        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        sessionPanel.setOpaque(false);

        sessionPanel.add(createLabel("Duration (min):"));
        durationMinutesField = new JTextField("45", 5);
        styleInputField(durationMinutesField);
        sessionPanel.add(durationMinutesField);

        sessionPanel.add(createLabel("Session Notes:"));
        notesField = new JTextField(20);
        styleInputField(notesField);
        sessionPanel.add(notesField);

        // Exercise input panel - 2 clean rows so all fields and buttons fit comfortably
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        exerciseCombo = new JComboBox<>(allExercises.toArray(new Exercise[0]));
        exerciseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exerciseCombo.setPreferredSize(new Dimension(240, 34));
        exerciseCombo.setBackground(ThemeManager.getInputBackground());
        exerciseCombo.setForeground(ThemeManager.getInputTextColor());
        exerciseCombo.addActionListener(e -> fillExerciseDefaults());

        setsField = new JTextField("3", 3);
        repsField = new JTextField("10", 3);
        weightField = new JTextField("0", 5);
        durationField = new JTextField("0", 5);

        styleInputField(setsField);
        styleInputField(repsField);
        styleInputField(weightField);
        styleInputField(durationField);

        // Row 1: Exercise Selection + Sets + Reps + Weight + Duration
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row1.setOpaque(false);
        row1.add(createLabel("Exercise:"));
        row1.add(exerciseCombo);
        row1.add(createLabel("Sets:"));
        row1.add(setsField);
        row1.add(createLabel("Reps:"));
        row1.add(repsField);
        row1.add(createLabel("Weight (kg):"));
        row1.add(weightField);
        row1.add(createLabel("Duration (s):"));
        row1.add(durationField);

        // Row 2: Action Buttons (+ Add Exercise & Rest Timer)
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row2.setOpaque(false);

        StyledButton addButton = new StyledButton("+ Add Exercise to Log", ThemeManager.ACCENT_BLUE);
        addButton.setPreferredSize(new Dimension(180, 36));
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.addActionListener(e -> addExerciseToLog());

        StyledButton restTimerButton = new StyledButton("⏱ Rest Timer (60s)", ThemeManager.ACCENT_PURPLE);
        restTimerButton.setPreferredSize(new Dimension(160, 36));
        restTimerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        StyledButton guideButton = new StyledButton("📖 View Form Guide", ThemeManager.ACCENT_COLOR);
        guideButton.setPreferredSize(new Dimension(160, 36));
        guideButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        guideButton.addActionListener(e -> {
            Exercise selected = (Exercise) exerciseCombo.getSelectedItem();
            if (selected != null) {
                ExerciseGuideDialog dialog = new ExerciseGuideDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), selected);
                dialog.setVisible(true);
            }
        });

        row2.add(addButton);
        row2.add(restTimerButton);
        row2.add(guideButton);

        inputPanel.add(row1);
        inputPanel.add(Box.createVerticalStrut(6));
        inputPanel.add(row2);

        // Logged exercises table
        String[] columns = {"Exercise", "Sets", "Reps", "Weight (kg)", "Duration (s)"};
        tableModel = new DefaultTableModel(columns, 0);
        logTable = new JTable(tableModel);
        logTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logTable.setRowHeight(36);
        logTable.setShowGrid(false);
        logTable.setIntercellSpacing(new Dimension(0, 0));
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setBackground(ThemeManager.getCardBackground());
        logTable.setForeground(ThemeManager.getTextColor());

        // Custom High-Contrast Table Header Renderer
        logTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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
        logTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true));

        // Summary Label
        summaryLabel = new JLabel("Exercises: 0 | Total Volume: 0 kg");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryLabel.setForeground(ThemeManager.ACCENT_BLUE);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        summaryPanel.setOpaque(false);
        summaryPanel.add(summaryLabel);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        buttonPanel.setOpaque(false);

        StyledButton removeButton = new StyledButton("Remove", ThemeManager.ACCENT_ORANGE);
        removeButton.setPreferredSize(new Dimension(100, 36));
        removeButton.addActionListener(e -> removeSelectedExercise());

        StyledButton clearButton = new StyledButton("Clear All", ThemeManager.ACCENT_RED);
        clearButton.setPreferredSize(new Dimension(100, 36));
        clearButton.addActionListener(e -> clearLog());

        saveButton = new StyledButton("✔ Save Workout", ThemeManager.ACCENT_GREEN);
        saveButton.setPreferredSize(new Dimension(160, 36));
        saveButton.addActionListener(e -> saveWorkout());

        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(sessionPanel, BorderLayout.CENTER);
        topPanel.add(inputPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(summaryPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        // Fill defaults for initially selected exercise
        if (exerciseCombo.getItemCount() > 0) {
            fillExerciseDefaults();
        }
    }

    /**
     * Auto-fill default values based on selected exercise type.
     */
    private void fillExerciseDefaults() {
        Exercise selected = (Exercise) exerciseCombo.getSelectedItem();
        if (selected == null) return;

        if (selected instanceof StrengthExercise) {
            StrengthExercise se = (StrengthExercise) selected;
            setsField.setText(String.valueOf(se.getDefaultSets()));
            repsField.setText(String.valueOf(se.getDefaultReps()));
            weightField.setText(String.valueOf(se.getDefaultWeight()));
            durationField.setText("0");
        } else if (selected instanceof CardioExercise) {
            CardioExercise ce = (CardioExercise) selected;
            setsField.setText("1");
            repsField.setText("1");
            weightField.setText("0");
            durationField.setText(String.valueOf(ce.getDefaultDurationSeconds()));
        } else {
            setsField.setText("3");
            repsField.setText("10");
            weightField.setText("0");
            durationField.setText("0");
        }
    }

    /**
     * Add an exercise entry to the log table.
     */
    private void addExerciseToLog() {
        Exercise selectedExercise = (Exercise) exerciseCombo.getSelectedItem();
        if (selectedExercise == null) return;

        int sets = InputValidator.parseIntSafe(setsField.getText());
        int reps = InputValidator.parseIntSafe(repsField.getText());
        double weight = InputValidator.parseDoubleSafe(weightField.getText());
        int duration = InputValidator.parseIntSafe(durationField.getText());

        if (sets <= 0) sets = 3;
        if (reps <= 0) reps = 10;
        if (weight < 0) weight = 0;
        if (duration < 0) duration = 0;

        WorkoutExercise we = new WorkoutExercise(selectedExercise.getId(), sets, reps, weight);
        we.setDurationSeconds(duration);
        we.setExerciseName(selectedExercise.getName());
        loggedExercises.add(we);

        tableModel.addRow(new Object[]{
            selectedExercise.getName(), sets, reps, weight, duration
        });

        updateSummary();
    }

    /**
     * Remove the currently selected exercise from the log table.
     */
    private void removeSelectedExercise() {
        int selectedRow = logTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < loggedExercises.size()) {
            loggedExercises.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            updateSummary();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an exercise row to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Save the complete workout session.
     */
    private void saveWorkout() {
        if (loggedExercises.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one exercise!", "Empty Workout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int durationMinutes = InputValidator.parseIntSafe(durationMinutesField.getText());
        if (durationMinutes <= 0) durationMinutes = 30;

        WorkoutSession session = new WorkoutSession(currentUser.getId(), LocalDate.now(), durationMinutes);
        session.setNotes(notesField.getText());
        session.setWorkoutExercises(new ArrayList<>(loggedExercises));

        saveButton.setEnabled(false);

        SwingWorker<List<PersonalRecord>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PersonalRecord> doInBackground() throws Exception {
                int sessionId = workoutService.logWorkout(session);
                if (sessionId <= 0) {
                    throw new Exception("Failed to save workout. Database returned 0.");
                }

                List<PersonalRecord> newPRs = new ArrayList<>();
                for (WorkoutExercise we : session.getWorkoutExercises()) {
                    newPRs.addAll(prService.checkAndSavePRs(currentUser.getId(), we));
                }
                return newPRs;
            }

            @Override
            protected void done() {
                saveButton.setEnabled(true);
                try {
                    List<PersonalRecord> newPRs = get();

                    // Show success message
                    StringBuilder message = new StringBuilder("Workout saved successfully!");
                    if (!newPRs.isEmpty()) {
                        message.append("\n\nNEW PERSONAL RECORDS:");
                        for (PersonalRecord pr : newPRs) {
                            message.append("\n  \u2022 ").append(pr.getExerciseName())
                                   .append(": ").append(pr.getFormattedValue());
                        }
                    }

                    JOptionPane.showMessageDialog(WorkoutLoggerPanel.this, message.toString(), "Workout Saved", JOptionPane.INFORMATION_MESSAGE);
                    clearLog();
                } catch (Exception e) {
                    String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(WorkoutLoggerPanel.this, "Failed to save workout: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Clear the logged exercises.
     */
    private void clearLog() {
        loggedExercises.clear();
        tableModel.setRowCount(0);
        updateSummary();
    }

    /**
     * Update the live summary label.
     */
    private void updateSummary() {
        if (summaryLabel == null) return;

        double totalVolume = 0;
        for (WorkoutExercise we : loggedExercises) {
            totalVolume += (we.getSets() * we.getReps() * we.getWeightKg());
        }

        summaryLabel.setText(String.format("Exercises: %d | Total Volume: %.1f kg", loggedExercises.size(), totalVolume));
    }

    /**
     * Pre-fill the logger with a list of exercises (e.g. from a generated plan).
     */
    public void prefillExercises(List<Exercise> exercisesToPrefill) {
        if (exercisesToPrefill == null || exercisesToPrefill.isEmpty()) return;

        clearLog();

        for (Exercise ex : exercisesToPrefill) {
            int sets = 3;
            int reps = 10;
            double weight = 0;
            int duration = 0;

            if (ex instanceof StrengthExercise) {
                StrengthExercise se = (StrengthExercise) ex;
                sets = se.getDefaultSets();
                reps = se.getDefaultReps();
                weight = se.getDefaultWeight();
            } else if (ex instanceof CardioExercise) {
                CardioExercise ce = (CardioExercise) ex;
                sets = 1;
                reps = 1;
                duration = ce.getDefaultDurationSeconds();
            }

            WorkoutExercise we = new WorkoutExercise(ex.getId(), sets, reps, weight);
            we.setDurationSeconds(duration);
            we.setExerciseName(ex.getName());
            loggedExercises.add(we);

            tableModel.addRow(new Object[]{
                ex.getName(), sets, reps, weight, duration
            });
        }

        updateSummary();
    }

    private void styleInputField(JTextField field) {
        field.setBackground(ThemeManager.getInputBackground());
        field.setForeground(ThemeManager.getInputTextColor());
        field.setCaretColor(ThemeManager.ACCENT_BLUE);
        field.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        buildUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
