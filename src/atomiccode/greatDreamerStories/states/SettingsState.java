package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.HorizontalSlider;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

import atomiccode.greatDreamerStories.Game;
import atomiccode.greatDreamerStories.GamePreferences;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * In-game settings reachable from the main menu. Returns to {@link MainMenuState} on Back or Esc.
 */
public class SettingsState implements State {

    private static final String BACKGROUND_MUSIC_ID = "background_music";

    private static final int ACTION_BUTTON_WIDTH = 130;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int DISPLAY_BUTTON_WIDTH = 130;
    private static final int DISPLAY_BUTTON_HEIGHT = 38;
    private static final int DISPLAY_BUTTON_GAP = 12;

    private static final int LABEL_COLUMN_WIDTH = 168;
    private static final int VALUE_COLUMN_WIDTH = 48;
    private static final int LABEL_SLIDER_GAP = 16;
    private static final int ROW_HEIGHT = 46;
    private static final int ROW_GAP = 14;
    private static final int PANEL_TOP_INNER = 22;
    private static final int PANEL_BOTTOM_INNER = 18;
    private static final int PANEL_SIDE_INSET = 22;
    private static final int BODY_ACTION_RESERVE_PX = 78;

    private static final int FOCUS_MUSIC = 0;
    private static final int FOCUS_SFX = 1;
    private static final int FOCUS_WINDOWED = 2;
    private static final int FOCUS_MAXIMIZED = 3;
    private static final int FOCUS_FULLSCREEN = 4;
    private static final int FOCUS_BACK = 5;
    private static final int FOCUS_COUNT = 6;

    private Button windowedButton;
    private Button maximizedButton;
    private Button fullscreenButton;
    private Button backButton;
    private HorizontalSlider musicSlider;
    private HorizontalSlider sfxSlider;

    private int selectedIndex = FOCUS_BACK;

    private Font buttonFont;
    private Font rowLabelFont;
    private Font titleFont;
    private Font subtitleFont;
    private GeneralMenuLayout layout;
    private UiRect audioPanelRect;

    private final Color textColor = GreatDreamerTheme.TEXT;
    private final Color mutedTextColor = GreatDreamerTheme.MUTED_TEXT;
    private final Color selectedColor = GreatDreamerTheme.SELECTED;
    private final Color selectedTextColor = GreatDreamerTheme.SELECTED_TEXT;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        buttonFont = GreatDreamerTheme.archiveFont(14);
        rowLabelFont = GreatDreamerTheme.archiveFont(14);
        titleFont = GreatDreamerTheme.archiveFont(26);
        subtitleFont = GreatDreamerTheme.archiveFont(12);

        musicSlider = new HorizontalSlider(0, 0, 200);
        sfxSlider = new HorizontalSlider(0, 0, 200);

        windowedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "WINDOWED");
        maximizedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "MAXIMIZED");
        fullscreenButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "FULLSCREEN");
        backButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "<  BACK");

        GreatDreamerTheme.styleArchiveButton(backButton, buttonFont);

        windowedButton.setFont(buttonFont);
        maximizedButton.setFont(buttonFont);
        fullscreenButton.setFont(buttonFont);
        windowedButton.setOnClick(() -> Game.setPersistedWindowMode(Window.Mode.WINDOWED));
        maximizedButton.setOnClick(() -> Game.setPersistedWindowMode(Window.Mode.MAXIMIZED));
        fullscreenButton.setOnClick(() -> Game.setPersistedWindowMode(Window.Mode.FULLSCREEN));
        backButton.setOnClick(() -> Engine.instance().stateProcessor.setState(new MainMenuState()));

        syncSlidersFromEngine();

        selectedIndex = FOCUS_BACK;
    }

    private void syncSlidersFromEngine() {
        float music = Engine.instance().audioManager.getMusicVolume(BACKGROUND_MUSIC_ID);
        musicSlider.setValue(Math.round(music * 100f));

        float sfx = Engine.instance().audioManager.getMasterSfxVolume();
        sfxSlider.setValue(Math.round(sfx * 100f));
    }

    private void applyMusicSlider() {
        float linear = musicSlider.getValue() / 100f;
        Engine.instance().audioManager.setVolume(BACKGROUND_MUSIC_ID, linear);
        GamePreferences.setMenuMusicVolume(linear);
    }

    private void applySfxSlider() {
        float linear = sfxSlider.getValue() / 100f;
        Engine.instance().audioManager.setMasterSfxVolume(linear);
        GamePreferences.setSfxVolume(linear);
    }

    @Override
    public void onExit() {
    }

    @Override
    public void tick() {
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            backButton.click();
            return;
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_UP)) {
            selectedIndex = (selectedIndex - 1 + FOCUS_COUNT) % FOCUS_COUNT;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DOWN)) {
            selectedIndex = (selectedIndex + 1) % FOCUS_COUNT;
        }

        int step = Engine.instance().keyboard.keyPressed(KeyEvent.VK_SHIFT) ? 10 : 2;
        if (selectedIndex == FOCUS_MUSIC) {
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
                musicSlider.setValue(musicSlider.getValue() - step);
                applyMusicSlider();
            }
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
                musicSlider.setValue(musicSlider.getValue() + step);
                applyMusicSlider();
            }
        } else if (selectedIndex == FOCUS_SFX) {
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
                sfxSlider.setValue(sfxSlider.getValue() - step);
                applySfxSlider();
            }
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
                sfxSlider.setValue(sfxSlider.getValue() + step);
                applySfxSlider();
            }
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            switch (selectedIndex) {
                case FOCUS_WINDOWED:
                    windowedButton.click();
                    break;
                case FOCUS_MAXIMIZED:
                    maximizedButton.click();
                    break;
                case FOCUS_FULLSCREEN:
                    fullscreenButton.click();
                    break;
                case FOCUS_BACK:
                    backButton.click();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void update() {
        updateLayout();

        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();

        if (musicSlider.update(mouseX, mouseY, mousePressed)) {
            applyMusicSlider();
        }
        if (sfxSlider.update(mouseX, mouseY, mousePressed)) {
            applySfxSlider();
        }

        windowedButton.update(mouseX, mouseY, mousePressed);
        maximizedButton.update(mouseX, mouseY, mousePressed);
        fullscreenButton.update(mouseX, mouseY, mousePressed);
        backButton.update(mouseX, mouseY, mousePressed);

        updateDisplayModeButtonColors();

        backButton.setSelected(selectedIndex == FOCUS_BACK);

        Window.Mode mode = Game.getDetectedWindowMode();
        windowedButton.setSelected(mode == Window.Mode.WINDOWED);
        maximizedButton.setSelected(mode == Window.Mode.MAXIMIZED);
        fullscreenButton.setSelected(mode == Window.Mode.FULLSCREEN);
    }

    private void updateDisplayModeButtonColors() {
        Window.Mode mode = Game.getDetectedWindowMode();
        Color normal = GreatDreamerTheme.BUTTON_NORMAL;
        Color hover = GreatDreamerTheme.BUTTON_HOVER;
        Color pressed = GreatDreamerTheme.BUTTON_PRESSED;

        styleModeButton(windowedButton, mode == Window.Mode.WINDOWED, normal, hover, pressed);
        styleModeButton(maximizedButton, mode == Window.Mode.MAXIMIZED, normal, hover, pressed);
        styleModeButton(fullscreenButton, mode == Window.Mode.FULLSCREEN, normal, hover, pressed);
    }

    private void styleModeButton(Button button, boolean active, Color normal, Color hover, Color pressed) {
        button.setColors(normal,
                active ? selectedColor : hover,
                pressed,
                active ? selectedTextColor : textColor);
    }

    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layout = GeneralMenuLayout.fromViewport(windowWidth, windowHeight);

        int bodyBottom = layout.content.bottom() - BODY_ACTION_RESERVE_PX;
        int bodyHeight = Math.max(260, bodyBottom - layout.body.y);
        UiRect constrainedBody = new UiRect(layout.body.x, layout.body.y, layout.body.width, bodyHeight);

        int rowBlockHeight = ROW_HEIGHT * 3 + ROW_GAP * 2;
        int idealPanelHeight = PANEL_TOP_INNER + rowBlockHeight + PANEL_BOTTOM_INNER;
        int panelHeight = Math.min(idealPanelHeight, constrainedBody.height - 18);
        audioPanelRect = new UiRect(
                constrainedBody.x + PANEL_SIDE_INSET,
                constrainedBody.y + 12,
                Math.max(200, constrainedBody.width - PANEL_SIDE_INSET * 2),
                Math.max(rowBlockHeight, panelHeight));

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
                int by = rowTop + (ROW_HEIGHT - DISPLAY_BUTTON_HEIGHT) / 2;
                windowedButton.x = bx;
                windowedButton.y = by;
                maximizedButton.x = bx + DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP;
                maximizedButton.y = by;
                fullscreenButton.x = bx + 2 * (DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP);
                fullscreenButton.y = by;
            }
        }

        UiRect backRect = layout.rightAction(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
        backButton.x = backRect.x;
        backButton.y = backRect.y;
    }

    @Override
    public void render(Graphics g) {
        updateLayout();

        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);

        GeneralMenuRenderer.drawHeader(g2d, layout.content, layout.content.right(), "SETTINGS",
                "AUDIO // DISPLAY", titleFont, subtitleFont);

        GeneralMenuRenderer.drawPanel(g2d, audioPanelRect);

        FontMetrics rowFm = g2d.getFontMetrics(rowLabelFont);
        int rowAscent = rowFm.getAscent();

        int innerLeft = audioPanelRect.x + 22;
        int valueRight = audioPanelRect.right() - 22;

        for (int row = 0; row < 3; row++) {
            int rowTop = audioPanelRect.y + PANEL_TOP_INNER + row * (ROW_HEIGHT + ROW_GAP);
            int labelBaseline = rowTop + ROW_HEIGHT / 2 + rowAscent / 2;

            String label;
            String valueText;
            if (row == 0) {
                label = "Menu music";
                valueText = musicSlider.getValue() + "%";
            } else if (row == 1) {
                label = "Sound effects";
                valueText = sfxSlider.getValue() + "%";
            } else {
                label = "Window mode";
                valueText = "";
            }

            g2d.setFont(rowLabelFont);
            g2d.setColor(textColor);
            g2d.drawString(label, innerLeft, labelBaseline);

            if (!valueText.isEmpty()) {
                g2d.setFont(subtitleFont);
                g2d.setColor(mutedTextColor);
                int vw = g2d.getFontMetrics().stringWidth(valueText);
                g2d.drawString(valueText, valueRight - vw, labelBaseline);
            }
        }

        musicSlider.render(g2d, GreatDreamerTheme.LINE, GreatDreamerTheme.BORDER,
                GreatDreamerTheme.MUTED_TEXT, GreatDreamerTheme.TEXT, selectedIndex == FOCUS_MUSIC);
        sfxSlider.render(g2d, GreatDreamerTheme.LINE, GreatDreamerTheme.BORDER,
                GreatDreamerTheme.MUTED_TEXT, GreatDreamerTheme.TEXT, selectedIndex == FOCUS_SFX);

        windowedButton.render(g);
        maximizedButton.render(g);
        fullscreenButton.render(g);
        backButton.render(g);
    }
}
