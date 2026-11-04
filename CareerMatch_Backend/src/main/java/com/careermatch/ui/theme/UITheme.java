package com.careermatch.ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Central design system and theme configuration for CareerMatch.
 * Implements a modern SaaS design system with strict color hierarchy,
 * crisp typography, and reusable layout metrics.
 */
public final class UITheme {

    private UITheme() {}

    // Primary SaaS Color Palette
    public static final Color PRIMARY_NAVY = new Color(0x17, 0x25, 0x54); // Dark Navy Blue #172554
    public static final Color SECONDARY_BLUE = new Color(0x25, 0x63, 0xEB); // Royal Blue #2563EB
    public static final Color ACCENT_TEAL = new Color(0x14, 0xB8, 0xA6); // Teal #14B8A6
    public static final Color BG_LIGHT = new Color(0xF8, 0xFA, 0xFC); // Light Grey #F8FAFC
    public static final Color CARD_BG = new Color(0xFF, 0xFF, 0xFF); // White #FFFFFF
    public static final Color TEXT_DARK = new Color(0x1E, 0x29, 0x3B); // Dark Grey #1E293B
    public static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B); // Slate Grey #64748B
    public static final Color BORDER_LIGHT = new Color(0xE2, 0xE8, 0xF0); // Border #E2E8F0
    public static final Color HOVER_BG = new Color(0xF1, 0xF5, 0xF9); // Subtle Slate Hover
    public static final Color ACTIVE_NAV_BG = new Color(0x1E, 0x29, 0x3B); // Sidebar Active Item
    public static final Color SIDEBAR_BG = new Color(0x0F, 0x17, 0x2A); // Slate 900 Navy

    // Status Colors
    public static final Color SUCCESS = new Color(0x16, 0xA3, 0x4A); // Green #16A34A
    public static final Color SUCCESS_LIGHT = new Color(0xDC, 0xFC, 0xE7); // Green Light Tint
    public static final Color WARNING = new Color(0xF5, 0x9E, 0x0B); // Orange #F59E0B
    public static final Color WARNING_LIGHT = new Color(0xFE, 0xF3, 0xC7); // Amber Light Tint
    public static final Color ERROR = new Color(0xDC, 0x26, 0x26); // Red #DC2626
    public static final Color ERROR_LIGHT = new Color(0xFE, 0xE2, 0xE2); // Red Light Tint
    public static final Color INFO_BLUE_LIGHT = new Color(0xDB, 0xEA, 0xFE); // Blue Light Tint

    // Modern Typography
    private static final String FONT_FAMILY = "Segoe UI";

    public static final Font FONT_TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font FONT_TINY = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font(FONT_FAMILY, Font.BOLD, 11);

    // Layout Metrics
    public static final int CORNER_RADIUS = 12;
    public static final int BUTTON_RADIUS = 8;
    public static final int BADGE_RADIUS = 16;
    public static final int SIDEBAR_WIDTH = 260;
    public static final int TOPBAR_HEIGHT = 68;

    // Standard Paddings & Borders
    public static final Border EMPTY_PADDING_LG = new EmptyBorder(24, 24, 24, 24);
    public static final Border EMPTY_PADDING_MD = new EmptyBorder(16, 16, 16, 16);
    public static final Border EMPTY_PADDING_SM = new EmptyBorder(8, 12, 8, 12);

    /**
     * Enables high-quality rendering hints for antialiasing and subpixel text.
     */
    public static void setupRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Applies system look and feel with modern defaults.
     */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to cross-platform LookAndFeel
        }

        // Apply clean modern font overrides across Swing components
        UIManager.put("Button.font", FONT_BODY_BOLD);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_BODY_BOLD);
        UIManager.put("TabbedPane.font", FONT_BODY_BOLD);
        UIManager.put("ToolTip.font", FONT_SMALL);

        // Subtle modern colors for default components
        UIManager.put("Panel.background", BG_LIGHT);
        UIManager.put("ScrollPane.background", BG_LIGHT);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }
}
