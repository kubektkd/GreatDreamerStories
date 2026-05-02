package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.layout.UiAlign;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** Centered panel over a dim scrim; caller positions action buttons (e.g. delete / cancel). */
public final class MenuConfirmationModal {

    public static final int DEFAULT_WIDTH = 520;
    public static final int DEFAULT_HEIGHT = 220;

    private MenuConfirmationModal() {
    }

    public record Geometry(int popupX, int popupY, int popupWidth, int popupHeight) {
    }

    public static Geometry centered(int windowWidth, int windowHeight, int popupWidth, int popupHeight) {
        int popupX = (windowWidth - popupWidth) / 2;
        int popupY = (windowHeight - popupHeight) / 2;
        return new Geometry(popupX, popupY, popupWidth, popupHeight);
    }

    public static void drawScrim(Graphics2D g2d, int windowWidth, int windowHeight) {
        g2d.setColor(new Color(0, 0, 0, 175));
        g2d.fillRect(0, 0, windowWidth, windowHeight);
    }

    public static void drawFrame(Graphics2D g2d, Geometry geo) {
        g2d.setColor(GreatDreamerTheme.PANEL_SOLID);
        g2d.fillRect(geo.popupX(), geo.popupY(), geo.popupWidth(), geo.popupHeight());

        g2d.setColor(GreatDreamerTheme.DANGER_HOVER);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRect(geo.popupX(), geo.popupY(), geo.popupWidth(), geo.popupHeight());
        g2d.setStroke(new BasicStroke(1f));
    }

    public static void drawText(Graphics2D g2d, Geometry geo, Font titleFont, Font bodyFont,
                                String title, String[] bodyLines) {
        int popupX = geo.popupX();
        int popupWidth = geo.popupWidth();

        g2d.setColor(GreatDreamerTheme.TEXT);
        g2d.setFont(titleFont);
        GeneralMenuRenderer.drawCenteredString(g2d, title, popupX, geo.popupY() + 45, popupWidth);

        g2d.setFont(bodyFont);
        g2d.setColor(GreatDreamerTheme.MUTED_TEXT);
        int lineY = geo.popupY() + 85;
        int step = 25;
        for (String line : bodyLines) {
            GeneralMenuRenderer.drawCenteredString(g2d, line, popupX, lineY, popupWidth);
            lineY += step;
        }
    }

    /**
     * Horizontally centered row of equal-width buttons below the popup center line.
     */
    public static void layoutButtonRow(int windowWidth, int windowHeight, int buttonWidth, int buttonHeight,
                                       int spacing, Button[] buttons) {
        int total = buttonWidth * buttons.length + spacing * (buttons.length - 1);
        UiRect row = new UiRect(0, windowHeight / 2 + 50, windowWidth, buttonHeight)
                .align(total, buttonHeight, UiAlign.CENTER, UiAlign.START);
        int x = row.x;
        for (Button b : buttons) {
            b.x = x;
            b.y = row.y;
            x += buttonWidth + spacing;
        }
    }
}
