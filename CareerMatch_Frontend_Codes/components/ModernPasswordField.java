package com.careermatch.ui.components;

import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern password field with placeholder support, focus highlight,
 * and an interactive show/hide password toggle button.
 */
public class ModernPasswordField extends JPasswordField {
    private String placeholder = "";
    private int cornerRadius = UITheme.BUTTON_RADIUS;
    private boolean isFocused = false;
    private boolean passwordVisible = false;
    private final char defaultEchoChar;

    public ModernPasswordField() {
        this("");
    }

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        this.defaultEchoChar = getEchoChar();
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_DARK);
        setCaretColor(UITheme.SECONDARY_BLUE);
        // Leave room on right for the eye icon
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 38));

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
                int iconX = getWidth() - 32;
                if (e.getX() >= iconX && e.getX() <= getWidth() - 8) {
                    togglePasswordVisibility();
                }
            }
        });
    }

    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar != 0 ? defaultEchoChar : '\u2022');
        }
        repaint();
    }

    public boolean isPasswordVisible() {
        return passwordVisible;
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

        // Placeholder
        if (getPassword().length == 0 && !isFocused && !placeholder.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int py = (h - fm.getHeight()) / 2 + fm.getAscent();
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left, py);
        }

        // Draw Eye toggle icon on right
        int eyeSize = 18;
        int eyeX = w - 30;
        int eyeY = (h - eyeSize) / 2;
        Icon eyeIcon = IconHelper.getIcon(
                passwordVisible ? IconHelper.IconType.EYE_OFF : IconHelper.IconType.EYE,
                eyeSize,
                passwordVisible ? UITheme.SECONDARY_BLUE : UITheme.TEXT_MUTED
        );
        eyeIcon.paintIcon(this, g2, eyeX, eyeY);

        g2.dispose();
        super.paintComponent(g);
    }
}
