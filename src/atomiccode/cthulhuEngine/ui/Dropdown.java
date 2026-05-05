package atomiccode.cthulhuEngine.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;

/**
 * Simple single-column dropdown: dossier-style panel fill, expands downward.
 * Caller positions via {@link #setBounds}; mouse and keyboard are driven from game state code.
 */
public final class Dropdown {

    public int x;
    public int y;

    private int width;
    private final int rowHeight;
    private String[] options = new String[0];
    private int selectedIndex;
    private boolean expanded;
    private int listHighlightIndex;

    private boolean wasMousePressed;
    /** Row under cursor when {@link #expanded}; -1 if cursor is not over the list. */
    private int hoveredListRow = -1;

    private Font font = new Font("Arial", Font.PLAIN, 14);
    private Color panelFill = new Color(11, 12, 15, 210);
    private Color border = new Color(55, 58, 65);
    private Color borderActive = Color.WHITE;
    private Color textColor = Color.WHITE;
    private Color mutedText = new Color(116, 116, 116);
    private Color itemHoverFill = new Color(42, 43, 48);
    private Color focusRingColor = new Color(245, 244, 238, 180);

    private boolean keyboardFocusRing;
    private Runnable onSelectionChange;

    private static final int TEXT_INSET_X = 10;
    private static final int CHEVRON_AREA = 22;

    public Dropdown(int x, int y, int width, int rowHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.rowHeight = rowHeight;
    }

    public void setOptions(String[] opts) {
        this.options = opts != null ? opts : new String[0];
        selectedIndex = clamp(selectedIndex, 0, Math.max(0, options.length - 1));
        listHighlightIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int idx) {
        selectedIndex = clamp(idx, 0, Math.max(0, options.length - 1));
        listHighlightIndex = selectedIndex;
    }

    public String getSelectedLabel() {
        if (options.length == 0 || selectedIndex < 0 || selectedIndex >= options.length) {
            return "";
        }
        return options[selectedIndex];
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setOnSelectionChange(Runnable onSelectionChange) {
        this.onSelectionChange = onSelectionChange;
    }

    public void setKeyboardFocusRing(boolean keyboardFocusRing) {
        this.keyboardFocusRing = keyboardFocusRing;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColors(Color panelFill, Color border, Color borderActive, Color textColor, Color mutedText,
                          Color itemHoverFill, Color focusRingColor) {
        this.panelFill = panelFill;
        this.border = border;
        this.borderActive = borderActive;
        this.textColor = textColor;
        this.mutedText = mutedText;
        this.itemHoverFill = itemHoverFill;
        this.focusRingColor = focusRingColor;
    }

    public void setBounds(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = Math.max(1, width);
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public boolean containsHeader(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + rowHeight;
    }

    /** Header plus open list (if expanded). */
    public boolean containsOpenBounds(int mx, int my) {
        if (containsHeader(mx, my)) {
            return true;
        }
        if (!expanded || options.length == 0) {
            return false;
        }
        int bottom = y + rowHeight + options.length * rowHeight;
        return mx >= x && mx <= x + width && my >= y + rowHeight && my <= bottom;
    }

    public void applyWheelSteps(int steps) {
        if (!expanded || options.length == 0 || steps == 0) {
            return;
        }
        listHighlightIndex = clamp(listHighlightIndex + steps, 0, options.length - 1);
    }

    public void closeWithoutApply() {
        expanded = false;
        listHighlightIndex = selectedIndex;
        hoveredListRow = -1;
    }

    /** When list is open: Up/Down/Enter/Space/Escape. Returns true if key was consumed. */
    public boolean handleKeyJustPressed(int keyCode) {
        if (!expanded || options.length == 0) {
            return false;
        }
        if (keyCode == KeyEvent.VK_UP) {
            listHighlightIndex = clamp(listHighlightIndex - 1, 0, options.length - 1);
            return true;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            listHighlightIndex = clamp(listHighlightIndex + 1, 0, options.length - 1);
            return true;
        }
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            confirmHighlight();
            return true;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            closeWithoutApply();
            return true;
        }
        return false;
    }

    /** When list is closed: Down / Enter / Space opens. Returns true if consumed. */
    public boolean handleOpenKeyJustPressed(int keyCode) {
        if (expanded || options.length == 0) {
            return false;
        }
        if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            expanded = true;
            listHighlightIndex = selectedIndex;
            return true;
        }
        return false;
    }

    public void update(int mx, int my, boolean mousePressed) {
        if (options.length == 0) {
            expanded = false;
            hoveredListRow = -1;
            wasMousePressed = mousePressed;
            return;
        }

        boolean released = wasMousePressed && !mousePressed;
        if (released) {
            boolean onHeader = containsHeader(mx, my);
            if (onHeader) {
                if (expanded) {
                    closeWithoutApply();
                } else {
                    expanded = true;
                    listHighlightIndex = selectedIndex;
                }
            } else if (expanded) {
                if (containsListArea(mx, my)) {
                    int idx = rowIndexAtY(my);
                    if (idx >= 0) {
                        listHighlightIndex = idx;
                        confirmHighlight();
                    }
                } else {
                    closeWithoutApply();
                }
            }
        }

        if (expanded) {
            if (containsListArea(mx, my)) {
                int hr = rowIndexAtY(my);
                hoveredListRow = hr >= 0 ? hr : -1;
            } else {
                hoveredListRow = -1;
            }
        } else {
            hoveredListRow = -1;
        }

        wasMousePressed = mousePressed;
    }

    private boolean containsListArea(int mx, int my) {
        int listTop = y + rowHeight;
        int listBottom = listTop + options.length * rowHeight;
        return mx >= x && mx <= x + width && my >= listTop && my <= listBottom;
    }

    private int rowIndexAtY(int my) {
        int listTop = y + rowHeight;
        if (my < listTop || my >= listTop + options.length * rowHeight) {
            return -1;
        }
        return (my - listTop) / rowHeight;
    }

    private void confirmHighlight() {
        if (options.length == 0) {
            expanded = false;
            return;
        }
        int newIdx = clamp(listHighlightIndex, 0, options.length - 1);
        boolean changed = newIdx != selectedIndex;
        selectedIndex = newIdx;
        listHighlightIndex = selectedIndex;
        expanded = false;
        hoveredListRow = -1;
        if (changed && onSelectionChange != null) {
            onSelectionChange.run();
        }
    }

    /** Closed row only; safe inside scroll clips. Open list is {@link #renderPopupList(Graphics)}. */
    public void renderHeader(Graphics g) {
        if (options.length == 0) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();
        Stroke oldStroke = g2d.getStroke();
        Object oldAa = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color headerBorder = expanded ? borderActive : border;
        g2d.setColor(panelFill);
        g2d.fillRect(x, y, width, rowHeight);
        g2d.setColor(headerBorder);
        g2d.drawRect(x, y, width, rowHeight);

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        String label = options[selectedIndex];
        int textY = y + (rowHeight + fm.getAscent() - fm.getDescent()) / 2;
        g2d.setColor(textColor);
        g2d.drawString(clipText(g2d, label, width - TEXT_INSET_X * 2 - CHEVRON_AREA), x + TEXT_INSET_X, textY);

        drawChevron(g2d, x + width - CHEVRON_AREA / 2, y + rowHeight / 2, expanded);

        if (keyboardFocusRing && !expanded) {
            g2d.setColor(focusRingColor);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRect(x + 1, y + 1, width - 3, rowHeight - 3);
            g2d.setStroke(oldStroke);
        }

        if (oldAa != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAa);
        }
        g2d.setFont(oldFont);
        g2d.setColor(oldColor);
        g2d.setStroke(oldStroke);
    }

