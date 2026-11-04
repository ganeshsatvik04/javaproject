package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Modern SaaS data table component with flat headers,
 * alternating row colors, comfortable row heights, and clean cell padding.
 */
public class ModernTable extends JTable {

    public ModernTable() {
        super();
        init();
    }

    public ModernTable(TableModel model) {
        super(model);
        init();
    }

    private void init() {
        setRowHeight(42);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_DARK);
        setSelectionBackground(UITheme.INFO_BLUE_LIGHT);
        setSelectionForeground(UITheme.TEXT_DARK);
        setFillsViewportHeight(true);

        // Modern Header
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setForeground(UITheme.TEXT_MUTED);
        header.setBackground(new Color(0xF1, 0xF5, 0xF9));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT));

        // Default cell renderer with padding and alternating row colors
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.BG_LIGHT);
                    c.setForeground(UITheme.TEXT_DARK);
                } else {
                    c.setBackground(UITheme.INFO_BLUE_LIGHT);
                    c.setForeground(UITheme.PRIMARY_NAVY);
                }
                return c;
            }
        });
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
}
