package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * Modern circular radial score meter for Job Match percentage and
 * Placement Readiness scores.
 */
public class RadialScoreGauge extends JPanel {
    private double score = 0; // 0 to 100
    private int size = 120;
    private int strokeWidth = 10;
    private String label = "Score";
    private Color arcColor = UITheme.SECONDARY_BLUE;
    private Color trackColor = new Color(0xE2, 0xE8, 0xF0);

    public RadialScoreGauge() {
        this(0, 120, "Readiness");
    }

    public RadialScoreGauge(double score, int size, String label) {
        this.score = Math.max(0, Math.min(100, score));
        this.size = size;
        this.label = label;
        this.strokeWidth = Math.max(6, size / 10);
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        updateColor();
    }

    public void setScore(double newScore) {
        this.score = Math.max(0, Math.min(100, newScore));
        updateColor();
        repaint();
    }

    public double getScore() {
        return score;
    }

    public void setLabel(String label) {
        this.label = label;
        repaint();
    }

    private void updateColor() {
        if (score >= 80) {
            arcColor = UITheme.SUCCESS;
        } else if (score >= 60) {
            arcColor = UITheme.SECONDARY_BLUE;
        } else if (score >= 40) {
            arcColor = UITheme.WARNING;
        } else {
            arcColor = UITheme.ERROR;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setupRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();
        int d = Math.min(w, h) - strokeWidth * 2;
        int x = (w - d) / 2;
        int y = (h - d) / 2;

        // Background track circle
        g2.setColor(trackColor);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(x, y, d, d);

        // Value Arc (starting at top = 90 deg, clockwise = negative angle)
        double extent = -(score / 100.0) * 360.0;
        g2.setColor(arcColor);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Arc2D.Double(x, y, d, d, 90, extent, Arc2D.OPEN));

        // Center Text: Score %
        String scoreText = String.format("%.0f%%", score);
        g2.setColor(UITheme.TEXT_DARK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.22f)));
        FontMetrics fmScore = g2.getFontMetrics();
        int sx = (w - fmScore.stringWidth(scoreText)) / 2;
        int sy = (h / 2) + (label != null && !label.isEmpty() ? 0 : fmScore.getAscent() / 3);
        g2.drawString(scoreText, sx, sy);

        // Subtitle / Label
        if (label != null && !label.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, (int) (size * 0.10f))));
            FontMetrics fmLabel = g2.getFontMetrics();
            int lx = (w - fmLabel.stringWidth(label)) / 2;
            int ly = sy + fmLabel.getAscent() + 4;
            g2.drawString(label, lx, ly);
        }

        g2.dispose();
    }
}
