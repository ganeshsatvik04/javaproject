package com.careermatch.ui.dialogs;

import com.careermatch.model.Skill;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.ModernTextField;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for adding or updating user technical skills and proficiency levels.
 */
public class AddEditSkillDialog extends JDialog {
    private final SkillService skillService;
    private final int userId;
    private boolean saved = false;

    private final ModernTextField skillField = new ModernTextField("Skill Name (e.g. Docker, Python, Java)");
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
    private final JComboBox<String> presetCombo;

    public AddEditSkillDialog(Frame parent, int userId, SkillService skillService, Skill existingSkill) {
        super(parent, existingSkill == null ? "Add Technical Skill" : "Edit Skill Proficiency", true);
        this.userId = userId;
        this.skillService = skillService;

        setSize(420, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existingSkill == null ? "Add Technical Skill" : "Update Skill Proficiency");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Keep your skill inventory up-to-date for accurate matching");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(16));

        // Quick Pick from known skills
        JLabel pickLbl = new JLabel("Select from Popular Skills (or type below):");
        pickLbl.setFont(UITheme.FONT_SMALL_BOLD);
        pickLbl.setForeground(UITheme.TEXT_MUTED);
        pickLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(pickLbl);
        card.add(Box.createVerticalStrut(4));

        List<String> allSkills = skillService.getAllSkills();
        presetCombo = new JComboBox<>(allSkills.toArray(new String[0]));
        presetCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        presetCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        presetCombo.insertItemAt("-- Choose predefined skill --", 0);
        presetCombo.setSelectedIndex(0);
        presetCombo.addActionListener(e -> {
            if (presetCombo.getSelectedIndex() > 0) {
                skillField.setText((String) presetCombo.getSelectedItem());
            }
        });
        card.add(presetCombo);
        card.add(Box.createVerticalStrut(12));

        JLabel nameLbl = new JLabel("Skill Name");
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        nameLbl.setForeground(UITheme.TEXT_DARK);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));

        skillField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        skillField.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (existingSkill != null) {
            skillField.setText(existingSkill.getName());
            skillField.setEditable(false);
        }
        card.add(skillField);
        card.add(Box.createVerticalStrut(12));

        JLabel lvlLbl = new JLabel("Proficiency Level");
        lvlLbl.setFont(UITheme.FONT_BODY_BOLD);
        lvlLbl.setForeground(UITheme.TEXT_DARK);
        lvlLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lvlLbl);
        card.add(Box.createVerticalStrut(4));

        levelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        levelCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (existingSkill != null && existingSkill.getProficiencyLevel() != null) {
            levelCombo.setSelectedItem(existingSkill.getProficiencyLevel());
        } else {
            levelCombo.setSelectedItem("Intermediate");
        }
        card.add(levelCombo);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        centerWrapper.add(card, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.OUTLINE);
        cancelBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton(existingSkill == null ? "Add Skill" : "Update Skill", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.addActionListener(e -> {
            String name = skillField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter or select a skill name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String level = (String) levelCombo.getSelectedItem();
            boolean ok = skillService.addSkill(userId, name, level);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save skill. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    public boolean isSaved() {
        return saved;
    }
}
