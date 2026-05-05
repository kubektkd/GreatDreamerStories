package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Attribute row: label (+ tooltip hit zone), step buttons, proportional slider on a fixed 0–99 track,
 * value label. Styling uses {@link GreatDreamerTheme}. User-adjustable range is clamped via
 * {@link #clampUserValue(int, int, int)} when applying mouse or other input.
 */
public final class AttributeSliderRow {

    public static final int TRACK_MIN = 0;
    public static final int TRACK_MAX = 99;

    public static final int BIG_STEP_BUTTON_WIDTH = 26;
    public static final int STEP_BUTTON_WIDTH = 22;
    public static final int ROW_BUTTON_Y_OFFSET = -4;

    private static final int LABEL_NAME_FONT_SIZE = 12;
    private static final int LABEL_CODE_FONT_SIZE = 10;
    private static final int VALUE_FONT_SIZE = 14;

    private static final int LABEL_PAD_LEFT = 20;
    private static final int LABEL_HOVER_X = 16;
    private static final int LABEL_HOVER_Y_OFF = -12;
    private static final int LABEL_HOVER_W = 102;
    private static final int LABEL_HOVER_H = 34;
    private static final int BIG_DEC_LEFT = 118;
    private static final int DEC_LEFT = 148;
    private static final int SLIDER_LEFT = 186;
    private static final int RIGHT_BLOCK_WIDTH = 120;
    private static final int SLIDER_Y_OFF = 7;
    private static final int VALUE_Y_OFF = 12;
    private static final int SLIDER_HIT_PAD_Y = 10;

    private AttributeSliderRow() {
    }

    public static void layout(UiRect panel, int rowBaselineY, Rectangle outLabelHoverBounds,
                              Button bigDec, Button dec, Button inc, Button bigInc) {
        int x = panel.x;
        int w = panel.width;
        outLabelHoverBounds.setBounds(x + LABEL_HOVER_X, rowBaselineY + LABEL_HOVER_Y_OFF, LABEL_HOVER_W, LABEL_HOVER_H);

        bigDec.x = x + BIG_DEC_LEFT;
        bigDec.y = rowBaselineY + ROW_BUTTON_Y_OFFSET;
        dec.x = x + DEC_LEFT;
        dec.y = rowBaselineY + ROW_BUTTON_Y_OFFSET;

        bigInc.x = x + w - 74;
        bigInc.y = rowBaselineY + ROW_BUTTON_Y_OFFSET;
        inc.x = x + w - 102;
        inc.y = rowBaselineY + ROW_BUTTON_Y_OFFSET;
    }

    public static int sliderX(UiRect panel) {
        return panel.x + SLIDER_LEFT;
    }

    public static int sliderWidth(UiRect panel) {
        return Math.max(40, panel.width - SLIDER_LEFT - RIGHT_BLOCK_WIDTH);
    }

    public static int sliderCenterY(int rowBaselineY) {
        return rowBaselineY + SLIDER_Y_OFF;
    }

    /** Hit test for dragging on the 0–99 track (not the step buttons). */
    public static boolean sliderContains(UiRect panel, int rowBaselineY, int mx, int my) {
        int sx = sliderX(panel);
        int sw = sliderWidth(panel);
        int sy = sliderCenterY(rowBaselineY);
        return mx >= sx && mx <= sx + sw && my >= sy - SLIDER_HIT_PAD_Y && my <= sy + SLIDER_HIT_PAD_Y;
    }

    /** Map mouse X to 0–99 along the full track (before per-stat clamp). */
    public static int valueAtMouse(int mouseX, UiRect panel, int rowBaselineY) {
        int sx = sliderX(panel);
        int sw = sliderWidth(panel);
        float t = (mouseX - sx) / (float) Math.max(1, sw);
        return clampUserValue(Math.round(t * (float) TRACK_MAX), TRACK_MIN, TRACK_MAX);
    }

    /** Map mouse X to {@code rangeMin}–{@code rangeMax} along the slider track. */
    public static int valueAtMouseRange(int mouseX, UiRect panel, int rowBaselineY, int rangeMin, int rangeMax) {
        int sx = sliderX(panel);
        int sw = sliderWidth(panel);
        float t = (mouseX - sx) / (float) Math.max(1, sw);
        float mapped = rangeMin + t * (rangeMax - rangeMin);
        return clampUserValue(Math.round(mapped), rangeMin, rangeMax);
    }

    public static int clampUserValue(int value, int userMin, int userMax) {
        return Math.max(userMin, Math.min(userMax, value));
    }

    public static void renderLabelsAndSlider(Graphics2D g2d, UiRect panel, int rowBaselineY, String statName,
                                           String statCode, int statValue) {
        Font nameFont = GreatDreamerTheme.archiveFont(LABEL_NAME_FONT_SIZE);
        Font codeFont = GreatDreamerTheme.archiveFont(LABEL_CODE_FONT_SIZE);
        Color textColor = GreatDreamerTheme.TEXT;
        Color mutedColor = GreatDreamerTheme.MUTED_TEXT;
        Color accentColor = GreatDreamerTheme.SELECTED;

        int sliderX = sliderX(panel);
        int sliderWidth = sliderWidth(panel);
        int sliderY = sliderCenterY(rowBaselineY);
        int knobX = knobXForValue(statValue, sliderX, sliderWidth);

        g2d.setColor(textColor);
        g2d.setFont(nameFont);
        g2d.drawString(statName.toUpperCase(), panel.x + LABEL_PAD_LEFT, rowBaselineY + 4);
        g2d.setFont(codeFont);
        g2d.setColor(mutedColor);
        g2d.drawString(statCode, panel.x + LABEL_PAD_LEFT, rowBaselineY + 18);

        g2d.setColor(new Color(52, 54, 60));
        g2d.drawLine(sliderX, sliderY, sliderX + sliderWidth, sliderY);
        g2d.setColor(new Color(55, 55, 58));
        int tickX = sliderX + sliderWidth / 2;
        g2d.drawLine(tickX, sliderY - 5, tickX, sliderY + 5);
        g2d.setFont(codeFont);
        g2d.setColor(mutedColor);
        g2d.drawString(String.valueOf(TRACK_MIN), sliderX - 3, sliderY + 16);
        g2d.drawString(String.valueOf(TRACK_MAX), sliderX + sliderWidth - 14, sliderY + 16);
        g2d.setColor(accentColor);
        g2d.drawLine(sliderX, sliderY, knobX, sliderY);
        g2d.fillRect(knobX - 2, sliderY - 7, 4, 14);
    }

    /**
     * Same row layout as {@link #renderLabelsAndSlider} but maps the knob and track endpoints to an arbitrary
     * inclusive integer range (e.g. investigator age).
     */
    public static void renderLabelsAndSliderRanged(Graphics2D g2d, UiRect panel, int rowBaselineY, String primaryLabel,
                                                   String secondaryLabel, int value, int rangeMin, int rangeMax) {
        Font nameFont = GreatDreamerTheme.archiveFont(LABEL_NAME_FONT_SIZE);
        Font codeFont = GreatDreamerTheme.archiveFont(LABEL_CODE_FONT_SIZE);
        Color textColor = GreatDreamerTheme.TEXT;
        Color mutedColor = GreatDreamerTheme.MUTED_TEXT;
        Color accentColor = GreatDreamerTheme.SELECTED;

        int sliderX = sliderX(panel);
        int sliderWidth = sliderWidth(panel);
        int sliderY = sliderCenterY(rowBaselineY);
        int knobX = knobXForValueInRange(value, rangeMin, rangeMax, sliderX, sliderWidth);

        g2d.setColor(textColor);
        g2d.setFont(nameFont);
        FontMetrics nameFm = g2d.getFontMetrics();
        if (secondaryLabel != null && !secondaryLabel.isEmpty()) {
            g2d.drawString(primaryLabel.toUpperCase(), panel.x + LABEL_PAD_LEFT, rowBaselineY + 4);
            g2d.setFont(codeFont);
            g2d.setColor(mutedColor);
            g2d.drawString(secondaryLabel, panel.x + LABEL_PAD_LEFT, rowBaselineY + 18);
        } else {
            // Align vertically with two-line stat rows (name baseline +4, code baseline +18).
            int labelBlockCenterY = rowBaselineY + 11;
            int primaryBaseline = labelBlockCenterY + (nameFm.getAscent() - nameFm.getDescent()) / 2;
            g2d.drawString(primaryLabel.toUpperCase(), panel.x + LABEL_PAD_LEFT, primaryBaseline);
        }

        g2d.setColor(new Color(52, 54, 60));
        g2d.drawLine(sliderX, sliderY, sliderX + sliderWidth, sliderY);
        g2d.setColor(new Color(55, 55, 58));
        int tickX = sliderX + sliderWidth / 2;
        g2d.drawLine(tickX, sliderY - 5, tickX, sliderY + 5);
        g2d.setFont(codeFont);
        g2d.setColor(mutedColor);
        g2d.drawString(String.valueOf(rangeMin), sliderX - 3, sliderY + 16);
        g2d.drawString(String.valueOf(rangeMax), sliderX + sliderWidth - 14, sliderY + 16);
        g2d.setColor(accentColor);
        g2d.drawLine(sliderX, sliderY, knobX, sliderY);
        g2d.fillRect(knobX - 2, sliderY - 7, 4, 14);
    }

    private static int knobXForValue(int statValue, int sliderX, int sliderWidth) {
        int v = clampUserValue(statValue, TRACK_MIN, TRACK_MAX);
        return sliderX + (v * sliderWidth) / Math.max(1, TRACK_MAX);
    }

    private static int knobXForValueInRange(int value, int rangeMin, int rangeMax, int sliderX, int sliderWidth) {
        int v = clampUserValue(value, rangeMin, rangeMax);
        return sliderX + Math.round((v - rangeMin) * (float) sliderWidth / (float) Math.max(1, rangeMax - rangeMin));
    }

    public static void renderValue(Graphics2D g2d, UiRect panel, int rowBaselineY, int statValue) {
        Font valueFont = GreatDreamerTheme.archiveFont(VALUE_FONT_SIZE);
        Color textColor = GreatDreamerTheme.TEXT;
        g2d.setColor(textColor);
        g2d.setFont(valueFont);
        String valueText = String.valueOf(statValue);
        FontMetrics valueMetrics = g2d.getFontMetrics();
        g2d.drawString(valueText, panel.x + panel.width - 20 - valueMetrics.stringWidth(valueText),
                rowBaselineY + VALUE_Y_OFF);
    }
}
