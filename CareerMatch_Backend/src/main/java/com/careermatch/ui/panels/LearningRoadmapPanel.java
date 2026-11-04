package com.careermatch.ui.panels;

import com.careermatch.dao.JobDAO;
import com.careermatch.model.Job;
import com.careermatch.model.LearningResource;
import com.careermatch.model.User;
import com.careermatch.service.LearningService;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.IconHelper;
import com.careermatch.ui.components.ModernBadge;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.ModernProgressBar;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Module 6: Personalized Learning Roadmap.
 * Step-based milestone timeline with curated learning resources,
 * clickable external web links, interactive completion tracking,
 * and dynamically updating progress analytics.
 * Demonstrates Multithreading via SwingWorker and desktop URI integration.
 */
public class LearningRoadmapPanel extends JPanel {
    private final LearningService learningService;
    private final SkillService skillService;
    private User currentUser;
    private Job currentJob;
    private List<Job> allJobs = new ArrayList<>();

    // Header Widgets
    private final JLabel targetRoleLabel = new JLabel("Personalized Career Roadmap");
    private final JLabel targetCompanyLabel = new JLabel("Curated learning paths tailored to close your skill gaps");
    private final JComboBox<Job> jobPickerCombo = new JComboBox<>();
    private final JLabel progressPctLabel = new JLabel("0% Completed");
    private final ModernProgressBar overallProgressBar = new ModernProgressBar(0, 10);
    private final JPanel missingSkillsChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

    // Timeline Container
    private final JPanel timelineContainer = new JPanel();

    public LearningRoadmapPanel(LearningService learningService, SkillService skillService) {
        this.learningService = learningService;
        this.skillService = skillService;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        // 1. Header Card with Job Selector and Overall Progress
        content.add(buildHeaderCard());
        content.add(Box.createVerticalStrut(20));

        // 2. Timeline Steps Section
        JLabel roadmapTitle = new JLabel("Milestone Learning Curriculum");
        roadmapTitle.setFont(UITheme.FONT_TITLE);
        roadmapTitle.setForeground(UITheme.TEXT_DARK);
        content.add(roadmapTitle);
        content.add(Box.createVerticalStrut(10));

        timelineContainer.setLayout(new BoxLayout(timelineContainer, BoxLayout.Y_AXIS));
        timelineContainer.setOpaque(false);
        content.add(timelineContainer);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        jobPickerCombo.addActionListener(e -> {
            Job selected = (Job) jobPickerCombo.getSelectedItem();
            if (selected != null && (currentJob == null || selected.getId() != currentJob.getId())) {
                this.currentJob = selected;
                refreshRoadmap();
            }
        });
    }

    private JPanel buildHeaderCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left info
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        targetRoleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        targetRoleLabel.setForeground(UITheme.PRIMARY_NAVY);

        targetCompanyLabel.setFont(UITheme.FONT_BODY);
        targetCompanyLabel.setForeground(UITheme.TEXT_MUTED);

        left.add(targetRoleLabel);
        left.add(Box.createVerticalStrut(4));
        left.add(targetCompanyLabel);
        left.add(Box.createVerticalStrut(12));

        JLabel reqSkillsTitle = new JLabel("Target Gaps Being Addressed:");
        reqSkillsTitle.setFont(UITheme.FONT_SMALL_BOLD);
        reqSkillsTitle.setForeground(UITheme.TEXT_MUTED);
        left.add(reqSkillsTitle);
        left.add(Box.createVerticalStrut(4));
        missingSkillsChips.setOpaque(false);
        left.add(missingSkillsChips);

        card.add(left, BorderLayout.CENTER);

