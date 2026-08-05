package com.repsync.ui.progress;

import com.repsync.model.BodyProgress;
import com.repsync.model.User;
import com.repsync.service.ProgressService;
import com.repsync.ui.ThemeManager;
import com.repsync.ui.components.StyledButton;
import com.repsync.ui.components.StyledTextField;
import com.repsync.util.InputValidator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Progress panel - displays body weight chart and workout frequency chart.
 * Uses JFreeChart for visualization. Premium design with section headers and styled cards.
 */
public class ProgressPanel extends JPanel {

    private User currentUser;
    private final ProgressService progressService = new ProgressService();
    private JPanel chartContainer;
    private StyledButton logButton;
    private StyledButton refreshButton;

    public ProgressPanel(User user) {
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

        JLabel title = new JLabel("📈  Progress Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ThemeManager.getTextColor());

        JLabel subtitle = new JLabel("Log your weight, track trends, and see your workout frequency over time");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ThemeManager.getSecondaryTextColor());

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);

        // Log weight input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        inputPanel.setOpaque(false);

        StyledTextField weightInput = new StyledTextField("Weight (kg)");
        weightInput.setPreferredSize(new Dimension(130, 36));

        StyledTextField notesInput = new StyledTextField("Notes (optional)");
        notesInput.setPreferredSize(new Dimension(200, 36));

