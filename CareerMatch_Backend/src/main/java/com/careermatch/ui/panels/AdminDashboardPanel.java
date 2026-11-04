package com.careermatch.ui.panels;

import com.careermatch.model.*;
import com.careermatch.service.AdminService;
import com.careermatch.ui.components.*;
import com.careermatch.ui.dialogs.CompanyDialog;
import com.careermatch.ui.dialogs.JobRoleDialog;
import com.careermatch.ui.dialogs.ResourceDialog;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module 8: Admin Dashboard.
 * Comprehensive administrative management console for platform data entities:
 * Users, Companies, Job Roles, Skills, and Learning Resources.
 * Demonstrates complete JDBC CRUD operations, Search, Multithreading, and Dialog Forms.
 */
public class AdminDashboardPanel extends JPanel {
    private final AdminService adminService;

    // Stat Cards Labels
    private final JLabel statUsersVal = new JLabel("0");
    private final JLabel statCompsVal = new JLabel("0");
    private final JLabel statJobsVal = new JLabel("0");
    private final JLabel statSkillsVal = new JLabel("0");

    // Tabbed Tables
    private final JTabbedPane tabbedPane = new JTabbedPane();

    // 1. Users Tab
    private final DefaultTableModel usersModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role", "Degree / Major"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ModernTable usersTable = new ModernTable(usersModel);
    private final SearchField userSearch = new SearchField("Search registered candidates...");
    private List<User> allUsersList;

