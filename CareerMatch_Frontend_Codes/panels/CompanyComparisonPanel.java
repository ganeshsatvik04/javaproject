package com.careermatch.ui.panels;

import com.careermatch.model.Company;
import com.careermatch.model.CompanyComparisonItem;
import com.careermatch.model.User;
import com.careermatch.service.CompanyComparisonService;
import com.careermatch.service.CompanyService;
import com.careermatch.ui.components.IconHelper;
import com.careermatch.ui.components.ModernBadge;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Module 4: Company Comparison.
 * Interactive side-by-side comparative evaluation of recruiting companies,
 * criteria analysis, and visual difference highlighting (top salary, highest match).
 * Demonstrates Collections (ArrayList, HashSet) and Java Streams API.
 */
public class CompanyComparisonPanel extends JPanel {
    private final CompanyComparisonService comparisonService;
    private final CompanyService companyService;
    private User currentUser;

    private final List<String> selectedCompanies = new ArrayList<>();
    private final JComboBox<String> companyCombo = new JComboBox<>();
    private final JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

    private final JPanel tableContainer = new JPanel(new BorderLayout());

    public CompanyComparisonPanel(CompanyComparisonService comparisonService, CompanyService companyService) {
        this.comparisonService = comparisonService;
        this.companyService = companyService;

        setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));

        topSection.add(buildControlToolbar());
        topSection.add(Box.createVerticalStrut(10));

        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(chipsPanel);

        add(topSection, BorderLayout.NORTH);

        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));
        add(tableContainer, BorderLayout.CENTER);
    }

    private JPanel buildControlToolbar() {
        ModernCard toolbar = new ModernCard();
        toolbar.setLayout(new BorderLayout(12, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel pickLbl = new JLabel("Select Target Company:");
        pickLbl.setFont(UITheme.FONT_BODY_BOLD);
        pickLbl.setForeground(UITheme.TEXT_DARK);
        left.add(pickLbl);

        companyCombo.setPreferredSize(new Dimension(200, 36));
        left.add(companyCombo);

        ModernButton addBtn = new ModernButton("+ Add Company to Compare", ModernButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> {
            String selected = (String) companyCombo.getSelectedItem();
            if (selected != null && !selected.isEmpty() && !selectedCompanies.contains(selected)) {
                selectedCompanies.add(selected);
                updateChipsAndCompare();
            }
        });
        left.add(addBtn);
        toolbar.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        ModernButton clearBtn = new ModernButton("Clear Comparison", ModernButton.ButtonStyle.OUTLINE);
        clearBtn.addActionListener(e -> {
            selectedCompanies.clear();
            updateChipsAndCompare();
        });
        right.add(clearBtn);

        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    public void loadData(User user) {
        this.currentUser = user;
        if (user == null) return;

        // Load company names into combo
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                return companyService.getCompanyNames();
            }

            @Override
            protected void done() {
                try {
                    List<String> list = get();
                    companyCombo.removeAllItems();
                    for (String name : list) {
                        companyCombo.addItem(name);
                    }
                    // Auto-select initial 2 companies for instant comparison if empty
                    if (selectedCompanies.isEmpty() && list.size() >= 2) {
                        selectedCompanies.add(list.get(0));
                        selectedCompanies.add(list.get(1));
                    }
                    updateChipsAndCompare();
                } catch (Exception ex) {
                    System.err.println("Error loading company list: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateChipsAndCompare() {
        chipsPanel.removeAll();
        for (String cName : new ArrayList<>(selectedCompanies)) {
            JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            chip.setBackground(UITheme.INFO_BLUE_LIGHT);
            chip.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_BLUE, 1));

            JLabel lbl = new JLabel(cName);
            lbl.setFont(UITheme.FONT_SMALL_BOLD);
            lbl.setForeground(UITheme.PRIMARY_NAVY);
            chip.add(lbl);

            JButton rem = new JButton("×");
            rem.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rem.setForeground(UITheme.TEXT_MUTED);
            rem.setOpaque(false);
            rem.setContentAreaFilled(false);
            rem.setBorderPainted(false);
            rem.setFocusPainted(false);
            rem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rem.addActionListener(e -> {
                selectedCompanies.remove(cName);
                updateChipsAndCompare();
            });
            chip.add(rem);

            chipsPanel.add(chip);
        }
        chipsPanel.revalidate();
        chipsPanel.repaint();

        runComparisonQuery();
    }

    private void runComparisonQuery() {
        if (currentUser == null) return;
        if (selectedCompanies.isEmpty()) {
            renderEmptyState("Select at least 2 companies above to view a detailed side-by-side comparison.");
            return;
        }

        SwingWorker<List<CompanyComparisonItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CompanyComparisonItem> doInBackground() {
                return comparisonService.getComparisonProfiles(selectedCompanies, currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<CompanyComparisonItem> profiles = get();
                    renderComparisonMatrix(profiles);
                } catch (Exception ex) {
                    renderEmptyState("Comparison error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void renderEmptyState(String message) {
        tableContainer.removeAll();
        ModernCard card = new ModernCard();
        card.setLayout(new GridBagLayout());
        JLabel msg = new JLabel(message);
        msg.setFont(UITheme.FONT_BODY);
        msg.setForeground(UITheme.TEXT_MUTED);
        card.add(msg);
        tableContainer.add(card, BorderLayout.CENTER);
        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private void renderComparisonMatrix(List<CompanyComparisonItem> profiles) {
        tableContainer.removeAll();

        if (profiles.isEmpty()) {
            renderEmptyState("No role data available for the selected companies.");
            return;
        }

        // Determine highlights: Max salary and Max match %
        double maxSalary = profiles.stream().mapToDouble(CompanyComparisonItem::getMaxSalary).max().orElse(0);
        double maxMatch = profiles.stream().mapToDouble(CompanyComparisonItem::getMatchPercentage).max().orElse(0);

        // Build dynamic comparison matrix
        String[] criteria = {
                "Recruiting Company",
                "Open Positions",
                "Representative Job Roles",
                "Estimated Salary Range",
                "Key Required Skills",
                "Eligibility Criteria",
                "Experience Requirement",
                "Your Profile Match %"
        };

        String[] columnHeaders = new String[profiles.size() + 1];
        columnHeaders[0] = "Comparison Criteria";
        for (int i = 0; i < profiles.size(); i++) {
            columnHeaders[i + 1] = profiles.get(i).getCompanyName();
        }

        DefaultTableModel matrixModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        // Populate criteria rows
        for (int c = 0; c < criteria.length; c++) {
            Object[] row = new Object[profiles.size() + 1];
            row[0] = criteria[c];
            for (int p = 0; p < profiles.size(); p++) {
                CompanyComparisonItem item = profiles.get(p);
                switch (c) {
                    case 0 -> row[p + 1] = item.getCompanyName();
                    case 1 -> row[p + 1] = item.getOpenPositions() + " active openings";
                    case 2 -> row[p + 1] = String.join(", ", item.getRoles());
                    case 3 -> {
                        String salText = item.getSalaryRange();
                        if (item.getMaxSalary() == maxSalary && maxSalary > 0) {
                            salText += "  [Top Comp]";
                        }
                        row[p + 1] = salText;
                    }
                    case 4 -> row[p + 1] = String.join(", ", item.getRequiredSkills());
                    case 5 -> row[p + 1] = item.getEligibility();
                    case 6 -> row[p + 1] = item.getExperience();
                    case 7 -> {
                        String matchText = String.format("%.0f%%", item.getMatchPercentage());
                        if (item.getMatchPercentage() == maxMatch && maxMatch > 0) {
                            matchText += "  [Highest Match]";
                        }
                        row[p + 1] = matchText;
                    }
                }
            }
            matrixModel.addRow(row);
        }

        JTable table = new JTable(matrixModel);
        table.setRowHeight(46);
        table.setShowGrid(true);
        table.setGridColor(UITheme.BORDER_LIGHT);
        table.setFont(UITheme.FONT_BODY);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.FONT_TITLE);
        header.setForeground(UITheme.PRIMARY_NAVY);
        header.setBackground(new Color(0xF1, 0xF5, 0xF9));
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        // Highlight custom renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int col) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, col);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

                if (col == 0) {
                    // Criteria label column
                    comp.setFont(UITheme.FONT_BODY_BOLD);
                    comp.setBackground(new Color(0xF8, 0xFA, 0xFC));
                    comp.setForeground(UITheme.PRIMARY_NAVY);
                } else {
                    String str = val != null ? val.toString() : "";
                    if (str.contains("[Top Comp]") || str.contains("[Highest Match]")) {
                        comp.setBackground(UITheme.SUCCESS_LIGHT);
                        comp.setForeground(UITheme.SUCCESS);
                        comp.setFont(UITheme.FONT_BODY_BOLD);
                    } else {
                        comp.setBackground(Color.WHITE);
                        comp.setForeground(UITheme.TEXT_DARK);
                        comp.setFont(UITheme.FONT_BODY);
                    }
                }
                return comp;
            }
        });

        ModernCard wrapper = new ModernCard();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        tableContainer.add(wrapper, BorderLayout.CENTER);
        tableContainer.revalidate();
        tableContainer.repaint();
    }
}
