package atomiccode.greatDreamerStories.ui;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.TextInput;
import atomiccode.cthulhuEngine.ui.Tooltip;

import java.awt.Color;
import java.awt.Font;

public class GreatDreamerTheme {
    public static final String ARCHIVE_FONT_PATH = "Special_Elite/SpecialElite-Regular.ttf";
    public static final String TITLE_FONT_PATH = "Milonga/Milonga-Regular.ttf";

    public static final Color PAGE = new Color(8, 9, 11);
    public static final Color PANEL = new Color(11, 12, 15, 210);
    public static final Color PANEL_SOLID = new Color(11, 12, 15, 245);
    public static final Color LINE = new Color(31, 34, 40);
    public static final Color BORDER = new Color(55, 58, 65);
    public static final Color MUTED_BORDER = new Color(155, 155, 155);
    public static final Color TEXT = new Color(235, 233, 225);
    public static final Color MUTED_TEXT = new Color(116, 116, 116);
    public static final Color SELECTED = new Color(245, 244, 238);
    public static final Color SELECTED_TEXT = new Color(18, 18, 18);
    public static final Color DANGER = new Color(125, 42, 42);
    public static final Color DANGER_HOVER = new Color(160, 58, 58);
    public static final Color BUTTON_NORMAL = new Color(12, 13, 16);
    public static final Color BUTTON_HOVER = new Color(42, 43, 48);
    public static final Color BUTTON_PRESSED = new Color(8, 8, 10);
    public static final Color BUTTON_DISABLED = new Color(13, 14, 17);
    public static final Color BUTTON_DISABLED_TEXT = new Color(65, 66, 70);

    private GreatDreamerTheme() {
    }

    public static Font archiveFont(int size) {
        return Engine.instance().resources.getFont(ARCHIVE_FONT_PATH, size);
    }

    public static Font titleFont(int size) {
        return Engine.instance().resources.getFont(TITLE_FONT_PATH, size);
    }

    public static void styleArchiveButton(Button button, Font font) {
        button.setColors(BUTTON_NORMAL, BUTTON_HOVER, BUTTON_PRESSED, TEXT);
        button.setFont(font);
    }

    public static void stylePrimaryButton(Button button, Font font) {
        button.setColors(SELECTED, new Color(210, 208, 198), new Color(165, 163, 154), SELECTED_TEXT);
        button.setFont(font);
    }

    public static void styleDangerButton(Button button, Font font) {
        button.setColors(DANGER, DANGER_HOVER, new Color(90, 30, 30), TEXT);
        button.setFont(font);
    }

    public static void styleTextInput(TextInput input, Font labelFont, Font textFont) {
        input.setFonts(labelFont, textFont);
        input.setColors(PANEL, LINE, SELECTED, TEXT, MUTED_TEXT, BORDER);
    }

    public static void styleTooltip(Tooltip tooltip, Font font) {
        tooltip.setFont(font);
        tooltip.setColors(PANEL_SOLID, BORDER, TEXT, MUTED_TEXT);
    }
}
