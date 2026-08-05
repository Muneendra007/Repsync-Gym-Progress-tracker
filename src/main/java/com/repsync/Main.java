package com.repsync;

import com.repsync.database.SchemaInitializer;
import com.repsync.ui.MainFrame;
import com.repsync.ui.ThemeManager;

import javax.swing.*;

/**
 * RepSync Application Entry Point.
 * 
 * Startup sequence:
 * 1. Initialize FlatLaf theme (dark mode)
 * 2. Initialize database (create tables if needed)
 * 3. Show the main application window
 */
public class Main {

    public static void main(String[] args) {
        // Step 1: Set up the FlatLaf dark theme
        ThemeManager.initialize();

        // Step 2: Initialize database (creates tables + seeds exercises)
        System.out.println("=== RepSync - Smart Gym & Fitness Progress Tracker ===");
        System.out.println("Initializing database...");
        SchemaInitializer.initialize();

        // Step 3: Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
