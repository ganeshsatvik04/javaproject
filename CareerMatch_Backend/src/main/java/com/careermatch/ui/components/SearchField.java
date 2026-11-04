package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern rounded search box with magnifying glass vector icon and clear action.
 */
public class SearchField extends JTextField {
    private String placeholder = "Search...";
    private int cornerRadius = 20; // Pill shape
    private boolean isFocused = false;

    public SearchField() {
        this("Search...");
    }

    public SearchField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_DARK);
        setCaretColor(UITheme.SECONDARY_BLUE);
        // Leave room for search icon on left and clear icon on right
        setBorder(BorderFactory.createEmptyBorder(8, 38, 8, 28));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!getText().isEmpty() && e.getX() >= getWidth() - 28) {
                    setText("");
                    postActionEvent();
                }
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Border
        if (isFocused) {
            g2.setColor(UITheme.SECONDARY_BLUE);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(UITheme.BORDER_LIGHT);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Search Icon on left
        int iconSize = 16;
        int iconY = (h - iconSize) / 2;
        Icon searchIcon = IconHelper.getIcon(IconHelper.IconType.SEARCH, iconSize, isFocused ? UITheme.SECONDARY_BLUE : UITheme.TEXT_MUTED);
        searchIcon.paintIcon(this, g2, 12, iconY);

        // Placeholder
        if (getText().isEmpty() && !isFocused && !placeholder.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int py = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 38, py);
        }

        // Clear '×' icon on right if text is entered
        if (!getText().isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int cx = w - 22;
            int cy = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("×", cx, cy);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
