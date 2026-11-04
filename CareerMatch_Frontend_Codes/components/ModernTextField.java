package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Modern rounded text input field with placeholder support and focus highlight glow.
 */
public class ModernTextField extends JTextField {
    private String placeholder = "";
    private int cornerRadius = UITheme.BUTTON_RADIUS;
    private boolean isFocused = false;

    public ModernTextField() {
        this("", "");
    }

    public ModernTextField(String placeholder) {
        this("", placeholder);
    }

    public ModernTextField(String text, String placeholder) {
        super(text);
        this.placeholder = placeholder != null ? placeholder : "";
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_DARK);
        setCaretColor(UITheme.SECONDARY_BLUE);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

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
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
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

        // Border: Focused gets Royal Blue, otherwise subtle light border
        if (isFocused) {
            g2.setColor(UITheme.SECONDARY_BLUE);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(UITheme.BORDER_LIGHT);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Placeholder
        if (getText().isEmpty() && !isFocused && !placeholder.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int py = (h - fm.getHeight()) / 2 + fm.getAscent();
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left, py);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
