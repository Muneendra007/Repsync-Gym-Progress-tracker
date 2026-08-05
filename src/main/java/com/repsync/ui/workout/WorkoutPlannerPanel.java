package com.repsync.ui.workout;

import com.repsync.dao.ExerciseDAO;
import com.repsync.model.CardioExercise;
import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.User;
import com.repsync.model.enums.ExerciseType;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.service.WorkoutPlanService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.util.ExerciseGuideCatalog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Workout planner panel — generates workout plans based on fitness goal and day
 * of the week.
 * Features visual goal selection cards, an interactive weekly split calendar
 * where any day
 * can be selected, and accurate daily recommendations (only exercises matching
 * that day's split).
 * Each workout name and exercise is listed with a visual picture/emoji badge.
 */
public class WorkoutPlannerPanel extends JPanel {

    private User currentUser;
    private final WorkoutPlanService planService = new WorkoutPlanService();
    private final ExerciseDAO exerciseDAO = new ExerciseDAO();
    private JPanel planResultPanel;
    private FitnessGoal selectedGoal;
    private int selectedDayIndex; // 0=Mon .. 6=Sun
    private JLabel selectedInfoLabel;
    private JLabel detailLabel;
    private StyledButton generateButton;
    private JPanel dayStripPanel;

    // Callback to MainFrame
    private java.util.function.Consumer<List<Exercise>> onStartWorkout;

    // Daily split data per goal: [dayIndex 0=Mon..6=Sun] -> {Workout Name,
    // Description, Pic/Icon}
    private static final String[][] STRENGTH_SPLIT = {
            { "Chest + Triceps", "Bench Press, Incline DB Press, Tricep Dips, Push Ups", "🫁💪" },
            { "Back + Biceps", "Deadlift, Barbell Row, Pull Ups, Barbell Curl", "🦍💪" },
            { "Legs", "Barbell Squat, Leg Press, Romanian Deadlift, Lunges", "🦵🔥" },
            { "Shoulders + Arms", "Overhead Press, Lateral Raises, Barbell Curl, Hammer Curls", "🛡️💪" },
            { "Full Body Power", "Barbell Squat, Bench Press, Barbell Row, Overhead Press, Deadlift", "⚡🏋️" },
            { "Active Recovery", "Plank, Cable Crunches, light stretching", "🧘🏃" },
            { "Rest Day", "Full rest day — let your muscles recover and grow", "💤🧘" }
    };

    private static final String[][] FAT_LOSS_SPLIT = {
            { "Full Body Circuit", "Burpees, Push Ups, Barbell Squat, Mountain Climbers, Jump Rope", "⚡🔥" },
            { "Cardio + Core", "Treadmill Running, Cycling, Plank, Cable Crunches", "🏃🎯" },
            { "Upper Body + Cardio", "Push Ups, Lat Pulldown, Lateral Raises, Jump Rope", "🫁🏃" },
            { "Lower Body + Cardio", "Lunges, Leg Press, Romanian Deadlift, Cycling", "🦵🏃" },
            { "Full Body HIIT", "Burpees, Mountain Climbers, Jump Rope, Push Ups", "🔥⚡" },
            { "Active Recovery", "Cycling, Plank, gentle walking", "🧘🏃" },
            { "Rest Day", "Full rest day — let your body recover", "💤🧘" }
    };

    private static final String[][] MUSCLE_GAIN_SPLIT = {
            { "Chest", "Bench Press, Incline DB Press, Push Ups, Tricep Dips", "🫁💎" },
            { "Back", "Deadlift, Barbell Row, Pull Ups, Lat Pulldown", "🦍💎" },
            { "Legs", "Barbell Squat, Leg Press, Romanian Deadlift, Lunges", "🦵💎" },
            { "Shoulders + Arms", "Overhead Press, Lateral Raises, Barbell Curl, Hammer Curls", "🛡️💪" },
            { "Chest + Back", "Bench Press, Barbell Row, Incline DB Press, Lat Pulldown", "🫁🦍" },
            { "Legs + Core", "Barbell Squat, Romanian Deadlift, Plank, Cable Crunches", "🦵🎯" },
            { "Rest Day", "Full rest day — muscles grow during recovery", "💤🧘" }
    };

    private static final String[][] ENDURANCE_SPLIT = {
            { "Legs + Cardio", "Barbell Squat, Lunges, Treadmill Running, Cycling", "🦵🏃" },
            { "Upper Body + Cardio", "Push Ups, Lat Pulldown, Overhead Press, Jump Rope", "💪🏃" },
            { "Cardio Only", "Treadmill Running, Cycling, Jump Rope, Mountain Climbers", "🏃🔥" },
            { "Full Body", "Barbell Squat, Push Ups, Barbell Row, Burpees", "⚡🏃" },
            { "Cardio + Core", "Treadmill Running, Plank, Cable Crunches, Cycling", "🏃🎯" },
            { "Active Recovery", "Cycling, Jump Rope, light stretching", "🧘🏃" },
            { "Rest Day", "Full rest day — recharge for next week", "💤🧘" }
    };

    // Goal card info: icon, title, description, accent color
    private static final Object[][] GOAL_INFO = {
            { FitnessGoal.STRENGTH, "💪", "Build Strength",
                    "Heavy lifts, low reps (5×5). Focus on compound movements like Squat, Bench, Deadlift, and OHP.",
                    ThemeManager.ACCENT_BLUE },
            { FitnessGoal.FAT_LOSS, "🔥", "Lose Fat",
                    "Circuit training with cardio. High reps (3×15), lighter weight, maximum calorie burn.",
                    ThemeManager.ACCENT_RED },
            { FitnessGoal.MUSCLE_GAIN, "💎", "Gain Muscle",
                    "Hypertrophy training. 4×10, high volume, multiple exercises per muscle group.",
                    ThemeManager.ACCENT_GREEN },
            { FitnessGoal.ENDURANCE, "🏃", "Build Endurance",
                    "High-rep training (3×20) with extended cardio. Build stamina and cardiovascular health.",
                    ThemeManager.ACCENT_ORANGE },
    };

    public WorkoutPlannerPanel(User user) {
        this.currentUser = user;
        if (currentUser.getFitnessGoal() != null) {
            this.selectedGoal = currentUser.getFitnessGoal();
        } else {
            this.selectedGoal = FitnessGoal.STRENGTH;
        }
        // Initialize selected day to today
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        this.selectedDayIndex = today.getValue() - 1; // 0=Mon .. 6=Sun

        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackground());
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
        // Auto-generate plan for selected goal and day on initial open
        SwingUtilities.invokeLater(() -> generatePlanForSelectedDay());
    }

    private void buildUI() {
        removeAll();

        // Use a Scrollable panel so the content tracks the viewport width
        JPanel contentPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension pref = super.getPreferredSize();
                // If inside a viewport, constrain width to viewport width
                if (getParent() instanceof JViewport viewport) {
                    pref.width = viewport.getWidth();
                }
                return pref;
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // ─── SECTION HEADER ───
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("📋  Workout Planner & Daily Split Guide");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel(
                "Select any day in the weekly split to view its workout name, picture icons, and exercises");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(subtitle);

        headerPanel.add(headerText, BorderLayout.CENTER);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        // ─── GOAL SELECTION CARDS (Horizontal Scrollable Strip) ───
        JPanel goalCardsStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        goalCardsStrip.setOpaque(false);

        for (Object[] info : GOAL_INFO) {
            FitnessGoal goal = (FitnessGoal) info[0];
            String icon = (String) info[1];
            String name = (String) info[2];
            String desc = (String) info[3];
            Color accent = (Color) info[4];
            JPanel card = createGoalCard(goal, icon, name, desc, accent);
            card.setPreferredSize(new Dimension(215, 130));
            goalCardsStrip.add(card);
        }

        JScrollPane goalScroll = new JScrollPane(goalCardsStrip);
        goalScroll.setOpaque(false);
        goalScroll.getViewport().setOpaque(false);
        goalScroll.setBorder(null);
        goalScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        goalScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        goalScroll.getHorizontalScrollBar().setUnitIncrement(16);
        goalScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
        goalScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(goalScroll);
        contentPanel.add(Box.createVerticalStrut(18));

        // ─── WEEKLY SPLIT CALENDAR (Interactive) ───
        JPanel weeklyCalPanel = createWeeklyCalendar();
        contentPanel.add(weeklyCalPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        // ─── GENERATE BUTTON ───
        JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        generatePanel.setOpaque(false);
        generatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        generatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] dayLabels = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        String[][] currentSplit = getSplitForGoal(selectedGoal);
        String currentDayName = dayLabels[selectedDayIndex];
        String currentWorkoutName = currentSplit[selectedDayIndex][0];

        generateButton = new StyledButton(
                "⚡ Generate Plan for " + currentDayName + " (" + currentWorkoutName + ")", ThemeManager.ACCENT_BLUE);
        generateButton.setPreferredSize(new Dimension(380, 42));
        generateButton.addActionListener(e -> generatePlanForSelectedDay());
        generatePanel.add(generateButton);

        contentPanel.add(generatePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // ─── RESULTS AREA ───
        planResultPanel = new JPanel();
        planResultPanel.setLayout(new BoxLayout(planResultPanel, BoxLayout.Y_AXIS));
        planResultPanel.setOpaque(false);
        planResultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel placeholderLabel = new JLabel("⚡  Generating your customized workout plan...");
        placeholderLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        placeholderLabel.setForeground(ThemeManager.getSecondaryTextColor());
        planResultPanel.add(placeholderLabel);

        contentPanel.add(planResultPanel);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Re-layout content when viewport is resized so cards fill available width
        mainScroll.getViewport().addChangeListener(e -> {
            contentPanel.revalidate();
        });

        add(mainScroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Create a goal selection card.
     */
    private JPanel createGoalCard(FitnessGoal goal, String icon, String name, String desc, Color accent) {
        JPanel card = new JPanel() {
            private boolean hovering = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovering = false;
                        repaint();
                    }

                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        selectedGoal = goal;
                        buildUI();
                        generatePlanForSelectedDay();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                boolean isSelected = (goal == selectedGoal);

                if (isSelected) {
                    g2.setColor(ThemeManager.withAlpha(accent, 20));
                } else {
                    g2.setColor(hovering ? ThemeManager.getCardHoverBackground() : ThemeManager.getCardBackground());
                }
                g2.fillRoundRect(0, 0, w, h, 14, 14);

                if (isSelected) {
                    g2.setColor(accent);
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(hovering ? ThemeManager.withAlpha(accent, 60) : new Color(60, 60, 80, 40));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

                if (isSelected) {
                    g2.setColor(accent);
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, 14, 14, 14));
                    g2.fillRect(0, 0, w, 4);
                    g2.setClip(null);
                }

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(accent);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><p style='width:120px'>" + desc + "</p></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(ThemeManager.getSecondaryTextColor());
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);

        return card;
    }

    /**
     * Create the weekly split calendar strip.
     */
    private JPanel createWeeklyCalendar() {
        String[][] split = getSplitForGoal(selectedGoal);
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayIndex = today.getValue() - 1; // 0=Mon .. 6=Sun

        JPanel calPanel = new JPanel() {
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
        calPanel.setLayout(new BoxLayout(calPanel, BoxLayout.Y_AXIS));
        calPanel.setOpaque(false);
        calPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        calPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        calPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title
        JLabel calTitle = new JLabel(
                "📅  Weekly Split — " + getGoalDisplayName(selectedGoal) + "  (Click any day to view its workout)");
        calTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        calTitle.setForeground(ThemeManager.getTextColor());
        calTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] fullDayNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        selectedInfoLabel = new JLabel("Selected Day: " + fullDayNames[selectedDayIndex] + " → " +
                split[selectedDayIndex][2] + "  " + split[selectedDayIndex][0] +
                (selectedDayIndex == todayIndex ? "  (Today)" : ""));
        selectedInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedInfoLabel.setForeground(ThemeManager.ACCENT_BLUE);
        selectedInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        calPanel.add(calTitle);
        calPanel.add(Box.createVerticalStrut(6));
        calPanel.add(selectedInfoLabel);
        calPanel.add(Box.createVerticalStrut(12));

        // Day strip with horizontal scrolling left-to-right
        dayStripPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dayStripPanel.setOpaque(false);

        String[] dayLabels = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (int i = 0; i < 7; i++) {
            boolean isToday = (i == todayIndex);
            boolean isRest = split[i][0].toLowerCase().contains("rest");
            JPanel cell = createDayCell(i, dayLabels[i], split[i][0], split[i][2], isToday, isRest);
            cell.setPreferredSize(new Dimension(135, 76));
            dayStripPanel.add(cell);
        }

        JScrollPane dayScroll = new JScrollPane(dayStripPanel);
        dayScroll.setOpaque(false);
        dayScroll.getViewport().setOpaque(false);
        dayScroll.setBorder(null);
        dayScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dayScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        dayScroll.getHorizontalScrollBar().setUnitIncrement(16);
        dayScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        dayScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        calPanel.add(dayScroll);

        // Today's detail
        calPanel.add(Box.createVerticalStrut(10));
        detailLabel = new JLabel("📝  Exercises: " + split[selectedDayIndex][1]);
        detailLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        detailLabel.setForeground(ThemeManager.getSecondaryTextColor());
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        calPanel.add(detailLabel);

        return calPanel;
    }

    private void selectDay(int dayIndex) {
        this.selectedDayIndex = dayIndex;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayIndex = today.getValue() - 1;
        String[] fullDayNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        String[][] split = getSplitForGoal(selectedGoal);

        if (selectedInfoLabel != null) {
            selectedInfoLabel.setText("Selected Day: " + fullDayNames[selectedDayIndex] + " → " +
                    split[selectedDayIndex][2] + "  " + split[selectedDayIndex][0] +
                    (selectedDayIndex == todayIndex ? "  (Today)" : ""));
        }
        if (detailLabel != null) {
            detailLabel.setText("📝  Exercises: " + split[selectedDayIndex][1]);
        }
        if (generateButton != null) {
            generateButton.setText("⚡ Generate Plan for " + fullDayNames[selectedDayIndex] + " (" + split[selectedDayIndex][0] + ")");
        }
        if (dayStripPanel != null) {
            dayStripPanel.repaint();
        }
        generatePlanForSelectedDay();
    }

    /**
     * Create a single day cell for the weekly calendar.
     */
    private JPanel createDayCell(int dayIndex, String dayName, String splitName, String iconPic,
            boolean isToday, boolean isRest) {
        JPanel cell = new JPanel() {
            private boolean hovering = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovering = false;
                        repaint();
                    }

                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        selectDay(dayIndex);
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                boolean currentlySelected = (dayIndex == selectedDayIndex);

                if (currentlySelected) {
                    g2.setColor(ThemeManager.withAlpha(ThemeManager.ACCENT_BLUE, 30));
                    g2.fillRoundRect(0, 0, w, h, 10, 10);
                    g2.setColor(ThemeManager.ACCENT_BLUE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                } else {
                    g2.setColor(hovering ? ThemeManager.getCardHoverBackground()
                            : (rowColor(dayIndex) ? ThemeManager.getCardBackground()
                                    : ThemeManager.getTableAlternateRow()));
                    g2.fillRoundRect(0, 0, w, h, 10, 10);
                    if (isToday) {
                        g2.setColor(ThemeManager.withAlpha(ThemeManager.ACCENT_BLUE, 60));
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                    }
                }

                g2.dispose();
            }

            private boolean rowColor(int idx) {
                return idx % 2 == 0;
            }
        };
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setOpaque(false);
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cell.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        boolean currentlySelected = (dayIndex == selectedDayIndex);

        String labelText = dayName + (isToday ? " •" : "");
        JLabel dayLbl = new JLabel(labelText, SwingConstants.CENTER);
        dayLbl.setFont(new Font("Segoe UI", currentlySelected || isToday ? Font.BOLD : Font.PLAIN, 11));
        dayLbl.setForeground(currentlySelected || isToday ? ThemeManager.ACCENT_BLUE : ThemeManager.getSecondaryTextColor());
        dayLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLbl = new JLabel(iconPic, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        String cleanSplit = splitName.replace(" + ", "+");
        if (cleanSplit.length() > 14) {
            cleanSplit = cleanSplit.substring(0, 13);
        }
        JLabel splitLbl = new JLabel(cleanSplit, SwingConstants.CENTER);
        splitLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        splitLbl.setForeground(isRest ? ThemeManager.getSecondaryTextColor()
                : currentlySelected ? ThemeManager.ACCENT_BLUE : ThemeManager.getTextColor());
        splitLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        cell.add(dayLbl);
        cell.add(Box.createVerticalStrut(3));
        cell.add(iconLbl);
        cell.add(Box.createVerticalStrut(3));
        cell.add(splitLbl);

        return cell;
    }

    /**
     * Get the split schedule for a given fitness goal.
     */
    private String[][] getSplitForGoal(FitnessGoal goal) {
        if (goal == null)
            return STRENGTH_SPLIT;
        return switch (goal) {
            case STRENGTH -> STRENGTH_SPLIT;
            case FAT_LOSS -> FAT_LOSS_SPLIT;
            case MUSCLE_GAIN -> MUSCLE_GAIN_SPLIT;
            case ENDURANCE -> ENDURANCE_SPLIT;
        };
    }

    private String getGoalDisplayName(FitnessGoal goal) {
        if (goal == null)
            return "Strength";
        return goal.getDisplayName();
    }

    /**
     * Generate a workout plan for the currently selected goal and day of the week.
     * Ensures only exercises belonging to that specific day's split are generated.
     */
    private void generatePlanForSelectedDay() {
        if (selectedGoal == null)
            return;

        planResultPanel.removeAll();
        JLabel loadingLabel = new JLabel(
                "⚡  Generating workout plan for " + getSplitForGoal(selectedGoal)[selectedDayIndex][0] + "...");
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loadingLabel.setForeground(ThemeManager.getSecondaryTextColor());
        planResultPanel.add(loadingLabel);
        planResultPanel.revalidate();
        planResultPanel.repaint();

        SwingWorker<List<Exercise>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Exercise> doInBackground() throws Exception {
                List<Exercise> allExercises = exerciseDAO.findAll();
                return generateDaySplitExercises(selectedGoal, selectedDayIndex, allExercises);
            }

            @Override
            protected void done() {
                try {
                    List<Exercise> exercises = get();
                    String[][] currentSplit = getSplitForGoal(selectedGoal);
                    String workoutName = currentSplit[selectedDayIndex][0];
                    String workoutDesc = currentSplit[selectedDayIndex][1];
                    String workoutPic = currentSplit[selectedDayIndex][2];
                    String[] dayLabels = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
                            "Sunday" };
                    String fullDayName = dayLabels[selectedDayIndex];

                    planResultPanel.removeAll();

                    // If it's a Rest Day, display a special recovery card
                    if (exercises.isEmpty() || workoutName.toLowerCase().contains("rest")) {
                        planResultPanel.add(createRestDayCard(fullDayName, workoutName, workoutDesc, workoutPic));
                        planResultPanel.revalidate();
                        planResultPanel.repaint();
                        return;
                    }

                    // Result card with green accent
                    JPanel resultCard = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(ThemeManager.getCardBackground());
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                            g2.setColor(new Color(60, 60, 80, 40));
                            g2.setStroke(new BasicStroke(1f));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                            // Top accent
                            g2.setColor(ThemeManager.ACCENT_GREEN);
                            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), 14, 14, 14));
                            g2.fillRect(0, 0, getWidth(), 4);
                            g2.setClip(null);

                            g2.dispose();
                        }
                    };
                    resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
                    resultCard.setOpaque(false);
                    resultCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
                    resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                    // Header with picture badge and workout name
                    JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                    titleRow.setOpaque(false);
                    titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel picLabel = new JLabel(workoutPic);
                    picLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

                    JLabel planTitle = new JLabel(fullDayName + " — " + workoutName + " Workout");
                    planTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
                    planTitle.setForeground(ThemeManager.ACCENT_GREEN);

                    titleRow.add(picLabel);
                    titleRow.add(planTitle);

                    JLabel descLabel = new JLabel("<html><p style='width:550px'><b>" + getGoalDisplayName(selectedGoal)
                            + " Focus:</b> " + workoutDesc + "</p></html>");
                    descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    descLabel.setForeground(ThemeManager.getSecondaryTextColor());
                    descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    resultCard.add(titleRow);
                    resultCard.add(Box.createVerticalStrut(8));
                    resultCard.add(descLabel);
                    resultCard.add(Box.createVerticalStrut(12));

                    // 1. Anatomical Sub-Muscle Head Breakdown Banner
                    String anatText = ExerciseGuideCatalog.getAnatomicalOverview(workoutName);
                    JPanel anatCard = createAnatomicalBannerCard(anatText);
                    resultCard.add(anatCard);
                    resultCard.add(Box.createVerticalStrut(16));

                    // 2. Exercise table with small picture badges & Guide column
                    String[] columns = { "Exercise & Pic", "Type / Muscle Group", "Sets", "Reps / Time", "Weight (kg)",
                            "Equipment", "📖 Guide & Machine" };
                    Object[][] data = new Object[exercises.size()][7];

                    for (int i = 0; i < exercises.size(); i++) {
                        Exercise ex = exercises.get(i);
                        int sets = 3, reps = 10;
                        double weight = 0;
                        String durationOrReps = "10 reps";

                        if (ex instanceof StrengthExercise se) {
                            sets = se.getDefaultSets();
                            reps = se.getDefaultReps();
                            weight = se.getDefaultWeight();
                            durationOrReps = reps + " reps";
                        } else if (ex instanceof CardioExercise ce) {
                            sets = 1;
                            reps = 1;
                            weight = 0;
                            durationOrReps = (ce.getDefaultDurationSeconds() / 60) + " min";
                        }

                        String exPic = getExercisePic(ex.getName(), ex.getMuscleGroup());
                        String musclePic = getMuscleGroupPic(ex.getMuscleGroup());

                        data[i] = new Object[] {
                                exPic + "   " + ex.getName(),
                                musclePic + "   " + ex.getMuscleGroup(),
                                sets,
                                durationOrReps,
                                weight > 0 ? String.format("%.1f kg", weight) : "Bodyweight",
                                ex.getEquipment(),
                                "📖 View Picture & Setup"
                        };
                    }

                    JTable exerciseTable = new JTable(data, columns);
                    exerciseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    exerciseTable.setRowHeight(38);
                    exerciseTable.setShowGrid(false);
                    exerciseTable.setIntercellSpacing(new Dimension(0, 0));
                    exerciseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
                    exerciseTable.getTableHeader().setForeground(ThemeManager.getSecondaryTextColor());
                    exerciseTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    exerciseTable.getColumnModel().getColumn(1).setPreferredWidth(140);
                    exerciseTable.getColumnModel().getColumn(6).setPreferredWidth(170);

                    // Alternating row colors and emoji font support
                    exerciseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                boolean hasFocus, int row, int column) {
                            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                                    column);
                            c.setBackground(row % 2 == 0 ? ThemeManager.getCardBackground()
                                    : ThemeManager.getTableAlternateRow());
                            c.setForeground(ThemeManager.getTextColor());
                            if (column == 0 || column == 1) {
                                c.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
                            } else if (column == 6) {
                                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                                c.setForeground(ThemeManager.PRIMARY_BLUE);
                            } else {
                                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                            }
                            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                            return c;
                        }
                    });

                    // Interactive click listener to open ExerciseGuideDialog with 2D illustration
                    exerciseTable.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            int row = exerciseTable.getSelectedRow();
                            if (row >= 0 && row < exercises.size()) {
                                Exercise selectedEx = exercises.get(row);
                                ExerciseGuideDialog dialog = new ExerciseGuideDialog(
                                        (Frame) SwingUtilities.getWindowAncestor(WorkoutPlannerPanel.this),
                                        selectedEx);
                                dialog.setVisible(true);
                            }
                        }
                    });

                    JScrollPane tableScroll = new JScrollPane(exerciseTable);
                    tableScroll.setBorder(null);
                    tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
                    tableScroll.setPreferredSize(new Dimension(0, Math.min(300, exercises.size() * 38 + 30)));
                    tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

                    resultCard.add(tableScroll);

                    resultCard.add(Box.createVerticalStrut(15));
                    StyledButton startButton = new StyledButton("🏋  Start " + fullDayName + " Workout",
                            ThemeManager.ACCENT_GREEN);
                    startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    startButton.setMaximumSize(new Dimension(280, 42));
                    startButton.addActionListener(e -> {
                        if (onStartWorkout != null) {
                            onStartWorkout.accept(exercises);
                        }
                    });
                    resultCard.add(startButton);

                    // 3. Visual Exercise Guides Strip (Cards below table)
                    resultCard.add(Box.createVerticalStrut(25));
                    JPanel visualGuideCards = createVisualGuideCardsStrip(exercises);
                    resultCard.add(visualGuideCards);

                    planResultPanel.add(resultCard);
                    planResultPanel.revalidate();
                    planResultPanel.repaint();

                } catch (Exception e) {
                    planResultPanel.removeAll();
                    JLabel errorLabel = new JLabel("❌  Error generating plan: " + e.getMessage());
                    errorLabel.setForeground(ThemeManager.ACCENT_RED);
                    planResultPanel.add(errorLabel);
                    planResultPanel.revalidate();
                    planResultPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    /**
     * Create a special Rest & Recovery Card when a rest day is selected.
     */
    private JPanel createRestDayCard(String dayName, String workoutName, String desc, String pic) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(ThemeManager.withAlpha(ThemeManager.ACCENT_PURPLE, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.setColor(ThemeManager.ACCENT_PURPLE);
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), 14, 14, 14));
                g2.fillRect(0, 0, getWidth(), 4);
                g2.setClip(null);

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel picLabel = new JLabel(pic);
        picLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel titleLabel = new JLabel(dayName + " — " + workoutName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ThemeManager.ACCENT_PURPLE);

        titleRow.add(picLabel);
        titleRow.add(titleLabel);

        JLabel subLabel = new JLabel("<html><p style='width:500px'><b>Recovery Focus:</b> " + desc + "</p></html>");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(ThemeManager.getTextColor());
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel adviceLabel = new JLabel("<html><p style='width:500px; margin-top:10px; color:" + ThemeManager.getHtmlSecondaryTextColorHex() + "'>" +
                "Muscles grow and repair during rest! Essential recovery checklist:<br>" +
                "• <b>Hydration:</b> Drink at least 2.5–3.5 liters of water today<br>" +
                "• <b>Nutrition:</b> Meet your protein target to support tissue repair<br>" +
                "• <b>Sleep:</b> Aim for 7–9 hours of quality sleep tonight<br>" +
                "• <b>Active Recovery (Optional):</b> 15–30 min light walking, yoga, or stretching</p></html>");
        adviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        adviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleRow);
        card.add(Box.createVerticalStrut(10));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(12));
        return card;
    }

    private JPanel createAnatomicalBannerCard(String htmlContent) {
        JPanel card = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.withAlpha(ThemeManager.PRIMARY_BLUE, 35));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(ThemeManager.withAlpha(ThemeManager.PRIMARY_BLUE, 100));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel titleLbl = new JLabel("🔬 Anatomical Muscle Heads & Biomechanical Target Today:");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(ThemeManager.PRIMARY_BLUE);

        JLabel descLbl = new JLabel(htmlContent);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(ThemeManager.getTextColor());

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createVisualGuideCardsStrip(List<Exercise> exercises) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headerLbl = new JLabel(
                "🏋️ Step-by-Step Biomechanical & Machine Guides (Click '📖 View Picture & Guide' to see 2D Illustration):");
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLbl.setForeground(ThemeManager.ACCENT_COLOR);
        headerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(headerLbl);
        container.add(Box.createVerticalStrut(10));

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 14, 14));
        gridPanel.setOpaque(false);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Exercise ex : exercises) {
            JPanel exCard = new JPanel(new BorderLayout(10, 8)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.getCardBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.setColor(ThemeManager.getBorderColor());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.dispose();
                }
            };
            exCard.setOpaque(false);
            exCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

            String exPic = getExercisePic(ex.getName(), ex.getMuscleGroup());
            JLabel nameLbl = new JLabel(exPic + "  " + ex.getName());
            nameLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            nameLbl.setForeground(ThemeManager.getTextColor());

            JLabel detailsLbl = new JLabel("<html><div style='line-height:1.3; font-size:11px;'>" +
                    "<b>🎯 Target Head:</b> " + ex.getTargetRegion() + "<br>" +
                    "<b>🛠️ Equipment:</b> " + ex.getMachineSetup() + "</div></html>");
            detailsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailsLbl.setForeground(ThemeManager.getSecondaryTextColor());

            StyledButton viewBtn = new StyledButton("📖 View Picture & Guide", ThemeManager.PRIMARY_BLUE);
            viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            viewBtn.addActionListener(e -> {
                ExerciseGuideDialog dialog = new ExerciseGuideDialog(
                        (Frame) SwingUtilities.getWindowAncestor(WorkoutPlannerPanel.this),
                        ex);
                dialog.setVisible(true);
            });

            exCard.add(nameLbl, BorderLayout.NORTH);
            exCard.add(detailsLbl, BorderLayout.CENTER);
            exCard.add(viewBtn, BorderLayout.SOUTH);

            gridPanel.add(exCard);
        }

        container.add(gridPanel);
        return container;
    }

    /**
     * Core logic: generates only the exercises that belong to the specified day of
     * the week
     * for the selected fitness goal.
     */
    private List<Exercise> generateDaySplitExercises(FitnessGoal goal, int dayIndex, List<Exercise> allExercises) {
        List<Exercise> selected = new ArrayList<>();
        String[] targetExerciseNames = getTargetExerciseNames(goal, dayIndex);

        if (targetExerciseNames.length == 0) {
            return selected; // Rest day -> return empty list
        }

        // 1. Pick exact exercises for this day's split
        for (String name : targetExerciseNames) {
            for (Exercise e : allExercises) {
                if (e.getName().equalsIgnoreCase(name)) {
                    configureExerciseForGoal(e, goal);
                    selected.add(e);
                    break;
                }
            }
        }

        // 2. If no exercises were matched by name (e.g. custom DB), pick exercises
        // matching target muscle groups
        if (selected.isEmpty()) {
            String[] targetGroups = getTargetMuscleGroups(goal, dayIndex);
            for (String group : targetGroups) {
                List<Exercise> groupMatches = allExercises.stream()
                        .filter(e -> group.equalsIgnoreCase(e.getMuscleGroup()))
                        .collect(Collectors.toList());
                for (Exercise e : groupMatches) {
                    if (selected.size() < 5 && !selected.contains(e)) {
                        configureExerciseForGoal(e, goal);
                        selected.add(e);
                    }
                }
            }
        }

        return selected;
    }

    /**
     * Set default sets, reps, and weight based on the user's fitness goal.
     */
    private void configureExerciseForGoal(Exercise exercise, FitnessGoal goal) {
        if (exercise instanceof StrengthExercise se) {
            switch (goal) {
                case STRENGTH -> {
                    se.setDefaultSets(5);
                    se.setDefaultReps(5);
                    se.setDefaultWeight(se.getDefaultWeight() * 1.1);
                }
                case FAT_LOSS -> {
                    se.setDefaultSets(3);
                    se.setDefaultReps(15);
                    se.setDefaultWeight(se.getDefaultWeight() * 0.7);
                }
                case MUSCLE_GAIN -> {
                    se.setDefaultSets(4);
                    se.setDefaultReps(10);
                }
                case ENDURANCE -> {
                    se.setDefaultSets(3);
                    se.setDefaultReps(20);
                    se.setDefaultWeight(se.getDefaultWeight() * 0.6);
                }
            }
        }
    }

    /**
     * Exact exercise list per goal and day index (0=Mon .. 6=Sun).
     */
    private String[] getTargetExerciseNames(FitnessGoal goal, int dayIndex) {
        if (goal == null)
            goal = FitnessGoal.STRENGTH;
        switch (goal) {
            case STRENGTH:
                return switch (dayIndex) {
                    case 0 -> new String[] { "Incline Dumbbell Press", "Bench Press", "Decline Bench Press",
                            "Cable Chest Fly", "Chest Dips", "EZ-Bar Skull Crushers", "Cable Tricep Pushdown",
                            "Rope Pushdown", "Overhead Dumbbell Extension" }; // Mon: Chest (3 Heads) + Tri (3 Heads)
                    case 1 -> new String[] { "Deadlift", "Pull Ups", "Barbell Row", "Seated Cable Row", "Lat Pulldown",
                            "Face Pulls", "Incline Dumbbell Curl", "Preacher Curl", "Barbell Curl", "Hammer Curls" }; // Tue:
                                                                                                                      // Back
                                                                                                                      // (3
                                                                                                                      // Regions)
                                                                                                                      // +
                                                                                                                      // Bi
                                                                                                                      // (3
                                                                                                                      // Heads)
                    case 2 -> new String[] { "Barbell Squat", "Leg Press", "Leg Extension", "Romanian Deadlift",
                            "Lying Leg Curl", "Barbell Hip Thrust", "Standing Calf Raises", "Lunges" }; // Wed: Legs
                                                                                                        // (Quads, Hams,
                                                                                                        // Glutes,
                                                                                                        // Calves)
                    case 3 -> new String[] { "Overhead Press", "Dumbbell Shoulder Press", "Lateral Raises",
                            "Rear Delt Cable Fly", "Incline Dumbbell Curl", "Preacher Curl", "EZ-Bar Skull Crushers",
                            "Cable Tricep Pushdown" }; // Thu: Shoulders (3 Delts) + Arms
                    case 4 -> new String[] { "Barbell Squat", "Bench Press", "Deadlift", "Barbell Row",
                            "Overhead Press", "Pull Ups", "Incline Dumbbell Press", "Lat Pulldown" }; // Fri: Full Body
                                                                                                      // Power
                    case 5 -> new String[] { "Plank", "Cable Crunches", "Treadmill Running", "Jump Rope" }; // Sat:
                                                                                                            // Active
                                                                                                            // Recovery
                                                                                                            // & Core
                    default -> new String[] {}; // Sun: Rest
                };
            case FAT_LOSS:
                return switch (dayIndex) {
                    case 0 -> new String[] { "Burpees", "Push Ups", "Barbell Squat", "Mountain Climbers", "Jump Rope",
                            "Plank" }; // Mon: Full Body Circuit
                    case 1 ->
                        new String[] { "Treadmill Running", "Cycling", "Plank", "Cable Crunches", "Mountain Climbers" }; // Tue:
                                                                                                                         // Cardio+Core
                    case 2 -> new String[] { "Incline Dumbbell Press", "Lat Pulldown", "Lateral Raises", "Push Ups",
                            "Treadmill Running", "Jump Rope" }; // Wed: Upper Body+Cardio
                    case 3 -> new String[] { "Lunges", "Leg Press", "Romanian Deadlift", "Cycling", "Burpees",
                            "Standing Calf Raises" }; // Thu: Lower Body+Cardio
                    case 4 -> new String[] { "Burpees", "Mountain Climbers", "Jump Rope", "Push Ups", "Barbell Squat",
                            "Plank" }; // Fri: Full Body HIIT
                    case 5 -> new String[] { "Cycling", "Plank", "Cable Crunches" }; // Sat: Active Recovery
                    default -> new String[] {}; // Sun: Rest
                };
            case MUSCLE_GAIN:
                return switch (dayIndex) {
                    case 0 -> new String[] { "Incline Dumbbell Press", "Bench Press", "Decline Bench Press",
                            "Cable Chest Fly", "Chest Dips", "EZ-Bar Skull Crushers", "Cable Tricep Pushdown",
                            "Rope Pushdown", "Overhead Dumbbell Extension" }; // Mon: Chest + Triceps
                    case 1 -> new String[] { "Deadlift", "Pull Ups", "Barbell Row", "Seated Cable Row", "Lat Pulldown",
                            "Face Pulls", "Incline Dumbbell Curl", "Preacher Curl", "Barbell Curl", "Hammer Curls" }; // Tue:
                                                                                                                      // Back
                                                                                                                      // +
                                                                                                                      // Biceps
                    case 2 -> new String[] { "Barbell Squat", "Leg Press", "Leg Extension", "Romanian Deadlift",
                            "Lying Leg Curl", "Barbell Hip Thrust", "Standing Calf Raises", "Lunges" }; // Wed: Legs
                    case 3 -> new String[] { "Overhead Press", "Dumbbell Shoulder Press", "Lateral Raises",
                            "Rear Delt Cable Fly", "Incline Dumbbell Curl", "Preacher Curl", "EZ-Bar Skull Crushers",
                            "Cable Tricep Pushdown" }; // Thu: Shoulders + Arms
                    case 4 -> new String[] { "Incline Dumbbell Press", "Bench Press", "Barbell Row", "Seated Cable Row",
                            "Lat Pulldown", "Cable Chest Fly" }; // Fri: Chest + Back Hypertrophy
                    case 5 -> new String[] { "Barbell Squat", "Leg Press", "Romanian Deadlift", "Plank",
                            "Cable Crunches", "Standing Calf Raises" }; // Sat: Legs + Core
                    default -> new String[] {}; // Sun: Rest
                };
            case ENDURANCE:
            default:
                return switch (dayIndex) {
                    case 0 -> new String[] { "Barbell Squat", "Lunges", "Treadmill Running", "Cycling",
                            "Standing Calf Raises" }; // Mon: Legs+Cardio
                    case 1 -> new String[] { "Push Ups", "Lat Pulldown", "Overhead Press", "Jump Rope", "Plank" }; // Tue:
                                                                                                                   // Upper
                                                                                                                   // Body+Cardio
                    case 2 -> new String[] { "Treadmill Running", "Cycling", "Jump Rope", "Mountain Climbers" }; // Wed:
                                                                                                                 // Cardio
                                                                                                                 // Only
                    case 3 -> new String[] { "Barbell Squat", "Push Ups", "Barbell Row", "Burpees", "Cable Crunches" }; // Thu:
                                                                                                                        // Full
                                                                                                                        // Body
                    case 4 ->
                        new String[] { "Treadmill Running", "Plank", "Cable Crunches", "Mountain Climbers", "Cycling" }; // Fri:
                                                                                                                         // Cardio+Core
                    case 5 -> new String[] { "Cycling", "Jump Rope" }; // Sat: Active Recovery
                    default -> new String[] {}; // Sun: Rest
                };
        }
    }

    /**
     * Fallback muscle groups per goal and day index.
     */
    private String[] getTargetMuscleGroups(FitnessGoal goal, int dayIndex) {
        return switch (dayIndex) {
            case 0 -> new String[] { "CHEST", "ARMS" };
            case 1 -> new String[] { "BACK", "ARMS" };
            case 2 -> new String[] { "LEGS" };
            case 3 -> new String[] { "SHOULDERS", "ARMS" };
            case 4 -> new String[] { "CHEST", "BACK", "LEGS", "SHOULDERS" };
            case 5 -> new String[] { "CORE", "CARDIO" };
            default -> new String[] {};
        };
    }

    /**
     * Returns a small picture/icon emoji badge for an exercise.
     */
    private String getExercisePic(String name, String muscleGroup) {
        if (name != null) {
            String lower = name.toLowerCase();
            if (lower.contains("bench") || lower.contains("push") || lower.contains("chest") || lower.contains("fly"))
                return "🫁";
            if (lower.contains("deadlift") || lower.contains("row") || lower.contains("pull") || lower.contains("lat"))
                return "🦍";
            if (lower.contains("squat") || lower.contains("leg") || lower.contains("lunge") || lower.contains("calf")
                    || lower.contains("rdl"))
                return "🦵";
            if (lower.contains("press")
                    && (lower.contains("overhead") || lower.contains("ohp") || lower.contains("shoulder")))
                return "🛡️";
            if (lower.contains("raise"))
                return "🛡️";
            if (lower.contains("curl") || lower.contains("dip") || lower.contains("tricep") || lower.contains("arm")
                    || lower.contains("skull"))
                return "💪";
            if (lower.contains("plank") || lower.contains("crunch") || lower.contains("core") || lower.contains("ab"))
                return "🎯";
            if (lower.contains("run") || lower.contains("treadmill") || lower.contains("bike") || lower.contains("cycl")
                    || lower.contains("jump") || lower.contains("burpee") || lower.contains("mountain"))
                return "🏃";
        }
        return getMuscleGroupPic(muscleGroup);
    }

    /**
     * Returns a small picture/icon emoji badge for a muscle group.
     */
    private String getMuscleGroupPic(String muscleGroup) {
        if (muscleGroup == null)
            return "🏋️";
        return switch (muscleGroup.toUpperCase()) {
            case "CHEST" -> "🫁";
            case "BACK" -> "🦍";
            case "LEGS" -> "🦵";
            case "SHOULDERS" -> "🛡️";
            case "ARMS" -> "💪";
            case "CORE" -> "🎯";
            case "CARDIO" -> "🏃";
            default -> "🏋️";
        };
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        buildUI();
        generatePlanForSelectedDay();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setOnStartWorkout(java.util.function.Consumer<List<Exercise>> callback) {
        this.onStartWorkout = callback;
    }
}
