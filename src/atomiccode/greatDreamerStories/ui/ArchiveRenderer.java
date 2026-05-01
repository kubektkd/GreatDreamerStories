package atomiccode.greatDreamerStories.ui;

import atomiccode.cthulhuEngine.ui.layout.UiRect;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ArchiveRenderer {
    private ArchiveRenderer() {
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
        g2d.drawLine(content.x, content.y + ArchiveScreenLayout.HEADER_LINE_Y, lineRight, content.y + ArchiveScreenLayout.HEADER_LINE_Y);
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

    public static void drawRightMetric(Graphics2D g2d, UiRect content, String value, String label,
                                       int valueRightInset, int labelRightInset, Font valueFont, Font labelFont) {
        g2d.setFont(valueFont);
        g2d.setColor(GreatDreamerTheme.TEXT);
        FontMetrics valueMetrics = g2d.getFontMetrics();
        g2d.drawString(value, content.right() - valueMetrics.stringWidth(value) - valueRightInset, content.y + 45);

        g2d.setFont(labelFont);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        g2d.drawString(label, content.right() - labelRightInset, content.y + 62);
    }

    public static void drawPanel(Graphics2D g2d, UiRect rect) {
        g2d.setColor(GreatDreamerTheme.PANEL);
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
        g2d.setColor(GreatDreamerTheme.BORDER);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    public static void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width) {
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y);
    }
}
