package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A modal dialog for timing rests between sets.
 */
public class RestTimerDialog extends JDialog {

    private JLabel timeLabel;
    private StyledButton btn30s, btn60s, btn90s, btn120s;
    private StyledButton startStopButton;
    private StyledButton resetButton;

    private Timer timer;
    private int remainingSeconds = 0;
    private boolean isRunning = false;

    public RestTimerDialog(Window owner) {
        super(owner, "Rest Timer", ModalityType.MODELESS);
        setSize(350, 250);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        // Stop timer when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timer != null) {
                    timer.stop();
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(ThemeManager.getBackground());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Time Display
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 64));
        timeLabel.setForeground(ThemeManager.getTextColor());
        mainPanel.add(timeLabel, BorderLayout.NORTH);

        // Preset buttons
        JPanel presetPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        presetPanel.setOpaque(false);
        
        btn30s = createPresetButton("30s", 30);
        btn60s = createPresetButton("60s", 60);
        btn90s = createPresetButton("90s", 90);
        btn120s = createPresetButton("120s", 120);
        
        presetPanel.add(btn30s);
        presetPanel.add(btn60s);
        presetPanel.add(btn90s);
        presetPanel.add(btn120s);
        
        mainPanel.add(presetPanel, BorderLayout.CENTER);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlPanel.setOpaque(false);
        
        startStopButton = new StyledButton("Start", ThemeManager.ACCENT_GREEN);
        startStopButton.setPreferredSize(new Dimension(100, 40));
        startStopButton.addActionListener(e -> toggleTimer());
        
        resetButton = new StyledButton("Reset", ThemeManager.ACCENT_ORANGE);
        resetButton.setPreferredSize(new Dimension(100, 40));
        resetButton.addActionListener(e -> resetTimer());
        
        controlPanel.add(startStopButton);
        controlPanel.add(resetButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Timer initialization
        timer = new Timer(1000, e -> updateTimer());
    }

    private StyledButton createPresetButton(String label, int seconds) {
        StyledButton btn = new StyledButton(label, ThemeManager.ACCENT_BLUE);
        btn.setPreferredSize(new Dimension(60, 35));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.addActionListener(e -> {
            setTimer(seconds);
            startStopButton.setText("Start");
            startStopButton.setButtonColor(ThemeManager.ACCENT_GREEN);
            isRunning = false;
            if(timer != null) timer.stop();
        });
        return btn;
    }

    private void setTimer(int seconds) {
        this.remainingSeconds = seconds;
        updateDisplay();
    }

    private void toggleTimer() {
        if (remainingSeconds <= 0) return;
        
        if (isRunning) {
            timer.stop();
            startStopButton.setText("Start");
            startStopButton.setButtonColor(ThemeManager.ACCENT_GREEN);
        } else {
            timer.start();
            startStopButton.setText("Pause");
            startStopButton.setButtonColor(ThemeManager.ACCENT_RED);
        }
        isRunning = !isRunning;
    }

    private void resetTimer() {
        timer.stop();
        remainingSeconds = 0;
        isRunning = false;
        updateDisplay();
        startStopButton.setText("Start");
        startStopButton.setButtonColor(ThemeManager.ACCENT_GREEN);
    }

    private void updateTimer() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            updateDisplay();
        } else {
            timer.stop();
            isRunning = false;
            startStopButton.setText("Start");
            startStopButton.setButtonColor(ThemeManager.ACCENT_GREEN);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Rest time is over! Back to work!", "Time's Up", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateDisplay() {
        long m = remainingSeconds / 60;
        long s = remainingSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", m, s));
    }
}
