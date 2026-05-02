package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.HorizontalSlider;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Settings-style rows: label left, {@link HorizontalSlider} or button group on the right, percentage in the value column.
 * Matches {@link atomiccode.greatDreamerStories.states.SettingsState} layout.
 */
public final class MenuSettingsSliderPanel {

    public static final int LABEL_COLUMN_WIDTH = 168;
    public static final int VALUE_COLUMN_WIDTH = 48;
    public static final int LABEL_SLIDER_GAP = 16;
    public static final int ROW_HEIGHT = 46;
    public static final int ROW_GAP = 14;
    public static final int PANEL_TOP_INNER = 22;
    public static final int PANEL_BOTTOM_INNER = 18;
    public static final int PANEL_SIDE_INSET = 22;
    public static final int BODY_ACTION_RESERVE_PX = 78;

    private MenuSettingsSliderPanel() {
    }

    /**
     * Settings block inside the menu body. {@code rowCount} should match {@link #drawRowLabels} row arrays.
     */
    public static UiRect layoutAudioPanelRect(GeneralMenuLayout layout, int rowCount) {
        int bodyBottom = layout.content.bottom() - BODY_ACTION_RESERVE_PX;
        int bodyHeight = Math.max(260, bodyBottom - layout.body.y);
        UiRect constrainedBody = new UiRect(layout.body.x, layout.body.y, layout.body.width, bodyHeight);

        int rowBlockHeight = ROW_HEIGHT * rowCount + ROW_GAP * Math.max(0, rowCount - 1);
        int idealPanelHeight = PANEL_TOP_INNER + rowBlockHeight + PANEL_BOTTOM_INNER;
        int panelHeight = Math.min(idealPanelHeight, constrainedBody.height - 18);
        return new UiRect(
                constrainedBody.x + PANEL_SIDE_INSET,
                constrainedBody.y + 12,
                Math.max(200, constrainedBody.width - PANEL_SIDE_INSET * 2),
                Math.max(rowBlockHeight, panelHeight));
    }

    public static void layoutRows(UiRect audioPanelRect, int displayButtonWidth, int displayButtonHeight,
                                  int displayButtonGap, HorizontalSlider musicSlider, HorizontalSlider sfxSlider,
                                  Button windowedButton, Button maximizedButton, Button fullscreenButton) {
        int innerLeft = audioPanelRect.x + 22;
        int valueColumnRight = audioPanelRect.right() - 22;

        int sliderX = innerLeft + LABEL_COLUMN_WIDTH + LABEL_SLIDER_GAP;
        int sliderW = Math.max(HorizontalSlider.THUMB_WIDTH + 20,
                valueColumnRight - VALUE_COLUMN_WIDTH - LABEL_SLIDER_GAP - sliderX);

        for (int row = 0; row < 3; row++) {
            int rowTop = audioPanelRect.y + PANEL_TOP_INNER + row * (ROW_HEIGHT + ROW_GAP);

            if (row < 2) {
                HorizontalSlider s = row == 0 ? musicSlider : sfxSlider;
                s.x = sliderX;
                s.y = rowTop + (ROW_HEIGHT - s.trackHeight) / 2;
                s.trackWidth = sliderW;
            } else {
                int bx = sliderX;
                int by = rowTop + (ROW_HEIGHT - displayButtonHeight) / 2;
                windowedButton.x = bx;
                windowedButton.y = by;
                maximizedButton.x = bx + displayButtonWidth + displayButtonGap;
                maximizedButton.y = by;
                fullscreenButton.x = bx + 2 * (displayButtonWidth + displayButtonGap);
                fullscreenButton.y = by;
            }
        }
    }

    public static void drawPanel(Graphics2D g2d, UiRect audioPanelRect) {
        GeneralMenuRenderer.drawPanel(g2d, audioPanelRect);
    }

    /**
     * @param rowLabels   left column text, one per row
     * @param rowValues   right column text; {@code null} or empty skips the value column for that row
     */
    public static void drawRowLabels(Graphics2D g2d, UiRect audioPanelRect, Font rowLabelFont, Font valueFont,
                                     Color textColor, Color mutedColor, String[] rowLabels, String[] rowValues) {
        if (rowLabels.length != rowValues.length) {
            throw new IllegalArgumentException("rowLabels and rowValues must have the same length");
        }
        FontMetrics rowFm = g2d.getFontMetrics(rowLabelFont);
        int rowAscent = rowFm.getAscent();

        int innerLeft = audioPanelRect.x + 22;
        int valueRight = audioPanelRect.right() - 22;

        for (int row = 0; row < rowLabels.length; row++) {
            int rowTop = audioPanelRect.y + PANEL_TOP_INNER + row * (ROW_HEIGHT + ROW_GAP);
            int labelBaseline = rowTop + ROW_HEIGHT / 2 + rowAscent / 2;

            g2d.setFont(rowLabelFont);
            g2d.setColor(textColor);
            g2d.drawString(rowLabels[row], innerLeft, labelBaseline);

            String valueText = rowValues[row];
            if (valueText != null && !valueText.isEmpty()) {
                g2d.setFont(valueFont);
                g2d.setColor(mutedColor);
                int vw = g2d.getFontMetrics().stringWidth(valueText);
                g2d.drawString(valueText, valueRight - vw, labelBaseline);
            }
        }
    }

    public static void renderSlider(Graphics2D g2d, HorizontalSlider slider, boolean focused) {
        slider.render(g2d, GreatDreamerTheme.LINE, GreatDreamerTheme.BORDER,
                GreatDreamerTheme.MUTED_TEXT, GreatDreamerTheme.TEXT, focused);
    }
}
