package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern rounded button component supporting SaaS action styles:
 * PRIMARY, SECONDARY, ACCENT, OUTLINE, DANGER, SUCCESS, and GHOST.
 */
public class ModernButton extends JButton {

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        ACCENT,
        OUTLINE,
        DANGER,
        SUCCESS,
        GHOST
    }

    private ButtonStyle style = ButtonStyle.PRIMARY;
    private int cornerRadius = UITheme.BUTTON_RADIUS;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        init();
    }

    public ModernButton(String text, Icon icon, ButtonStyle style) {
        super(text, icon);
        this.style = style;
        init();
    }

    private void init() {
        setFont(UITheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setButtonStyle(ButtonStyle style) {
        this.style = style;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        switch (style) {
            case PRIMARY -> {
                bg = isPressed ? new Color(0x1D, 0x4E, 0xD8) : (isHovered ? new Color(0x3B, 0x82, 0xF6) : UITheme.SECONDARY_BLUE);
                fg = Color.WHITE;
            }
            case SECONDARY -> {
                bg = isPressed ? new Color(0x0F, 0x17, 0x2A) : (isHovered ? new Color(0x1E, 0x29, 0x3B) : UITheme.PRIMARY_NAVY);
                fg = Color.WHITE;
            }
            case ACCENT -> {
                bg = isPressed ? new Color(0x0F, 0x76, 0x6E) : (isHovered ? new Color(0x0D, 0x94, 0x88) : UITheme.ACCENT_TEAL);
                fg = Color.WHITE;
            }
            case SUCCESS -> {
                bg = isPressed ? new Color(0x15, 0x80, 0x3D) : (isHovered ? new Color(0x22, 0xC5, 0x5E) : UITheme.SUCCESS);
                fg = Color.WHITE;
            }
            case DANGER -> {
                bg = isPressed ? new Color(0xB9, 0x1C, 0x1C) : (isHovered ? new Color(0xEF, 0x44, 0x44) : UITheme.ERROR);
                fg = Color.WHITE;
            }
            case OUTLINE -> {
                bg = isPressed ? new Color(0xE2, 0xE8, 0xF0) : (isHovered ? UITheme.HOVER_BG : Color.WHITE);
                fg = UITheme.TEXT_DARK;
                border = isHovered ? UITheme.SECONDARY_BLUE : UITheme.BORDER_LIGHT;
            }
            case GHOST -> {
                bg = isHovered ? UITheme.HOVER_BG : new Color(0, 0, 0, 0);
                fg = isHovered ? UITheme.SECONDARY_BLUE : UITheme.TEXT_MUTED;
            }
            default -> {
                bg = UITheme.SECONDARY_BLUE;
                fg = Color.WHITE;
            }
        }

        if (!isEnabled()) {
            bg = new Color(0xE2, 0xE8, 0xF0);
            fg = new Color(0x94, 0xA3, 0xB8);
            border = null;
        }

        // Fill background
        if (bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // Border if any
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        setForeground(fg);
        g2.dispose();
        super.paintComponent(g);
    }
}
