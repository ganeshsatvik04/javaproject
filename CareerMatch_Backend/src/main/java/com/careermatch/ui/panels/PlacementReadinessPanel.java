package com.careermatch.ui.panels;

import com.careermatch.model.ReadinessScore;
import com.careermatch.model.User;
import com.careermatch.service.ReadinessService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.dialogs.UpdateReadinessDialog;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Module 7: Placement Readiness Score.
 * Professional career analytics dashboard featuring a large weighted score gauge,
 * component breakdown meters, strengths analysis, growth areas, and personalized recommendations.
 * Demonstrates Multithreading via SwingWorker and algorithmic score evaluation.
 */
public class PlacementReadinessPanel extends JPanel {
    private final ReadinessService readinessService;
    private User currentUser;
    private ReadinessScore currentScore;

    // Hero Widgets
    private final RadialScoreGauge heroGauge = new RadialScoreGauge(0, 150, "Readiness");
    private final JLabel statusTitle = new JLabel("Evaluating Readiness...");
    private final JLabel formulaSub = new JLabel("Calculated from: Technical (40%), DSA (25%), Projects (20%), Interview (15%)");

    // Component Metric Bars
    private final JLabel techScoreVal = new JLabel("0%");
    private final ModernProgressBar techBar = new ModernProgressBar(0, 8);

    private final JLabel dsaScoreVal = new JLabel("0%");
    private final ModernProgressBar dsaBar = new ModernProgressBar(0, 8);

    private final JLabel projScoreVal = new JLabel("0%");
    private final ModernProgressBar projBar = new ModernProgressBar(0, 8);

    private final JLabel interScoreVal = new JLabel("0%");
    private final ModernProgressBar interBar = new ModernProgressBar(0, 8);

    // Insights Containers
    private final JPanel strengthsContainer = new JPanel();
    private final JPanel weaknessesContainer = new JPanel();
    private final JPanel recommendationsContainer = new JPanel();

    public PlacementReadinessPanel(ReadinessService readinessService) {
        this.readinessService = readinessService;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        // 1. Hero Analytics Card
        content.add(buildHeroCard());
        content.add(Box.createVerticalStrut(20));

        // 2. Component Progress Indicators Row
        content.add(buildComponentMetricsRow());
        content.add(Box.createVerticalStrut(20));

        // 3. Three Insight Cards Row
        content.add(buildInsightsRow());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeroCard() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BorderLayout(24, 0));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Left: Large Gauge
        card.add(heroGauge, BorderLayout.WEST);

        // Center: Status title & formula
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel h = new JLabel("Overall Placement Readiness Evaluation");
        h.setFont(UITheme.FONT_TITLE_LARGE);
        h.setForeground(UITheme.PRIMARY_NAVY);

        statusTitle.setFont(UITheme.FONT_TITLE);
        statusTitle.setForeground(UITheme.SUCCESS);

        formulaSub.setFont(UITheme.FONT_BODY);
        formulaSub.setForeground(UITheme.TEXT_MUTED);

        center.add(h);
        center.add(Box.createVerticalStrut(6));
        center.add(statusTitle);
        center.add(Box.createVerticalStrut(8));
        center.add(formulaSub);
        card.add(center, BorderLayout.CENTER);

        // Right: Update Score Action Button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);

        ModernButton updateBtn = new ModernButton("Calibrate / Update Scores", IconHelper.getIcon(IconHelper.IconType.EDIT, 16, Color.WHITE), ModernButton.ButtonStyle.PRIMARY);
        updateBtn.addActionListener(e -> openUpdateDialog());
        right.add(updateBtn);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JPanel buildComponentMetricsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        row.add(createMetricCard("Technical Foundation", "Weight: 40%", techScoreVal, techBar, UITheme.SECONDARY_BLUE));
        row.add(createMetricCard("Data Structures & Algorithms", "Weight: 25%", dsaScoreVal, dsaBar, UITheme.PRIMARY_NAVY));
        row.add(createMetricCard("Practical Projects", "Weight: 20%", projScoreVal, projBar, UITheme.ACCENT_TEAL));
        row.add(createMetricCard("Interview & Communication", "Weight: 15%", interScoreVal, interBar, UITheme.WARNING));

