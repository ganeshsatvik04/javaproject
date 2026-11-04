package com.careermatch.ui.panels;

import com.careermatch.model.Job;
import com.careermatch.model.SkillGapResult;
import com.careermatch.model.User;
import com.careermatch.service.JobMatchingService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Module 5: Skill Gap Analysis.
 * Calibrates candidate skills against a target company and job opening.
 * Categorizes skills into three distinct visual tiers:
 * 1. Skills You Have (Green)
 * 2. Skills You Need (Red/Orange)
 * 3. Skills to Improve (Yellow/Amber)
 * Includes direct action to Generate Personalized Learning Roadmap.
 */
public class SkillGapPanel extends JPanel {
    private final JobMatchingService matchingService;
    private final Consumer<Job> onGenerateRoadmapAction;
    private User currentUser;
    private List<Job> allJobs = new ArrayList<>();
    private Job selectedJob = null;

    // Selection Controls
    private final JComboBox<String> companyCombo = new JComboBox<>();
    private final JComboBox<JobItem> roleCombo = new JComboBox<>();

    // Summary Metric Widgets
    private final JLabel targetJobTitle = new JLabel("Select Target Opening");
    private final JLabel targetCompanySub = new JLabel("Choose a company and role above to begin gap analysis");
    private final RadialScoreGauge matchGauge = new RadialScoreGauge(0, 110, "Match");
    private final JLabel gapPctLabel = new JLabel("0% Skill Gap");
    private final ModernProgressBar gapBar = new ModernProgressBar(0, 8);
    private final ModernButton generateRoadmapBtn = new ModernButton("Generate Learning Roadmap →", ModernButton.ButtonStyle.PRIMARY);

    // Three Distinct Sections Panels
    private final JPanel havePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    private final JPanel needPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    private final JPanel improvePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

    public SkillGapPanel(JobMatchingService matchingService, Consumer<Job> onGenerateRoadmapAction) {
        this.matchingService = matchingService;
        this.onGenerateRoadmapAction = onGenerateRoadmapAction;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        // 1. Target Selector Toolbar
        content.add(buildSelectorToolbar());
        content.add(Box.createVerticalStrut(16));

        // 2. Summary Metric Card
        content.add(buildSummaryCard());
        content.add(Box.createVerticalStrut(20));

        // 3. Three Visually Distinct Sections
        content.add(buildThreeDistinctSections());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        initComboListeners();
    }

    private JPanel buildSelectorToolbar() {
        ModernCard card = new ModernCard();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel cLbl = new JLabel("Target Company:");
        cLbl.setFont(UITheme.FONT_BODY_BOLD);
        card.add(cLbl);

        companyCombo.setPreferredSize(new Dimension(180, 36));
        card.add(companyCombo);

        JLabel rLbl = new JLabel("Target Job Role:");
        rLbl.setFont(UITheme.FONT_BODY_BOLD);
        card.add(rLbl);

        roleCombo.setPreferredSize(new Dimension(280, 36));
        card.add(roleCombo);

        return card;
    }

