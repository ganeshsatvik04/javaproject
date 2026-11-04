package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern pill badge chip for skill tags, match percentages,
 * proficiency levels, and status indicators.
 */
public class ModernBadge extends JLabel {
    private Color backgroundColor;
    private Color textColor;
    private Color borderColor;
    private int cornerRadius = UITheme.BADGE_RADIUS;

    public ModernBadge(String text) {
        this(text, UITheme.INFO_BLUE_LIGHT, UITheme.SECONDARY_BLUE);
    }

    public ModernBadge(String text, Color bg, Color fg) {
        this(text, bg, fg, null);
    }

    public ModernBadge(String text, Color bg, Color fg, Color border) {
        super(text);
        this.backgroundColor = bg;
        this.textColor = fg;
        this.borderColor = border;
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(UITheme.FONT_BADGE);
        setForeground(textColor);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setBadgeColors(Color bg, Color fg) {
        this.backgroundColor = bg;
        this.textColor = fg;
        setForeground(fg);
        repaint();
    }

    public static ModernBadge success(String text) {
        return new ModernBadge(text, UITheme.SUCCESS_LIGHT, UITheme.SUCCESS);
    }

    public static ModernBadge warning(String text) {
        return new ModernBadge(text, UITheme.WARNING_LIGHT, UITheme.WARNING);
    }

    public static ModernBadge error(String text) {
        return new ModernBadge(text, UITheme.ERROR_LIGHT, UITheme.ERROR);
    }

    public static ModernBadge info(String text) {
        return new ModernBadge(text, UITheme.INFO_BLUE_LIGHT, UITheme.SECONDARY_BLUE);
    }

    public static ModernBadge neutral(String text) {
        return new ModernBadge(text, UITheme.HOVER_BG, UITheme.TEXT_DARK, UITheme.BORDER_LIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Pill shape background
        g2.setColor(backgroundColor != null ? backgroundColor : UITheme.HOVER_BG);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Border if specified
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
