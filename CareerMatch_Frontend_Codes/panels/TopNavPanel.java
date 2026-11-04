package com.careermatch.ui.panels;

import com.careermatch.model.User;
import com.careermatch.ui.components.IconHelper;
import com.careermatch.ui.components.ModernBadge;
import com.careermatch.ui.components.SearchField;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Top SaaS Navigation Bar displaying page title, global search bar,
 * notification bell, and user status badge.
 */
public class TopNavPanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Dashboard Overview");
    private final JLabel subtitleLabel = new JLabel("Welcome back to CareerMatch");
    private final SearchField searchField = new SearchField("Search jobs, roles, or skills...");
    private final JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    private final JLabel userNameLabel = new JLabel("");
    private final JLabel userAvatar = new JLabel();

    private final Consumer<String> onSearchAction;

    public TopNavPanel(Consumer<String> onSearchAction) {
        this.onSearchAction = onSearchAction;

        setPreferredSize(new Dimension(Integer.MAX_VALUE, UITheme.TOPBAR_HEIGHT));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT));
        setLayout(new BorderLayout());

        // Left: Page Title & Breadcrumbs
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 16));

        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_NAVY);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(subtitleLabel);
        add(titlePanel, BorderLayout.WEST);

        // Center: Global Search Bar
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        searchField.setPreferredSize(new Dimension(320, 38));
        searchField.addActionListener(e -> {
            if (onSearchAction != null) {
                onSearchAction.accept(searchField.getText().trim());
            }
        });
        centerPanel.add(searchField);
        add(centerPanel, BorderLayout.CENTER);

        // Right: Notification Bell & User Chip
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        rightPanel.setOpaque(false);

        // Notification Bell Button
        JButton bellBtn = new JButton(IconHelper.getIcon(IconHelper.IconType.BELL, 20, UITheme.TEXT_MUTED));
        bellBtn.setOpaque(false);
        bellBtn.setContentAreaFilled(false);
        bellBtn.setBorderPainted(false);
        bellBtn.setFocusPainted(false);
        bellBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bellBtn.setToolTipText("Notifications: 2 new matching jobs added");
        bellBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Notifications:\n• Google added new opening: Backend Engineer (Java)\n• Your profile match readiness increased to 78%",
                    "Notifications", JOptionPane.INFORMATION_MESSAGE);
        });
        rightPanel.add(bellBtn);

        // User Profile Summary Chip
        userSection.setOpaque(false);
        userNameLabel.setFont(UITheme.FONT_BODY_BOLD);
        userNameLabel.setForeground(UITheme.TEXT_DARK);
        userSection.add(userAvatar);
        userSection.add(userNameLabel);
        rightPanel.add(userSection);

        add(rightPanel, BorderLayout.EAST);
    }

    public void setPageTitle(String title, String subtitle) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
    }

    public void setUser(User user) {
        if (user != null) {
            userNameLabel.setText(user.getName());
            userAvatar.setIcon(IconHelper.getMonogramIcon(user.getName(), 32, UITheme.SECONDARY_BLUE, Color.WHITE));
        } else {
            userNameLabel.setText("");
            userAvatar.setIcon(null);
        }
    }

    public SearchField getSearchField() {
        return searchField;
    }
}
