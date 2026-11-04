package com.careermatch.ui;

import com.careermatch.dao.UserDAO;
import com.careermatch.model.Job;
import com.careermatch.model.User;
import com.careermatch.service.*;
import com.careermatch.ui.dialogs.LoginRegisterDialog;
import com.careermatch.ui.panels.*;
import com.careermatch.ui.theme.UITheme;
import com.careermatch.util.Database;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame for CareerMatch.
 * Unified SaaS desktop platform coordinating all eight modules:
 * - Module 1: User Registration and Login
 * - Module 2: User Profile and Skill Management
 * - Module 3: Smart Job Recommendation
 * - Module 4: Company Comparison
 * - Module 5: Skill Gap Analysis
 * - Module 6: Personalized Learning Roadmap
 * - Module 7: Placement Readiness Score
 * - Module 8: Admin Dashboard
 * Uses CardLayout navigation and role-based permissions.
 */
public class MainFrame extends JFrame {

    private final AuthService authService = new AuthServiceImpl();
    private final UserDAO userDAO = new UserDAO();
    private final SkillService skillService = new SkillService();
    private final MatchingService matchingService = new MatchingService();
    private final CompanyService companyService = new CompanyService();
    private final CompanyComparisonService comparisonService = new CompanyComparisonService();
    private final LearningService learningService = new LearningService();
    private final ReadinessService readinessService = new ReadinessService();
    private final AdminService adminService = new AdminService();

    private User currentUser;

    // Navigation and Layout
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardsPanel = new JPanel(cardLayout);

    private final SidebarPanel sidebarPanel;
    private final TopNavPanel topNavPanel;

    // Module Panels
    private final DashboardPanel dashboardPanel;
    private final ProfilePanel profilePanel;
    private final JobRecommendationPanel jobRecPanel;
    private final CompanyComparisonPanel companyCompPanel;
    private final SkillGapPanel skillGapPanel;
    private final LearningRoadmapPanel roadmapPanel;
    private final PlacementReadinessPanel readinessPanel;
    private final AdminDashboardPanel adminPanel;
    private final SettingsPanel settingsPanel;

