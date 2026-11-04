package com.careermatch.ui.panels;

import com.careermatch.dao.UserDAO;
import com.careermatch.model.JobSeeker;
import com.careermatch.model.Skill;
import com.careermatch.model.User;
import com.careermatch.service.SkillService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.dialogs.AddEditSkillDialog;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Module 2: User Profile and Skill Management.
 * Manages candidate personal information, education, graduation year,
 * career interests, experience level, and technical skills with proficiency tags.
 */
public class ProfilePanel extends JPanel {
    private final SkillService skillService;
    private final UserDAO userDAO;
    private User currentUser;

    // Profile Details Fields
    private final ModernTextField nameField = new ModernTextField("Full Name");
    private final ModernTextField emailField = new ModernTextField("Email Address");
    private final ModernTextField educationField = new ModernTextField("Education (e.g. B.Tech Computer Science)");
    private final JComboBox<String> gradYearCombo = new JComboBox<>(new String[]{"2023", "2024", "2025", "2026", "2027", "2028"});
    private final JComboBox<String> expLevelCombo = new JComboBox<>(new String[]{"Fresher / Entry Level", "Junior (1-2 years)", "Mid-Level (3-5 years)", "Senior (5+ years)"});
    private final ModernTextField interestsField = new ModernTextField("Career Interests (e.g., Cloud Architecture, Java APIs)");

    // Skills Table & Model
    private final String[] columns = {"Skill Name", "Category", "Proficiency Level"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final ModernTable skillsTable = new ModernTable(tableModel);
    private List<Skill> currentSkills;

    public ProfilePanel(SkillService skillService, UserDAO userDAO) {
        this.skillService = skillService;
        this.userDAO = userDAO;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridLayout(1, 2, 24, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        content.add(buildProfileInfoCard());
        content.add(buildSkillsManagementCard());

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildProfileInfoCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Personal & Academic Profile");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Information used for role eligibility and match calibration");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(16));

        addField(card, "Full Name", nameField);
        addField(card, "Email Address (Login ID)", emailField);
        emailField.setEditable(false); // Email is identity key
        addField(card, "Degree / Major", educationField);

        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel gp = new JPanel(); gp.setLayout(new BoxLayout(gp, BoxLayout.Y_AXIS)); gp.setOpaque(false);
        JLabel gl = new JLabel("Graduation Year"); gl.setFont(UITheme.FONT_SMALL_BOLD);
        gp.add(gl); gp.add(Box.createVerticalStrut(2)); gp.add(gradYearCombo);
        row.add(gp);

        JPanel ep = new JPanel(); ep.setLayout(new BoxLayout(ep, BoxLayout.Y_AXIS)); ep.setOpaque(false);
        JLabel el = new JLabel("Experience Level"); el.setFont(UITheme.FONT_SMALL_BOLD);
        ep.add(el); ep.add(Box.createVerticalStrut(2)); ep.add(expLevelCombo);
        row.add(ep);

        card.add(row);
        card.add(Box.createVerticalStrut(10));

        addField(card, "Career Interests & Specializations", interestsField);

        card.add(Box.createVerticalStrut(14));
        ModernButton saveBtn = new ModernButton("Save Profile Details", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfileDetails());
        card.add(saveBtn);

        return card;
    }

    private JPanel buildSkillsManagementCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(0, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Technical Skills Inventory");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);

        JLabel sub = new JLabel("Proficiencies used to calculate job recommendation percentages");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(4));
        titleGrp.add(sub);
        top.add(titleGrp, BorderLayout.CENTER);

        // Action Buttons Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toolbar.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Skill", ModernButton.ButtonStyle.PRIMARY);
        addBtn.setFont(UITheme.FONT_SMALL_BOLD);
        addBtn.addActionListener(e -> openAddSkillDialog());

        ModernButton editBtn = new ModernButton("Edit Level", ModernButton.ButtonStyle.OUTLINE);
        editBtn.setFont(UITheme.FONT_SMALL_BOLD);
        editBtn.addActionListener(e -> openEditSkillDialog());

        ModernButton delBtn = new ModernButton("Delete", ModernButton.ButtonStyle.DANGER);
        delBtn.setFont(UITheme.FONT_SMALL_BOLD);
        delBtn.addActionListener(e -> deleteSelectedSkill());

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(delBtn);
        top.add(toolbar, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);

        // Skills Table
        JScrollPane tableScroll = ModernTable.createScrollPane(skillsTable);
        card.add(tableScroll, BorderLayout.CENTER);

        return card;
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
        parent.add(Box.createVerticalStrut(10));
    }

    public void loadProfile(User user) {
        this.currentUser = user;
        if (user == null) return;

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        if (user instanceof JobSeeker seeker) {
            educationField.setText(seeker.getEducation());
            interestsField.setText(seeker.getInterests());
            gradYearCombo.setSelectedItem(seeker.getGraduationYear());
            expLevelCombo.setSelectedItem(seeker.getExperienceLevel());
        }

        refreshSkillsTable();
    }

    private void refreshSkillsTable() {
        if (currentUser == null) return;
        SwingWorker<List<Skill>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Skill> doInBackground() {
                return skillService.getUserSkillObjects(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    currentSkills = get();
                    tableModel.setRowCount(0);
                    for (Skill s : currentSkills) {
                        tableModel.addRow(new Object[]{
                                s.getName(),
                                s.getCategory(),
                                s.getProficiencyLevel()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading user skills: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void saveProfileDetails() {
        if (currentUser == null) return;

        String name = nameField.getText().trim();
        String edu = educationField.getText().trim();
        String interests = interestsField.getText().trim();
        String gradYear = (String) gradYearCombo.getSelectedItem();
        String expLevel = (String) expLevelCombo.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.setName(name);
        if (currentUser instanceof JobSeeker seeker) {
            seeker.setEducation(edu);
            seeker.setInterests(interests);
            seeker.setGraduationYear(gradYear);
            seeker.setExperienceLevel(expLevel);
            userDAO.update(seeker);
        }

        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAddSkillDialog() {
        if (currentUser == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        AddEditSkillDialog dialog = new AddEditSkillDialog(parent, currentUser.getId(), skillService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshSkillsTable();
        }
    }

    private void openEditSkillDialog() {
        int row = skillsTable.getSelectedRow();
        if (row < 0 || currentSkills == null || row >= currentSkills.size()) {
            JOptionPane.showMessageDialog(this, "Please select a skill from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Skill selectedSkill = currentSkills.get(row);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        AddEditSkillDialog dialog = new AddEditSkillDialog(parent, currentUser.getId(), skillService, selectedSkill);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshSkillsTable();
        }
    }

    private void deleteSelectedSkill() {
        int row = skillsTable.getSelectedRow();
        if (row < 0 || currentSkills == null || row >= currentSkills.size()) {
            JOptionPane.showMessageDialog(this, "Please select a skill from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Skill s = currentSkills.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove '" + s.getName() + "' from your profile?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            skillService.removeSkill(currentUser.getId(), s.getName());
            refreshSkillsTable();
        }
    }
}
