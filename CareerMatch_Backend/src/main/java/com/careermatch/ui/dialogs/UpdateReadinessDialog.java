package com.careermatch.ui.dialogs;

import com.careermatch.model.ReadinessScore;
import com.careermatch.service.ReadinessService;
import com.careermatch.ui.components.ModernButton;
import com.careermatch.ui.components.ModernCard;
import com.careermatch.ui.components.RadialScoreGauge;
import com.careermatch.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Self-assessment score update dialog for placement readiness.
 * Calculates weighted score with real-time visual feedback:
 * Technical (40%), DSA (25%), Projects (20%), Interview (15%).
 */
public class UpdateReadinessDialog extends JDialog {
    private final ReadinessService readinessService;
    private final int userId;
    private boolean saved = false;

    private final JSlider techSlider = createSlider();
    private final JSlider dsaSlider = createSlider();
    private final JSlider projSlider = createSlider();
    private final JSlider interSlider = createSlider();

    private final JLabel techVal = new JLabel("75%");
    private final JLabel dsaVal = new JLabel("70%");
    private final JLabel projVal = new JLabel("80%");
    private final JLabel interVal = new JLabel("65%");

    private final RadialScoreGauge previewGauge = new RadialScoreGauge(0, 110, "Preview");

    public UpdateReadinessDialog(Frame parent, int userId, ReadinessService readinessService, ReadinessScore current) {
        super(parent, "Update Placement Readiness Assessment", true);
        this.userId = userId;
        this.readinessService = readinessService;

        setSize(560, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        if (current != null) {
            techSlider.setValue((int) current.getTechnical());
            dsaSlider.setValue((int) current.getDsa());
            projSlider.setValue((int) current.getProjects());
            interSlider.setValue((int) current.getInterview());
        } else {
            techSlider.setValue(75);
            dsaSlider.setValue(70);
            projSlider.setValue(80);
            interSlider.setValue(65);
        }

        ChangeListener updateListener = (ChangeEvent e) -> updatePreview();
        techSlider.addChangeListener(updateListener);
        dsaSlider.addChangeListener(updateListener);
        projSlider.addChangeListener(updateListener);
        interSlider.addChangeListener(updateListener);

        ModernCard card = new ModernCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel topRow = new JPanel(new BorderLayout(16, 0));
        topRow.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel title = new JLabel("Self-Assessment Evaluation");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        JLabel sub = new JLabel("Calibrate your readiness across industry hiring benchmarks");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        info.add(title);
        info.add(Box.createVerticalStrut(4));
        info.add(sub);

        topRow.add(info, BorderLayout.CENTER);
        topRow.add(previewGauge, BorderLayout.EAST);
        card.add(topRow);
        card.add(Box.createVerticalStrut(16));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(14));

        addSliderRow(card, "Technical Skills Foundation (40% Weight)", techSlider, techVal);
        addSliderRow(card, "Data Structures & Algorithms (25% Weight)", dsaSlider, dsaVal);
        addSliderRow(card, "Hands-on Project Portfolio (20% Weight)", projSlider, projVal);
        addSliderRow(card, "Aptitude & Interview Preparation (15% Weight)", interSlider, interVal);

        updatePreview();

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        centerWrapper.add(card, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.OUTLINE);
        cancelBtn.addActionListener(e -> dispose());

        ModernButton saveBtn = new ModernButton("Save Assessment", ModernButton.ButtonStyle.PRIMARY);
        saveBtn.addActionListener(e -> {
            try {
                boolean ok = readinessService.saveScore(
                        userId,
                        techSlider.getValue(),
                        dsaSlider.getValue(),
                        projSlider.getValue(),
                        interSlider.getValue()
                );
                if (ok) {
                    saved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not save scores.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        footer.add(cancelBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void updatePreview() {
        techVal.setText(techSlider.getValue() + "%");
        dsaVal.setText(dsaSlider.getValue() + "%");
        projVal.setText(projSlider.getValue() + "%");
        interVal.setText(interSlider.getValue() + "%");

        double total = readinessService.calculateScore(
                techSlider.getValue(),
                dsaSlider.getValue(),
                projSlider.getValue(),
                interSlider.getValue()
        );
        previewGauge.setScore(total);
    }

    private void addSliderRow(JPanel parent, String title, JSlider slider, JLabel valLabel) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_BODY_BOLD);
        t.setForeground(UITheme.TEXT_DARK);
        valLabel.setFont(UITheme.FONT_BODY_BOLD);
        valLabel.setForeground(UITheme.SECONDARY_BLUE);
        header.add(t, BorderLayout.WEST);
        header.add(valLabel, BorderLayout.EAST);

        parent.add(header);
        parent.add(Box.createVerticalStrut(2));
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(slider);
        parent.add(Box.createVerticalStrut(10));
    }

    private JSlider createSlider() {
        JSlider slider = new JSlider(0, 100, 75);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setOpaque(false);
        slider.setFocusable(false);
        return slider;
    }

    public boolean isSaved() {
        return saved;
    }
}
