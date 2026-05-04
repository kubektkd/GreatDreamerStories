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
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;
import atomiccode.greatDreamerStories.ui.menu.MenuSettingsSliderPanel;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.Color;
import java.awt.Font;
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

    private static final int SETTINGS_ROW_COUNT = 3;

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
        subtitleFont = GreatDreamerTheme.archiveFont(12);

        musicSlider = new HorizontalSlider(0, 0, 200);
        sfxSlider = new HorizontalSlider(0, 0, 200);

        windowedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "WINDOWED");
        maximizedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "MAXIMIZED");
        fullscreenButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, "FULLSCREEN");
        backButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "<  BACK");

        GreatDreamerTheme.styleSecondaryButton(backButton, buttonFont);

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
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (musicSlider.contains(mx, my) || sfxSlider.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        if (windowedButton.contains(mx, my) || maximizedButton.contains(mx, my)
                || fullscreenButton.contains(mx, my) || backButton.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        return Cursor.SystemCursor.Arrow;
    }

    private void updateDisplayModeButtonColors() {
        Window.Mode mode = Game.getDetectedWindowMode();
        Color normal = GreatDreamerTheme.BUTTON_NORMAL;
        Color hover = GreatDreamerTheme.BUTTON_HOVER;
        Color pressed = GreatDreamerTheme.BUTTON_PRESSED;

        styleModeButton(windowedButton, mode == Window.Mode.WINDOWED, normal, hover, pressed);
        styleModeButton(maximizedButton, mode == Window.Mode.MAXIMIZED, normal, hover, pressed);
        styleModeButton(fullscreenButton, mode == Window.Mode.FULLSCREEN, normal, hover, pressed);

        windowedButton.setSelected(mode == Window.Mode.WINDOWED);
        maximizedButton.setSelected(mode == Window.Mode.MAXIMIZED);
        fullscreenButton.setSelected(mode == Window.Mode.FULLSCREEN);

        windowedButton.setKeyboardFocusRing(selectedIndex == FOCUS_WINDOWED);
        maximizedButton.setKeyboardFocusRing(selectedIndex == FOCUS_MAXIMIZED);
        fullscreenButton.setKeyboardFocusRing(selectedIndex == FOCUS_FULLSCREEN);
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

        audioPanelRect = MenuSettingsSliderPanel.layoutAudioPanelRect(layout, SETTINGS_ROW_COUNT);
        MenuSettingsSliderPanel.layoutRows(audioPanelRect, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, DISPLAY_BUTTON_GAP,
                musicSlider, sfxSlider, windowedButton, maximizedButton, fullscreenButton);

        MenuActionStrip.placePrimaryRight(layout, backButton, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
    }

    @Override
    public void render(Graphics g) {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);

        MenuScreenTitle.draw(g2d, layout.content, layout.content.right(), "SETTINGS",
                "AUDIO // DISPLAY", null);

        MenuSettingsSliderPanel.drawPanel(g2d, audioPanelRect);
        MenuSettingsSliderPanel.drawRowLabels(g2d, audioPanelRect, rowLabelFont, subtitleFont, textColor, mutedTextColor,
                new String[]{"Game music", "Sound effects", "Display mode"},
                new String[]{
                        musicSlider.getValue() + "%",
                        sfxSlider.getValue() + "%",
                        null
                });

        MenuSettingsSliderPanel.renderSlider(g2d, musicSlider, selectedIndex == FOCUS_MUSIC);
        MenuSettingsSliderPanel.renderSlider(g2d, sfxSlider, selectedIndex == FOCUS_SFX);

        windowedButton.render(g);
        maximizedButton.render(g);
        fullscreenButton.render(g);
        backButton.render(g);
    }
}
