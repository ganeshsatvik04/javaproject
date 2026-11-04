package com.careermatch.ui.panels;

import com.careermatch.model.User;
import com.careermatch.ui.components.IconHelper;
import com.careermatch.ui.components.ModernBadge;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern SaaS sidebar navigation with active pill highlight,
 * brand branding, user profile indicator, and role-based menu display.
 */
public class SidebarPanel extends JPanel {

    public static final String NAV_DASHBOARD = "DASHBOARD";
    public static final String NAV_PROFILE = "PROFILE";
    public static final String NAV_JOBS = "JOBS";
    public static final String NAV_COMPARE = "COMPARE";
    public static final String NAV_SKILL_GAP = "SKILL_GAP";
    public static final String NAV_ROADMAP = "ROADMAP";
    public static final String NAV_READINESS = "READINESS";
    public static final String NAV_ADMIN = "ADMIN";
    public static final String NAV_SETTINGS = "SETTINGS";

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private String selectedKey = NAV_DASHBOARD;

    private final List<NavItem> navItems = new ArrayList<>();
    private final JPanel menuContainer = new JPanel();
    private final JPanel userProfilePanel = new JPanel();

    private User currentUser;

    public SidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setBackground(UITheme.SIDEBAR_BG);
        setLayout(new BorderLayout());

        add(buildLogoSection(), BorderLayout.NORTH);

        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initNavItems();
        rebuildMenu();

        JScrollPane scroll = new JScrollPane(menuContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        add(buildBottomSection(), BorderLayout.SOUTH);
    }

    private JPanel buildLogoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoIcon = new JLabel(IconHelper.getIcon(IconHelper.IconType.JOBS, 24, UITheme.ACCENT_TEAL));
        JLabel logoText = new JLabel("CareerMatch");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(Color.WHITE);

        brandRow.add(logoIcon);
        brandRow.add(logoText);

