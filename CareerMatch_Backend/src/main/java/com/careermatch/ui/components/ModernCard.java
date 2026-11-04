package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern SaaS dashboard container card with rounded corners,
 * crisp border, and clean white surface.
 */
public class ModernCard extends JPanel {
    private int cornerRadius = UITheme.CORNER_RADIUS;
    private Color backgroundColor = UITheme.CARD_BG;
    private Color borderColor = UITheme.BORDER_LIGHT;
    private boolean shadowEnabled = true;

    public ModernCard() {
        this(UITheme.CORNER_RADIUS);
    }

    public ModernCard(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    }

    public ModernCard(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    public void setCardBackground(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setCardBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setShadowEnabled(boolean enabled) {
        this.shadowEnabled = enabled;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int width = getWidth();
        int height = getHeight();

        if (shadowEnabled) {
            // Subtle 1px shadow glow
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fillRoundRect(1, 2, width - 2, height - 2, cornerRadius + 2, cornerRadius + 2);
        }

        // Card body
        g2.setColor(backgroundColor != null ? backgroundColor : UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
