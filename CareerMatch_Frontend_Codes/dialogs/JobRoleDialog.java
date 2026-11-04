package com.careermatch.ui.dialogs;

import com.careermatch.model.Company;
import com.careermatch.model.Job;
import com.careermatch.service.AdminService;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.ModernTextField;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin dialog for adding or modifying job openings and role requirements.
 */
public class JobRoleDialog extends JDialog {
    private final AdminService adminService;
    private final Job existing;
    private boolean saved = false;

    private final JComboBox<Company> companyCombo = new JComboBox<>();
    private final ModernTextField titleField = new ModernTextField("Job Title (e.g., Senior Backend Engineer)");
    private final ModernTextField minSalField = new ModernTextField("Min Salary (e.g., 90000)");
    private final ModernTextField maxSalField = new ModernTextField("Max Salary (e.g., 140000)");
    private final ModernTextField expField = new ModernTextField("Experience (e.g., 1 - 3 years)");
    private final ModernTextField skillsField = new ModernTextField("Comma-separated skills (e.g., Java, Spring Boot, SQL, Docker)");

    public JobRoleDialog(Frame parent, AdminService adminService, Job existing) {
        super(parent, existing == null ? "Add Career Job Role" : "Edit Job Role", true);
        this.adminService = adminService;
        this.existing = existing;

        setSize(520, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        List<Company> companies = adminService.viewCompanies();
        for (Company c : companies) {
            companyCombo.addItem(c);
            if (existing != null && c.getId() == existing.getCompanyId()) {
                companyCombo.setSelectedItem(c);
            }
        }

        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existing == null ? "Create New Job Opening" : "Update Job Opening");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        addField(card, "Recruiting Company", companyCombo);
        addField(card, "Role / Title", titleField);

        JPanel salPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        salPanel.setOpaque(false);
        salPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        salPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel minP = new JPanel(); minP.setLayout(new BoxLayout(minP, BoxLayout.Y_AXIS)); minP.setOpaque(false);
        JLabel minL = new JLabel("Min Salary ($ / yr)"); minL.setFont(UITheme.FONT_SMALL_BOLD);
        minP.add(minL); minP.add(Box.createVerticalStrut(2)); minP.add(minSalField);
        salPanel.add(minP);

        JPanel maxP = new JPanel(); maxP.setLayout(new BoxLayout(maxP, BoxLayout.Y_AXIS)); maxP.setOpaque(false);
        JLabel maxL = new JLabel("Max Salary ($ / yr)"); maxL.setFont(UITheme.FONT_SMALL_BOLD);
        maxP.add(maxL); maxP.add(Box.createVerticalStrut(2)); maxP.add(maxSalField);
        salPanel.add(maxP);

        card.add(salPanel);
        card.add(Box.createVerticalStrut(8));

        addField(card, "Experience Requirements", expField);
        addField(card, "Mandatory Skills (comma separated)", skillsField);

        if (existing != null) {
            titleField.setText(existing.getTitle());
            minSalField.setText(String.format("%.0f", existing.getMinSalary()));
            maxSalField.setText(String.format("%.0f", existing.getMaxSalary()));
            expField.setText(existing.getExperience());
            if (existing.getRequiredSkills() != null) {
                skillsField.setText(String.join(", ", existing.getRequiredSkills()));
            }
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        wrapper.add(card, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.OUTLINE);
        cancelBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton(existing == null ? "Create Job" : "Update Job", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.addActionListener(e -> {
            Company comp = (Company) companyCombo.getSelectedItem();
            if (comp == null) {
                JOptionPane.showMessageDialog(this, "Please select a recruiting company.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String jTitle = titleField.getText().trim();
            if (jTitle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Job title is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double minSal = 0, maxSal = 0;
            try {
                minSal = Double.parseDouble(minSalField.getText().replaceAll("[^0-9.]", ""));
                maxSal = Double.parseDouble(maxSalField.getText().replaceAll("[^0-9.]", ""));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers for salary fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String exp = expField.getText().trim();
            List<String> skills = Arrays.stream(skillsField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            boolean ok;
            if (existing == null) {
                ok = adminService.addJobWithSkills(comp.getId(), jTitle, minSal, maxSal, exp, skills);
            } else {
                existing.setCompanyId(comp.getId());
                existing.setCompanyName(comp.getName());
                existing.setTitle(jTitle);
                existing.setMinSalary(minSal);
                existing.setMaxSalary(maxSal);
                existing.setExperience(exp);
                existing.setRequiredSkills(skills);
                ok = adminService.updateJob(existing);
            }

            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save job opening.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void addField(JPanel parent, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setFont(UITheme.FONT_SMALL_BOLD);
        l.setForeground(UITheme.TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(l);
        parent.add(Box.createVerticalStrut(2));
        parent.add(field);
        parent.add(Box.createVerticalStrut(8));
    }

    public boolean isSaved() {
        return saved;
    }
}
