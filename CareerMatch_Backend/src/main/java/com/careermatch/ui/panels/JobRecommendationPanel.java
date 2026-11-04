package com.careermatch.ui.panels;

import com.careermatch.model.Job;
import com.careermatch.model.JobMatch;
import com.careermatch.model.User;
import com.careermatch.service.JobMatchingService;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.dialogs.JobDetailsDialog;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Module 3: Smart Job Recommendation.
 * Interactive dashboard presenting smart recommendations with circular/radial match gauges,
 * multi-criteria filters (keyword, company, salary, match %), sort options, and modal details.
 * Demonstrates Multithreading via SwingWorker and Java Streams API.
 */
public class JobRecommendationPanel extends JPanel {
    private final JobMatchingService matchingService;
    private final SkillService skillService;
    private final Consumer<Job> onAnalyzeGapAction;
    private User currentUser;

    // Filter Controls
    private final SearchField searchField = new SearchField("Search role, company, or skills...");
    private final JComboBox<String> companyFilter = new JComboBox<>();
    private final JComboBox<String> salaryFilter = new JComboBox<>(new String[]{"All Salaries", "$60k+ / yr", "$90k+ / yr", "$120k+ / yr"});
    private final JComboBox<String> matchFilter = new JComboBox<>(new String[]{"All Match Levels", "75%+ High Match", "50%+ Moderate", "25%+ Emerging"});
    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Match % (Highest First)", "Salary: High to Low", "Company: A to Z", "Job Title: A to Z"});

    private final JPanel cardsGridContainer = new JPanel();
    private final JLabel resultCountLabel = new JLabel("Loading recommendations...");

