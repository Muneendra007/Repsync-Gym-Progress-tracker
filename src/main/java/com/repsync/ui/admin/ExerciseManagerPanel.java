package com.repsync.ui.admin;

import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.CardioExercise;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;
import com.repsync.model.enums.MuscleGroup;
import com.repsync.service.ExerciseService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin panel for managing exercises (add, edit, delete).
 * Only visible to admin users.
 */
public class ExerciseManagerPanel extends JPanel {

    private final ExerciseService exerciseService = new ExerciseService();
    private DefaultTableModel tableModel;
    private StyledButton addButton, deleteButton, refreshButton;

    // Input fields
    private JTextField nameField, descriptionField, defaultSetsField, defaultRepsField, defaultWeightField, defaultDurationField;
    private JComboBox<ExerciseType> typeCombo;
    private JComboBox<String> muscleGroupCombo;
    private JComboBox<Difficulty> difficultyCombo;
    private JComboBox<String> equipmentCombo;

    public ExerciseManagerPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackground());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        buildUI();
    }

    private void buildUI() {
        // Title
        JLabel title = new JLabel("⚙ Exercise Manager (Admin)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        // Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Exercise"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        typeCombo = new JComboBox<>(ExerciseType.values());
        muscleGroupCombo = new JComboBox<>(new String[]{"CHEST", "BACK", "LEGS", "SHOULDERS", "ARMS", "CORE", "CARDIO"});
        equipmentCombo = new JComboBox<>(new String[]{"BARBELL", "DUMBBELL", "MACHINE", "BODYWEIGHT", "CABLE", "OTHER"});
        difficultyCombo = new JComboBox<>(Difficulty.values());
        descriptionField = new JTextField(20);
        defaultSetsField = new JTextField("3", 4);
        defaultRepsField = new JTextField("10", 4);
        defaultWeightField = new JTextField("0", 4);
        defaultDurationField = new JTextField("0", 4);

        int row = 0;
        addFormField(formPanel, gbc, row++, "Name:", nameField);
        addFormField(formPanel, gbc, row++, "Type:", typeCombo);
        addFormField(formPanel, gbc, row++, "Muscle Group:", muscleGroupCombo);
        addFormField(formPanel, gbc, row++, "Equipment:", equipmentCombo);
        addFormField(formPanel, gbc, row++, "Difficulty:", difficultyCombo);
        addFormField(formPanel, gbc, row++, "Description:", descriptionField);
        addFormField(formPanel, gbc, row++, "Default Sets:", defaultSetsField);
        addFormField(formPanel, gbc, row++, "Default Reps:", defaultRepsField);
        addFormField(formPanel, gbc, row++, "Default Weight (kg):", defaultWeightField);
        addFormField(formPanel, gbc, row++, "Default Duration (s):", defaultDurationField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setOpaque(false);

        addButton = new StyledButton("Add Exercise", ThemeManager.ACCENT_GREEN);
        addButton.addActionListener(e -> addExercise());

        deleteButton = new StyledButton("Delete Selected", ThemeManager.ACCENT_RED);
        deleteButton.addActionListener(e -> deleteExercise());

        refreshButton = new StyledButton("Refresh", ThemeManager.ACCENT_BLUE);
        refreshButton.addActionListener(e -> loadExercises());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Exercise table
        String[] columns = {"ID", "Name", "Type", "Muscle Group", "Equipment", "Difficulty"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row1, int column) {
                return false;
            }
        };

        JTable exerciseTable = new JTable(tableModel);
        exerciseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        exerciseTable.setRowHeight(36);
        exerciseTable.setShowGrid(false);
        exerciseTable.setIntercellSpacing(new Dimension(0, 0));
        exerciseTable.setBackground(ThemeManager.getCardBackground());
        exerciseTable.setForeground(ThemeManager.getTextColor());

        // Custom High-Contrast Table Header Renderer
        exerciseTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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
        exerciseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(exerciseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getDividerColor(), 1, true));

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadExercises();
    }

    private void addExercise() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Exercise name cannot be empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExerciseType type = (ExerciseType) typeCombo.getSelectedItem();
        String muscleGroup = (String) muscleGroupCombo.getSelectedItem();
        String equipment = (String) equipmentCombo.getSelectedItem();
        Difficulty difficulty = (Difficulty) difficultyCombo.getSelectedItem();
        String description = descriptionField.getText().trim();

        Exercise exercise;
        if (type == ExerciseType.STRENGTH) {
            StrengthExercise se = new StrengthExercise();
            se.setDefaultSets(InputValidator.parseIntSafe(defaultSetsField.getText()));
            se.setDefaultReps(InputValidator.parseIntSafe(defaultRepsField.getText()));
            se.setDefaultWeight(InputValidator.parseDoubleSafe(defaultWeightField.getText()));
            exercise = se;
        } else {
            CardioExercise ce = new CardioExercise();
            ce.setDefaultDurationSeconds(InputValidator.parseIntSafe(defaultDurationField.getText()));
            exercise = ce;
        }

        exercise.setName(name);
        exercise.setExerciseType(type);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setDifficulty(difficulty);
        exercise.setDescription(description);

        if (addButton != null) addButton.setEnabled(false);

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return exerciseService.addExercise(exercise);
            }

            @Override
            protected void done() {
                if (addButton != null) addButton.setEnabled(true);
                try {
                    int id = get();
                    if (id > 0) {
                        JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Exercise added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadExercises();
                    } else {
                        JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Failed to add exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteExercise() {
        // Get selected row from table
        // For simplicity, ask for exercise ID
        String idStr = JOptionPane.showInputDialog(this, "Enter exercise ID to delete:");
        if (idStr != null && !idStr.isEmpty()) {
            int id = InputValidator.parseIntSafe(idStr);
            if (id > 0) {
                if (deleteButton != null) deleteButton.setEnabled(false);
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return exerciseService.deleteExercise(id);
                    }

                    @Override
                    protected void done() {
                        if (deleteButton != null) deleteButton.setEnabled(true);
                        try {
                            boolean success = get();
                            if (success) {
                                JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Exercise deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                loadExercises();
                            } else {
                                JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Failed to delete exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        }
    }

    private void loadExercises() {
        if (refreshButton != null) refreshButton.setEnabled(false);
        tableModel.setRowCount(0);

        SwingWorker<List<Exercise>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Exercise> doInBackground() throws Exception {
                return exerciseService.getAllExercises();
            }

            @Override
            protected void done() {
                if (refreshButton != null) refreshButton.setEnabled(true);
                try {
                    List<Exercise> exercises = get();
                    for (Exercise ex : exercises) {
                        tableModel.addRow(new Object[]{
                            ex.getId(),
                            ex.getName(),
                            ex.getExerciseType().name(),
                            ex.getMuscleGroup(),
                            ex.getEquipment(),
                            ex.getDifficulty() != null ? ex.getDifficulty().getDisplayName() : "N/A"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ExerciseManagerPanel.this, "Failed to load exercises.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        nameField.setText("");
        descriptionField.setText("");
        defaultSetsField.setText("3");
        defaultRepsField.setText("10");
        defaultWeightField.setText("0");
        defaultDurationField.setText("0");
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTextColor());

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
}
