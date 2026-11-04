package com.careermatch.ui.dialogs;

import com.careermatch.model.Job;
import com.careermatch.service.MatchingService;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.IconHelper;
import com.careermatch.ui.components.ModernBadge;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Detailed view dialog displaying job opening specifics,
 * highlighting matching skills (green) and missing skills (amber/red).
 */
public class JobDetailsDialog extends JDialog {

    public JobDetailsDialog(Frame parent, Job job, int userId,
                            MatchingService matchingService, SkillService skillService,
                            Consumer<Job> onAnalyzeGapAction) {
        super(parent, "Job Opening Details — " + job.getTitle(), true);
        setSize(560, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        Set<String> userSkills = skillService.getUserSkills(userId);
        double matchPct = matchingService.calculateMatch(userSkills, job.getRequiredSkills());

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel logo = new JLabel(IconHelper.getMonogramIcon(job.getCompanyName(), 48, UITheme.PRIMARY_NAVY, Color.WHITE));
        header.add(logo, BorderLayout.WEST);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGrp.setOpaque(false);
        JLabel titleLbl = new JLabel(job.getTitle());
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_DARK);

        JLabel compLbl = new JLabel(job.getCompanyName() + " • " + job.getLocation());
        compLbl.setFont(UITheme.FONT_BODY);
        compLbl.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(titleLbl);
        titleGrp.add(compLbl);
        header.add(titleGrp, BorderLayout.CENTER);

        ModernBadge matchBadge = matchPct >= 75 ? ModernBadge.success(String.format("%.0f%% Match", matchPct)) :
                                 (matchPct >= 40 ? ModernBadge.warning(String.format("%.0f%% Match", matchPct)) :
                                                   ModernBadge.error(String.format("%.0f%% Match", matchPct)));
        header.add(matchBadge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Body Content Card
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        addMetaRow(card, "Estimated Compensation", job.getSalaryFormatted(), UITheme.SUCCESS);
        addMetaRow(card, "Experience Requirement", job.getExperience(), UITheme.TEXT_DARK);
        addMetaRow(card, "Education / Eligibility", job.getEligibility(), UITheme.TEXT_DARK);

        card.add(Box.createVerticalStrut(14));
        card.add(new JSeparator(JSeparator.HORIZONTAL));
        card.add(Box.createVerticalStrut(14));

        JLabel skillsTitle = new JLabel("Skills Competency Breakdown");
        skillsTitle.setFont(UITheme.FONT_BODY_BOLD);
        skillsTitle.setForeground(UITheme.TEXT_DARK);
        skillsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(skillsTitle);
        card.add(Box.createVerticalStrut(8));

        // Matching skills section
        JLabel matchLbl = new JLabel("Skills You Have (Matched):");
        matchLbl.setFont(UITheme.FONT_SMALL_BOLD);
        matchLbl.setForeground(UITheme.SUCCESS);
        matchLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(matchLbl);
        card.add(Box.createVerticalStrut(4));

        JPanel matchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        matchPanel.setOpaque(false);
        matchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean hasMatched = false;
        for (String req : job.getRequiredSkills()) {
            if (userSkills.contains(req.trim().toLowerCase())) {
                matchPanel.add(ModernBadge.success("✓ " + req.trim()));
                hasMatched = true;
            }
        }
        if (!hasMatched) {
            JLabel none = new JLabel("No matching skills found in your profile yet.");
            none.setFont(UITheme.FONT_SMALL);
            none.setForeground(UITheme.TEXT_MUTED);
            matchPanel.add(none);
        }
        card.add(matchPanel);
        card.add(Box.createVerticalStrut(10));

        // Missing skills section
        JLabel missLbl = new JLabel("Skills You Need to Acquire:");
        missLbl.setFont(UITheme.FONT_SMALL_BOLD);
        missLbl.setForeground(UITheme.ERROR);
        missLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(missLbl);
        card.add(Box.createVerticalStrut(4));

        JPanel missPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        missPanel.setOpaque(false);
        missPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean hasMissing = false;
        for (String req : job.getRequiredSkills()) {
            if (!userSkills.contains(req.trim().toLowerCase())) {
                missPanel.add(ModernBadge.error("! " + req.trim()));
                hasMissing = true;
            }
        }
        if (!hasMissing) {
            JLabel allMatched = new JLabel("Congratulations! You possess 100% of required skills!");
            allMatched.setFont(UITheme.FONT_SMALL);
            allMatched.setForeground(UITheme.SUCCESS);
            missPanel.add(allMatched);
        }
        card.add(missPanel);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        centerWrapper.add(card, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footer.setOpaque(false);

        ModernButton closeBtn = new ModernButton("Close", ModernButton.ButtonStyle.OUTLINE);
        closeBtn.addActionListener(e -> dispose());

        ModernButton gapBtn = new ModernButton("Analyze Skill Gap & Roadmap →", ModernButton.ButtonStyle.PRIMARY);
        gapBtn.addActionListener(e -> {
            dispose();
            if (onAnalyzeGapAction != null) {
                onAnalyzeGapAction.accept(job);
            }
        });

        footer.add(closeBtn);
        footer.add(gapBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void addMetaRow(JPanel parent, String labelText, String valText, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(labelText);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_MUTED);

        JLabel v = new JLabel(valText);
        v.setFont(UITheme.FONT_BODY_BOLD);
        v.setForeground(valColor);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(4));
    }
}