    public JobRecommendationPanel(JobMatchingService matchingService, SkillService skillService,
                                  Consumer<Job> onAnalyzeGapAction) {
        this.matchingService = matchingService;
        this.skillService = skillService;
        this.onAnalyzeGapAction = onAnalyzeGapAction;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));

        topSection.add(buildFilterToolbar());
        topSection.add(Box.createVerticalStrut(10));

        JPanel countRow = new JPanel(new BorderLayout());
        countRow.setOpaque(false);
        resultCountLabel.setFont(UITheme.FONT_SMALL_BOLD);
        resultCountLabel.setForeground(UITheme.TEXT_MUTED);
        countRow.add(resultCountLabel, BorderLayout.WEST);
        topSection.add(countRow);

        add(topSection, BorderLayout.NORTH);

        cardsGridContainer.setLayout(new GridBagLayout());
        cardsGridContainer.setOpaque(false);
        cardsGridContainer.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));

        JScrollPane scroll = new JScrollPane(cardsGridContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        initFilterListeners();
    }

    private JPanel buildFilterToolbar() {
        ModernCard toolbar = new ModernCard();
        toolbar.setLayout(new BorderLayout(12, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        searchField.setPreferredSize(new Dimension(280, 38));
        toolbar.add(searchField, BorderLayout.WEST);

        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtersRow.setOpaque(false);

        companyFilter.setPreferredSize(new Dimension(150, 36));
        companyFilter.addItem("All Companies");

        salaryFilter.setPreferredSize(new Dimension(130, 36));
        matchFilter.setPreferredSize(new Dimension(150, 36));
        sortCombo.setPreferredSize(new Dimension(180, 36));

        filtersRow.add(companyFilter);
        filtersRow.add(salaryFilter);
        filtersRow.add(matchFilter);
        filtersRow.add(sortCombo);

        ModernButton refreshBtn = new ModernButton("", IconHelper.getIcon(IconHelper.IconType.REFRESH, 16, UITheme.TEXT_DARK), ModernButton.ButtonStyle.OUTLINE);
        refreshBtn.setPreferredSize(new Dimension(36, 36));
        refreshBtn.setToolTipText("Refresh Opportunities");
        refreshBtn.addActionListener(e -> applyFilters());
        filtersRow.add(refreshBtn);

        toolbar.add(filtersRow, BorderLayout.CENTER);
        return toolbar;
    }

    private void initFilterListeners() {
        searchField.addActionListener(e -> applyFilters());
        companyFilter.addActionListener(e -> applyFilters());
        salaryFilter.addActionListener(e -> applyFilters());
        matchFilter.addActionListener(e -> applyFilters());
        sortCombo.addActionListener(e -> applyFilters());
    }

    public void loadRecommendations(User user) {
        this.currentUser = user;
        if (user == null) return;

        // Populate company filter list if empty
        if (companyFilter.getItemCount() <= 1) {
            SwingWorker<List<String>, Void> compWorker = new SwingWorker<>() {
                @Override
                protected List<String> doInBackground() {
                    return matchingService.getRecommendations(user.getId()).stream()
                            .map(m -> m.getJob().getCompanyName())
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());
                }

                @Override
                protected void done() {
                    try {
                        List<String> comps = get();
                        for (String c : comps) companyFilter.addItem(c);
                    } catch (Exception ignored) {}
                }
            };
            compWorker.execute();
        }

        applyFilters();
    }

    private void applyFilters() {
        if (currentUser == null) return;
        resultCountLabel.setText("Updating matches...");

        final String keyword = searchField.getText().trim();
        final String company = (String) companyFilter.getSelectedItem();

        Double minSal = null;
        int salIdx = salaryFilter.getSelectedIndex();
        if (salIdx == 1) minSal = 60000.0;
        else if (salIdx == 2) minSal = 90000.0;
        else if (salIdx == 3) minSal = 120000.0;

        Double minMatch = null;
        int matchIdx = matchFilter.getSelectedIndex();
        if (matchIdx == 1) minMatch = 75.0;
        else if (matchIdx == 2) minMatch = 50.0;
        else if (matchIdx == 3) minMatch = 25.0;

        final Double fMinSal = minSal;
        final Double fMinMatch = minMatch;
        final int sortIdx = sortCombo.getSelectedIndex();

        // Multithreaded recommendation processing with SwingWorker
        SwingWorker<List<JobMatch>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<JobMatch> doInBackground() {
                List<JobMatch> list = matchingService.getRecommendations(
                        currentUser.getId(), keyword, company, fMinSal, fMinMatch
                );

                // Apply dynamic sorting using Java Streams
                switch (sortIdx) {
                    case 0 -> list.sort(Comparator.comparingDouble(JobMatch::getMatchPercentage).reversed());
                    case 1 -> list.sort(Comparator.comparingDouble((JobMatch m) -> m.getJob().getMaxSalary()).reversed());
                    case 2 -> list.sort(Comparator.comparing(m -> m.getJob().getCompanyName().toLowerCase()));
                    case 3 -> list.sort(Comparator.comparing(m -> m.getJob().getTitle().toLowerCase()));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<JobMatch> matches = get();
                    renderJobCards(matches);
                } catch (Exception ex) {
                    resultCountLabel.setText("Error loading recommendations: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void renderJobCards(List<JobMatch> matches) {
        cardsGridContainer.removeAll();
        resultCountLabel.setText(String.format("Showing %d matching career opportunities", matches.size()));

        if (matches.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            JLabel msg = new JLabel("No jobs match your current filter criteria. Try adjusting filters.");
            msg.setFont(UITheme.FONT_BODY);
            msg.setForeground(UITheme.TEXT_MUTED);
            empty.add(msg);
            cardsGridContainer.add(empty);
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0.5;
            gbc.weighty = 0.0;

            int col = 0;
            int row = 0;
            for (JobMatch match : matches) {
                gbc.gridx = col;
                gbc.gridy = row;
                cardsGridContainer.add(createJobCard(match), gbc);

                col++;
                if (col > 1) { // 2 cards per row on standard desktop
                    col = 0;
                    row++;
                }
            }
        }

        cardsGridContainer.revalidate();
        cardsGridContainer.repaint();
    }

    private JPanel createJobCard(JobMatch match) {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setPreferredSize(new Dimension(480, 230));

        // Top Row: Logo, Title, Company, Circular Match Gauge
        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);

        JLabel logo = new JLabel(IconHelper.getMonogramIcon(match.getJob().getCompanyName(), 44, UITheme.PRIMARY_NAVY, Color.WHITE));
        topRow.add(logo, BorderLayout.WEST);

        JPanel midInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        midInfo.setOpaque(false);
        JLabel title = new JLabel(match.getJob().getTitle());
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JLabel compLoc = new JLabel(match.getJob().getCompanyName() + " • " + match.getJob().getLocation());
        compLoc.setFont(UITheme.FONT_SMALL);
        compLoc.setForeground(UITheme.TEXT_MUTED);

        midInfo.add(title);
        midInfo.add(compLoc);
        topRow.add(midInfo, BorderLayout.CENTER);

        // Circular Radial Match Meter
        RadialScoreGauge gauge = new RadialScoreGauge(match.getMatchPercentage(), 64, "");
        topRow.add(gauge, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Center Content: Metadata Pills & Skill Badges
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // Pills row (Salary, Experience)
        JPanel metaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        metaPills.setOpaque(false);
        metaPills.setAlignmentX(Component.LEFT_ALIGNMENT);

        metaPills.add(ModernBadge.neutral("💰 " + match.getJob().getSalaryFormatted()));
        metaPills.add(ModernBadge.neutral("⏱ " + match.getJob().getExperience()));
        center.add(metaPills);
        center.add(Box.createVerticalStrut(10));

        // Skills chips
        JPanel skillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        skillsRow.setOpaque(false);
        skillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        int maxSkills = Math.min(5, match.getJob().getRequiredSkills().size());
        for (int i = 0; i < maxSkills; i++) {
            skillsRow.add(ModernBadge.info(match.getJob().getRequiredSkills().get(i)));
        }
        if (match.getJob().getRequiredSkills().size() > 5) {
            skillsRow.add(ModernBadge.neutral("+" + (match.getJob().getRequiredSkills().size() - 5) + " more"));
        }
        center.add(skillsRow);
        card.add(center, BorderLayout.CENTER);

        // Bottom Row: Action Button
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        ModernButton detailsBtn = new ModernButton("View Full Details & Skill Match →", ModernButton.ButtonStyle.PRIMARY);
        detailsBtn.setFont(UITheme.FONT_SMALL_BOLD);
        detailsBtn.addActionListener(e -> {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            JobDetailsDialog dialog = new JobDetailsDialog(
                    parent, match.getJob(), currentUser.getId(),
                    matchingService instanceof com.careermatch.service.MatchingService ? (com.careermatch.service.MatchingService) matchingService : new com.careermatch.service.MatchingService(),
                    skillService,
                    job -> {
                        if (onAnalyzeGapAction != null) onAnalyzeGapAction.accept(job);
                    }
            );
            dialog.setVisible(true);
        });
        bottom.add(detailsBtn, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }
}
