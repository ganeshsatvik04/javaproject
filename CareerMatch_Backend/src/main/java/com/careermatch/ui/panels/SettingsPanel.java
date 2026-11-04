package com.careermatch.ui.panels;

import com.careermatch.model.User;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Settings & System Diagnostics panel.
 * Displays application architecture specs, database connectivity diagnostics,
 * and academic concept execution indicators.
 */
public class SettingsPanel extends JPanel {
    private final JLabel userNameLabel = new JLabel("");
    private final JLabel userRoleLabel = new JLabel("");
    private final JLabel userEmailLabel = new JLabel("");

    public SettingsPanel() {
        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // 1. Account Settings Card
        content.add(buildAccountCard());
        content.add(Box.createVerticalStrut(20));

        // 2. System Diagnostics Card
        content.add(buildDiagnosticsCard());
        content.add(Box.createVerticalStrut(20));

        // 3. Academic Implementation Summary Card
        content.add(buildAcademicSummaryCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildAccountCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Current Account Session");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        addRow(card, "Active User Name:", userNameLabel);
        addRow(card, "Email / Login ID:", userEmailLabel);
        addRow(card, "Security Role:", userRoleLabel);

        return card;
    }

    private JPanel buildDiagnosticsCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Database & Environment Diagnostics");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        addRow(card, "Database Engine:", new JLabel("SQLite 3 (via Xerial JDBC 3.46.1.0)"));
        addRow(card, "Connection URL:", new JLabel("jdbc:sqlite:careermatch.db"));
        addRow(card, "Journal Mode:", new JLabel("WAL (Write-Ahead Logging) — High Concurrency"));
        addRow(card, "Java Runtime:", new JLabel(System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")"));
        addRow(card, "GUI Architecture:", new JLabel("Pure Java Swing with SaaS Antialiased Vector UI System"));

        return card;
    }

    private JPanel buildAcademicSummaryCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Academic Concepts Demonstrated");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        addRow(card, "OOP - Inheritance & Abstraction:", new JLabel("abstract class User -> JobSeeker, Admin with getDashboardName()"));
        addRow(card, "OOP - Interfaces & Polymorphism:", new JLabel("DatabaseOperations<T>, AuthService, JobMatchingService"));
        addRow(card, "OOP - Method Overloading:", new JLabel("ReadinessService.calculateScore(), MatchingService.getRecommendations()"));
        addRow(card, "Advanced - Multithreading:", new JLabel("SwingWorker<T, V> for responsive non-blocking background queries"));
        addRow(card, "Advanced - Collections Framework:", new JLabel("ArrayList, HashSet for skill set difference, LinkedHashMap"));
        addRow(card, "Advanced - Generics:", new JLabel("GenericDAO<T> parameterized repository pattern"));
        addRow(card, "Advanced - Java Streams API:", new JLabel("Stream filter, map, sorted, count, collect with lambdas & method refs"));
        addRow(card, "JDBC Connectivity:", new JLabel("PreparedStatement, ResultSet, transactions (commit/rollback), CRUD"));

        return card;
    }

    private void addRow(JPanel parent, String labelText, JLabel valLabel) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel l = new JLabel(labelText);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(UITheme.FONT_BODY);
        valLabel.setForeground(UITheme.TEXT_DARK);

        r.add(l, BorderLayout.WEST);
        r.add(valLabel, BorderLayout.EAST);
        parent.add(r);
        parent.add(Box.createVerticalStrut(6));
    }

    public void setUser(User user) {
        if (user != null) {
            userNameLabel.setText(user.getName());
            userEmailLabel.setText(user.getEmail());
            userRoleLabel.setText(user.getRole() + (user.isAdmin() ? " (Full Administrative Privileges)" : " (Job Seeker)"));
        }
    }
}
