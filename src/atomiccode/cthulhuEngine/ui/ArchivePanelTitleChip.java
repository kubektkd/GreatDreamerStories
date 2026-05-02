package atomiccode.cthulhuEngine.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Tight padded label pill used on dossier panels; baseline coordinates match {@link Graphics2D#drawString}.
 */
public final class ArchivePanelTitleChip {

    private static final int PADDING_X = 5;
    private static final int PADDING_Y = 2;

    private ArchivePanelTitleChip() {
    }

    public static void paint(Graphics2D g2d, String title, int baselineX, int baselineY, Font titleFont,
                            Color chipFillColor, Color textColor) {
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();
        try {
            g2d.setFont(titleFont);
            FontMetrics fm = g2d.getFontMetrics();

            int rectX = baselineX - PADDING_X;
            int rectY = baselineY - fm.getAscent() - PADDING_Y;
            int rectWidth = fm.stringWidth(title) + PADDING_X * 2;
            int rectHeight = fm.getAscent() + fm.getDescent() + PADDING_Y * 2;

            g2d.setColor(chipFillColor);
            g2d.fillRect(rectX, rectY, rectWidth, rectHeight);
            g2d.setColor(textColor);
            g2d.drawString(title, baselineX, baselineY);
        } finally {
            g2d.setFont(oldFont);
            g2d.setColor(oldColor);
        }
    }
}
