package com.careermatch.ui.dialogs;

import com.careermatch.model.Company;
import com.careermatch.service.AdminService;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.ModernTextField;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Admin dialog for adding or editing hiring partner companies.
 */
public class CompanyDialog extends JDialog {
    private final AdminService adminService;
    private final Company existing;
    private boolean saved = false;

    private final ModernTextField nameField = new ModernTextField("Company Name (e.g. Google, Atlassian)");
    private final ModernTextField locField = new ModernTextField("Location (e.g. Bangalore & Mountain View)");

    public CompanyDialog(Frame parent, AdminService adminService, Company existing) {
        super(parent, existing == null ? "Add Recruiting Company" : "Edit Company Profile", true);
        this.adminService = adminService;
        this.existing = existing;

        setSize(420, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(existing == null ? "Register New Company" : "Update Company Details");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        JLabel nl = new JLabel("Company Name");
        nl.setFont(UITheme.FONT_BODY_BOLD);
        card.add(nl);
        card.add(Box.createVerticalStrut(4));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));

        JLabel ll = new JLabel("Headquarters / Office Locations");
        ll.setFont(UITheme.FONT_BODY_BOLD);
        card.add(ll);
        card.add(Box.createVerticalStrut(4));
        locField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(locField);

        if (existing != null) {
            nameField.setText(existing.getName());
            locField.setText(existing.getLocation());
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

        ModernButton saveBtn = new ModernButton(existing == null ? "Add Company" : "Save Changes", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String loc = locField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok;
            if (existing == null) {
                ok = adminService.addCompany(name, loc);
            } else {
                ok = adminService.updateCompany(existing.getId(), name, loc);
            }
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save company record.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
