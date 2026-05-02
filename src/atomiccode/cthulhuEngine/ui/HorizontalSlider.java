package atomiccode.cthulhuEngine.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Draggable horizontal slider with integer {@code 0}–{@code 100} values (screen-pixel coordinates).
 */
public final class HorizontalSlider {

    public static final int DEFAULT_TRACK_HEIGHT = 20;
    public static final int THUMB_WIDTH = 12;

    public int x;
    public int y;
    public int trackWidth;
    public int trackHeight = DEFAULT_TRACK_HEIGHT;

    private int value;
    private boolean dragging;

    public HorizontalSlider(int x, int y, int trackWidth) {
        this.x = x;
        this.y = y;
        this.trackWidth = Math.max(THUMB_WIDTH + 4, trackWidth);
        this.value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        value = clamp(v);
    }

    public boolean isDragging() {
        return dragging;
    }

    public boolean contains(int px, int py) {
        int padY = 6;
        return px >= x && px <= x + trackWidth && py >= y - padY && py <= y + trackHeight + padY;
    }

    /** @return true when {@link #getValue()} changed since last frame */
    public boolean update(int mouseX, int mouseY, boolean leftPressed) {
        int before = value;
        if (leftPressed) {
            if (!dragging && contains(mouseX, mouseY)) {
                dragging = true;
                applyMouseX(mouseX);
            } else if (dragging) {
                applyMouseX(mouseX);
            }
        } else {
            dragging = false;
        }
        return before != value;
    }

    private void applyMouseX(int mouseX) {
        float rel = (mouseX - (float) x) / Math.max(1f, trackWidth - 1f);
        value = clamp(Math.round(rel * 100f));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    public void render(Graphics2D g2d, Color trackBg, Color trackFill, Color thumbBorder, Color thumbFill, boolean focused) {
        Object oldAa = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = 6;
        int pad = 2;
        int innerW = trackWidth - pad * 2;
        int fillW = Math.max(0, innerW * value / 100);

        g2d.setColor(trackBg);
        g2d.fill(new RoundRectangle2D.Float(x, y, trackWidth, trackHeight, r, r));

        g2d.setColor(trackFill);
        g2d.fill(new RoundRectangle2D.Float(x + pad, y + pad, fillW, trackHeight - pad * 2, r - 2, r - 2));

        int span = Math.max(0, trackWidth - THUMB_WIDTH);
        int thumbX = x + span * value / 100;
        int thumbY = y + (trackHeight - THUMB_WIDTH) / 2;
        g2d.setColor(thumbFill);
        g2d.fillRoundRect(thumbX, thumbY, THUMB_WIDTH, THUMB_WIDTH, 4, 4);
        g2d.setColor(thumbBorder);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(thumbX, thumbY, THUMB_WIDTH, THUMB_WIDTH, 4, 4);

        if (focused) {
            g2d.setColor(new Color(245, 244, 238, 180));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x - 2, y - 2, trackWidth + 4, trackHeight + 4, r + 2, r + 2);
        }

        if (oldAa != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAa);
        }
    }
}