        logButton = new StyledButton("⚖ Log Weight", ThemeManager.ACCENT_GREEN);
        logButton.setPreferredSize(new Dimension(140, 36));
        logButton.addActionListener(e -> {
            double weight = InputValidator.parseDoubleSafe(weightInput.getActualText());
            if (weight > 0) {
                logButton.setEnabled(false);
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        progressService.logWeight(currentUser.getId(), weight, currentUser.getHeightCm(), notesInput.getActualText());
                        return null;
                    }
                    @Override
                    protected void done() {
                        logButton.setEnabled(true);
                        try {
                            get();
                            weightInput.clearField();
                            notesInput.clearField();
                            refreshCharts();
                            JOptionPane.showMessageDialog(ProgressPanel.this, "Weight logged: " + weight + " kg", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ProgressPanel.this, "Error logging weight: " + ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid weight.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshButton = new StyledButton("🔄 Refresh", ThemeManager.ACCENT_BLUE);
        refreshButton.setPreferredSize(new Dimension(120, 36));
        refreshButton.addActionListener(e -> refreshCharts());

        inputPanel.add(weightInput);
        inputPanel.add(notesInput);
        inputPanel.add(logButton);
        inputPanel.add(refreshButton);

        // Top
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.SOUTH);

        // Chart container
        chartContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        chartContainer.setOpaque(false);

        add(topPanel, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);

        refreshCharts();
    }

    /**
     * Refresh the charts with latest data.
     */
    private void refreshCharts() {
        if (refreshButton != null) refreshButton.setEnabled(false);
        chartContainer.removeAll();
        JLabel loadingLabel = new JLabel("Loading Charts...");
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loadingLabel.setForeground(ThemeManager.getSecondaryTextColor());
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chartContainer.add(loadingLabel);
        chartContainer.revalidate();
        chartContainer.repaint();

        SwingWorker<ChartPanel[], Void> worker = new SwingWorker<>() {
            @Override
            protected ChartPanel[] doInBackground() throws Exception {
                ChartPanel[] panels = new ChartPanel[2];
                panels[0] = createWeightChart();
                panels[1] = createWorkoutFrequencyChart();
                return panels;
            }

            @Override
            protected void done() {
                if (refreshButton != null) refreshButton.setEnabled(true);
                chartContainer.removeAll();
                try {
                    ChartPanel[] panels = get();

                    if (panels[0] != null) {
                        chartContainer.add(wrapInCard(panels[0]));
                    } else {
                        JPanel noDataPanel = createEmptyStateCard(
                            "No weight data yet!",
                            "Log your weight above to start tracking your progress."
                        );
                        chartContainer.add(noDataPanel);
                    }

                    if (panels[1] != null) {
                        chartContainer.add(wrapInCard(panels[1]));
                    }
                } catch (Exception e) {
                    JLabel errorLabel = new JLabel("Error loading charts: " + e.getMessage());
                    errorLabel.setForeground(ThemeManager.ACCENT_RED);
                    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    chartContainer.add(errorLabel);
                }
                chartContainer.revalidate();
                chartContainer.repaint();
            }
        };
        worker.execute();
    }

    /**
     * Wrap a chart panel in a styled card.
     */
    private JPanel wrapInCard(ChartPanel chartPanel) {
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Create an empty state card.
     */
    private JPanel createEmptyStateCard(String title, String message) {
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(ThemeManager.getSecondaryTextColor());
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(msgLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    /**
     * Create the weight progress line chart.
     */
    private ChartPanel createWeightChart() {
        List<BodyProgress> history = progressService.getWeightHistory(currentUser.getId());
        if (history.isEmpty()) return null;

        XYSeries series = new XYSeries("Body Weight (kg)");
        for (int i = 0; i < history.size(); i++) {
            series.add(i + 1, history.get(i).getWeightKg());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Body Weight Over Time", "Entry", "Weight (kg)",
            dataset, PlotOrientation.VERTICAL, true, true, false
        );

        // Style the chart
        styleChart(chart);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, ThemeManager.ACCENT_BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    /**
     * Create the weekly workout frequency bar chart.
     */
    private ChartPanel createWorkoutFrequencyChart() {
        int[] weeklyCounts = progressService.getWeeklyWorkoutCounts(currentUser.getId(), 8);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = weeklyCounts.length - 1; i >= 0; i--) {
            String weekLabel = i == 0 ? "This Week" : i + "w ago";
            dataset.addValue(weeklyCounts[i], "Workouts", weekLabel);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Workouts Per Week", "Week", "Workouts",
            dataset, PlotOrientation.VERTICAL, true, true, false
        );

        // Style the chart
        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, ThemeManager.ACCENT_GREEN);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    /**
     * Apply consistent styling to charts (dark mode friendly).
     */
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(ThemeManager.getCardBackground());
        chart.getTitle().setPaint(ThemeManager.getTextColor());
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(ThemeManager.getCardBackground());
            chart.getLegend().setItemPaint(ThemeManager.getTextColor());
        }

        if (chart.getPlot() instanceof XYPlot xyPlot) {
            xyPlot.setBackgroundPaint(ThemeManager.getCardBackground());
            xyPlot.setDomainGridlinePaint(ThemeManager.getSecondaryTextColor());
            xyPlot.setRangeGridlinePaint(ThemeManager.getSecondaryTextColor());
            xyPlot.getDomainAxis().setTickLabelPaint(ThemeManager.getTextColor());
            xyPlot.getRangeAxis().setTickLabelPaint(ThemeManager.getTextColor());
            xyPlot.getDomainAxis().setLabelPaint(ThemeManager.getTextColor());
            xyPlot.getRangeAxis().setLabelPaint(ThemeManager.getTextColor());
        } else if (chart.getPlot() instanceof CategoryPlot catPlot) {
            catPlot.setBackgroundPaint(ThemeManager.getCardBackground());
            catPlot.setDomainGridlinePaint(ThemeManager.getSecondaryTextColor());
            catPlot.setRangeGridlinePaint(ThemeManager.getSecondaryTextColor());
            catPlot.getDomainAxis().setTickLabelPaint(ThemeManager.getTextColor());
            catPlot.getRangeAxis().setTickLabelPaint(ThemeManager.getTextColor());
            catPlot.getDomainAxis().setLabelPaint(ThemeManager.getTextColor());
            catPlot.getRangeAxis().setLabelPaint(ThemeManager.getTextColor());
        }
    }

    public void refresh() {
        setBackground(ThemeManager.getBackground());
        removeAll();
        buildUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshCharts();
    }
}
