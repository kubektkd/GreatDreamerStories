package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/** Even grid of text cells that fills {@code bounds}; draws light dossier borders. */
public final class MenuTable {

    /** Header outline (top, sides, bottom under header rows). */
    private static final float HEADER_FRAME_STROKE = 2f;
    /** Extra vertical inset inside each header row for text (top and bottom). */
    private static final int HEADER_CELL_EXTRA_PAD_Y = 2;

    private MenuTable() {
    }

    /**
     * Draws a uniform grid; lines close on the integer cell grid (no overshoot past the last column/row).
     * Horizontal lines used to extend to {@code bounds.right()} while column width was {@code width/cols}, so
     * when {@code width % cols != 0} the last vertical grid line sat left of the right edge and horizontals looked
     * wider than the cell block — fixed by capping line length to {@code cols * cw} and height to {@code rows * ch}.
     */
    public static void draw(Graphics2D g2d, UiRect bounds, int rows, int cols, String[][] cells, Font cellFont) {
        draw(g2d, bounds, rows, cols, cells, cellFont, 0);
    }

    /**
     * Tooltip text for the cell under {@code (mx, my)}, or {@code null} if outside the grid or that cell has no tip.
     * {@code tooltips} aligns with {@code cells}: same row/column shape; use {@code null} or empty string for no tip.
     */
    public static String tooltipAt(UiRect bounds, int rows, int cols, String[][] tooltips, int mx, int my) {
        if (tooltips == null || rows <= 0 || cols <= 0 || !bounds.contains(mx, my)) {
            return null;
        }
        int cw = bounds.width / cols;
        int ch = bounds.height / rows;
        if (cw <= 0 || ch <= 0) {
            return null;
        }
        int c = (mx - bounds.x) / cw;
        int r = (my - bounds.y) / ch;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return null;
        }
        if (r >= tooltips.length || tooltips[r] == null || c >= tooltips[r].length) {
            return null;
        }
        String t = tooltips[r][c];
        return (t != null && !t.isEmpty()) ? t : null;
    }

    /**
     * @param headerRowCount if {@code > 0}, draws a 2px frame around the header block
     *                       (top, left, right, and bottom separator under the last header row). Inner column lines
     *                       in the header stay 1px.
     */
    public static void draw(Graphics2D g2d, UiRect bounds, int rows, int cols, String[][] cells, Font cellFont,
                            int headerRowCount) {
        if (rows <= 0 || cols <= 0) {
            return;
        }
        int cw = bounds.width / cols;
        int ch = bounds.height / rows;
        int gridRight = bounds.x + cols * cw;
        int gridBottom = bounds.y + rows * ch;
        int yHeaderBottom = headerRowCount > 0 ? bounds.y + headerRowCount * ch : bounds.y;

        g2d.setFont(cellFont);
        FontMetrics fm = g2d.getFontMetrics();
        int pad = 6;

        Stroke oldStroke = g2d.getStroke();
        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.setStroke(new BasicStroke(1f));

        for (int r = 0; r <= rows; r++) {
            int y = bounds.y + r * ch;
            if (headerRowCount > 0 && r == 0) {
                continue;
            }
            if (headerRowCount > 0 && r == headerRowCount) {
                continue;
            }
            g2d.drawLine(bounds.x, y, gridRight, y);
        }

        for (int c = 0; c <= cols; c++) {
            int x = bounds.x + c * cw;
            int y0 = bounds.y;
            if (headerRowCount > 0 && (c == 0 || c == cols)) {
                y0 = yHeaderBottom;
            }
            g2d.drawLine(x, y0, x, gridBottom);
        }

        if (headerRowCount > 0 && headerRowCount <= rows) {
            float sw = HEADER_FRAME_STROKE;
            float inset = sw / 2f;
            float fw = gridRight - bounds.x;
            float fh = yHeaderBottom - bounds.y;
            g2d.setStroke(new BasicStroke(sw, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g2d.draw(new Rectangle2D.Float(bounds.x + inset, bounds.y + inset, fw - sw, fh - sw));
        }

        g2d.setStroke(oldStroke);

        g2d.setColor(GreatDreamerTheme.TEXT);
        for (int r = 0; r < rows; r++) {
            boolean headerCell = headerRowCount > 0 && r < headerRowCount;
            int extraY = headerCell ? HEADER_CELL_EXTRA_PAD_Y : 0;
            int rowTop = bounds.y + r * ch + extraY;
            int rowH = ch - 2 * extraY;

            for (int c = 0; c < cols; c++) {
                String text = "";
                if (cells != null && r < cells.length && cells[r] != null && c < cells[r].length) {
                    text = cells[r][c] != null ? cells[r][c] : "";
                }
                int cellLeft = bounds.x + c * cw;
                int maxW = cw - pad * 2;
                while (text.length() > 0 && fm.stringWidth(text) > maxW && text.length() > 1) {
                    text = text.substring(0, text.length() - 1);
                }
                if (fm.stringWidth(text) > maxW) {
                    text = "";
                }
                int textW = fm.stringWidth(text);
                int cx = cellLeft + Math.max(pad, (cw - textW) / 2);
                if (cx + textW > cellLeft + cw - pad) {
                    cx = cellLeft + cw - pad - textW;
                }
                int baseline = rowTop + rowH / 2 + fm.getAscent() / 2;
                g2d.drawString(text, cx, baseline);
            }
        }
    }
}
