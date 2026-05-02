package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** Even grid of text cells that fills {@code bounds}; draws light dossier borders. */
public final class MenuTable {

    private MenuTable() {
    }

    public static void draw(Graphics2D g2d, UiRect bounds, int rows, int cols, String[][] cells, Font cellFont) {
        if (rows <= 0 || cols <= 0) {
            return;
        }
        int cw = bounds.width / cols;
        int ch = bounds.height / rows;

        g2d.setFont(cellFont);
        FontMetrics fm = g2d.getFontMetrics();
        int pad = 6;

        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.setStroke(new BasicStroke(1f));
        for (int r = 0; r <= rows; r++) {
            int y = bounds.y + r * ch;
            g2d.drawLine(bounds.x, y, bounds.right(), y);
        }
        for (int c = 0; c <= cols; c++) {
            int x = bounds.x + c * cw;
            g2d.drawLine(x, bounds.y, x, bounds.bottom());
        }

        g2d.setColor(GreatDreamerTheme.TEXT);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String text = "";
                if (cells != null && r < cells.length && cells[r] != null && c < cells[r].length) {
                    text = cells[r][c] != null ? cells[r][c] : "";
                }
                int cx = bounds.x + c * cw + pad;
                int baseline = bounds.y + r * ch + ch / 2 + fm.getAscent() / 2;
                int maxW = cw - pad * 2;
                while (text.length() > 0 && fm.stringWidth(text) > maxW && text.length() > 1) {
                    text = text.substring(0, text.length() - 1);
                }
                if (fm.stringWidth(text) > maxW) {
                    text = "";
                }
                g2d.drawString(text, cx, baseline);
            }
        }
    }
}