    /** Open list only; draw after clearing scroll clip so it is not cut off. */
    public void renderPopupList(Graphics g) {
        if (options.length == 0 || !expanded) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();
        Stroke oldStroke = g2d.getStroke();
        Object oldAa = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int listY = y + rowHeight;
        boolean hoverOverList = hoveredListRow >= 0;
        for (int i = 0; i < options.length; i++) {
            int ry = listY + i * rowHeight;
            boolean rowHot = hoverOverList
                    ? i == hoveredListRow
                    : i == listHighlightIndex;
            g2d.setColor(rowHot ? itemHoverFill : panelFill);
            g2d.fillRect(x, ry, width, rowHeight);
            g2d.setColor(border);
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawRect(x, ry, width, rowHeight);

            g2d.setFont(font);
            g2d.setColor(textColor);
            int optY = ry + (rowHeight + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(clipText(g2d, options[i], width - TEXT_INSET_X * 2), x + TEXT_INSET_X, optY);

            boolean showKbRing = keyboardFocusRing && i == listHighlightIndex && !hoverOverList;
            boolean showHoverRing = hoverOverList && i == hoveredListRow;
            if (showKbRing || showHoverRing) {
                g2d.setColor(focusRingColor);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRect(x + 1, ry + 1, width - 3, rowHeight - 3);
                g2d.setStroke(oldStroke);
            }
        }

        if (oldAa != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAa);
        }
        g2d.setFont(oldFont);
        g2d.setColor(oldColor);
        g2d.setStroke(oldStroke);
    }

    /** Full control (header + optional list); list is still clipped like header if used inside clip. */
    public void render(Graphics g) {
        renderHeader(g);
        renderPopupList(g);
    }

    private void drawChevron(Graphics2D g2d, int cx, int midY, boolean open) {
        g2d.setColor(mutedText);
        int s = 4;
        int[] xs;
        int[] ys;
        if (!open) {
            xs = new int[]{cx - s, cx + s, cx};
            ys = new int[]{midY - s, midY - s, midY + s};
        } else {
            xs = new int[]{cx - s, cx + s, cx};
            ys = new int[]{midY + s, midY + s, midY - s};
        }
        g2d.fill(new Polygon(xs, ys, 3));
    }

    private static String clipText(Graphics2D g2d, String s, int maxW) {
        if (s == null) {
            return "";
        }
        FontMetrics fm = g2d.getFontMetrics();
        if (fm.stringWidth(s) <= maxW) {
            return s;
        }
        String ell = "...";
        for (int len = s.length() - 1; len >= 0; len--) {
            String t = s.substring(0, len) + ell;
            if (fm.stringWidth(t) <= maxW) {
                return t;
            }
        }
        return ell;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
