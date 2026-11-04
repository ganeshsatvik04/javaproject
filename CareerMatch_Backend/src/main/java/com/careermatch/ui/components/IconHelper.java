package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Procedural vector icon generator for crisp, resolution-independent Swing icons.
 * Eliminates missing image asset dependencies while delivering sharp SaaS icons.
 */
public class IconHelper {

    public enum IconType {
        DASHBOARD,
        PROFILE,
        JOBS,
        COMPARE,
        SKILL_GAP,
        ROADMAP,
        READINESS,
        ADMIN,
        SETTINGS,
        LOGOUT,
        SEARCH,
        BELL,
        CHECK,
        PLUS,
        EDIT,
        TRASH,
        EXTERNAL_LINK,
        EYE,
        EYE_OFF,
        REFRESH,
        FILTER,
        BUILDING,
        STAR,
        TRENDING_UP
    }

    public static Icon getIcon(IconType type, int size, Color color) {
        return new VectorIcon(type, size, color);
    }

    public static Icon getMonogramIcon(String text, int size, Color bg, Color fg) {
        return new MonogramIcon(text, size, bg, fg);
    }

    private static class VectorIcon implements Icon {
        private final IconType type;
        private final int size;
        private final Color color;

        public VectorIcon(IconType type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.setupRenderingHints(g2);
            g2.translate(x, y);
            g2.setColor(color != null ? color : UITheme.TEXT_DARK);
            g2.setStroke(new BasicStroke(Math.max(1.5f, size / 10.0f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float s = size;
            float pad = s * 0.15f;
            float w = s - 2 * pad;

            switch (type) {
                case DASHBOARD -> {
                    float half = (w - 2) / 2;
                    g2.drawRoundRect((int) pad, (int) pad, (int) half, (int) half, 3, 3);
                    g2.drawRoundRect((int) (pad + half + 2), (int) pad, (int) half, (int) half, 3, 3);
                    g2.drawRoundRect((int) pad, (int) (pad + half + 2), (int) half, (int) half, 3, 3);
                    g2.drawRoundRect((int) (pad + half + 2), (int) (pad + half + 2), (int) half, (int) half, 3, 3);
                }
                case PROFILE -> {
                    float headR = s * 0.18f;
                    g2.drawOval((int) (s / 2 - headR), (int) (s * 0.2f), (int) (headR * 2), (int) (headR * 2));
                    Arc2D arc = new Arc2D.Float(s * 0.2f, s * 0.55f, s * 0.6f, s * 0.5f, 0, 180, Arc2D.OPEN);
                    g2.draw(arc);
                }
                case JOBS -> {
                    float bw = s * 0.65f;
                    float bh = s * 0.45f;
                    float bx = (s - bw) / 2;
                    float by = s * 0.42f;
                    g2.drawRoundRect((int) bx, (int) by, (int) bw, (int) bh, 4, 4);
                    // Handle
                    g2.drawArc((int) (s * 0.35f), (int) (s * 0.22f), (int) (s * 0.3f), (int) (s * 0.25f), 0, 180);
                    // Latches line
                    g2.drawLine((int) bx, (int) (by + bh * 0.4f), (int) (bx + bw), (int) (by + bh * 0.4f));
                }
                case COMPARE -> {
                    // Two comparison bar columns
                    g2.drawRoundRect((int) (s * 0.2f), (int) (s * 0.45f), (int) (s * 0.22f), (int) (s * 0.4f), 3, 3);
                    g2.drawRoundRect((int) (s * 0.55f), (int) (s * 0.22f), (int) (s * 0.22f), (int) (s * 0.63f), 3, 3);
                }
                case SKILL_GAP -> {
                    // Target crosshair
                    g2.drawOval((int) (s * 0.2f), (int) (s * 0.2f), (int) (s * 0.6f), (int) (s * 0.6f));
                    g2.drawOval((int) (s * 0.35f), (int) (s * 0.35f), (int) (s * 0.3f), (int) (s * 0.3f));
                    g2.drawLine((int) (s * 0.1f), (int) (s * 0.5f), (int) (s * 0.9f), (int) (s * 0.5f));
                    g2.drawLine((int) (s * 0.5f), (int) (s * 0.1f), (int) (s * 0.5f), (int) (s * 0.9f));
                }
                case ROADMAP -> {
                    // Path / Milestones
                    g2.drawOval((int) (s * 0.2f), (int) (s * 0.25f), (int) (s * 0.18f), (int) (s * 0.18f));
                    g2.drawOval((int) (s * 0.6f), (int) (s * 0.55f), (int) (s * 0.18f), (int) (s * 0.18f));
                    g2.drawOval((int) (s * 0.25f), (int) (s * 0.75f), (int) (s * 0.18f), (int) (s * 0.18f));
                    // Connecting path
                    Path2D path = new Path2D.Float();
                    path.moveTo(s * 0.35f, s * 0.34f);
                    path.curveTo(s * 0.6f, s * 0.35f, s * 0.5f, s * 0.55f, s * 0.6f, s * 0.64f);
                    path.curveTo(s * 0.6f, s * 0.75f, s * 0.45f, s * 0.8f, s * 0.4f, s * 0.84f);
                    g2.draw(path);
                }
                case READINESS -> {
                    // Speedometer gauge / award star
                    g2.drawArc((int) (s * 0.15f), (int) (s * 0.2f), (int) (s * 0.7f), (int) (s * 0.7f), 0, 180);
                    // Pointer needle
                    g2.drawLine((int) (s * 0.5f), (int) (s * 0.55f), (int) (s * 0.68f), (int) (s * 0.32f));
                    g2.fillOval((int) (s * 0.46f), (int) (s * 0.51f), (int) (s * 0.08f), (int) (s * 0.08f));
                }
                case ADMIN -> {
                    // Shield
                    Path2D shield = new Path2D.Float();
                    shield.moveTo(s * 0.5f, s * 0.18f);
                    shield.lineTo(s * 0.8f, s * 0.28f);
                    shield.lineTo(s * 0.8f, s * 0.58f);
                    shield.quadTo(s * 0.8f, s * 0.82f, s * 0.5f, s * 0.88f);
                    shield.quadTo(s * 0.2f, s * 0.82f, s * 0.2f, s * 0.58f);
                    shield.lineTo(s * 0.2f, s * 0.28f);
                    shield.closePath();
                    g2.draw(shield);
                    g2.drawLine((int) (s * 0.5f), (int) (s * 0.2f), (int) (s * 0.5f), (int) (s * 0.86f));
                }
                case SETTINGS -> {
                    g2.drawOval((int) (s * 0.35f), (int) (s * 0.35f), (int) (s * 0.3f), (int) (s * 0.3f));
                    for (int i = 0; i < 6; i++) {
                        double angle = i * Math.PI / 3;
                        float x1 = (float) (s * 0.5f + Math.cos(angle) * (s * 0.28f));
                        float y1 = (float) (s * 0.5f + Math.sin(angle) * (s * 0.28f));
                        float x2 = (float) (s * 0.5f + Math.cos(angle) * (s * 0.42f));
                        float y2 = (float) (s * 0.5f + Math.sin(angle) * (s * 0.42f));
                        g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    }
                }
                case LOGOUT -> {
                    // Door opening and arrow
                    g2.drawRoundRect((int) (s * 0.2f), (int) (s * 0.2f), (int) (s * 0.35f), (int) (s * 0.6f), 3, 3);
                    g2.drawLine((int) (s * 0.45f), (int) (s * 0.5f), (int) (s * 0.82f), (int) (s * 0.5f));
                    g2.drawLine((int) (s * 0.72f), (int) (s * 0.38f), (int) (s * 0.82f), (int) (s * 0.5f));
                    g2.drawLine((int) (s * 0.72f), (int) (s * 0.62f), (int) (s * 0.82f), (int) (s * 0.5f));
                }
                case SEARCH -> {
                    float r = s * 0.25f;
                    g2.drawOval((int) (s * 0.2f), (int) (s * 0.2f), (int) (r * 2), (int) (r * 2));
                    g2.drawLine((int) (s * 0.55f), (int) (s * 0.55f), (int) (s * 0.82f), (int) (s * 0.82f));
                }
                case BELL -> {
                    g2.drawArc((int) (s * 0.25f), (int) (s * 0.25f), (int) (s * 0.5f), (int) (s * 0.5f), 0, 180);
                    g2.drawLine((int) (s * 0.25f), (int) (s * 0.5f), (int) (s * 0.2f), (int) (s * 0.72f));
                    g2.drawLine((int) (s * 0.75f), (int) (s * 0.5f), (int) (s * 0.8f), (int) (s * 0.72f));
                    g2.drawLine((int) (s * 0.2f), (int) (s * 0.72f), (int) (s * 0.8f), (int) (s * 0.72f));
                    g2.drawArc((int) (s * 0.42f), (int) (s * 0.72f), (int) (s * 0.16f), (int) (s * 0.14f), 180, 180);
                }
                case CHECK -> {
                    g2.drawLine((int) (s * 0.22f), (int) (s * 0.52f), (int) (s * 0.42f), (int) (s * 0.72f));
                    g2.drawLine((int) (s * 0.42f), (int) (s * 0.72f), (int) (s * 0.78f), (int) (s * 0.28f));
                }
                case PLUS -> {
                    g2.drawLine((int) (s * 0.2f), (int) (s * 0.5f), (int) (s * 0.8f), (int) (s * 0.5f));
                    g2.drawLine((int) (s * 0.5f), (int) (s * 0.2f), (int) (s * 0.5f), (int) (s * 0.8f));
                }
                case EDIT -> {
                    g2.drawLine((int) (s * 0.25f), (int) (s * 0.75f), (int) (s * 0.7f), (int) (s * 0.3f));
                    g2.drawLine((int) (s * 0.7f), (int) (s * 0.3f), (int) (s * 0.8f), (int) (s * 0.2f));
                    g2.drawLine((int) (s * 0.25f), (int) (s * 0.75f), (int) (s * 0.2f), (int) (s * 0.8f));
                }
                case TRASH -> {
                    g2.drawRoundRect((int) (s * 0.28f), (int) (s * 0.35f), (int) (s * 0.44f), (int) (s * 0.52f), 3, 3);
                    g2.drawLine((int) (s * 0.2f), (int) (s * 0.35f), (int) (s * 0.8f), (int) (s * 0.35f));
                    g2.drawLine((int) (s * 0.4f), (int) (s * 0.22f), (int) (s * 0.6f), (int) (s * 0.22f));
                    g2.drawLine((int) (s * 0.42f), (int) (s * 0.45f), (int) (s * 0.42f), (int) (s * 0.75f));
                    g2.drawLine((int) (s * 0.58f), (int) (s * 0.45f), (int) (s * 0.58f), (int) (s * 0.75f));
                }
                case EXTERNAL_LINK -> {
                    g2.drawRoundRect((int) (s * 0.2f), (int) (s * 0.35f), (int) (s * 0.45f), (int) (s * 0.45f), 3, 3);
                    g2.drawLine((int) (s * 0.45f), (int) (s * 0.55f), (int) (s * 0.8f), (int) (s * 0.2f));
                    g2.drawLine((int) (s * 0.6f), (int) (s * 0.2f), (int) (s * 0.8f), (int) (s * 0.2f));
                    g2.drawLine((int) (s * 0.8f), (int) (s * 0.2f), (int) (s * 0.8f), (int) (s * 0.4f));
                }
                case EYE -> {
                    g2.drawArc((int) (s * 0.15f), (int) (s * 0.3f), (int) (s * 0.7f), (int) (s * 0.4f), 0, 180);
                    g2.drawArc((int) (s * 0.15f), (int) (s * 0.3f), (int) (s * 0.7f), (int) (s * 0.4f), 180, 180);
                    g2.fillOval((int) (s * 0.42f), (int) (s * 0.42f), (int) (s * 0.16f), (int) (s * 0.16f));
                }
                case EYE_OFF -> {
                    g2.drawArc((int) (s * 0.15f), (int) (s * 0.3f), (int) (s * 0.7f), (int) (s * 0.4f), 0, 180);
                    g2.drawArc((int) (s * 0.15f), (int) (s * 0.3f), (int) (s * 0.7f), (int) (s * 0.4f), 180, 180);
                    g2.drawLine((int) (s * 0.2f), (int) (s * 0.8f), (int) (s * 0.8f), (int) (s * 0.2f));
                }
                case REFRESH -> {
                    g2.drawArc((int) (s * 0.2f), (int) (s * 0.2f), (int) (s * 0.6f), (int) (s * 0.6f), 45, 270);
                    g2.drawLine((int) (s * 0.65f), (int) (s * 0.15f), (int) (s * 0.75f), (int) (s * 0.28f));
                    g2.drawLine((int) (s * 0.82f), (int) (s * 0.18f), (int) (s * 0.75f), (int) (s * 0.28f));
                }
                case FILTER -> {
                    Path2D funnel = new Path2D.Float();
                    funnel.moveTo(s * 0.2f, s * 0.25f);
                    funnel.lineTo(s * 0.8f, s * 0.25f);
                    funnel.lineTo(s * 0.55f, s * 0.55f);
                    funnel.lineTo(s * 0.55f, s * 0.8f);
                    funnel.lineTo(s * 0.45f, s * 0.72f);
                    funnel.lineTo(s * 0.45f, s * 0.55f);
                    funnel.closePath();
                    g2.draw(funnel);
                }
                case BUILDING -> {
                    g2.drawRoundRect((int) (s * 0.25f), (int) (s * 0.2f), (int) (s * 0.5f), (int) (s * 0.68f), 3, 3);
                    g2.fillRect((int) (s * 0.35f), (int) (s * 0.32f), (int) (s * 0.1f), (int) (s * 0.1f));
                    g2.fillRect((int) (s * 0.55f), (int) (s * 0.32f), (int) (s * 0.1f), (int) (s * 0.1f));
                    g2.fillRect((int) (s * 0.35f), (int) (s * 0.5f), (int) (s * 0.1f), (int) (s * 0.1f));
                    g2.fillRect((int) (s * 0.55f), (int) (s * 0.5f), (int) (s * 0.1f), (int) (s * 0.1f));
                }
                case STAR -> {
                    g2.drawPolygon(
                            new int[]{(int) (s * 0.5f), (int) (s * 0.62f), (int) (s * 0.88f), (int) (s * 0.67f), (int) (s * 0.74f), (int) (s * 0.5f), (int) (s * 0.26f), (int) (s * 0.33f), (int) (s * 0.12f), (int) (s * 0.38f)},
                            new int[]{(int) (s * 0.15f), (int) (s * 0.38f), (int) (s * 0.38f), (int) (s * 0.56f), (int) (s * 0.82f), (int) (s * 0.67f), (int) (s * 0.82f), (int) (s * 0.56f), (int) (s * 0.38f), (int) (s * 0.38f)},
                            10
                    );
                }
                case TRENDING_UP -> {
                    g2.drawLine((int) (s * 0.2f), (int) (s * 0.75f), (int) (s * 0.45f), (int) (s * 0.48f));
                    g2.drawLine((int) (s * 0.45f), (int) (s * 0.48f), (int) (s * 0.62f), (int) (s * 0.6f));
                    g2.drawLine((int) (s * 0.62f), (int) (s * 0.6f), (int) (s * 0.82f), (int) (s * 0.28f));
                    g2.drawLine((int) (s * 0.68f), (int) (s * 0.28f), (int) (s * 0.82f), (int) (s * 0.28f));
                    g2.drawLine((int) (s * 0.82f), (int) (s * 0.28f), (int) (s * 0.82f), (int) (s * 0.42f));
                }
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }
        @Override
        public int getIconHeight() { return size; }
    }

    private static class MonogramIcon implements Icon {
        private final String text;
        private final int size;
        private final Color bg;
        private final Color fg;

        public MonogramIcon(String text, int size, Color bg, Color fg) {
            this.text = text != null && !text.isEmpty() ? text.substring(0, 1).toUpperCase() : "?";
            this.size = size;
            this.bg = bg;
            this.fg = fg;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.setupRenderingHints(g2);
            g2.translate(x, y);

            // Circle background
            g2.setColor(bg);
            g2.fillOval(0, 0, size, size);

            // Text
            g2.setColor(fg);
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.52f)));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(text)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }
        @Override
        public int getIconHeight() { return size; }
    }
}