        JLabel subTag = new JLabel("CAREER-TECH PLATFORM");
        subTag.setFont(new Font("Segoe UI", Font.BOLD, 9));
        subTag.setForeground(new Color(0x64, 0x74, 0x8B));
        subTag.setBorder(BorderFactory.createEmptyBorder(4, 38, 0, 0));
        subTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(brandRow);
        panel.add(subTag);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x1E, 0x29, 0x3B));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(Box.createVerticalStrut(16));
        panel.add(sep);

        return panel;
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem(NAV_DASHBOARD, "Dashboard", IconHelper.IconType.DASHBOARD, false));
        navItems.add(new NavItem(NAV_PROFILE, "My Profile", IconHelper.IconType.PROFILE, false));
        navItems.add(new NavItem(NAV_JOBS, "Job Recommendations", IconHelper.IconType.JOBS, false));
        navItems.add(new NavItem(NAV_COMPARE, "Company Comparison", IconHelper.IconType.COMPARE, false));
        navItems.add(new NavItem(NAV_SKILL_GAP, "Skill Gap Analysis", IconHelper.IconType.SKILL_GAP, false));
        navItems.add(new NavItem(NAV_ROADMAP, "Learning Roadmap", IconHelper.IconType.ROADMAP, false));
        navItems.add(new NavItem(NAV_READINESS, "Placement Readiness", IconHelper.IconType.READINESS, false));
        navItems.add(new NavItem(NAV_ADMIN, "Admin Console", IconHelper.IconType.ADMIN, true));
        navItems.add(new NavItem(NAV_SETTINGS, "Settings", IconHelper.IconType.SETTINGS, false));
    }

    public void rebuildMenu() {
        menuContainer.removeAll();
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        for (NavItem item : navItems) {
            if (item.adminOnly && !isAdmin) {
                continue; // Hide admin nav from normal job seekers
            }
            menuContainer.add(item.component);
            menuContainer.add(Box.createVerticalStrut(4));
        }
        menuContainer.revalidate();
        menuContainer.repaint();
    }

    private JPanel buildBottomSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 20, 16));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x1E, 0x29, 0x3B));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(12));

        // User profile tile
        userProfilePanel.setLayout(new BorderLayout(10, 0));
        userProfilePanel.setOpaque(false);
        updateUserProfileTile();
        panel.add(userProfilePanel);
        panel.add(Box.createVerticalStrut(10));

        // Logout Button
        JPanel logoutRow = new JPanel(new BorderLayout(8, 0));
        logoutRow.setOpaque(false);
        logoutRow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutRow.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel logoutIcon = new JLabel(IconHelper.getIcon(IconHelper.IconType.LOGOUT, 16, new Color(0xEF, 0x44, 0x44)));
        JLabel logoutText = new JLabel("Log Out");
        logoutText.setFont(UITheme.FONT_BODY_BOLD);
        logoutText.setForeground(new Color(0xEF, 0x44, 0x44));

        logoutRow.add(logoutIcon, BorderLayout.WEST);
        logoutRow.add(logoutText, BorderLayout.CENTER);

        logoutRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onLogout != null) onLogout.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutText.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutText.setForeground(new Color(0xEF, 0x44, 0x44));
            }
        });

        panel.add(logoutRow);
        return panel;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserProfileTile();
        rebuildMenu();
    }

    private void updateUserProfileTile() {
        userProfilePanel.removeAll();
        if (currentUser != null) {
            JLabel avatar = new JLabel(IconHelper.getMonogramIcon(currentUser.getName(), 36, UITheme.SECONDARY_BLUE, Color.WHITE));
            userProfilePanel.add(avatar, BorderLayout.WEST);

            JPanel names = new JPanel(new GridLayout(2, 1, 0, 2));
            names.setOpaque(false);
            JLabel nameLabel = new JLabel(currentUser.getName());
            nameLabel.setFont(UITheme.FONT_BODY_BOLD);
            nameLabel.setForeground(Color.WHITE);

            JLabel roleBadge = new JLabel(currentUser.isAdmin() ? "ADMINISTRATOR" : "JOB SEEKER");
            roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            roleBadge.setForeground(currentUser.isAdmin() ? UITheme.WARNING : UITheme.ACCENT_TEAL);

            names.add(nameLabel);
            names.add(roleBadge);
            userProfilePanel.add(names, BorderLayout.CENTER);
        }
        userProfilePanel.revalidate();
        userProfilePanel.repaint();
    }

    public void setSelectedNav(String key) {
        this.selectedKey = key;
        for (NavItem item : navItems) {
            item.updateSelection(key.equals(item.key));
        }
    }

    private class NavItem {
        final String key;
        final String title;
        final IconHelper.IconType iconType;
        final boolean adminOnly;
        final JPanel component;
        final JLabel iconLabel;
        final JLabel titleLabel;
        boolean selected = false;

        NavItem(String key, String title, IconHelper.IconType iconType, boolean adminOnly) {
            this.key = key;
            this.title = title;
            this.iconType = iconType;
            this.adminOnly = adminOnly;

            component = new JPanel(new BorderLayout(12, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    if (selected) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        UITheme.setupRenderingHints(g2);
                        g2.setColor(UITheme.ACTIVE_NAV_BG);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        // Left active indicator strip
                        g2.setColor(UITheme.SECONDARY_BLUE);
                        g2.fillRoundRect(0, 4, 4, getHeight() - 8, 2, 2);
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            component.setOpaque(false);
            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            component.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH - 24, 40));
            component.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            iconLabel = new JLabel(IconHelper.getIcon(iconType, 18, new Color(0x94, 0xA3, 0xB8)));
            titleLabel = new JLabel(title);
            titleLabel.setFont(UITheme.FONT_BODY);
            titleLabel.setForeground(new Color(0x94, 0xA3, 0xB8));

            component.add(iconLabel, BorderLayout.WEST);
            component.add(titleLabel, BorderLayout.CENTER);

            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedNav(key);
                    if (onNavigate != null) onNavigate.accept(key);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected) {
                        titleLabel.setForeground(Color.WHITE);
                        iconLabel.setIcon(IconHelper.getIcon(iconType, 18, Color.WHITE));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) {
                        titleLabel.setForeground(new Color(0x94, 0xA3, 0xB8));
                        iconLabel.setIcon(IconHelper.getIcon(iconType, 18, new Color(0x94, 0xA3, 0xB8)));
                    }
                }
            });
        }

        void updateSelection(boolean isSelected) {
            this.selected = isSelected;
            if (isSelected) {
                titleLabel.setFont(UITheme.FONT_BODY_BOLD);
                titleLabel.setForeground(Color.WHITE);
                iconLabel.setIcon(IconHelper.getIcon(iconType, 18, UITheme.ACCENT_TEAL));
            } else {
                titleLabel.setFont(UITheme.FONT_BODY);
                titleLabel.setForeground(new Color(0x94, 0xA3, 0xB8));
                iconLabel.setIcon(IconHelper.getIcon(iconType, 18, new Color(0x94, 0xA3, 0xB8)));
            }
            component.repaint();
        }
    }
}
