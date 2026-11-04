package com.careermatch.ui.panels;

import com.careermatch.model.JobMatch;
import com.careermatch.model.ReadinessScore;
import com.careermatch.model.User;
import com.careermatch.service.JobMatchingService;
import com.careermatch.service.LearningService;
import com.careermatch.service.ReadinessService;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main SaaS Dashboard Overview providing high-level KPIs,
 * recommended jobs snapshot, placement readiness meter, and quick navigation.
 * Demonstrates Multithreading via SwingWorker for non-blocking loading.
 */
public class DashboardPanel extends JPanel {
    private final JobMatchingService matchingService;
    private final SkillService skillService;
    private final ReadinessService readinessService;
    private final LearningService learningService;
    private final Consumer<String> navigationHandler;

    private User currentUser;

    // KPI Labels
    private final JLabel kpiJobsVal = new JLabel("0");
    private final JLabel kpiReadinessVal = new JLabel("0%");
    private final JLabel kpiSkillsVal = new JLabel("0");
    private final JLabel kpiRoadmapVal = new JLabel("0%");

    private final JPanel jobsListContainer = new JPanel();
    private final RadialScoreGauge readinessGauge = new RadialScoreGauge(0, 120, "Readiness");
    private final ModernProgressBar techBar = new ModernProgressBar(0, 6);
    private final ModernProgressBar dsaBar = new ModernProgressBar(0, 6);
    private final ModernProgressBar projBar = new ModernProgressBar(0, 6);
    private final ModernProgressBar interBar = new ModernProgressBar(0, 6);

    public DashboardPanel(JobMatchingService matchingService, SkillService skillService,
                          ReadinessService readinessService, LearningService learningService,
                          Consumer<String> navigationHandler) {
        this.matchingService = matchingService;
        this.skillService = skillService;
        this.readinessService = readinessService;
        this.learningService = learningService;
        this.navigationHandler = navigationHandler;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // 1. Hero Banner
        content.add(buildHeroBanner());
        content.add(Box.createVerticalStrut(20));

        // 2. KPI Cards Row
        content.add(buildKpiRow());
        content.add(Box.createVerticalStrut(20));

        // 3. Main Two-Column Layout
        content.add(buildTwoColumnSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeroBanner() {
        ModernCard banner = new ModernCard(16);
        banner.setCardBackground(UITheme.PRIMARY_NAVY);
        banner.setCardBorderColor(null);
        banner.setLayout(new BorderLayout(20, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel greeting = new JLabel("Accelerate Your Placement Preparation");
        greeting.setFont(UITheme.FONT_TITLE_LARGE);
        greeting.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Discover high-matching opportunities, target missing skills, and track readiness analytics.");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(0x94, 0xA3, 0xB8));

        left.add(greeting);
        left.add(Box.createVerticalStrut(6));
        left.add(sub);
        banner.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);

        ModernButton expBtn = new ModernButton("Explore Recommendations →", ModernButton.ButtonStyle.PRIMARY);
        expBtn.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_JOBS);
        });
        right.add(expBtn);
        banner.add(right, BorderLayout.EAST);

        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        return banner;
    }

    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        row.add(createKpiCard("High-Match Jobs", kpiJobsVal, "Open roles matched to skills", IconHelper.IconType.JOBS, UITheme.SECONDARY_BLUE));
        row.add(createKpiCard("Placement Readiness", kpiReadinessVal, "Overall calibrated score", IconHelper.IconType.READINESS, UITheme.SUCCESS));
        row.add(createKpiCard("Skills Acquired", kpiSkillsVal, "Verified technical proficiencies", IconHelper.IconType.SKILL_GAP, UITheme.ACCENT_TEAL));
        row.add(createKpiCard("Roadmap Completion", kpiRoadmapVal, "Curated learning progress", IconHelper.IconType.ROADMAP, UITheme.WARNING));

