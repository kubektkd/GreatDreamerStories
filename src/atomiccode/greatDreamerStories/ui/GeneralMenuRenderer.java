package atomiccode.greatDreamerStories.ui;

import atomiccode.cthulhuEngine.ui.ArchivePanelTitleChip;
import atomiccode.cthulhuEngine.ui.layout.UiRect;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;


/** Shared dossier-style drawing for screens that use {@link GeneralMenuLayout}. */
public class GeneralMenuRenderer {
    /** Baseline Y offset from {@link UiRect#y} content top; must match {@link #drawRightMetric}. */
    public static final int RIGHT_METRIC_VALUE_BASELINE_OFFSET = 45;
    public static final int RIGHT_METRIC_LABEL_BASELINE_OFFSET = 62;

    private GeneralMenuRenderer() {
    }

    public static void drawPage(Graphics2D g2d, int width, int height) {
        g2d.setColor(GreatDreamerTheme.PAGE);
        g2d.fillRect(0, 0, width, height);
    }

    public static void drawSubtleBackground(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 8));
        for (int y = 0; y < height; y += 4) {
            g2d.drawLine(0, y, width, y);
        }
    }

    public static void drawHeader(Graphics2D g2d, UiRect content, int lineRight, String title, String subtitle,
                                  Font titleFont, Font subtitleFont) {
        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(titleFont);
        g2d.drawString(title, content.x, content.y + 38);
        g2d.setFont(subtitleFont);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString(subtitle, content.x + 2, content.y + 62);
        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.drawLine(content.x, content.y + GeneralMenuLayout.HEADER_LINE_Y, lineRight, content.y + GeneralMenuLayout.HEADER_LINE_Y);
    }

    public static void drawSectionHeader(Graphics2D g2d, int x, int y, String title, String subtitle,
                                         Font titleFont, Font subtitleFont) {
        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(titleFont);
        g2d.drawString(title, x, y + 38);
        g2d.setFont(subtitleFont);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString(subtitle, x, y + 62);
    }

    /**
     * Chip on panel top ({@link ArchivePanelTitleChip}), same inset as archive {@link atomiccode.cthulhuEngine.ui.TextInput}.
     * Call after the panel fill so the chip paints above the panel.
     */
    public static void drawPanelTopArchiveChip(Graphics2D g2d, int panelLeftX, int panelTopY, String chipLabel,
                                               Font chipFont) {
        int chipBaseline = panelTopY + 5;
        ArchivePanelTitleChip.paint(g2d, chipLabel, panelLeftX + 15, chipBaseline, chipFont,
                GreatDreamerTheme.PANEL, GreatDreamerTheme.MUTED_TEXT);
    }

    /**
     * Single title line with its text bottom aligned to the {@link #drawRightMetric} label baseline + descent (e.g.
     * &quot;PTS REMAINING&quot; bottom edge).
     */
    public static void drawTitleAlignedToRightMetricLabelBottom(Graphics2D g2d, UiRect content, int titleLeftX,
                                                               String title, Font titleFont, Font metricLabelFont) {
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();
        try {
            g2d.setFont(metricLabelFont);
            int metricLabelBaseline = content.y + RIGHT_METRIC_LABEL_BASELINE_OFFSET;
            int sharedBottom = metricLabelBaseline + g2d.getFontMetrics().getDescent();

            g2d.setFont(titleFont);
            FontMetrics titleFm = g2d.getFontMetrics();
            int titleBaseline = sharedBottom - titleFm.getDescent();

            g2d.setColor(GreatDreamerTheme.TEXT);
            g2d.drawString(title, titleLeftX, titleBaseline);
        } finally {
            g2d.setFont(oldFont);
            g2d.setColor(oldColor);
        }
    }

    public static void drawRightMetric(Graphics2D g2d, UiRect content, String value, String label,
                                       int valueRightInset, int labelRightInset, Font valueFont, Font labelFont) {
        g2d.setFont(valueFont);
        g2d.setColor(GreatDreamerTheme.TEXT);
        FontMetrics valueMetrics = g2d.getFontMetrics();
        g2d.drawString(value, content.right() - valueMetrics.stringWidth(value) - valueRightInset,
                content.y + RIGHT_METRIC_VALUE_BASELINE_OFFSET);

        g2d.setFont(labelFont);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString(label, content.right() - labelRightInset, content.y + RIGHT_METRIC_LABEL_BASELINE_OFFSET);
    }

    public static void drawPanel(Graphics2D g2d, UiRect rect) {
        g2d.setColor(GreatDreamerTheme.PANEL);
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
        g2d.setColor(GreatDreamerTheme.BORDER);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Small title strip on a filled panel: {@code (x, y)} is the text baseline (matches {@link Graphics2D#drawString}).
     */
    public static void drawPanelTitle(Graphics2D g2d, String title, int x, int y, Font titleFont) {
        ArchivePanelTitleChip.paint(g2d, title, x, y, titleFont, GreatDreamerTheme.PANEL, GreatDreamerTheme.MUTED_TEXT);
    }

    public static void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width) {
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y);
    }
}