        return row;
    }

    private JPanel createMetricCard(String title, String weight, JLabel valLabel, ModernProgressBar bar, Color barColor) {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL_BOLD);
        t.setForeground(UITheme.TEXT_DARK);

        JLabel w = new JLabel(weight);
        w.setFont(UITheme.FONT_TINY);
        w.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(UITheme.TEXT_DARK);

        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        bar.setFillColor(barColor);

        card.add(t);
        card.add(w);
        card.add(Box.createVerticalStrut(6));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(bar);

        return card;
    }

    private JPanel buildInsightsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        // Strengths
        ModernCard strCard = new ModernCard();
        strCard.setLayout(new BorderLayout(0, 10));
        JLabel st = new JLabel("Key Profile Strengths");
        st.setFont(UITheme.FONT_SUBTITLE);
        st.setForeground(UITheme.SUCCESS);
        st.setIcon(IconHelper.getIcon(IconHelper.IconType.CHECK, 18, UITheme.SUCCESS));
        strCard.add(st, BorderLayout.NORTH);
        strengthsContainer.setLayout(new BoxLayout(strengthsContainer, BoxLayout.Y_AXIS));
        strengthsContainer.setOpaque(false);
        strCard.add(strengthsContainer, BorderLayout.CENTER);

        // Areas to Improve
        ModernCard impCard = new ModernCard();
        impCard.setLayout(new BorderLayout(0, 10));
        JLabel it = new JLabel("Areas for Improvement");
        it.setFont(UITheme.FONT_SUBTITLE);
        it.setForeground(UITheme.WARNING);
        it.setIcon(IconHelper.getIcon(IconHelper.IconType.TRENDING_UP, 18, UITheme.WARNING));
        impCard.add(it, BorderLayout.NORTH);
        weaknessesContainer.setLayout(new BoxLayout(weaknessesContainer, BoxLayout.Y_AXIS));
        weaknessesContainer.setOpaque(false);
        impCard.add(weaknessesContainer, BorderLayout.CENTER);

        // Recommendations
        ModernCard recCard = new ModernCard();
        recCard.setLayout(new BorderLayout(0, 10));
        JLabel rt = new JLabel("Recommended Next Actions");
        rt.setFont(UITheme.FONT_SUBTITLE);
        rt.setForeground(UITheme.SECONDARY_BLUE);
        rt.setIcon(IconHelper.getIcon(IconHelper.IconType.STAR, 18, UITheme.SECONDARY_BLUE));
        recCard.add(rt, BorderLayout.NORTH);
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));
        recommendationsContainer.setOpaque(false);
        recCard.add(recommendationsContainer, BorderLayout.CENTER);

        row.add(strCard);
        row.add(impCard);
        row.add(recCard);
        return row;
    }

    public void loadReadinessData(User user) {
        this.currentUser = user;
        if (user == null) return;

        SwingWorker<ReadinessScore, Void> worker = new SwingWorker<>() {
            @Override
            protected ReadinessScore doInBackground() {
                return readinessService.getLatestScore(user.getId());
            }

            @Override
            protected void done() {
                try {
                    currentScore = get();
                    updateReadinessUI(currentScore);
                } catch (Exception ex) {
                    System.err.println("Readiness score load error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateReadinessUI(ReadinessScore score) {
        if (score == null) return;

        heroGauge.setScore(score.getTotal());
        statusTitle.setText(score.getStatusDescriptor());
        if (score.getTotal() >= 75) {
            statusTitle.setForeground(UITheme.SUCCESS);
        } else if (score.getTotal() >= 55) {
            statusTitle.setForeground(UITheme.WARNING);
        } else {
            statusTitle.setForeground(UITheme.ERROR);
        }

        techScoreVal.setText(String.format("%.0f%%", score.getTechnical()));
        techBar.setValue((int) score.getTechnical());

        dsaScoreVal.setText(String.format("%.0f%%", score.getDsa()));
        dsaBar.setValue((int) score.getDsa());

        projScoreVal.setText(String.format("%.0f%%", score.getProjects()));
        projBar.setValue((int) score.getProjects());

        interScoreVal.setText(String.format("%.0f%%", score.getInterview()));
        interBar.setValue((int) score.getInterview());

        populateBullets(strengthsContainer, readinessService.getStrengths(score), UITheme.SUCCESS);
        populateBullets(weaknessesContainer, readinessService.getAreasForImprovement(score), UITheme.WARNING);
        populateBullets(recommendationsContainer, readinessService.getRecommendations(score), UITheme.TEXT_DARK);
    }

    private void populateBullets(JPanel container, List<String> items, Color fg) {
        container.removeAll();
        for (String item : items) {
            JLabel lbl = new JLabel("• " + item);
            lbl.setFont(UITheme.FONT_BODY);
            lbl.setForeground(fg);
            container.add(lbl);
            container.add(Box.createVerticalStrut(6));
        }
        container.revalidate();
        container.repaint();
    }

    private void openUpdateDialog() {
        if (currentUser == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        UpdateReadinessDialog dialog = new UpdateReadinessDialog(parent, currentUser.getId(), readinessService, currentScore);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadReadinessData(currentUser);
        }
    }
}
