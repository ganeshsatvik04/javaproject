package com.careermatch.ui.dialogs;

import com.careermatch.model.LearningResource;
import com.careermatch.model.Skill;
import com.careermatch.service.AdminService;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.ModernTextField;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Admin dialog for adding or editing curated learning resources and documentation links.
 */
public class ResourceDialog extends JDialog {
    private final AdminService adminService;
    private final LearningResource existing;
    private boolean saved = false;

    private final JComboBox<Skill> skillCombo = new JComboBox<>();
    private final ModernTextField titleField = new ModernTextField("Resource Title (e.g. Official Spring Boot Quickstart)");
    private final ModernTextField urlField = new ModernTextField("URL (e.g. https://spring.io/guides/quickstart)");

    public ResourceDialog(Frame parent, AdminService adminService, LearningResource existing) {
        super(parent, existing == null ? "Add Learning Resource" : "Edit Resource", true);
        this.adminService = adminService;
        this.existing = existing;

        setSize(480, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        List<Skill> skills = adminService.viewSkills();
        for (Skill s : skills) {
            skillCombo.addItem(s);
            if (existing != null && s.getId() == existing.getSkillId()) {
                skillCombo.setSelectedItem(s);
            }
        }

        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existing == null ? "Add Learning Resource" : "Edit Resource Details");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        addField(card, "Associated Technical Skill", skillCombo);
        addField(card, "Resource Title / Course Name", titleField);
        addField(card, "Resource Web Link / URL", urlField);

        if (existing != null) {
            titleField.setText(existing.getTitle());
            urlField.setText(existing.getUrl());
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        wrapper.add(card, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.OUTLINE);
        cancelBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton(existing == null ? "Save Resource" : "Update Resource", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.addActionListener(e -> {
            Skill s = (Skill) skillCombo.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Please select an associated skill.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String t = titleField.getText().trim();
            String u = urlField.getText().trim();
            if (t.isEmpty() || u.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and URL are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = adminService.addLearningResource(s.getId(), t, u);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save resource.", "Error", JOptionPane.ERROR_MESSAGE);
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
