package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * Column-even dossier grid using {@link GreatDreamerTheme#menuTableCellFont()}. Optional single header row from
 * {@linkplain HeaderSpan merged segments}; body values only in {@code bodyCells}.
 */
public final class MenuTable {

    private static final int CELL_PAD = 6;
    private static final float GRID_LINE_STROKE = 1f;
    /** Header row is this many pixels taller than each body row. */
    private static final int HEADER_ROW_HEIGHT_DELTA_PX = 4;
    private static final int BODY_ROW_MIN_HEIGHT = 24;
    /** Header outline stroke (pixels). */
    private static final float HEADER_BORDER_STROKE = 2f;
    /** Small downward nudge for body cell text under the top rule. */
    private static final int BODY_TEXT_BASELINE_NUDGE_Y = 2;

    /** One header segment: {@code colSpan} adjacent columns share this label. Sum of spans must equal column count. */
    public static final class HeaderSpan {
        public final String text;
        public final int colSpan;

        public HeaderSpan(String text, int colSpan) {
            if (colSpan < 1) {
                throw new IllegalArgumentException("colSpan must be >= 1");
            }
            this.text = text != null ? text : "";
            this.colSpan = colSpan;
        }
    }

    private MenuTable() {
    }

    // --- public API ------------------------------------------------------------------------------------------------

    /**
     * Draws optional {@code header} row (merged spans allowed) then {@code bodyCells}. Uses
     * {@link GreatDreamerTheme#menuTableCellFont()}.
     */
    public static void draw(Graphics2D g2d, UiRect bounds, int cols, HeaderSpan[] header, String[][] bodyCells) {
        if (cols <= 0 || bodyCells == null || bodyCells.length == 0) {
            return;
        }
        if (header != null && header.length > 0 && !headerSpansMatchColumns(header, cols)) {
            throw new IllegalArgumentException("Header spans must sum to cols (" + cols + ")");
        }
        TableGeometry geo = TableGeometry.compute(bounds, cols, header, bodyCells.length);
        g2d.setFont(GreatDreamerTheme.menuTableCellFont());
        FontMetrics fm = g2d.getFontMetrics();

        Stroke oldStroke = g2d.getStroke();
        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.setStroke(new BasicStroke(GRID_LINE_STROKE));
        drawHorizontalGridLines(g2d, geo);
        drawVerticalGridLines(g2d, geo, header);
        g2d.setStroke(oldStroke);

        if (geo.headerRows > 0) {
            drawHeaderBorder(g2d, geo);
        }

        g2d.setColor(GreatDreamerTheme.TEXT);
        if (geo.headerRows > 0) {
            drawHeaderTexts(g2d, fm, geo, header);
        }
        drawBodyCells(g2d, fm, geo, bodyCells);
    }

    public static String tooltipAt(UiRect bounds, int cols, HeaderSpan[] header, String[][] bodyTooltips, int mx,
                                   int my) {
        if (bodyTooltips == null || cols <= 0 || bodyTooltips.length == 0 || !bounds.contains(mx, my)) {
            return null;
        }
        if (header != null && header.length > 0 && !headerSpansMatchColumns(header, cols)) {
            return null;
        }
        TableGeometry geo = TableGeometry.compute(bounds, cols, header, bodyTooltips.length);
        if (geo.cw <= 0 || bounds.height <= 0) {
            return null;
        }
        int relY = my - bounds.y;
        int r = rowIndexForYOffset(geo.rowTop, relY);
        if (r < 0 || r >= geo.totalRows) {
            return null;
        }
        if (geo.headerRows > 0 && r < geo.headerRows) {
            return null;
        }
        int c = (mx - bounds.x) / geo.cw;
        if (c < 0 || c >= cols) {
            return null;
        }
        int br = r - geo.headerRows;
        if (br >= bodyTooltips.length || bodyTooltips[br] == null || c >= bodyTooltips[br].length) {
            return null;
        }
        String t = bodyTooltips[br][c];
        return (t != null && !t.isEmpty()) ? t : null;
    }

    /** Legacy: uniform rows, no separate header spans. */
    public static void draw(Graphics2D g2d, UiRect bounds, int rows, int cols, String[][] cells) {
        draw(g2d, bounds, rows, cols, cells, 0);
    }

    /** Legacy: row 0 is single-column header spans; remaining rows are body. */
    public static void draw(Graphics2D g2d, UiRect bounds, int rows, int cols, String[][] cells, int headerRowCount) {
        if (rows <= 0 || cols <= 0 || cells == null) {
            return;
        }
        if (headerRowCount <= 0) {
            String[][] body = new String[rows][];
            System.arraycopy(cells, 0, body, 0, rows);
            draw(g2d, bounds, cols, null, body);
            return;
        }
        if (headerRowCount != 1 || rows < 2) {
            throw new IllegalArgumentException("Legacy draw supports headerRowCount 0 or 1 with at least one body row");
        }
        HeaderSpan[] spans = new HeaderSpan[cols];
        for (int c = 0; c < cols; c++) {
            String t = cells[0] != null && c < cells[0].length ? cells[0][c] : "";
            spans[c] = new HeaderSpan(t, 1);
        }
        String[][] body = new String[rows - 1][];
        System.arraycopy(cells, 1, body, 0, rows - 1);
        draw(g2d, bounds, cols, spans, body);
    }

    public static String tooltipAt(UiRect bounds, int rows, int cols, String[][] tooltips, int mx, int my) {
        return tooltipAt(bounds, rows, cols, tooltips, mx, my, 0);
    }

    public static String tooltipAt(UiRect bounds, int rows, int cols, String[][] tooltips, int mx, int my,
                                   int headerRowCount) {
        if (tooltips == null || rows <= 0 || cols <= 0) {
            return null;
        }
        if (headerRowCount <= 0) {
            return tooltipAt(bounds, cols, null, tooltips, mx, my);
        }
        if (headerRowCount != 1 || rows < 2) {
            return null;
        }
        HeaderSpan[] spans = new HeaderSpan[cols];
        for (int c = 0; c < cols; c++) {
            spans[c] = new HeaderSpan("", 1);
        }
        String[][] bodyTips = new String[rows - 1][];
        System.arraycopy(tooltips, 1, bodyTips, 0, rows - 1);
        return tooltipAt(bounds, cols, spans, bodyTips, mx, my);
    }

    // --- layout -----------------------------------------------------------------------------------------------------

    private static final class TableGeometry {
        final int boundsX;
        final int boundsY;
        final int cols;
        final int cw;
        final int headerRows;
        final int bodyRows;
        final int totalRows;
        final int[] rowTop;
        final int gridRight;
        final int gridBottom;
        final int yHeaderBottom;

        private TableGeometry(UiRect bounds, int cols, int cw, int headerRows, int bodyRows, int[] rowTop) {
            this.boundsX = bounds.x;
            this.boundsY = bounds.y;
            this.cols = cols;
            this.cw = cw;
            this.headerRows = headerRows;
            this.bodyRows = bodyRows;
            this.totalRows = headerRows + bodyRows;
            this.rowTop = rowTop;
            this.gridRight = bounds.x + cols * cw;
            this.gridBottom = bounds.y + rowTop[totalRows];
            this.yHeaderBottom = headerRows > 0 ? bounds.y + rowTop[headerRows] : bounds.y;
        }

        static TableGeometry compute(UiRect bounds, int cols, HeaderSpan[] header, int bodyRows) {
            int headerRows = headerRowCount(header);
            int totalRows = headerRows + bodyRows;
            int cw = bounds.width / cols;
            int[] rowTop = new int[totalRows + 1];
            fillRowTopOffsets(bounds.height, totalRows, headerRows, bodyRows, rowTop);
            return new TableGeometry(bounds, cols, cw, headerRows, bodyRows, rowTop);
        }
    }

    private static int headerRowCount(HeaderSpan[] header) {
        return (header == null || header.length == 0) ? 0 : 1;
    }

    private static boolean headerSpansMatchColumns(HeaderSpan[] header, int cols) {
        if (header == null || header.length == 0) {
            return true;
        }
        int sum = 0;
        for (HeaderSpan span : header) {
            sum += span.colSpan;
        }
        return sum == cols;
    }

    /**
     * With header: each body row height {@code b}, each header row {@code b + HEADER_ROW_HEIGHT_DELTA_PX}. Remainder
     * height is spread across the first body rows.
     */
    private static void fillRowTopOffsets(int height, int totalRows, int headerRowCount, int bodyRows, int[] rowTop) {
        rowTop[0] = 0;
        if (totalRows <= 0) {
            return;
        }
        if (headerRowCount <= 0 || headerRowCount > totalRows || bodyRows == 0) {
            distributeEvenRowHeights(height, totalRows, rowTop);
            return;
        }
        int d = HEADER_ROW_HEIGHT_DELTA_PX;
        int maxB = Math.max(1, (height - d * headerRowCount) / totalRows);
        int b = Math.min(Math.max(1, BODY_ROW_MIN_HEIGHT), maxB);
        int h = b + d;
        if (h * headerRowCount + b * bodyRows > height) {
            b = maxB;
            h = b + d;
        }
        if (h * headerRowCount + b * bodyRows > height) {
            distributeEvenRowHeights(height, totalRows, rowTop);
            return;
        }
        int used = h * headerRowCount + b * bodyRows;
        int slack = height - used;
        int p = 0;
        for (int i = 0; i < headerRowCount; i++) {
            p += h;
            rowTop[i + 1] = p;
        }
        for (int i = 0; i < bodyRows; i++) {
            int grow = (i < slack) ? 1 : 0;
            p += b + grow;
            rowTop[headerRowCount + i + 1] = p;
        }
    }

    private static void distributeEvenRowHeights(int height, int rows, int[] rowTop) {
        int ch = height / rows;
        int rem = height - ch * rows;
        int p = 0;
        for (int i = 0; i < rows; i++) {
            p += ch + (i < rem ? 1 : 0);
            rowTop[i + 1] = p;
        }
    }

    private static int rowIndexForYOffset(int[] rowTop, int relY) {
        if (relY < 0) {
            return -1;
        }
        for (int r = 0; r < rowTop.length - 1; r++) {
            if (relY < rowTop[r + 1]) {
                return r;
            }
        }
        return -1;
    }

    // --- grid lines -------------------------------------------------------------------------------------------------

    private static void drawHorizontalGridLines(Graphics2D g2d, TableGeometry geo) {
        for (int r = 0; r <= geo.totalRows; r++) {
            if (geo.headerRows > 0 && r == geo.headerRows) {
                continue;
            }
            int y = geo.boundsY + geo.rowTop[r];
            g2d.drawLine(geo.boundsX, y, geo.gridRight, y);
        }
    }

    private static void drawVerticalGridLines(Graphics2D g2d, TableGeometry geo, HeaderSpan[] header) {
        for (int c = 0; c <= geo.cols; c++) {
            int x = geo.boundsX + c * geo.cw;
            int y0 = geo.headerRows > 0
                    ? verticalLineTopY(header, c, geo.cols, geo.boundsY, geo.yHeaderBottom)
                    : geo.boundsY;
            g2d.drawLine(x, y0, x, geo.gridBottom);
        }
    }

    /**
     * Outer verticals start below the header frame; inner lines at span boundaries run full height; other inner lines
     * start below the header so they do not cut through merged header text.
     */
    private static int verticalLineTopY(HeaderSpan[] header, int c, int cols, int boundsTop, int yHeaderBottom) {
        if (c == 0 || c == cols) {
            return yHeaderBottom;
        }
        if (header == null || header.length == 0) {
            return boundsTop;
        }
        int cum = 0;
        for (HeaderSpan span : header) {
            cum += span.colSpan;
            if (c == cum) {
                return boundsTop;
            }
        }
        return yHeaderBottom;
    }

    private static void drawHeaderBorder(Graphics2D g2d, TableGeometry geo) {
        float sw = HEADER_BORDER_STROKE;
        float inset = sw / 2f;
        float w = geo.gridRight - geo.boundsX;
        float h = geo.yHeaderBottom - geo.boundsY;
        Stroke old = g2d.getStroke();
        g2d.setColor(GreatDreamerTheme.LINE);
        g2d.setStroke(new BasicStroke(sw, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2d.draw(new Rectangle2D.Float(geo.boundsX + inset, geo.boundsY + inset, w - sw, h - sw));
        g2d.setStroke(old);
    }

    // --- text -------------------------------------------------------------------------------------------------------

    private static void drawHeaderTexts(Graphics2D g2d, FontMetrics fm, TableGeometry geo, HeaderSpan[] header) {
        int rowH = geo.rowTop[1] - geo.rowTop[0];
        int rowTopPx = geo.boundsY + geo.rowTop[0];
        int textHeight = fm.getHeight();
        int startCol = 0;
        for (HeaderSpan span : header) {
            int spanPx = span.colSpan * geo.cw;
            int cellLeft = geo.boundsX + startCol * geo.cw;
            drawCenteredCellText(g2d, fm, span.text, cellLeft, rowTopPx, spanPx, rowH, textHeight, 0);
            startCol += span.colSpan;
        }
    }

    private static void drawBodyCells(Graphics2D g2d, FontMetrics fm, TableGeometry geo, String[][] bodyCells) {
        int textHeight = fm.getHeight();
        for (int br = 0; br < geo.bodyRows; br++) {
            int r = geo.headerRows + br;
            int rowH = geo.rowTop[r + 1] - geo.rowTop[r];
            int rowTopPx = geo.boundsY + geo.rowTop[r];
            for (int c = 0; c < geo.cols; c++) {
                String text = "";
                if (bodyCells[br] != null && c < bodyCells[br].length) {
                    text = bodyCells[br][c] != null ? bodyCells[br][c] : "";
                }
                drawCenteredCellText(g2d, fm, text, geo.boundsX + c * geo.cw, rowTopPx, geo.cw, rowH, textHeight,
                        BODY_TEXT_BASELINE_NUDGE_Y);
            }
        }
    }

    /**
     * Horizontally and vertically centers text in the cell; truncates with ellipsis behavior (trim) if too wide.
     *
     * @param baselineNudge extra pixels added to baseline (body cells only; use 0 for header).
     */
    private static void drawCenteredCellText(Graphics2D g2d, FontMetrics fm, String rawText, int cellLeft, int rowTopPx,
                                             int cellWidth, int rowH, int textHeight, int baselineNudge) {
        String text = rawText != null ? rawText : "";
        int maxW = cellWidth - 2 * CELL_PAD;
        while (text.length() > 0 && fm.stringWidth(text) > maxW && text.length() > 1) {
            text = text.substring(0, text.length() - 1);
        }
        if (fm.stringWidth(text) > maxW) {
            text = "";
        }
        int textW = fm.stringWidth(text);
        int cx = cellLeft + Math.max(CELL_PAD, (cellWidth - textW) / 2);
        if (cx + textW > cellLeft + cellWidth - CELL_PAD) {
            cx = cellLeft + cellWidth - CELL_PAD - textW;
        }
        int baseline = rowTopPx + (rowH - textHeight) / 2 + fm.getAscent() + baselineNudge;
        g2d.drawString(text, cx, baseline);
    }
}
