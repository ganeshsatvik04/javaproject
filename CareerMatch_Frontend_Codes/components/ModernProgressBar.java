package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern rounded linear progress bar with customizable track and fill colors.
 */
public class ModernProgressBar extends JComponent {
    private int value = 0; // 0 to 100
    private Color fillColor = UITheme.SECONDARY_BLUE;
    private Color trackColor = UITheme.BORDER_LIGHT;
    private int height = 8;
    private boolean showPercentage = false;

    public ModernProgressBar() {
        this(0, 8);
    }

    public ModernProgressBar(int value) {
        this(value, 8);
    }

    public ModernProgressBar(int value, int height) {
        this.value = Math.max(0, Math.min(100, value));
        this.height = height;
        setPreferredSize(new Dimension(150, height));
    }

    public void setValue(int val) {
        this.value = Math.max(0, Math.min(100, val));
        repaint();
    }

    public int getValue() {
        return value;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
        repaint();
    }

    public void setTrackColor(Color color) {
        this.trackColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();
        int r = h;

        // Background track
        g2.setColor(trackColor);
        g2.fillRoundRect(0, (h - height) / 2, w, height, height, height);

        // Progress fill
        int fillW = (int) ((value / 100.0) * w);
        if (fillW > 0) {
            g2.setColor(fillColor);
            g2.fillRoundRect(0, (h - height) / 2, fillW, height, height, height);
        }

        g2.dispose();
    }
}