    // 2. Companies Tab
    private final DefaultTableModel compsModel = new DefaultTableModel(new String[]{"ID", "Company Name", "Headquarters / Locations"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ModernTable compsTable = new ModernTable(compsModel);
    private final SearchField compSearch = new SearchField("Search companies...");
    private List<Company> allCompsList;

    // 3. Job Roles Tab
    private final DefaultTableModel jobsModel = new DefaultTableModel(new String[]{"ID", "Company", "Job Title", "Salary Range", "Experience", "Required Skills"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ModernTable jobsTable = new ModernTable(jobsModel);
    private final SearchField jobSearch = new SearchField("Search job roles...");
    private List<Job> allJobsList;

    // 4. Skills Tab
    private final DefaultTableModel skillsModel = new DefaultTableModel(new String[]{"ID", "Skill Name", "Category"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ModernTable skillsTable = new ModernTable(skillsModel);
    private final SearchField skillSearch = new SearchField("Search skills catalog...");
    private List<Skill> allSkillsList;

    // 5. Resources Tab
    private final DefaultTableModel resModel = new DefaultTableModel(new String[]{"ID", "Skill Name", "Resource Title", "URL / Platform"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ModernTable resTable = new ModernTable(resModel);
    private final SearchField resSearch = new SearchField("Search learning courses & resources...");
    private List<LearningResource> allResList;

    public AdminDashboardPanel(AdminService adminService) {
        this.adminService = adminService;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        // 1. Top Stat Cards
        content.add(buildStatCardsRow());
        content.add(Box.createVerticalStrut(20));

        // 2. Main Tabbed Console
        content.add(buildTabbedConsole());

        add(content, BorderLayout.CENTER);
        initSearchListeners();
    }

    private JPanel buildStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        row.add(createStatCard("Registered Users", statUsersVal, IconHelper.IconType.PROFILE, UITheme.SECONDARY_BLUE));
        row.add(createStatCard("Recruiting Companies", statCompsVal, IconHelper.IconType.BUILDING, UITheme.SUCCESS));
        row.add(createStatCard("Active Job Roles", statJobsVal, IconHelper.IconType.JOBS, UITheme.ACCENT_TEAL));
        row.add(createStatCard("Skills in Catalog", statSkillsVal, IconHelper.IconType.SKILL_GAP, UITheme.WARNING));

        return row;
    }

    private JPanel createStatCard(String title, JLabel valLabel, IconHelper.IconType iconType, Color iconColor) {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel textGrp = new JPanel();
        textGrp.setLayout(new BoxLayout(textGrp, BoxLayout.Y_AXIS));
        textGrp.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL_BOLD);
        t.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(UITheme.TEXT_DARK);

        textGrp.add(t);
        textGrp.add(Box.createVerticalStrut(4));
        textGrp.add(valLabel);

        card.add(textGrp, BorderLayout.CENTER);

        JLabel iconBadge = new JLabel(IconHelper.getIcon(iconType, 24, iconColor));
        iconBadge.setVerticalAlignment(SwingConstants.TOP);
        card.add(iconBadge, BorderLayout.EAST);

        return card;
    }

    private JPanel buildTabbedConsole() {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout());

        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Manage Users", buildUsersTab());
        tabbedPane.addTab("Manage Companies", buildCompaniesTab());
        tabbedPane.addTab("Manage Job Roles", buildJobRolesTab());
        tabbedPane.addTab("Manage Skills", buildSkillsTab());
        tabbedPane.addTab("Manage Learning Resources", buildResourcesTab());

        card.add(tabbedPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        userSearch.setPreferredSize(new Dimension(300, 36));
        toolbar.add(userSearch, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        ModernButton delBtn = new ModernButton("Delete User", ModernButton.ButtonStyle.DANGER);
        delBtn.addActionListener(e -> deleteSelectedUser());
        actions.add(delBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        panel.add(ModernTable.createScrollPane(usersTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCompaniesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        compSearch.setPreferredSize(new Dimension(300, 36));
        toolbar.add(compSearch, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Company", ModernButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> openCompanyDialog(null));

        ModernButton editBtn = new ModernButton("Edit Company", ModernButton.ButtonStyle.OUTLINE);
        editBtn.addActionListener(e -> {
            int row = compsTable.getSelectedRow();
            if (row < 0 || allCompsList == null || row >= allCompsList.size()) {
                JOptionPane.showMessageDialog(this, "Select a company to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openCompanyDialog(allCompsList.get(row));
        });

        ModernButton delBtn = new ModernButton("Delete", ModernButton.ButtonStyle.DANGER);
        delBtn.addActionListener(e -> deleteSelectedCompany());

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        panel.add(ModernTable.createScrollPane(compsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildJobRolesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        jobSearch.setPreferredSize(new Dimension(300, 36));
        toolbar.add(jobSearch, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Job Role", ModernButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> openJobRoleDialog(null));

        ModernButton editBtn = new ModernButton("Edit Job", ModernButton.ButtonStyle.OUTLINE);
        editBtn.addActionListener(e -> {
            int row = jobsTable.getSelectedRow();
            if (row < 0 || allJobsList == null || row >= allJobsList.size()) {
                JOptionPane.showMessageDialog(this, "Select a job role to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openJobRoleDialog(allJobsList.get(row));
        });

        ModernButton delBtn = new ModernButton("Delete", ModernButton.ButtonStyle.DANGER);
        delBtn.addActionListener(e -> deleteSelectedJob());

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        panel.add(ModernTable.createScrollPane(jobsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSkillsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        skillSearch.setPreferredSize(new Dimension(300, 36));
        toolbar.add(skillSearch, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Skill", ModernButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter New Technical Skill Name:", "Add Skill", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                adminService.addSkill(name.trim());
                loadAdminData();
            }
        });

        ModernButton delBtn = new ModernButton("Delete", ModernButton.ButtonStyle.DANGER);
        delBtn.addActionListener(e -> deleteSelectedSkill());

        actions.add(addBtn);
        actions.add(delBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        panel.add(ModernTable.createScrollPane(skillsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildResourcesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        resSearch.setPreferredSize(new Dimension(300, 36));
        toolbar.add(resSearch, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Resource", ModernButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> openResourceDialog(null));

        ModernButton delBtn = new ModernButton("Delete", ModernButton.ButtonStyle.DANGER);
        delBtn.addActionListener(e -> deleteSelectedResource());

        actions.add(addBtn);
        actions.add(delBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        panel.add(ModernTable.createScrollPane(resTable), BorderLayout.CENTER);
        return panel;
    }

    private void initSearchListeners() {
        userSearch.addActionListener(e -> filterUsers());
        compSearch.addActionListener(e -> filterCompanies());
        jobSearch.addActionListener(e -> filterJobs());
        skillSearch.addActionListener(e -> filterSkills());
        resSearch.addActionListener(e -> filterResources());
    }

    public void loadAdminData() {
        SwingWorker<AdminData, Void> worker = new SwingWorker<>() {
            @Override
            protected AdminData doInBackground() {
                AdminData data = new AdminData();
                data.users = adminService.viewAllUsers();
                data.comps = adminService.viewCompanies();
                data.jobs = adminService.viewJobs();
                data.skills = adminService.viewSkills();
                data.resources = adminService.viewResources();
                return data;
            }

            @Override
            protected void done() {
                try {
                    AdminData data = get();
                    allUsersList = data.users;
                    allCompsList = data.comps;
                    allJobsList = data.jobs;
                    allSkillsList = data.skills;
                    allResList = data.resources;

                    // Update Top Stats
                    statUsersVal.setText(String.valueOf(data.users.size()));
                    statCompsVal.setText(String.valueOf(data.comps.size()));
                    statJobsVal.setText(String.valueOf(data.jobs.size()));
                    statSkillsVal.setText(String.valueOf(data.skills.size()));

                    filterUsers();
                    filterCompanies();
                    filterJobs();
                    filterSkills();
                    filterResources();
                } catch (Exception ex) {
                    System.err.println("Admin data load error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void filterUsers() {
        if (allUsersList == null) return;
        String kw = userSearch.getText().trim().toLowerCase();
        usersModel.setRowCount(0);
        for (User u : allUsersList) {
            if (kw.isEmpty() || u.getName().toLowerCase().contains(kw) || u.getEmail().toLowerCase().contains(kw)) {
                String edu = u instanceof JobSeeker s ? s.getEducation() : "System Admin";
                usersModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole(), edu});
            }
        }
    }

    private void filterCompanies() {
        if (allCompsList == null) return;
        String kw = compSearch.getText().trim().toLowerCase();
        compsModel.setRowCount(0);
        for (Company c : allCompsList) {
            if (kw.isEmpty() || c.getName().toLowerCase().contains(kw) || (c.getLocation() != null && c.getLocation().toLowerCase().contains(kw))) {
                compsModel.addRow(new Object[]{c.getId(), c.getName(), c.getLocation()});
            }
        }
    }

    private void filterJobs() {
        if (allJobsList == null) return;
        String kw = jobSearch.getText().trim().toLowerCase();
        jobsModel.setRowCount(0);
        for (Job j : allJobsList) {
            if (kw.isEmpty() || j.getTitle().toLowerCase().contains(kw) || j.getCompanyName().toLowerCase().contains(kw)) {
                String skills = j.getRequiredSkills() != null ? String.join(", ", j.getRequiredSkills()) : "";
                jobsModel.addRow(new Object[]{
                        j.getId(), j.getCompanyName(), j.getTitle(),
                        j.getSalaryFormatted(), j.getExperience(), skills
                });
            }
        }
    }

    private void filterSkills() {
        if (allSkillsList == null) return;
        String kw = skillSearch.getText().trim().toLowerCase();
        skillsModel.setRowCount(0);
        for (Skill s : allSkillsList) {
            if (kw.isEmpty() || s.getName().toLowerCase().contains(kw)) {
                skillsModel.addRow(new Object[]{s.getId(), s.getName(), s.getCategory()});
            }
        }
    }

    private void filterResources() {
        if (allResList == null) return;
        String kw = resSearch.getText().trim().toLowerCase();
        resModel.setRowCount(0);
        for (LearningResource r : allResList) {
            if (kw.isEmpty() || r.getTitle().toLowerCase().contains(kw) || r.getSkillName().toLowerCase().contains(kw)) {
                resModel.addRow(new Object[]{r.getId(), r.getSkillName(), r.getTitle(), r.getUrl()});
            }
        }
    }

    private void deleteSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0 || allUsersList == null || row >= allUsersList.size()) {
            JOptionPane.showMessageDialog(this, "Select a user to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User u = allUsersList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + u.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.removeUser(u.getId());
            loadAdminData();
        }
    }

    private void deleteSelectedCompany() {
        int row = compsTable.getSelectedRow();
        if (row < 0 || allCompsList == null || row >= allCompsList.size()) {
            JOptionPane.showMessageDialog(this, "Select a company to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Company c = allCompsList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete company '" + c.getName() + "' and its associated job roles?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.removeCompany(c.getId());
            loadAdminData();
        }
    }

    private void deleteSelectedJob() {
        int row = jobsTable.getSelectedRow();
        if (row < 0 || allJobsList == null || row >= allJobsList.size()) {
            JOptionPane.showMessageDialog(this, "Select a job role to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Job j = allJobsList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete job role '" + j.getTitle() + "' at " + j.getCompanyName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.removeJob(j.getId());
            loadAdminData();
        }
    }

    private void deleteSelectedSkill() {
        int row = skillsTable.getSelectedRow();
        if (row < 0 || allSkillsList == null || row >= allSkillsList.size()) {
            JOptionPane.showMessageDialog(this, "Select a skill to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Skill s = allSkillsList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete skill '" + s.getName() + "' from platform catalog?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.removeSkill(s.getId());
            loadAdminData();
        }
    }

    private void deleteSelectedResource() {
        int row = resTable.getSelectedRow();
        if (row < 0 || allResList == null || row >= allResList.size()) {
            JOptionPane.showMessageDialog(this, "Select a learning resource to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LearningResource r = allResList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete resource '" + r.getTitle() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.removeLearningResource(r.getId());
            loadAdminData();
        }
    }

    private void openCompanyDialog(Company c) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CompanyDialog dialog = new CompanyDialog(parent, adminService, c);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadAdminData();
    }

    private void openJobRoleDialog(Job j) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JobRoleDialog dialog = new JobRoleDialog(parent, adminService, j);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadAdminData();
    }

    private void openResourceDialog(LearningResource r) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ResourceDialog dialog = new ResourceDialog(parent, adminService, r);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadAdminData();
    }

    private static class AdminData {
        List<User> users;
        List<Company> comps;
        List<Job> jobs;
        List<Skill> skills;
        List<LearningResource> resources;
    }
}