    public MainFrame() {
        super("CareerMatch – Smart Career Planning and Job Recommendation System");

        // Apply theme look and feel
        UITheme.applyLookAndFeel();

        // 1366 x 768 recommended desktop resolution, minimum 1100 x 680
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sidebar Navigation
        sidebarPanel = new SidebarPanel(this::navigateTo, this::handleLogout);

        // Top Navigation Bar
        topNavPanel = new TopNavPanel(this::handleGlobalSearch);

        // Initialize Module Panels
        dashboardPanel = new DashboardPanel(matchingService, skillService, readinessService, learningService, this::navigateTo);
        profilePanel = new ProfilePanel(skillService, userDAO);
        jobRecPanel = new JobRecommendationPanel(matchingService, skillService, this::navigateToSkillGapWithJob);
        companyCompPanel = new CompanyComparisonPanel(comparisonService, companyService);
        skillGapPanel = new SkillGapPanel(matchingService, this::navigateToRoadmapWithJob);
        roadmapPanel = new LearningRoadmapPanel(learningService, skillService);
        readinessPanel = new PlacementReadinessPanel(readinessService);
        adminPanel = new AdminDashboardPanel(adminService);
        settingsPanel = new SettingsPanel();

        // Register Content Cards
        contentCardsPanel.add(dashboardPanel, SidebarPanel.NAV_DASHBOARD);
        contentCardsPanel.add(profilePanel, SidebarPanel.NAV_PROFILE);
        contentCardsPanel.add(jobRecPanel, SidebarPanel.NAV_JOBS);
        contentCardsPanel.add(companyCompPanel, SidebarPanel.NAV_COMPARE);
        contentCardsPanel.add(skillGapPanel, SidebarPanel.NAV_SKILL_GAP);
        contentCardsPanel.add(roadmapPanel, SidebarPanel.NAV_ROADMAP);
        contentCardsPanel.add(readinessPanel, SidebarPanel.NAV_READINESS);
        contentCardsPanel.add(adminPanel, SidebarPanel.NAV_ADMIN);
        contentCardsPanel.add(settingsPanel, SidebarPanel.NAV_SETTINGS);

        // Master Layout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(topNavPanel, BorderLayout.NORTH);
        mainContainer.add(contentCardsPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(mainContainer, BorderLayout.CENTER);
    }

    public void startApplication() {
        // Initialize schema and sample data
        Database.createTables();

        // Show Authentication Dialog first
        promptAuthentication();
    }

    private void promptAuthentication() {
        LoginRegisterDialog dialog = new LoginRegisterDialog(this, authService);
        dialog.setVisible(true);

        User authenticated = dialog.getAuthenticatedUser();
        if (authenticated != null) {
            setCurrentUser(authenticated);
            setVisible(true);
        } else {
            // User closed dialog without authenticating
            System.exit(0);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        sidebarPanel.setCurrentUser(user);
        topNavPanel.setUser(user);
        settingsPanel.setUser(user);

        // Navigate to default view based on role
        if (user.isAdmin()) {
            navigateTo(SidebarPanel.NAV_ADMIN);
        } else {
            navigateTo(SidebarPanel.NAV_DASHBOARD);
        }
    }

    public void navigateTo(String key) {
        // Enforce role-based access
        if (SidebarPanel.NAV_ADMIN.equals(key) && (currentUser == null || !currentUser.isAdmin())) {
            JOptionPane.showMessageDialog(this, "Access Denied: The Admin Console requires administrative privileges.", "Authorization Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sidebarPanel.setSelectedNav(key);
        cardLayout.show(contentCardsPanel, key);

        // Update Top Navigation Bar Title and refresh panel data
        switch (key) {
            case SidebarPanel.NAV_DASHBOARD -> {
                topNavPanel.setPageTitle("Dashboard Overview", "Welcome back, " + currentUser.getName() + " • Real-Time Career Intelligence");
                dashboardPanel.loadDashboardData(currentUser);
            }
            case SidebarPanel.NAV_PROFILE -> {
                topNavPanel.setPageTitle("My Profile & Technical Skills", "Manage academic credentials, experience levels, and skill competencies");
                profilePanel.loadProfile(currentUser);
            }
            case SidebarPanel.NAV_JOBS -> {
                topNavPanel.setPageTitle("Smart Job Recommendations", "Algorithmic matching across hiring partners based on your profile skills");
                jobRecPanel.loadRecommendations(currentUser);
            }
            case SidebarPanel.NAV_COMPARE -> {
                topNavPanel.setPageTitle("Company Comparison Analysis", "Side-by-side criteria evaluation across hiring organizations");
                companyCompPanel.loadData(currentUser);
            }
            case SidebarPanel.NAV_SKILL_GAP -> {
                topNavPanel.setPageTitle("Skill Gap Analysis", "Identify acquired, missing, and improving skills for your target role");
                skillGapPanel.loadData(currentUser, null);
            }
            case SidebarPanel.NAV_ROADMAP -> {
                topNavPanel.setPageTitle("Personalized Learning Roadmap", "Step-by-step milestone curriculum with curated resources");
                roadmapPanel.loadRoadmap(currentUser, null);
            }
            case SidebarPanel.NAV_READINESS -> {
                topNavPanel.setPageTitle("Placement Readiness Analytics", "Comprehensive evaluation across Technical, DSA, Projects, and Interview");
                readinessPanel.loadReadinessData(currentUser);
            }
            case SidebarPanel.NAV_ADMIN -> {
                topNavPanel.setPageTitle("Platform Administration Console", "Manage users, companies, job roles, required skills, and resources");
                adminPanel.loadAdminData();
            }
            case SidebarPanel.NAV_SETTINGS -> {
                topNavPanel.setPageTitle("Settings & System Diagnostics", "Session configuration, database connection, and architectural specs");
            }
        }
    }

    private void navigateToSkillGapWithJob(Job job) {
        sidebarPanel.setSelectedNav(SidebarPanel.NAV_SKILL_GAP);
        cardLayout.show(contentCardsPanel, SidebarPanel.NAV_SKILL_GAP);
        topNavPanel.setPageTitle("Skill Gap Analysis", "Analyzing requirements for " + job.getTitle() + " at " + job.getCompanyName());
        skillGapPanel.loadData(currentUser, job);
    }

    private void navigateToRoadmapWithJob(Job job) {
        sidebarPanel.setSelectedNav(SidebarPanel.NAV_ROADMAP);
        cardLayout.show(contentCardsPanel, SidebarPanel.NAV_ROADMAP);
        topNavPanel.setPageTitle("Personalized Learning Roadmap", "Targeting role: " + job.getTitle() + " at " + job.getCompanyName());
        roadmapPanel.loadRoadmap(currentUser, job);
    }

    private void handleGlobalSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        navigateTo(SidebarPanel.NAV_JOBS);
        jobRecPanel.loadRecommendations(currentUser);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of your session?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            setVisible(false);
            currentUser = null;
            promptAuthentication();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.startApplication();
        });
    }
}
