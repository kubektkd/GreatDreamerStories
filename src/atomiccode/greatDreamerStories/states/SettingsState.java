package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.Dropdown;
import atomiccode.cthulhuEngine.ui.HorizontalSlider;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

import atomiccode.greatDreamerStories.Game;
import atomiccode.greatDreamerStories.GameDifficulty;
import atomiccode.greatDreamerStories.GamePreferences;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuChipPanel;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;
import atomiccode.greatDreamerStories.ui.menu.MenuSettingsSliderPanel;
import atomiccode.greatDreamerStories.i18n.GameTexts;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;

/**
 * In-game settings reachable from the main menu. Returns to {@link MainMenuState} on Back or Esc.
 */
public class SettingsState implements State {

    private static final String BACK_BUTTON_GRAPHIC_PREFIX = "<  ";

    private static final String BACKGROUND_MUSIC_ID = "background_music";

    private static final int ACTION_BUTTON_WIDTH = 130;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int DISPLAY_BUTTON_WIDTH = 130;
    private static final int DISPLAY_BUTTON_HEIGHT = 38;
    private static final int DISPLAY_BUTTON_GAP = 12;

    private static final int PANEL_TOP_PADDING = 12;
    private static final int PANEL_GAP_Y = 16;

    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_RIGHT_INSET = 16;
    private static final int SCROLLBAR_CONTENT_GAP = 14;
    private static final int CONTENT_BOTTOM_PADDING = 24;
    private static final int SCROLL_WHEEL_STEP_PX = 38;

    /** MenuSettingsSliderPanel expects 22px from panel border; MenuChipPanel uses 15px. */
    private static final int LABEL_VALUE_RIGHT_INSET_ADJUST = 7;

    private static final int FOCUS_MUSIC = 0;
    private static final int FOCUS_SFX = 1;
    private static final int FOCUS_WINDOWED = 2;
    private static final int FOCUS_MAXIMIZED = 3;
    private static final int FOCUS_FULLSCREEN = 4;
    private static final int FOCUS_DIFFICULTY_EASY = 5;
    private static final int FOCUS_DIFFICULTY_NORMAL = 6;
    private static final int FOCUS_DIFFICULTY_HARD = 7;
    private static final int FOCUS_LANGUAGE = 8;
    private static final int FOCUS_BACK = 9;
    private static final int FOCUS_SAVE = 10;
    private static final int FOCUS_COUNT = 11;

    private Button windowedButton;
    private Button maximizedButton;
    private Button fullscreenButton;
    private Button difficultyEasyButton;
    private Button difficultyNormalButton;
    private Button difficultyHardButton;
    private Button saveButton;
    private Button backButton;
    private HorizontalSlider musicSlider;
    private HorizontalSlider sfxSlider;

    private int selectedIndex = FOCUS_BACK;

    private Font buttonFont;
    private Font rowLabelFont;
    private Font subtitleFont;
    private GeneralMenuLayout layout;

    private MenuChipPanel audioPanel;
    private MenuChipPanel displayPanel;
    private MenuChipPanel gameplayPanel;
    private MenuChipPanel languagePanel;
    private MenuChipPanel keybindingsPanel;

    private UiRect scrollViewportRect;
    private int scrollOffsetY;
    private int maxScrollOffsetY;

    private UiRect scrollbarTrackRect;
    private UiRect scrollbarThumbRect;
    private boolean draggingScrollbarThumb;
    private int dragThumbMouseOffsetY;
    private boolean wasMouseLeftPressed;

    private int chipInnerInsetsTotalPx = -1;

    private int selectedLanguageIndex = 0;

    private Dropdown languageDropdown;

    private boolean hasUnsavedChanges = false;
    private int baselineMusicPercent;
    private int baselineSfxPercent;
    private Window.Mode baselineWindowMode;
    private int baselineLanguageIndex;

    private GameDifficulty selectedDifficulty = GameDifficulty.NORMAL;
    private GameDifficulty baselineDifficulty = GameDifficulty.NORMAL;

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

        windowedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.window.windowed"));
        maximizedButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.window.maximized"));
        fullscreenButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.window.fullscreen"));
        saveButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, GameTexts.tr("settings.action.save_changes"));
        backButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT,
                BACK_BUTTON_GRAPHIC_PREFIX + GameTexts.tr("common.back"));

        GreatDreamerTheme.stylePrimaryButton(saveButton, buttonFont);
        GreatDreamerTheme.styleSecondaryButton(backButton, buttonFont);

        windowedButton.setFont(buttonFont);
        maximizedButton.setFont(buttonFont);
        fullscreenButton.setFont(buttonFont);

        difficultyEasyButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.difficulty.easy"));
        difficultyNormalButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.difficulty.normal"));
        difficultyHardButton = new Button(0, 0, DISPLAY_BUTTON_WIDTH, DISPLAY_BUTTON_HEIGHT, GameTexts.tr("settings.difficulty.hard"));
        difficultyEasyButton.setFont(buttonFont);
        difficultyNormalButton.setFont(buttonFont);
        difficultyHardButton.setFont(buttonFont);
        difficultyEasyButton.setOnClick(() -> {
            selectedDifficulty = GameDifficulty.EASY;
            refreshDirtyFlag();
        });
        difficultyNormalButton.setOnClick(() -> {
            selectedDifficulty = GameDifficulty.NORMAL;
            refreshDirtyFlag();
        });
        difficultyHardButton.setOnClick(() -> {
            selectedDifficulty = GameDifficulty.HARD;
            refreshDirtyFlag();
        });

        windowedButton.setOnClick(() -> {
            Game.setSessionWindowMode(Window.Mode.WINDOWED);
            refreshDirtyFlag();
        });
        maximizedButton.setOnClick(() -> {
            Game.setSessionWindowMode(Window.Mode.MAXIMIZED);
            refreshDirtyFlag();
        });
        fullscreenButton.setOnClick(() -> {
            Game.setSessionWindowMode(Window.Mode.FULLSCREEN);
            refreshDirtyFlag();
        });
        saveButton.setOnClick(this::saveChanges);
        backButton.setOnClick(this::exitWithoutSaving);

        audioPanel = new MenuChipPanel(new UiRect(0, 0, 100, 100), GameTexts.tr("settings.panel.audio"));
        displayPanel = new MenuChipPanel(new UiRect(0, 0, 100, 100), GameTexts.tr("settings.panel.graphics"));
        gameplayPanel = new MenuChipPanel(new UiRect(0, 0, 100, 100), GameTexts.tr("settings.panel.gameplay"));
        languagePanel = new MenuChipPanel(new UiRect(0, 0, 100, 100), GameTexts.tr("settings.panel.language"));
        keybindingsPanel = new MenuChipPanel(new UiRect(0, 0, 100, 100), GameTexts.tr("settings.panel.keybindings"));

        languageDropdown = new Dropdown(0, 0, 200, DISPLAY_BUTTON_HEIGHT);
        GreatDreamerTheme.styleDropdown(languageDropdown, buttonFont);
        languageDropdown.setOnSelectionChange(() -> {
            selectedLanguageIndex = languageDropdown.getSelectedIndex();
            GameTexts.setLocaleForTag(languageTagForIndex(selectedLanguageIndex));
            refreshLanguageDropdownOptions();
            refreshDirtyFlag();
        });

        syncSlidersFromEngine();

        selectedLanguageIndex = languageIndexForTag(GamePreferences.getLanguageTag());
        GameTexts.setLocaleForTag(languageTagForIndex(selectedLanguageIndex));
        baselineMusicPercent = musicSlider.getValue();
        baselineSfxPercent = sfxSlider.getValue();
        baselineWindowMode = GamePreferences.getWindowMode();
        baselineLanguageIndex = selectedLanguageIndex;
        selectedDifficulty = GamePreferences.getDifficulty();
        baselineDifficulty = selectedDifficulty;
        refreshLanguageDropdownOptions();
        refreshDirtyFlag();

        selectedIndex = FOCUS_BACK;

        scrollOffsetY = 0;
        maxScrollOffsetY = 0;
        draggingScrollbarThumb = false;
        wasMouseLeftPressed = false;
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
        refreshDirtyFlag();
    }

    private void applySfxSlider() {
        float linear = sfxSlider.getValue() / 100f;
        Engine.instance().audioManager.setMasterSfxVolume(linear);
        refreshDirtyFlag();
    }

    @Override
    public void onExit() {
    }

    private void saveChanges() {
        if (!hasUnsavedChanges) {
            return;
        }

        float music = musicSlider.getValue() / 100f;
        float sfx = sfxSlider.getValue() / 100f;
        GamePreferences.setMenuMusicVolume(music);
        GamePreferences.setSfxVolume(sfx);

        Window.Mode mode = Game.getDetectedWindowMode();
        GamePreferences.setWindowMode(mode);

        GamePreferences.setDifficulty(selectedDifficulty);
        GamePreferences.setLanguageTag(languageTagForIndex(selectedLanguageIndex));

        baselineMusicPercent = musicSlider.getValue();
        baselineSfxPercent = sfxSlider.getValue();
        baselineWindowMode = mode;
        baselineLanguageIndex = selectedLanguageIndex;
        baselineDifficulty = selectedDifficulty;
        refreshDirtyFlag();
    }

    private void exitWithoutSaving() {
        // Revert runtime to baseline (persisted) values.
        Engine.instance().audioManager.setVolume(BACKGROUND_MUSIC_ID, baselineMusicPercent / 100f);
        Engine.instance().audioManager.setMasterSfxVolume(baselineSfxPercent / 100f);
        Game.setSessionWindowMode(baselineWindowMode);

        musicSlider.setValue(baselineMusicPercent);
        sfxSlider.setValue(baselineSfxPercent);
        selectedLanguageIndex = baselineLanguageIndex;
        selectedDifficulty = baselineDifficulty;
        GameTexts.setLocaleForTag(languageTagForIndex(baselineLanguageIndex));
        if (languageDropdown != null) {
            languageDropdown.closeWithoutApply();
            refreshLanguageDropdownOptions();
        }

        Engine.instance().stateProcessor.setState(new MainMenuState());
    }

    private void refreshDirtyFlag() {
        Window.Mode currentMode = Game.getDetectedWindowMode();
        boolean dirty = musicSlider != null && sfxSlider != null
                && (musicSlider.getValue() != baselineMusicPercent
                || sfxSlider.getValue() != baselineSfxPercent
                || currentMode != baselineWindowMode
                || selectedDifficulty != baselineDifficulty
                || selectedLanguageIndex != baselineLanguageIndex);
        hasUnsavedChanges = dirty;

        if (saveButton != null) {
            if (hasUnsavedChanges) {
                GreatDreamerTheme.stylePrimaryButton(saveButton, buttonFont);
            } else {
                GreatDreamerTheme.styleDisabledButton(saveButton, buttonFont);
            }
        }
    }

    @Override
    public void tick() {
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            if (languageDropdown != null && languageDropdown.isExpanded()) {
                languageDropdown.closeWithoutApply();
                return;
            }
            backButton.click();
            return;
        }

        boolean langExpanded = languageDropdown != null && languageDropdown.isExpanded();

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_UP)) {
            if (selectedIndex == FOCUS_LANGUAGE && langExpanded) {
                languageDropdown.handleKeyJustPressed(KeyEvent.VK_UP);
                return;
            }
            selectedIndex = (selectedIndex - 1 + FOCUS_COUNT) % FOCUS_COUNT;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DOWN)) {
            if (selectedIndex == FOCUS_LANGUAGE && langExpanded) {
                languageDropdown.handleKeyJustPressed(KeyEvent.VK_DOWN);
                return;
            }
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

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_SPACE)) {
            if (selectedIndex == FOCUS_LANGUAGE && languageDropdown != null) {
                if (langExpanded) {
                    languageDropdown.handleKeyJustPressed(KeyEvent.VK_SPACE);
                    return;
                }
                if (languageDropdown.handleOpenKeyJustPressed(KeyEvent.VK_SPACE)) {
                    return;
                }
            }
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            if (selectedIndex == FOCUS_LANGUAGE && languageDropdown != null) {
                if (langExpanded) {
                    languageDropdown.handleKeyJustPressed(KeyEvent.VK_ENTER);
                    return;
                }
                if (languageDropdown.handleOpenKeyJustPressed(KeyEvent.VK_ENTER)) {
                    return;
                }
            }
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
                case FOCUS_DIFFICULTY_EASY:
                    difficultyEasyButton.click();
                    break;
                case FOCUS_DIFFICULTY_NORMAL:
                    difficultyNormalButton.click();
                    break;
                case FOCUS_DIFFICULTY_HARD:
                    difficultyHardButton.click();
                    break;
                case FOCUS_SAVE:
                    saveButton.click();
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
        boolean mouseJustPressed = mousePressed && !wasMouseLeftPressed;
        int wheelSteps = Engine.instance().mouse.consumeScrollY();

        if (wheelSteps != 0 && languageDropdown != null && languageDropdown.isExpanded()
                && languageDropdown.containsOpenBounds(mouseX, mouseY)) {
            languageDropdown.applyWheelSteps(wheelSteps);
        } else if (wheelSteps != 0 && scrollViewportRect != null && scrollViewportRect.contains(mouseX, mouseY)) {
            int previous = scrollOffsetY;
            scrollOffsetY = clamp(scrollOffsetY + wheelSteps * SCROLL_WHEEL_STEP_PX, 0, maxScrollOffsetY);
            if (scrollOffsetY != previous) {
                updateLayout();
            }
        }

        boolean scrollChanged = handleScrollBarInput(mouseX, mouseY, mousePressed, mouseJustPressed);
        if (scrollChanged) {
            updateLayout();
        }

        boolean updateInteractiveControls = !draggingScrollbarThumb;

        if (updateInteractiveControls) {
            if (musicSlider.update(mouseX, mouseY, mousePressed)) {
                applyMusicSlider();
            }
            if (sfxSlider.update(mouseX, mouseY, mousePressed)) {
                applySfxSlider();
            }

            windowedButton.update(mouseX, mouseY, mousePressed);
            maximizedButton.update(mouseX, mouseY, mousePressed);
            fullscreenButton.update(mouseX, mouseY, mousePressed);

            difficultyEasyButton.update(mouseX, mouseY, mousePressed);
            difficultyNormalButton.update(mouseX, mouseY, mousePressed);
            difficultyHardButton.update(mouseX, mouseY, mousePressed);
            saveButton.update(mouseX, mouseY, mousePressed);
            backButton.update(mouseX, mouseY, mousePressed);

            if (languageDropdown != null) {
                languageDropdown.update(mouseX, mouseY, mousePressed);
            }
        }

        updateDisplayModeButtonColors();
        updateDifficultyButtonColors();
        if (languageDropdown != null) {
            languageDropdown.setKeyboardFocusRing(selectedIndex == FOCUS_LANGUAGE);
        }
        saveButton.setKeyboardFocusRing(selectedIndex == FOCUS_SAVE);
        saveButton.setSelected(selectedIndex == FOCUS_SAVE);
        backButton.setSelected(selectedIndex == FOCUS_BACK);
        wasMouseLeftPressed = mousePressed;
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (musicSlider.contains(mx, my) || sfxSlider.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        if (scrollbarTrackRect != null && scrollbarTrackRect.contains(mx, my)
                || scrollbarThumbRect != null && scrollbarThumbRect.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        if (windowedButton.contains(mx, my) || maximizedButton.contains(mx, my)
                || fullscreenButton.contains(mx, my)
                || difficultyEasyButton.contains(mx, my)
                || difficultyNormalButton.contains(mx, my)
                || difficultyHardButton.contains(mx, my)
                || saveButton.contains(mx, my) || backButton.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        if (languageDropdown != null && languageDropdown.containsOpenBounds(mx, my)) {
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

    private void updateDifficultyButtonColors() {
        Color normal = GreatDreamerTheme.BUTTON_NORMAL;
        Color hover = GreatDreamerTheme.BUTTON_HOVER;
        Color pressed = GreatDreamerTheme.BUTTON_PRESSED;

        styleModeButton(difficultyEasyButton, selectedDifficulty == GameDifficulty.EASY, normal, hover, pressed);
        styleModeButton(difficultyNormalButton, selectedDifficulty == GameDifficulty.NORMAL, normal, hover, pressed);
        styleModeButton(difficultyHardButton, selectedDifficulty == GameDifficulty.HARD, normal, hover, pressed);

        difficultyEasyButton.setSelected(selectedDifficulty == GameDifficulty.EASY);
        difficultyNormalButton.setSelected(selectedDifficulty == GameDifficulty.NORMAL);
        difficultyHardButton.setSelected(selectedDifficulty == GameDifficulty.HARD);

        difficultyEasyButton.setKeyboardFocusRing(selectedIndex == FOCUS_DIFFICULTY_EASY);
        difficultyNormalButton.setKeyboardFocusRing(selectedIndex == FOCUS_DIFFICULTY_NORMAL);
        difficultyHardButton.setKeyboardFocusRing(selectedIndex == FOCUS_DIFFICULTY_HARD);
    }

    private void styleModeButton(Button button, boolean active, Color normal, Color hover, Color pressed) {
        button.setColors(normal,
                active ? selectedColor : hover,
                pressed,
                active ? selectedTextColor : textColor);
    }

    private boolean handleScrollBarInput(int mouseX, int mouseY, boolean mousePressed, boolean mouseJustPressed) {
        if (maxScrollOffsetY <= 0 || scrollbarTrackRect == null || scrollbarThumbRect == null) {
            draggingScrollbarThumb = false;
            return false;
        }

        boolean changed = false;
        if (mouseJustPressed) {
            if (scrollbarThumbRect.contains(mouseX, mouseY)) {
                draggingScrollbarThumb = true;
                dragThumbMouseOffsetY = mouseY - scrollbarThumbRect.y;
            } else if (scrollbarTrackRect.contains(mouseX, mouseY)) {
                int scrollablePixels = scrollbarTrackRect.height - scrollbarThumbRect.height;
                if (scrollablePixels > 0) {
                    int desiredThumbTop = (mouseY - scrollbarTrackRect.y) - scrollbarThumbRect.height / 2;
                    int thumbTop = clamp(desiredThumbTop, 0, scrollablePixels);
                    float t = thumbTop / (float) scrollablePixels;
                    int newScroll = Math.round(t * maxScrollOffsetY);
                    changed = newScroll != scrollOffsetY;
                    scrollOffsetY = newScroll;
                }
            }
        }

        if (draggingScrollbarThumb) {
            if (!mousePressed) {
                draggingScrollbarThumb = false;
            } else {
                int scrollablePixels = scrollbarTrackRect.height - scrollbarThumbRect.height;
                if (scrollablePixels <= 0) {
                    int newScroll = 0;
                    changed = newScroll != scrollOffsetY;
                    scrollOffsetY = newScroll;
                } else {
                    int desiredThumbTop = (mouseY - scrollbarTrackRect.y) - dragThumbMouseOffsetY;
                    int thumbTop = clamp(desiredThumbTop, 0, scrollablePixels);
                    float t = thumbTop / (float) scrollablePixels;
                    int newScroll = Math.round(t * maxScrollOffsetY);
                    changed = newScroll != scrollOffsetY;
                    scrollOffsetY = newScroll;
                }
            }
        }

        return changed;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layout = GeneralMenuLayout.fromViewport(windowWidth, windowHeight);

        int bodyBottom = layout.content.bottom() - MenuSettingsSliderPanel.BODY_ACTION_RESERVE_PX;
        scrollViewportRect = new UiRect(layout.body.x, layout.body.y, layout.body.width,
                Math.max(0, bodyBottom - layout.body.y));

        int panelX = scrollViewportRect.x + MenuSettingsSliderPanel.PANEL_SIDE_INSET;
        int scrollLaneReserve = SCROLLBAR_RIGHT_INSET + SCROLLBAR_WIDTH + SCROLLBAR_CONTENT_GAP;
        int panelW = Math.max(200, scrollViewportRect.width - MenuSettingsSliderPanel.PANEL_SIDE_INSET * 2 - scrollLaneReserve);

        if (chipInnerInsetsTotalPx < 0) {
            // MenuChipPanel inner content rect has chip-dependent top inset; compute once for our sizing.
            UiRect dummy = new UiRect(0, 0, 100, 200);
            UiRect inner = MenuChipPanel.innerContentRectForBounds(dummy);
            chipInnerInsetsTotalPx = dummy.height - inner.height;
        }

        int audioInnerHeightNeeded = MenuSettingsSliderPanel.ROW_HEIGHT * 2
                + MenuSettingsSliderPanel.ROW_GAP;
        int displayInnerHeightNeeded = MenuSettingsSliderPanel.ROW_HEIGHT;
        int gameplayInnerHeightNeeded = MenuSettingsSliderPanel.ROW_HEIGHT;

        int languageInnerHeightNeeded = MenuSettingsSliderPanel.ROW_HEIGHT;

        int keybindRows = keybindRowLabels().length;
        int keybindingsInnerHeightNeeded = keybindRows * MenuSettingsSliderPanel.ROW_HEIGHT
                + Math.max(0, keybindRows - 1) * MenuSettingsSliderPanel.ROW_GAP + 44;

        int audioPanelHeight = audioInnerHeightNeeded + chipInnerInsetsTotalPx;
        int displayPanelHeight = displayInnerHeightNeeded + chipInnerInsetsTotalPx;
        int gameplayPanelHeight = gameplayInnerHeightNeeded + chipInnerInsetsTotalPx;
        int languagePanelHeight = languageInnerHeightNeeded + chipInnerInsetsTotalPx;
        int keybindingsPanelHeight = keybindingsInnerHeightNeeded + chipInnerInsetsTotalPx;

        int contentY = scrollViewportRect.y + PANEL_TOP_PADDING;
        UiRect audioBase = new UiRect(panelX, contentY, panelW, audioPanelHeight);
        contentY += audioPanelHeight + PANEL_GAP_Y;

        UiRect displayBase = new UiRect(panelX, contentY, panelW, displayPanelHeight);
        contentY += displayPanelHeight + PANEL_GAP_Y;

        UiRect gameplayBase = new UiRect(panelX, contentY, panelW, gameplayPanelHeight);
        contentY += gameplayPanelHeight + PANEL_GAP_Y;

        UiRect languageBase = new UiRect(panelX, contentY, panelW, languagePanelHeight);
        contentY += languagePanelHeight + PANEL_GAP_Y;

        UiRect keybindingsBase = new UiRect(panelX, contentY, panelW, keybindingsPanelHeight);
        int contentBottom = keybindingsBase.y + keybindingsBase.height + CONTENT_BOTTOM_PADDING;

        int unscrolledContentHeight = contentBottom - scrollViewportRect.y;
        maxScrollOffsetY = Math.max(0, unscrolledContentHeight - scrollViewportRect.height);
        scrollOffsetY = clamp(scrollOffsetY, 0, maxScrollOffsetY);
        draggingScrollbarThumb = draggingScrollbarThumb && maxScrollOffsetY > 0;

        UiRect audioBounds = audioBase.withY(audioBase.y - scrollOffsetY);
        UiRect displayBounds = displayBase.withY(displayBase.y - scrollOffsetY);
        UiRect gameplayBounds = gameplayBase.withY(gameplayBase.y - scrollOffsetY);
        UiRect languageBounds = languageBase.withY(languageBase.y - scrollOffsetY);
        UiRect keybindingsBounds = keybindingsBase.withY(keybindingsBase.y - scrollOffsetY);
        audioPanel.setBounds(audioBounds);
        displayPanel.setBounds(displayBounds);
        gameplayPanel.setBounds(gameplayBounds);
        languagePanel.setBounds(languageBounds);
        keybindingsPanel.setBounds(keybindingsBounds);
        audioPanel.setChipLabel(GameTexts.tr("settings.panel.audio"));
        displayPanel.setChipLabel(GameTexts.tr("settings.panel.graphics"));
        gameplayPanel.setChipLabel(GameTexts.tr("settings.panel.gameplay"));
        languagePanel.setChipLabel(GameTexts.tr("settings.panel.language"));
        keybindingsPanel.setChipLabel(GameTexts.tr("settings.panel.keybindings"));

        MenuActionStrip.placeSecondaryBeforePrimary(layout, saveButton, ACTION_BUTTON_WIDTH,
                backButton, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, 12);

        // Scrollbar geometry (in viewport space).
        int trackW = SCROLLBAR_WIDTH;
        int trackX = scrollViewportRect.right() - SCROLLBAR_RIGHT_INSET - trackW;
        scrollbarTrackRect = new UiRect(trackX, scrollViewportRect.y, trackW, scrollViewportRect.height);

        if (maxScrollOffsetY <= 0 || scrollViewportRect.height <= 0) {
            scrollbarThumbRect = scrollbarTrackRect;
        } else {
            double contentHeightD = Math.max(1, unscrolledContentHeight);
            double thumbHDouble = scrollViewportRect.height * scrollViewportRect.height / contentHeightD;
            int thumbH = (int) Math.round(thumbHDouble);
            thumbH = clamp(thumbH, 24, scrollViewportRect.height);

            int scrollablePixels = scrollViewportRect.height - thumbH;
            float t = scrollOffsetY / (float) maxScrollOffsetY;
            int thumbY = scrollViewportRect.y + Math.round(scrollablePixels * t);

            scrollbarThumbRect = new UiRect(trackX, thumbY, trackW, thumbH);
        }

        // Audio controls inside audio inner area.
        UiRect audioInner = MenuChipPanel.innerContentRectForBounds(audioPanel.bounds());
        int labelX = audioInner.x + LABEL_VALUE_RIGHT_INSET_ADJUST;
        int valueRightX = audioInner.right() - LABEL_VALUE_RIGHT_INSET_ADJUST;

        int sliderX = labelX + MenuSettingsSliderPanel.LABEL_COLUMN_WIDTH + MenuSettingsSliderPanel.LABEL_SLIDER_GAP;
        int sliderW = valueRightX - MenuSettingsSliderPanel.VALUE_COLUMN_WIDTH - MenuSettingsSliderPanel.LABEL_SLIDER_GAP - sliderX;
        sliderW = Math.max(HorizontalSlider.THUMB_WIDTH + 20, sliderW);

        for (int row = 0; row < 2; row++) {
            int rowTop = audioInner.y + row * (MenuSettingsSliderPanel.ROW_HEIGHT + MenuSettingsSliderPanel.ROW_GAP);
            int by = rowTop + (MenuSettingsSliderPanel.ROW_HEIGHT - musicSlider.trackHeight) / 2;
            if (row == 0) {
                musicSlider.x = sliderX;
                musicSlider.y = by;
                musicSlider.trackWidth = sliderW;
            } else {
                sfxSlider.x = sliderX;
                sfxSlider.y = by;
                sfxSlider.trackWidth = sliderW;
            }
        }

        // Display mode controls inside display inner area.
        UiRect displayInner = MenuChipPanel.innerContentRectForBounds(displayPanel.bounds());
        int displayLabelX = displayInner.x + LABEL_VALUE_RIGHT_INSET_ADJUST;
        int displaySliderX = displayLabelX + MenuSettingsSliderPanel.LABEL_COLUMN_WIDTH + MenuSettingsSliderPanel.LABEL_SLIDER_GAP;
        int displayRowTop = displayInner.y;
        int displayButtonY = displayRowTop + (MenuSettingsSliderPanel.ROW_HEIGHT - DISPLAY_BUTTON_HEIGHT) / 2;

        windowedButton.x = displaySliderX;
        windowedButton.y = displayButtonY;
        maximizedButton.x = displaySliderX + DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP;
        maximizedButton.y = displayButtonY;
        fullscreenButton.x = displaySliderX + 2 * (DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP);
        fullscreenButton.y = displayButtonY;

        UiRect gameplayInner = MenuChipPanel.innerContentRectForBounds(gameplayPanel.bounds());
        int gameplayLabelX = gameplayInner.x + LABEL_VALUE_RIGHT_INSET_ADJUST;
        int gameplaySliderX = gameplayLabelX + MenuSettingsSliderPanel.LABEL_COLUMN_WIDTH + MenuSettingsSliderPanel.LABEL_SLIDER_GAP;
        int gameplayRowTop = gameplayInner.y;
        int gameplayButtonY = gameplayRowTop + (MenuSettingsSliderPanel.ROW_HEIGHT - DISPLAY_BUTTON_HEIGHT) / 2;

        difficultyEasyButton.x = gameplaySliderX;
        difficultyEasyButton.y = gameplayButtonY;
        difficultyNormalButton.x = gameplaySliderX + DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP;
        difficultyNormalButton.y = gameplayButtonY;
        difficultyHardButton.x = gameplaySliderX + 2 * (DISPLAY_BUTTON_WIDTH + DISPLAY_BUTTON_GAP);
        difficultyHardButton.y = gameplayButtonY;

        // Language dropdown (same column alignment as display / gameplay rows).
        UiRect languageInner = MenuChipPanel.innerContentRectForBounds(languagePanel.bounds());
        int languageSliderX = languageInner.x + LABEL_VALUE_RIGHT_INSET_ADJUST
                + MenuSettingsSliderPanel.LABEL_COLUMN_WIDTH + MenuSettingsSliderPanel.LABEL_SLIDER_GAP;
        int languageRowTop = languageInner.y;
        int languageDdY = languageRowTop + (MenuSettingsSliderPanel.ROW_HEIGHT - DISPLAY_BUTTON_HEIGHT) / 2;
        int languageDdW = languageInner.right() - LABEL_VALUE_RIGHT_INSET_ADJUST - languageSliderX;
        if (languageDropdown != null) {
            languageDropdown.setBounds(languageSliderX, languageDdY, Math.max(80, languageDdW));
        }
    }

    private void drawChipPanelRowLabels(Graphics2D g2d, UiRect innerRect, String[] rowLabels, String[] rowValues) {
        if (rowLabels.length != rowValues.length) {
            throw new IllegalArgumentException("rowLabels and rowValues must have the same length");
        }

        FontMetrics rowFm = g2d.getFontMetrics(rowLabelFont);
        int rowAscent = rowFm.getAscent();
        int labelX = innerRect.x + LABEL_VALUE_RIGHT_INSET_ADJUST;
        int valueRightX = innerRect.right() - LABEL_VALUE_RIGHT_INSET_ADJUST;

        for (int row = 0; row < rowLabels.length; row++) {
            int rowTop = innerRect.y + row * (MenuSettingsSliderPanel.ROW_HEIGHT + MenuSettingsSliderPanel.ROW_GAP);
            int labelBaseline = rowTop + MenuSettingsSliderPanel.ROW_HEIGHT / 2 + rowAscent / 2;

            g2d.setFont(rowLabelFont);
            g2d.setColor(textColor);
            g2d.drawString(rowLabels[row], labelX, labelBaseline);

            String valueText = rowValues[row];
            if (valueText != null && !valueText.isEmpty()) {
                g2d.setFont(subtitleFont);
                g2d.setColor(mutedTextColor);
                int vw = g2d.getFontMetrics().stringWidth(valueText);
                g2d.drawString(valueText, valueRightX - vw, labelBaseline);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);

        MenuScreenTitle.draw(g2d, layout.content, layout.content.right(), GameTexts.tr("settings.title"),
                GameTexts.archiveStrapline("archive.strapline.maintenance_room"), null);

        Shape oldClip = g2d.getClip();
        if (scrollViewportRect != null) {
            g2d.setClip(scrollViewportRect.x, scrollViewportRect.y, scrollViewportRect.width, scrollViewportRect.height);
        }

        // AUDIO
        audioPanel.drawPanelBody(g2d);
        UiRect audioInner = MenuChipPanel.innerContentRectForBounds(audioPanel.bounds());
        drawChipPanelRowLabels(g2d, audioInner,
                new String[]{GameTexts.tr("settings.row.game_music"), GameTexts.tr("settings.row.sound_effects")},
                new String[]{musicSlider.getValue() + "%", sfxSlider.getValue() + "%"});
        MenuSettingsSliderPanel.renderSlider(g2d, musicSlider, selectedIndex == FOCUS_MUSIC);
        MenuSettingsSliderPanel.renderSlider(g2d, sfxSlider, selectedIndex == FOCUS_SFX);
        audioPanel.drawChip(g2d);

        // DISPLAY
        displayPanel.drawPanelBody(g2d);
        UiRect displayInner = MenuChipPanel.innerContentRectForBounds(displayPanel.bounds());
        drawChipPanelRowLabels(g2d, displayInner,
                new String[]{GameTexts.tr("settings.row.display_mode")},
                new String[]{null});
        windowedButton.render(g);
        maximizedButton.render(g);
        fullscreenButton.render(g);
        displayPanel.drawChip(g2d);

        // GAMEPLAY
        gameplayPanel.drawPanelBody(g2d);
        UiRect gameplayInner = MenuChipPanel.innerContentRectForBounds(gameplayPanel.bounds());
        drawChipPanelRowLabels(g2d, gameplayInner,
                new String[]{GameTexts.tr("settings.row.difficulty")},
                new String[]{null});
        difficultyEasyButton.render(g);
        difficultyNormalButton.render(g);
        difficultyHardButton.render(g);
        gameplayPanel.drawChip(g2d);

        // LANGUAGE
        languagePanel.drawPanelBody(g2d);
        UiRect languageInner = MenuChipPanel.innerContentRectForBounds(languagePanel.bounds());
        drawChipPanelRowLabels(g2d, languageInner,
                new String[]{GameTexts.tr("settings.row.language")},
                new String[]{null});
        if (languageDropdown != null) {
            languageDropdown.renderHeader(g);
        }
        languagePanel.drawChip(g2d);

        // KEYBINDINGS (mock table)
        keybindingsPanel.drawPanelBody(g2d);
        UiRect keybindInner = MenuChipPanel.innerContentRectForBounds(keybindingsPanel.bounds());
        drawChipPanelRowLabels(g2d, keybindInner, keybindRowLabels(), keybindRowValues());
        g2d.setFont(subtitleFont);
        g2d.setColor(mutedTextColor);
        int kbRows = keybindRowLabels().length;
        int noteY = keybindInner.y + kbRows * MenuSettingsSliderPanel.ROW_HEIGHT
                + Math.max(0, kbRows - 1) * MenuSettingsSliderPanel.ROW_GAP + 18;
        g2d.drawString(GameTexts.tr("settings.keybind.note"), keybindInner.x + LABEL_VALUE_RIGHT_INSET_ADJUST, noteY);
        keybindingsPanel.drawChip(g2d);

        // Restore clip; scrollbar and Back button are outside of the scroll region.
        g2d.setClip(oldClip);

        if (languageDropdown != null) {
            languageDropdown.renderPopupList(g);
        }

        renderScrollbar(g2d);
        saveButton.render(g);
        backButton.render(g);
    }

    private static String languageTagForIndex(int index) {
        return index == 1 ? "pl" : "en";
    }

    private static int languageIndexForTag(String tag) {
        return tag != null && tag.equalsIgnoreCase("pl") ? 1 : 0;
    }

    private void refreshLanguageDropdownOptions() {
        if (languageDropdown == null) {
            return;
        }
        int idx = selectedLanguageIndex;
        languageDropdown.setOptions(new String[]{GameTexts.tr("meta.lang.en"), GameTexts.tr("meta.lang.pl")});
        languageDropdown.setSelectedIndex(idx);
    }

    private static String[] keybindRowLabels() {
        return new String[]{
                GameTexts.tr("settings.keybind.move"),
                GameTexts.tr("settings.keybind.interact"),
                GameTexts.tr("settings.keybind.inventory"),
                GameTexts.tr("settings.keybind.journal")
        };
    }

    private static String[] keybindRowValues() {
        return new String[]{
                GameTexts.tr("settings.keybind.values.move"),
                GameTexts.tr("settings.keybind.values.interact"),
                GameTexts.tr("settings.keybind.values.inventory"),
                GameTexts.tr("settings.keybind.values.journal")
        };
    }

    private void renderScrollbar(Graphics2D g2d) {
        if (scrollbarTrackRect == null || scrollbarThumbRect == null || scrollViewportRect == null) {
            return;
        }

        int mx = Engine.instance().mouse.getX();
        int my = Engine.instance().mouse.getY();
        boolean hoverThumb = scrollbarThumbRect.contains(mx, my);
        boolean show = maxScrollOffsetY > 0;

        if (!show) {
            // Keep scrollbar minimal/hidden when everything fits.
            return;
        }

        Color trackFill = new Color(30, 32, 38, 120);
        Color trackBorder = GreatDreamerTheme.BORDER;
        Color thumbFill = hoverThumb ? GreatDreamerTheme.SELECTED : new Color(245, 244, 238, 165);
        Color thumbBorder = new Color(165, 163, 154, 220);

        g2d.setColor(trackFill);
        g2d.fillRect(scrollbarTrackRect.x, scrollbarTrackRect.y, scrollbarTrackRect.width, scrollbarTrackRect.height);
        g2d.setColor(trackBorder);
        g2d.drawRect(scrollbarTrackRect.x, scrollbarTrackRect.y, scrollbarTrackRect.width, scrollbarTrackRect.height);

        g2d.setColor(thumbFill);
        g2d.fillRect(scrollbarThumbRect.x, scrollbarThumbRect.y, scrollbarThumbRect.width, scrollbarThumbRect.height);
        g2d.setColor(thumbBorder);
        g2d.drawRect(scrollbarThumbRect.x, scrollbarThumbRect.y, scrollbarThumbRect.width, scrollbarThumbRect.height);
    }
}
