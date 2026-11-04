package com.careermatch;

import com.careermatch.ui.MainFrame;
import com.careermatch.util.Database;

import javax.swing.*;
import java.awt.*;

/**
 * Application Entry Point for CareerMatch — Smart Career Planning and Job Recommendation System.
 * Initializes the SQLite database and launches the modern Java Swing desktop GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Ensure database tables and initial dataset are initialized
        Database.createTables();

        boolean headless = GraphicsEnvironment.isHeadless();
        for (String arg : args) {
            if ("--headless".equalsIgnoreCase(arg)) {
                headless = true;
                break;
            }
        }

        if (headless) {
            System.out.println("CareerMatch is running in headless mode. Database initialized successfully.");
            return;
        }

        // Launch modern SaaS Java Swing GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame app = new MainFrame();
                app.startApplication();
            } catch (Exception e) {
                System.err.println("Fatal error launching CareerMatch GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
