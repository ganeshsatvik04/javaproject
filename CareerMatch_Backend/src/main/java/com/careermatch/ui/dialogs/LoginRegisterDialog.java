package com.careermatch.ui.dialogs;

import com.careermatch.exception.AuthenticationException;
import com.careermatch.exception.ValidationException;
import com.careermatch.model.JobSeeker;
import com.careermatch.model.User;
import com.careermatch.service.AuthService;
import com.careermatch.service.AuthServiceImpl;
import com.careermatch.ui.components.*;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern SaaS authentication dialog supporting Login and Registration.
 * Demonstrates:
 * - Form validation and error feedback
 * - Polymorphic authentication (JobSeeker and Admin)
 * - Quick demo access for seamless evaluation
 */
public class LoginRegisterDialog extends JDialog {
    private final AuthService authService;
    private User authenticatedUser = null;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);

    // Login Fields
    private final ModernTextField loginEmailField = new ModernTextField("Enter your email address");
    private final ModernPasswordField loginPasswordField = new ModernPasswordField("Enter your password");
    private final JCheckBox rememberMeCheckbox = new JCheckBox("Remember me on this machine");
    private final JLabel loginErrorLabel = new JLabel(" ");

    // Register Fields
    private final ModernTextField regNameField = new ModernTextField("Full Name (e.g., Jane Doe)");
    private final ModernTextField regEmailField = new ModernTextField("Email Address");
    private final ModernPasswordField regPasswordField = new ModernPasswordField("Create Password (min. 6 chars)");
    private final ModernPasswordField regConfirmPasswordField = new ModernPasswordField("Confirm Password");
    private final ModernTextField regEducationField = new ModernTextField("Degree / Major (e.g., B.Tech CSE)");
    private final ModernTextField regInterestsField = new ModernTextField("Career Interests (e.g., Cloud, Java, AI)");
    private final JLabel regErrorLabel = new JLabel(" ");

    public LoginRegisterDialog(Frame parent) {
        this(parent, new AuthServiceImpl());
    }

    public LoginRegisterDialog(Frame parent, AuthService authService) {
        super(parent, "CareerMatch — Authentication", true);
        this.authService = authService;

        setSize(480, 680);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel headerPanel = buildHeader();
        add(headerPanel, BorderLayout.NORTH);

        cardsPanel.setOpaque(false);
        cardsPanel.add(buildLoginCard(), "LOGIN");
        cardsPanel.add(buildRegisterCard(), "REGISTER");
        add(cardsPanel, BorderLayout.CENTER);

        cardLayout.show(cardsPanel, "LOGIN");
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        JLabel logoLabel = new JLabel("CareerMatch");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoLabel.setForeground(UITheme.PRIMARY_NAVY);
        logoLabel.setIcon(IconHelper.getIcon(IconHelper.IconType.JOBS, 28, UITheme.SECONDARY_BLUE));
        logoLabel.setIconTextGap(10);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Smart Career Planning & Placement Platform");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildLoginCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to access personalized recommendations");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginErrorLabel.setFont(UITheme.FONT_SMALL);
        loginErrorLabel.setForeground(UITheme.ERROR);
        loginErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(UITheme.FONT_BODY_BOLD);
        emailLabel.setForeground(UITheme.TEXT_DARK);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.FONT_BODY_BOLD);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        rememberMeCheckbox.setFont(UITheme.FONT_SMALL);
        rememberMeCheckbox.setForeground(UITheme.TEXT_MUTED);
        rememberMeCheckbox.setOpaque(false);
        rememberMeCheckbox.setFocusPainted(false);
        rememberMeCheckbox.setSelected(true);
        rememberMeCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton loginBtn = new ModernButton("Sign In to CareerMatch", ModernButton.ButtonStyle.PRIMARY);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());

        // Quick demo login bar
        JPanel demoPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        demoPanel.setOpaque(false);
        demoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton demoUserBtn = new ModernButton("Demo: Job Seeker", ModernButton.ButtonStyle.OUTLINE);
        demoUserBtn.setFont(UITheme.FONT_SMALL_BOLD);
        demoUserBtn.addActionListener(e -> {
            loginEmailField.setText("ravi@gmail.com");
            loginPasswordField.setText("password123");
            performLogin();
        });

        ModernButton demoAdminBtn = new ModernButton("Demo: Admin", ModernButton.ButtonStyle.OUTLINE);
        demoAdminBtn.setFont(UITheme.FONT_SMALL_BOLD);
        demoAdminBtn.addActionListener(e -> {
            loginEmailField.setText("admin@careermatch.com");
            loginPasswordField.setText("admin123");
            performLogin();
        });

        demoPanel.add(demoUserBtn);
        demoPanel.add(demoAdminBtn);

        // Switch to Register link
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        switchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noAccLabel = new JLabel("Don't have an account?");
        noAccLabel.setFont(UITheme.FONT_SMALL);
        noAccLabel.setForeground(UITheme.TEXT_MUTED);

        ModernButton switchToRegBtn = new ModernButton("Create Account", ModernButton.ButtonStyle.GHOST);
        switchToRegBtn.setFont(UITheme.FONT_SMALL_BOLD);
        switchToRegBtn.addActionListener(e -> {
            loginErrorLabel.setText(" ");
            cardLayout.show(cardsPanel, "REGISTER");
        });

        switchPanel.add(noAccLabel);
        switchPanel.add(switchToRegBtn);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(12));
        card.add(loginErrorLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(loginEmailField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(loginPasswordField);
        card.add(Box.createVerticalStrut(8));
        card.add(rememberMeCheckbox);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(new JSeparator(JSeparator.HORIZONTAL));
        card.add(Box.createVerticalStrut(12));
        JLabel quickLabel = new JLabel("Instant Evaluator Demo Access:");
        quickLabel.setFont(UITheme.FONT_TINY);
        quickLabel.setForeground(UITheme.TEXT_MUTED);
        quickLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(quickLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(demoPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(switchPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildRegisterCard() {
        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Create an Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Start your personalized career preparation roadmap");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        regErrorLabel.setFont(UITheme.FONT_SMALL);
        regErrorLabel.setForeground(UITheme.ERROR);
        regErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(8));
        card.add(regErrorLabel);
        card.add(Box.createVerticalStrut(4));

        addField(card, "Full Name", regNameField);
        addField(card, "Email Address", regEmailField);
        addField(card, "Password (min 6 characters)", regPasswordField);
        addField(card, "Confirm Password", regConfirmPasswordField);
        addField(card, "Current Degree / Education", regEducationField);
        addField(card, "Primary Career Interests", regInterestsField);

        ModernButton registerBtn = new ModernButton("Complete Registration", ModernButton.ButtonStyle.SUCCESS);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> performRegister());
        card.add(Box.createVerticalStrut(12));
        card.add(registerBtn);

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        switchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hasAccLabel = new JLabel("Already have an account?");
        hasAccLabel.setFont(UITheme.FONT_SMALL);
        hasAccLabel.setForeground(UITheme.TEXT_MUTED);

        ModernButton switchToLoginBtn = new ModernButton("Sign In", ModernButton.ButtonStyle.GHOST);
        switchToLoginBtn.setFont(UITheme.FONT_SMALL_BOLD);
        switchToLoginBtn.addActionListener(e -> {
            regErrorLabel.setText(" ");
            cardLayout.show(cardsPanel, "LOGIN");
        });

        switchPanel.add(hasAccLabel);
        switchPanel.add(switchToLoginBtn);
        card.add(Box.createVerticalStrut(4));
        card.add(switchPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private void addField(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_SMALL_BOLD);
        label.setForeground(UITheme.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(2));
        parent.add(field);
        parent.add(Box.createVerticalStrut(6));
    }

    private void performLogin() {
        String email = loginEmailField.getText().trim();
        String pass = new String(loginPasswordField.getPassword());

        try {
            User user = authService.login(email, pass);
            this.authenticatedUser = user;
            dispose();
        } catch (ValidationException | AuthenticationException ex) {
            loginErrorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            loginErrorLabel.setText("System error during login: " + ex.getMessage());
        }
    }

    private void performRegister() {
        String name = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String pass = new String(regPasswordField.getPassword());
        String confirm = new String(regConfirmPasswordField.getPassword());
        String edu = regEducationField.getText().trim();
        String interests = regInterestsField.getText().trim();

        try {
            JobSeeker seeker = authService.register(name, email, pass, confirm, edu, interests);
            this.authenticatedUser = seeker;
            JOptionPane.showMessageDialog(this,
                    "Account registered successfully! Welcome to CareerMatch.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ValidationException ex) {
            regErrorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            regErrorLabel.setText("Registration error: " + ex.getMessage());
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