    private JPanel buildSummaryCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(24, 0));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left info
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        targetJobTitle.setFont(UITheme.FONT_TITLE_LARGE);
        targetJobTitle.setForeground(UITheme.TEXT_DARK);

        targetCompanySub.setFont(UITheme.FONT_BODY);
        targetCompanySub.setForeground(UITheme.TEXT_MUTED);

        gapPctLabel.setFont(UITheme.FONT_BODY_BOLD);
        gapPctLabel.setForeground(UITheme.WARNING);

        left.add(targetJobTitle);
        left.add(Box.createVerticalStrut(4));
        left.add(targetCompanySub);
        left.add(Box.createVerticalStrut(14));
        left.add(gapPctLabel);
        left.add(Box.createVerticalStrut(4));
        gapBar.setMaximumSize(new Dimension(320, 8));
        gapBar.setFillColor(UITheme.WARNING);
        left.add(gapBar);

        card.add(left, BorderLayout.CENTER);

        // Center Gauge
        card.add(matchGauge, BorderLayout.EAST);

        // Action button row at bottom
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionRow.setOpaque(false);
        generateRoadmapBtn.setFont(UITheme.FONT_BODY_BOLD);
        generateRoadmapBtn.addActionListener(e -> {
            if (selectedJob != null && onGenerateRoadmapAction != null) {
                onGenerateRoadmapAction.accept(selectedJob);
            }
        });
        actionRow.add(generateRoadmapBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.add(actionRow, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildThreeDistinctSections() {
        JPanel container = new JPanel(new GridLayout(1, 3, 16, 0));
        container.setOpaque(false);

        // 1. Skills You Have (Green)
        ModernCard haveCard = new ModernCard();
        haveCard.setCardBackground(new Color(0xFA, 0xFD, 0xFA));
        haveCard.setCardBorderColor(new Color(0xBB, 0xF7, 0xD0));
        haveCard.setLayout(new BorderLayout(0, 10));

        JLabel haveTitle = new JLabel("1. Skills You Have");
        haveTitle.setFont(UITheme.FONT_SUBTITLE);
        haveTitle.setForeground(UITheme.SUCCESS);
        haveTitle.setIcon(IconHelper.getIcon(IconHelper.IconType.CHECK, 18, UITheme.SUCCESS));
        haveCard.add(haveTitle, BorderLayout.NORTH);

        havePanel.setOpaque(false);
        haveCard.add(havePanel, BorderLayout.CENTER);

        // 2. Skills You Need (Red/Orange)
        ModernCard needCard = new ModernCard();
        needCard.setCardBackground(new Color(0xFE, 0xF8, 0xF8));
        needCard.setCardBorderColor(new Color(0xFE, 0xCD, 0xCD));
        needCard.setLayout(new BorderLayout(0, 10));

        JLabel needTitle = new JLabel("2. Skills You Need (Missing)");
        needTitle.setFont(UITheme.FONT_SUBTITLE);
        needTitle.setForeground(UITheme.ERROR);
        needTitle.setIcon(IconHelper.getIcon(IconHelper.IconType.SKILL_GAP, 18, UITheme.ERROR));
        needCard.add(needTitle, BorderLayout.NORTH);

        needPanel.setOpaque(false);
        needCard.add(needPanel, BorderLayout.CENTER);

        // 3. Skills to Improve (Yellow/Amber)
        ModernCard improveCard = new ModernCard();
        improveCard.setCardBackground(new Color(0xFF, 0xFD, 0xF5));
        improveCard.setCardBorderColor(new Color(0xFE, 0xF0, 0x8A));
        improveCard.setLayout(new BorderLayout(0, 10));

        JLabel impTitle = new JLabel("3. Skills to Improve");
        impTitle.setFont(UITheme.FONT_SUBTITLE);
        impTitle.setForeground(UITheme.WARNING);
        impTitle.setIcon(IconHelper.getIcon(IconHelper.IconType.TRENDING_UP, 18, UITheme.WARNING));
        improveCard.add(impTitle, BorderLayout.NORTH);

        improvePanel.setOpaque(false);
        improveCard.add(improvePanel, BorderLayout.CENTER);

        container.add(haveCard);
        container.add(needCard);
        container.add(improveCard);
        return container;
    }

    private void initComboListeners() {
        companyCombo.addActionListener(e -> {
            String selectedCompany = (String) companyCombo.getSelectedItem();
            if (selectedCompany != null) {
                filterRolesByCompany(selectedCompany);
            }
        });

        roleCombo.addActionListener(e -> {
            JobItem item = (JobItem) roleCombo.getSelectedItem();
            if (item != null) {
                this.selectedJob = item.job;
                analyzeSelectedJob();
            }
        });
    }

    public void loadData(User user, Job preselectedJob) {
        this.currentUser = user;
        if (user == null) return;

        SwingWorker<List<Job>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Job> doInBackground() {
                com.careermatch.dao.JobDAO dao = new com.careermatch.dao.JobDAO();
                return dao.getAllJobs();
            }

            @Override
            protected void done() {
                try {
                    allJobs = get();
                    companyCombo.removeAllItems();
                    List<String> companies = allJobs.stream()
                            .map(Job::getCompanyName)
                            .distinct()
                            .sorted()
                            .toList();

                    for (String c : companies) companyCombo.addItem(c);

                    if (preselectedJob != null) {
                        companyCombo.setSelectedItem(preselectedJob.getCompanyName());
                        filterRolesByCompany(preselectedJob.getCompanyName());
                        for (int i = 0; i < roleCombo.getItemCount(); i++) {
                            if (roleCombo.getItemAt(i).job.getId() == preselectedJob.getId()) {
                                roleCombo.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else if (!companies.isEmpty()) {
                        companyCombo.setSelectedIndex(0);
                        filterRolesByCompany(companies.get(0));
                    }
                } catch (Exception ex) {
                    System.err.println("SkillGap loadData error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void filterRolesByCompany(String companyName) {
        roleCombo.removeAllItems();
        List<Job> matched = allJobs.stream()
                .filter(j -> j.getCompanyName().equalsIgnoreCase(companyName))
                .toList();

        for (Job j : matched) {
            roleCombo.addItem(new JobItem(j));
        }

        if (roleCombo.getItemCount() > 0) {
            roleCombo.setSelectedIndex(0);
        }
    }

    private void analyzeSelectedJob() {
        if (selectedJob == null || currentUser == null) return;

        targetJobTitle.setText(selectedJob.getTitle());
        targetCompanySub.setText(selectedJob.getCompanyName() + " • " + selectedJob.getLocation() + " • " + selectedJob.getSalaryFormatted());

        SwingWorker<SkillGapResult, Void> worker = new SwingWorker<>() {
            @Override
            protected SkillGapResult doInBackground() {
                return matchingService.analyzeSkillGap(currentUser.getId(), selectedJob.getId());
            }

            @Override
            protected void done() {
                try {
                    SkillGapResult res = get();
                    if (res != null) {
                        matchGauge.setScore(res.getMatchPercentage());
                        gapPctLabel.setText(String.format("%.0f%% Competency Gap Identified", res.getGapPercentage()));
                        gapBar.setValue((int) res.getGapPercentage());

                        renderSkills(havePanel, res.getSkillsYouHave(), UITheme.SUCCESS_LIGHT, UITheme.SUCCESS, "✓ ");
                        renderSkills(needPanel, res.getSkillsYouNeed(), UITheme.ERROR_LIGHT, UITheme.ERROR, "! ");
                        renderSkills(improvePanel, res.getSkillsToImprove(), UITheme.WARNING_LIGHT, UITheme.WARNING, "⚡ ");
                    }
                } catch (Exception ex) {
                    System.err.println("SkillGap analysis error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void renderSkills(JPanel panel, List<String> skills, Color bg, Color fg, String prefix) {
        panel.removeAll();
        if (skills.isEmpty()) {
            JLabel empty = new JLabel("None identified.");
            empty.setFont(UITheme.FONT_SMALL);
            empty.setForeground(UITheme.TEXT_MUTED);
            panel.add(empty);
        } else {
            for (String s : skills) {
                panel.add(new ModernBadge(prefix + s, bg, fg));
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private static class JobItem {
        final Job job;
        JobItem(Job job) { this.job = job; }
        @Override
        public String toString() { return job.getTitle(); }
    }
}