        return row;
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String sub, IconHelper.IconType iconType, Color iconColor) {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel textGrp = new JPanel();
        textGrp.setLayout(new BoxLayout(textGrp, BoxLayout.Y_AXIS));
        textGrp.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL_BOLD);
        t.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLabel.setForeground(UITheme.TEXT_DARK);

        JLabel s = new JLabel(sub);
        s.setFont(UITheme.FONT_TINY);
        s.setForeground(UITheme.TEXT_MUTED);

        textGrp.add(t);
        textGrp.add(Box.createVerticalStrut(4));
        textGrp.add(valLabel);
        textGrp.add(Box.createVerticalStrut(4));
        textGrp.add(s);

        card.add(textGrp, BorderLayout.CENTER);

        JLabel iconBadge = new JLabel(IconHelper.getIcon(iconType, 24, iconColor));
        iconBadge.setVerticalAlignment(SwingConstants.TOP);
        card.add(iconBadge, BorderLayout.EAST);

        return card;
    }

    private JPanel buildTwoColumnSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        // Left Column: Top Recommended Jobs
        ModernCard jobsCard = new ModernCard();
        jobsCard.setLayout(new BorderLayout());

        JPanel jobsHeader = new JPanel(new BorderLayout());
        jobsHeader.setOpaque(false);
        jobsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel jobsTitle = new JLabel("Top Matching Opportunities");
        jobsTitle.setFont(UITheme.FONT_SUBTITLE);
        jobsTitle.setForeground(UITheme.TEXT_DARK);

        ModernButton viewAllBtn = new ModernButton("View All", ModernButton.ButtonStyle.GHOST);
        viewAllBtn.setFont(UITheme.FONT_SMALL_BOLD);
        viewAllBtn.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_JOBS);
        });

        jobsHeader.add(jobsTitle, BorderLayout.WEST);
        jobsHeader.add(viewAllBtn, BorderLayout.EAST);
        jobsCard.add(jobsHeader, BorderLayout.NORTH);

        jobsListContainer.setLayout(new BoxLayout(jobsListContainer, BoxLayout.Y_AXIS));
        jobsListContainer.setOpaque(false);
        jobsCard.add(jobsListContainer, BorderLayout.CENTER);

        // Right Column: Placement Readiness Snapshot & Quick Actions
        ModernCard rightCard = new ModernCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel scoreTitle = new JLabel("Placement Readiness Snapshot");
        scoreTitle.setFont(UITheme.FONT_SUBTITLE);
        scoreTitle.setForeground(UITheme.TEXT_DARK);
        scoreTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(scoreTitle);
        rightCard.add(Box.createVerticalStrut(14));

        JPanel gaugeRow = new JPanel(new BorderLayout(16, 0));
        gaugeRow.setOpaque(false);
        gaugeRow.add(readinessGauge, BorderLayout.WEST);

        JPanel barsPanel = new JPanel();
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.Y_AXIS));
        barsPanel.setOpaque(false);

        addBarMetric(barsPanel, "Technical Competency (40%)", techBar);
        addBarMetric(barsPanel, "DSA & Algorithms (25%)", dsaBar);
        addBarMetric(barsPanel, "Practical Projects (20%)", projBar);
        addBarMetric(barsPanel, "Interview Preparation (15%)", interBar);

        gaugeRow.add(barsPanel, BorderLayout.CENTER);
        gaugeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(gaugeRow);
        rightCard.add(Box.createVerticalStrut(16));
        rightCard.add(new JSeparator());
        rightCard.add(Box.createVerticalStrut(14));

        JLabel qaTitle = new JLabel("Quick Action Shortcuts");
        qaTitle.setFont(UITheme.FONT_BODY_BOLD);
        qaTitle.setForeground(UITheme.TEXT_DARK);
        qaTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(qaTitle);
        rightCard.add(Box.createVerticalStrut(10));

        JPanel actionsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        actionsGrid.setOpaque(false);
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton qaSkills = new ModernButton("+ Add New Skill", ModernButton.ButtonStyle.OUTLINE);
        qaSkills.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_PROFILE);
        });

        ModernButton qaGap = new ModernButton("Analyze Target Gap", ModernButton.ButtonStyle.OUTLINE);
        qaGap.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_SKILL_GAP);
        });

        ModernButton qaCompare = new ModernButton("Compare Companies", ModernButton.ButtonStyle.OUTLINE);
        qaCompare.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_COMPARE);
        });

        ModernButton qaRoadmap = new ModernButton("Resume Roadmap", ModernButton.ButtonStyle.OUTLINE);
        qaRoadmap.addActionListener(e -> {
            if (navigationHandler != null) navigationHandler.accept(SidebarPanel.NAV_ROADMAP);
        });

        actionsGrid.add(qaSkills);
        actionsGrid.add(qaGap);
        actionsGrid.add(qaCompare);
        actionsGrid.add(qaRoadmap);
        rightCard.add(actionsGrid);

        panel.add(jobsCard);
        panel.add(rightCard);
        return panel;
    }

    private void addBarMetric(JPanel parent, String title, ModernProgressBar bar) {
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_TINY);
        l.setForeground(UITheme.TEXT_MUTED);
        parent.add(l);
        parent.add(Box.createVerticalStrut(2));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        parent.add(bar);
        parent.add(Box.createVerticalStrut(8));
    }

    /**
     * Loads dashboard data asynchronously via SwingWorker.
     * Demonstrates Multithreading in Java Swing.
     */
    public void loadDashboardData(User user) {
        this.currentUser = user;
        if (user == null) return;

        // Execute background database queries using SwingWorker
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                DashboardData data = new DashboardData();
                data.recommendations = matchingService.getRecommendations(user.getId());
                data.skillCount = skillService.getUserSkills(user.getId()).size();
                data.readiness = readinessService.getLatestScore(user.getId());
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    // Update UI components safely on EDT
                    updateUIWithData(data);
                } catch (Exception ex) {
                    System.err.println("Dashboard async loading error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateUIWithData(DashboardData data) {
        int matchCount = (int) data.recommendations.stream()
                .filter(m -> m.getMatchPercentage() >= 60).count();
        kpiJobsVal.setText(String.valueOf(matchCount > 0 ? matchCount : data.recommendations.size()));
        kpiSkillsVal.setText(String.valueOf(data.skillCount));

        if (data.readiness != null) {
            kpiReadinessVal.setText(String.format("%.0f%%", data.readiness.getTotal()));
            readinessGauge.setScore(data.readiness.getTotal());
            techBar.setValue((int) data.readiness.getTechnical());
            dsaBar.setValue((int) data.readiness.getDsa());
            projBar.setValue((int) data.readiness.getProjects());
            interBar.setValue((int) data.readiness.getInterview());
        }

        // Populate top 3 job preview items
        jobsListContainer.removeAll();
        int limit = Math.min(3, data.recommendations.size());
        for (int i = 0; i < limit; i++) {
            JobMatch match = data.recommendations.get(i);
            jobsListContainer.add(buildJobPreviewRow(match));
            if (i < limit - 1) {
                jobsListContainer.add(Box.createVerticalStrut(10));
            }
        }
        if (data.recommendations.isEmpty()) {
            JLabel empty = new JLabel("No job recommendations available. Add skills to your profile!");
            empty.setFont(UITheme.FONT_SMALL);
            empty.setForeground(UITheme.TEXT_MUTED);
            jobsListContainer.add(empty);
        }
        jobsListContainer.revalidate();
        jobsListContainer.repaint();
    }

    private JPanel buildJobPreviewRow(JobMatch match) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel logo = new JLabel(IconHelper.getMonogramIcon(match.getJob().getCompanyName(), 36, UITheme.PRIMARY_NAVY, Color.WHITE));
        row.add(logo, BorderLayout.WEST);

        JPanel mid = new JPanel(new GridLayout(2, 1, 0, 2));
        mid.setOpaque(false);
        JLabel title = new JLabel(match.getJob().getTitle());
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_DARK);

        JLabel comp = new JLabel(match.getJob().getCompanyName() + " • " + match.getJob().getSalaryFormatted());
        comp.setFont(UITheme.FONT_SMALL);
        comp.setForeground(UITheme.TEXT_MUTED);

        mid.add(title);
        mid.add(comp);
        row.add(mid, BorderLayout.CENTER);

        double pct = match.getMatchPercentage();
        ModernBadge badge = pct >= 75 ? ModernBadge.success(String.format("%.0f%%", pct)) :
                            (pct >= 40 ? ModernBadge.warning(String.format("%.0f%%", pct)) :
                                         ModernBadge.error(String.format("%.0f%%", pct)));
        row.add(badge, BorderLayout.EAST);

        return row;
    }

    private static class DashboardData {
        List<JobMatch> recommendations;
        int skillCount;
        ReadinessScore readiness;
    }
}
