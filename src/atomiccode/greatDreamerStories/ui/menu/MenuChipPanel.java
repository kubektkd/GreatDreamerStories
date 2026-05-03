package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Dossier panel with archive chip title; supports absolute bounds and normalized child placement inside the
 * content area below the chip.
 */
public final class MenuChipPanel {

    public static final int CHIP_FONT_SIZE = 12;
    private static final int CHIP_BASELINE_OFFSET = 5;
    private static final int INNER_SIDE_INSET = 15;
    private static final int INNER_BOTTOM_INSET = 15;

    private UiRect bounds;
    private final String chipLabel;

    public MenuChipPanel(UiRect bounds, String chipLabel) {
        this.bounds = bounds;
        this.chipLabel = chipLabel;
    }

    /** Updates panel geometry when the viewport changes; chip label is unchanged. */
    public void setBounds(UiRect bounds) {
        this.bounds = bounds;
    }

    public UiRect bounds() {
        return bounds;
    }

    public String chipLabel() {
        return chipLabel;
    }

    public static Font chipFont() {
        return GreatDreamerTheme.archiveFont(CHIP_FONT_SIZE);
    }

    /** Padded region under the chip, before the bottom border. */
    public UiRect innerContentRect(Graphics2D g2d) {
        Font f = chipFont();
        FontMetrics fm = g2d.getFontMetrics(f);
        int chipBottom = bounds.y + CHIP_BASELINE_OFFSET + fm.getDescent() + 8;
        int top = chipBottom - bounds.y;
        return bounds.inset(INNER_SIDE_INSET, top, INNER_SIDE_INSET, INNER_BOTTOM_INSET);
    }

    /**
     * Places a rectangle using normalized coordinates (0–1) within {@link #innerContentRect(Graphics2D)}.
     */
    public UiRect relativeRect(Graphics2D g2d, double nx, double ny, double nWidth, double nHeight) {
        UiRect inner = innerContentRect(g2d);
        int cx = inner.x + (int) Math.round(inner.width * nx);
        int cy = inner.y + (int) Math.round(inner.height * ny);
        int cw = (int) Math.round(inner.width * nWidth);
        int ch = (int) Math.round(inner.height * nHeight);
        return new UiRect(cx, cy, cw, ch);
    }

    /** Panel fill and border only (draw child content after this, then {@link #drawChip(Graphics2D)}). */
    public void drawPanelBody(Graphics2D g2d) {
        GeneralMenuRenderer.drawPanel(g2d, bounds);
    }

    /** Archive chip on top of panel content. */
    public void drawChip(Graphics2D g2d) {
        GeneralMenuRenderer.drawPanelTopArchiveChip(g2d, bounds.x, bounds.y, chipLabel, chipFont());
    }

    /** Panel then chip in one pass (use when there is no overlapping content under the chip). */
    public void draw(Graphics2D g2d) {
        drawPanelBody(g2d);
        drawChip(g2d);
    }
}