        // Right info: Job Picker & Progress Bar
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(280, 120));

        JLabel switchLbl = new JLabel("Switch Target Job:");
        switchLbl.setFont(UITheme.FONT_SMALL_BOLD);
        switchLbl.setForeground(UITheme.TEXT_DARK);
        right.add(switchLbl);
        right.add(Box.createVerticalStrut(4));
        jobPickerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        right.add(jobPickerCombo);
        right.add(Box.createVerticalStrut(14));

        JPanel progHeader = new JPanel(new BorderLayout());
        progHeader.setOpaque(false);
        JLabel progLbl = new JLabel("Roadmap Completion");
        progLbl.setFont(UITheme.FONT_SMALL_BOLD);
        progLbl.setForeground(UITheme.TEXT_MUTED);
        progHeader.add(progLbl, BorderLayout.WEST);
        progressPctLabel.setFont(UITheme.FONT_BODY_BOLD);
        progressPctLabel.setForeground(UITheme.SUCCESS);
        progHeader.add(progressPctLabel, BorderLayout.EAST);
        right.add(progHeader);
        right.add(Box.createVerticalStrut(4));

        overallProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        overallProgressBar.setFillColor(UITheme.SUCCESS);
        right.add(overallProgressBar);

        card.add(right, BorderLayout.EAST);
        return card;
    }

    public void loadRoadmap(User user, Job targetJob) {
        this.currentUser = user;
        if (user == null) return;

        SwingWorker<List<Job>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Job> doInBackground() {
                JobDAO dao = new JobDAO();
                return dao.getAllJobs();
            }

            @Override
            protected void done() {
                try {
                    allJobs = get();
                    jobPickerCombo.removeAllItems();
                    for (Job j : allJobs) jobPickerCombo.addItem(j);

                    if (targetJob != null) {
                        for (int i = 0; i < jobPickerCombo.getItemCount(); i++) {
                            if (jobPickerCombo.getItemAt(i).getId() == targetJob.getId()) {
                                jobPickerCombo.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else if (!allJobs.isEmpty()) {
                        jobPickerCombo.setSelectedIndex(0);
                    }
                    refreshRoadmap();
                } catch (Exception ex) {
                    System.err.println("Roadmap load error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void refreshRoadmap() {
        if (currentJob == null || currentUser == null) return;

        targetRoleLabel.setText("Roadmap: " + currentJob.getTitle());
        targetCompanyLabel.setText(currentJob.getCompanyName() + " • " + currentJob.getLocation());

        SwingWorker<RoadmapData, Void> worker = new SwingWorker<>() {
            @Override
            protected RoadmapData doInBackground() {
                RoadmapData data = new RoadmapData();
                data.missingSkills = skillService.getMissingSkills(currentUser.getId(), currentJob.getRequiredSkills());
                // Fallback to all required skills if user has already met all or needs full review
                List<String> targetList = !data.missingSkills.isEmpty() ? data.missingSkills : currentJob.getRequiredSkills();
                data.resources = learningService.getRoadmapResources(targetList, currentUser.getId());
                data.progress = learningService.calculateRoadmapProgress(targetList, currentUser.getId());
                return data;
            }

            @Override
            protected void done() {
                try {
                    RoadmapData data = get();
                    renderRoadmapUI(data);
                } catch (Exception ex) {
                    System.err.println("Roadmap render error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void renderRoadmapUI(RoadmapData data) {
        // Missing skill badges
        missingSkillsChips.removeAll();
        if (data.missingSkills.isEmpty()) {
            missingSkillsChips.add(ModernBadge.success("✓ You have acquired all core required skills! Reviewing advanced resources."));
        } else {
            for (String s : data.missingSkills) {
                missingSkillsChips.add(ModernBadge.error("! " + s));
            }
        }
        missingSkillsChips.revalidate();
        missingSkillsChips.repaint();

        // Progress bar
        progressPctLabel.setText(data.progress + "% Completed");
        overallProgressBar.setValue(data.progress);

        // Timeline milestones
        timelineContainer.removeAll();
        if (data.resources.isEmpty()) {
            ModernCard emptyCard = new ModernCard();
            emptyCard.add(new JLabel("No specific resources cataloged for this role yet. Check back soon!"));
            timelineContainer.add(emptyCard);
        } else {
            for (int i = 0; i < data.resources.size(); i++) {
                LearningResource r = data.resources.get(i);
                timelineContainer.add(buildMilestoneCard(r, i + 1));
                timelineContainer.add(Box.createVerticalStrut(12));
            }
        }
        timelineContainer.revalidate();
        timelineContainer.repaint();
    }

    private JPanel buildMilestoneCard(LearningResource r, int stepNum) {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Step number circle
        JLabel stepCircle = new JLabel(String.valueOf(stepNum), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.setupRenderingHints(g2);
                g2.setColor(r.isCompleted() ? UITheme.SUCCESS : UITheme.SECONDARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        stepCircle.setPreferredSize(new Dimension(36, 36));
        stepCircle.setFont(UITheme.FONT_BODY_BOLD);
        stepCircle.setForeground(Color.WHITE);
        card.add(stepCircle, BorderLayout.WEST);

        // Center content
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setOpaque(false);

        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tagRow.setOpaque(false);
        tagRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        tagRow.add(ModernBadge.info("Skill: " + r.getSkillName()));
        tagRow.add(ModernBadge.neutral("Source: " + r.getPlatform()));
        mid.add(tagRow);
        mid.add(Box.createVerticalStrut(4));

        JLabel title = new JLabel(r.getTitle());
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mid.add(title);
        mid.add(Box.createVerticalStrut(4));

        JLabel urlDesc = new JLabel(r.getUrl());
        urlDesc.setFont(UITheme.FONT_TINY);
        urlDesc.setForeground(UITheme.TEXT_MUTED);
        urlDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        mid.add(urlDesc);

        card.add(mid, BorderLayout.CENTER);

        // Right actions: Checkbox + Clickable Link
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        rightActions.setOpaque(false);

        JCheckBox completeCheckbox = new JCheckBox("Completed", r.isCompleted());
        completeCheckbox.setFont(UITheme.FONT_SMALL_BOLD);
        completeCheckbox.setForeground(r.isCompleted() ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
        completeCheckbox.setOpaque(false);
        completeCheckbox.setFocusPainted(false);
        completeCheckbox.addActionListener(e -> {
            boolean isChecked = completeCheckbox.isSelected();
            learningService.updateProgress(currentUser.getId(), r.getId(), isChecked);
            r.setCompleted(isChecked);
            refreshRoadmap();
        });
        rightActions.add(completeCheckbox);

        ModernButton linkBtn = new ModernButton("Open Tutorial ↗", ModernButton.ButtonStyle.PRIMARY);
        linkBtn.setFont(UITheme.FONT_SMALL_BOLD);
        linkBtn.addActionListener(e -> openBrowserLink(r.getUrl()));
        rightActions.add(linkBtn);

        card.add(rightActions, BorderLayout.EAST);
        return card;
    }

    private void openBrowserLink(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                JOptionPane.showMessageDialog(this, "Link: " + url, "Learning Resource URL", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open browser. Link: " + url, "Resource Link", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class RoadmapData {
        List<String> missingSkills;
        List<LearningResource> resources;
        int progress;
    }
}
