package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.TextInput;
import atomiccode.cthulhuEngine.ui.Tooltip;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;
import atomiccode.greatDreamerStories.ui.menu.AttributeSliderRow;
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuChipPanel;
import atomiccode.greatDreamerStories.ui.menu.MenuPortrait;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;
import atomiccode.greatDreamerStories.i18n.GameTexts;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Character creation popup state for creating new characters.
 * Allows players to distribute skill points, select gender, and name their character.
 */
public class CharacterCreationState implements State {

    private static final String BACK_BUTTON_GRAPHIC_PREFIX = "<  ";
    private static final String CREATE_BUTTON_GRAPHIC_SUFFIX = "  >";
    private static final int MAX_NAME_LENGTH = 24;
    private static final int STAT_COUNT = 9;
    private static final int ATTR_PANEL_TOP_OFFSET = 80;
    private static final int ATTR_PANEL_HEIGHT = 425;
    private static final int STAT_ROW_TOP_OFFSET = 34;
    private static final int STAT_ROW_SPACING = 42;
    private static final int CREATE_BUTTON_WIDTH = 210;
    private static final int BACK_BUTTON_WIDTH = 130;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int ACTION_BUTTON_GAP = 16;
    private static final int GENDER_BUTTON_WIDTH = 130;
    private static final int GENDER_BUTTON_HEIGHT = 38;
    private static final int GENDER_BUTTON_GAP = 12;
    private static final int AGE_SLIDER_MIN = 20;
    private static final int AGE_SLIDER_MAX = 65;
    private static final int AGE_PANEL_GAP_BELOW_NAME = 12;
    private static final int AGE_ROW_TOP_OFFSET = 30;
    private static final int AGE_PANEL_HEIGHT = 78;
    private static final int NAME_INPUT_CLEAR_BELOW_GENDER = 22;
    private static final int NAME_PANEL_MIN_HEIGHT = 64;
    private static final long STAT_TOOLTIP_DELAY = 300;
    /** Matches stat.*.short / .full / .desc keys in locale files. */
    private static final String[] STAT_STEMS = {"str", "pow", "edu", "con", "int", "app", "lck", "siz", "dex"};
    private static final String MALE_PORTRAIT = "characters/main-officer-male.jfif";
    private static final String FEMALE_PORTRAIT = "characters/main-officer-female.jfif";
    private static final String FALLBACK_PORTRAIT = "characters/test.jfif";
    
    private final int targetSlot;
    private final State returnState;
    
    // Character creation data
    private String characterName = "";
    private Character.Gender selectedGender = Character.Gender.MALE;
    private int strength = Character.MIN_STAT_VALUE;
    private int power = Character.MIN_STAT_VALUE;
    private int education = Character.MIN_STAT_VALUE;
    private int constitution = Character.MIN_STAT_VALUE;
    private int intelligence = Character.MIN_STAT_VALUE;
    private int appearance = Character.MIN_STAT_VALUE;
    private int luck = Character.MIN_STAT_VALUE;
    private int size = Character.MIN_STAT_VALUE;
    private int dexterity = Character.MIN_STAT_VALUE;
    private int age = Character.DEFAULT_INVESTIGATOR_AGE;
    
    // UI components
    private Button[] genderButtons;
    private Button ageBigDecButton;
    private Button ageDecButton;
    private Button ageIncButton;
    private Button ageBigIncButton;
    private Button[] statBigIncButtons;
    private Button[] statIncButtons;
    private Button[] statBigDecButtons;
    private Button[] statDecButtons;
    private Button createButton;
    private Button backButton;
    private Button[] menuButtons;
    private TextInput nameInput;
    private Tooltip statTooltip;
    private Rectangle[] statLabelBounds = new Rectangle[STAT_COUNT];
    private GeneralMenuLayout layout;
    private MenuChipPanel namePanel;
    private MenuChipPanel agePanel;
    private MenuChipPanel attributesPanel;
    private int layoutY;
    private int leftWidth;
    private int rightX;
    private int portraitSize;
    private int portraitX;
    private int portraitY;
    private int agePanelX;
    private int agePanelY;
    private int agePanelWidth;
    private int agePanelHeight;
    private int attrPanelX;
    private int attrPanelY;
    private int attrPanelWidth;
    private int attrPanelHeight;
    
    // Input handling
    private int selectedIndex = 0;
    private int hoveredStatIndex = -1;
    private int draggingStatIndex = -1;
    private boolean draggingAgeSlider;
    private final Rectangle ageLabelBounds = new Rectangle();
    
    // Colors and fonts
    private final Color textColor = GreatDreamerTheme.TEXT;
    private final Color mutedTextColor = GreatDreamerTheme.MUTED_TEXT;
    private final Color selectedColor = GreatDreamerTheme.SELECTED;
    private final Color selectedTextColor = GreatDreamerTheme.SELECTED_TEXT;
    private Font buttonFont;
    private Font smallFont;
    private Font tooltipFont;
    private Image malePortrait;
    private Image femalePortrait;
    
    public CharacterCreationState(int targetSlot, State returnState) {
        this.targetSlot = targetSlot;
        this.returnState = returnState;
    }
    
    @Override
    public int getPriority() {
        return 100; // High priority popup
    }
    
    @Override
    public boolean isOpaque() {
        return true;
    }
    
    @Override
    public void onEnter() {
        // Initialize fonts
        buttonFont = GreatDreamerTheme.archiveFont(14);
        smallFont = GreatDreamerTheme.archiveFont(10);
        tooltipFont = GreatDreamerTheme.archiveFont(14);
        malePortrait = loadPortrait(MALE_PORTRAIT);
        femalePortrait = loadPortrait(FEMALE_PORTRAIT);
        
        initializeInputs();
        initializeButtons();
        resetCharacterData();
    }

    private void initializeInputs() {
        nameInput = new TextInput(0, 0, 0, 0, "", GameTexts.tr("character.default_name"), MAX_NAME_LENGTH);
        GreatDreamerTheme.styleTextInput(nameInput, smallFont, buttonFont);
        nameInput.setDrawChrome(false);

        statTooltip = new Tooltip(STAT_TOOLTIP_DELAY);
        GreatDreamerTheme.styleTooltip(statTooltip, tooltipFont);
    }

    private Image loadPortrait(String portraitPath) {
        try {
            return Engine.instance().resources.getImage(portraitPath);
        } catch (RuntimeException e) {
            return Engine.instance().resources.getImage(FALLBACK_PORTRAIT);
        }
    }
    
    private void initializeButtons() {
        // Gender selection buttons
        genderButtons = new Button[]{new Button(0, 0, GENDER_BUTTON_WIDTH, GENDER_BUTTON_HEIGHT,
                GameTexts.tr("screen.character_create.gender.male_officer")),
                                     new Button(0, 0, GENDER_BUTTON_WIDTH, GENDER_BUTTON_HEIGHT,
                                             GameTexts.tr("screen.character_create.gender.female_officer"))};
        Character.Gender[] selectableGenders = {Character.Gender.MALE, Character.Gender.FEMALE};
        for (int i = 0; i < genderButtons.length; i++) {
            Character.Gender gender = selectableGenders[i];
            genderButtons[i].setColors(new Color(12, 13, 16), selectedColor, new Color(32, 32, 32), textColor);
            genderButtons[i].setFont(buttonFont);
            final Character.Gender selectedGender = gender;
            genderButtons[i].setOnClick(() -> this.selectedGender = selectedGender);
        }

        ageBigDecButton = new Button(0, 0, AttributeSliderRow.BIG_STEP_BUTTON_WIDTH, 22, "--");
        ageBigDecButton.setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        ageBigDecButton.setFont(smallFont);
        ageBigDecButton.setOnClick(() -> adjustAge(-10));
        ageDecButton = new Button(0, 0, AttributeSliderRow.STEP_BUTTON_WIDTH, 22, "-");
        ageDecButton.setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        ageDecButton.setFont(smallFont);
        ageDecButton.setOnClick(() -> adjustAge(-1));
        ageIncButton = new Button(0, 0, AttributeSliderRow.STEP_BUTTON_WIDTH, 22, "+");
        ageIncButton.setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        ageIncButton.setFont(smallFont);
        ageIncButton.setOnClick(() -> adjustAge(1));
        ageBigIncButton = new Button(0, 0, AttributeSliderRow.BIG_STEP_BUTTON_WIDTH, 22, "++");
        ageBigIncButton.setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        ageBigIncButton.setFont(smallFont);
        ageBigIncButton.setOnClick(() -> adjustAge(10));
        
        // Stat adjustment buttons
        statBigIncButtons = new Button[STAT_COUNT];
        statIncButtons = new Button[STAT_COUNT];
        statBigDecButtons = new Button[STAT_COUNT];
        statDecButtons = new Button[STAT_COUNT];
        
        for (int i = 0; i < STAT_COUNT; i++) {
            statLabelBounds[i] = new Rectangle();

            statBigIncButtons[i] = new Button(0, 0, AttributeSliderRow.BIG_STEP_BUTTON_WIDTH, 22, "++");
            statBigIncButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statBigIncButtons[i].setFont(smallFont);
            
            statIncButtons[i] = new Button(0, 0, AttributeSliderRow.STEP_BUTTON_WIDTH, 22, "+");
            statIncButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statIncButtons[i].setFont(smallFont);
            
            statBigDecButtons[i] = new Button(0, 0, AttributeSliderRow.BIG_STEP_BUTTON_WIDTH, 22, "--");
            statBigDecButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statBigDecButtons[i].setFont(smallFont);
            
            statDecButtons[i] = new Button(0, 0, AttributeSliderRow.STEP_BUTTON_WIDTH, 22, "-");
            statDecButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statDecButtons[i].setFont(smallFont);
            
            final int statIndex = i;
            statBigIncButtons[i].setOnClick(() -> adjustStat(statIndex, 10));
            statIncButtons[i].setOnClick(() -> adjustStat(statIndex, 1));
            statBigDecButtons[i].setOnClick(() -> adjustStat(statIndex, -10));
            statDecButtons[i].setOnClick(() -> adjustStat(statIndex, -1));
        }
        
        // Action buttons
        createButton = new Button(0, 0, CREATE_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT,
                GameTexts.tr("screen.character_create.action.create") + CREATE_BUTTON_GRAPHIC_SUFFIX);
        GreatDreamerTheme.stylePrimaryButton(createButton, buttonFont);
        createButton.setOnClick(this::createCharacter);

        backButton = new Button(0, 0, BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT,
                BACK_BUTTON_GRAPHIC_PREFIX + GameTexts.tr("common.back"));
        GreatDreamerTheme.styleSecondaryButton(backButton, buttonFont);
        backButton.setOnClick(this::cancelCreation);

        // Setup menu buttons array for navigation
        menuButtons = new Button[genderButtons.length + 4 + statBigIncButtons.length + statIncButtons.length +
                                 statBigDecButtons.length + statDecButtons.length + 2];
        int index = 0;
        System.arraycopy(genderButtons, 0, menuButtons, index, genderButtons.length);
        index += genderButtons.length;
        menuButtons[index++] = ageBigDecButton;
        menuButtons[index++] = ageDecButton;
        menuButtons[index++] = ageIncButton;
        menuButtons[index++] = ageBigIncButton;
        System.arraycopy(statBigIncButtons, 0, menuButtons, index, statBigIncButtons.length);
        index += statBigIncButtons.length;
        System.arraycopy(statIncButtons, 0, menuButtons, index, statIncButtons.length);
        index += statIncButtons.length;
        System.arraycopy(statBigDecButtons, 0, menuButtons, index, statBigDecButtons.length);
        index += statBigDecButtons.length;
        System.arraycopy(statDecButtons, 0, menuButtons, index, statDecButtons.length);
        index += statDecButtons.length;
        menuButtons[index++] = backButton;
        menuButtons[index] = createButton;
    }
    
    private void resetCharacterData() {
        characterName = GameTexts.tr("character.default_name");
        nameInput.setText(characterName);
        selectedGender = Character.Gender.MALE;
        strength = Character.INITIAL_STAT_VALUE;
        power = Character.INITIAL_STAT_VALUE;
        education = Character.INITIAL_STAT_VALUE;
        constitution = Character.INITIAL_STAT_VALUE;
        intelligence = Character.INITIAL_STAT_VALUE;
        appearance = Character.INITIAL_STAT_VALUE;
        luck = Character.INITIAL_STAT_VALUE;
        size = Character.INITIAL_STAT_VALUE;
        dexterity = Character.INITIAL_STAT_VALUE;
        age = AttributeSliderRow.clampUserValue(Character.DEFAULT_INVESTIGATOR_AGE, AGE_SLIDER_MIN, AGE_SLIDER_MAX);
    }

    private void adjustAge(int delta) {
        age = AttributeSliderRow.clampUserValue(age + delta, AGE_SLIDER_MIN, AGE_SLIDER_MAX);
    }

    private void applyAgeFromSliderMouse(int mouseX) {
        int raw = AttributeSliderRow.valueAtMouseRange(mouseX, agePanel.bounds(), getAgeRowY(), AGE_SLIDER_MIN, AGE_SLIDER_MAX);
        age = raw;
    }
    
    private void adjustStat(int statIndex, int amount) {
        int currentValue = getStatValue(statIndex);
        if (!canAdjustStat(currentValue, amount)) {
            return;
        }
        int adjustedValue = currentValue + amount;

        switch (statIndex) {
            case 0: strength = adjustedValue; break;
            case 1: power = adjustedValue; break;
            case 2: education = adjustedValue; break;
            case 3: constitution = adjustedValue; break;
            case 4: intelligence = adjustedValue; break;
            case 5: appearance = adjustedValue; break;
            case 6: luck = adjustedValue; break;
            case 7: size = adjustedValue; break;
            case 8: dexterity = adjustedValue; break;
        }
    }

    private boolean canAdjustStat(int currentValue, int amount) {
        if (amount > 0) {
            return currentValue + amount <= Character.MAX_STAT_VALUE && getRemainingPoints() >= amount;
        }
        return currentValue + amount >= Character.MIN_STAT_VALUE;
    }
    
    private int getRemainingPoints() {
        int usedPoints = strength + power + education + constitution + intelligence + appearance + luck + size + dexterity;
        return Character.INITIAL_SKILL_POINTS - usedPoints;
    }

    private int getStatValue(int statIndex) {
        switch (statIndex) {
            case 0: return strength;
            case 1: return power;
            case 2: return education;
            case 3: return constitution;
            case 4: return intelligence;
            case 5: return appearance;
            case 6: return luck;
            case 7: return size;
            case 8: return dexterity;    
            default: return Character.INITIAL_STAT_VALUE;
        }
    }

    private void setStatTo(int statIndex, int newValue) {
        int cur = getStatValue(statIndex);
        int maxAllowed = Math.min(Character.MAX_STAT_VALUE, cur + getRemainingPoints());
        int minAllowed = Character.MIN_STAT_VALUE;
        int v = AttributeSliderRow.clampUserValue(newValue, minAllowed, maxAllowed);
        switch (statIndex) {
            case 0: strength = v; break;
            case 1: power = v; break;
            case 2: education = v; break;
            case 3: constitution = v; break;
            case 4: intelligence = v; break;
            case 5: appearance = v; break;
            case 6: luck = v; break;
            case 7: size = v; break;
            case 8: dexterity = v; break;
            default: break;
        }
    }

    private void applyStatFromSliderMouse(int statIndex, int mouseX) {
        int raw = AttributeSliderRow.valueAtMouse(mouseX, attributesPanel.bounds(), getStatRowY(statIndex));
        setStatTo(statIndex, raw);
    }

    private void updateAttributeSliderDrag(int mouseX, int mouseY, boolean mousePressed) {
        if (!mousePressed) {
            draggingStatIndex = -1;
            draggingAgeSlider = false;
            return;
        }
        if (draggingAgeSlider) {
            applyAgeFromSliderMouse(mouseX);
            return;
        }
        if (draggingStatIndex >= 0) {
            applyStatFromSliderMouse(draggingStatIndex, mouseX);
            return;
        }
        UiRect ageBounds = agePanel.bounds();
        if (AttributeSliderRow.sliderContains(ageBounds, getAgeRowY(), mouseX, mouseY)) {
            draggingAgeSlider = true;
            applyAgeFromSliderMouse(mouseX);
            return;
        }
        UiRect b = attributesPanel.bounds();
        for (int i = 0; i < STAT_COUNT; i++) {
            if (AttributeSliderRow.sliderContains(b, getStatRowY(i), mouseX, mouseY)) {
                draggingStatIndex = i;
                applyStatFromSliderMouse(i, mouseX);
                return;
            }
        }
    }
    
    private void createCharacter() {
        characterName = nameInput.getText();
        if (characterName.trim().isEmpty()) {
            characterName = GameTexts.tr("character.default_name");
            nameInput.setText(characterName);
        }
        
        if (getRemainingPoints() != 0) {
            return; // Must use all points
        }
        
        try {
            Character newCharacter = new Character(characterName.trim(), selectedGender, 
                                                 strength, power, education, constitution, intelligence, appearance, luck, size, dexterity, age);
            
            if (SaveManager.getInstance().saveCharacter(newCharacter, targetSlot)) {
                // Character created successfully, return to character select
                Engine.instance().stateProcessor.setState(returnState);
            }
        } catch (IllegalArgumentException e) {
            // Invalid character data, do nothing
            System.err.println("Failed to create character: " + e.getMessage());
        }
    }
    
    private void cancelCreation() {
        Engine.instance().stateProcessor.setState(returnState);
    }
    
    @Override
    public void onExit() {
        // Cleanup if needed
    }
    
    @Override
    public void tick() {
        boolean wasTypingName = nameInput.isActive();
        nameInput.tick();

        // Handle navigation when text input does not own the keyboard.
        if (!wasTypingName && !nameInput.isActive()) {
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
                if (selectedIndex < menuButtons.length) {
                    menuButtons[selectedIndex].click();
                }
            }
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            cancelCreation();
        }
    }
    
    @Override
    public void update() {
        updateLayout();
        
        // Update all buttons
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();

        if (!nameInput.isActive()) {
            updateAttributeSliderDrag(mouseX, mouseY, mousePressed);
        } else {
            draggingStatIndex = -1;
            draggingAgeSlider = false;
        }

        nameInput.update(mouseX, mouseY, mousePressed);
        updateStatTooltip(mouseX, mouseY);
        
        for (Button button : menuButtons) {
            if (button != null) {
                button.update(mouseX, mouseY, mousePressed);
            }
        }
        
        // Update selection highlighting
        for (int i = 0; i < menuButtons.length; i++) {
            if (menuButtons[i] != null) {
                menuButtons[i].setSelected(!nameInput.isActive() && i == selectedIndex);
            }
        }
        
        // Update gender button highlighting
        genderButtons[0].setSelected(selectedGender == Character.Gender.MALE);
        genderButtons[1].setSelected(selectedGender == Character.Gender.FEMALE);
        updateGenderButtonColors();
        updateAgeButtonColors();
        updateStatButtonColors();
        updateCreateButtonColor();
    }

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (nameInput.isActive() && nameInput.contains(mx, my)) {
            return Cursor.SystemCursor.Ibeam;
        }
        if (nameInput.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        for (Button b : genderButtons) {
            if (b.contains(mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
        }
        if (agePanel != null) {
            UiRect ab = agePanel.bounds();
            if (AttributeSliderRow.sliderContains(ab, getAgeRowY(), mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
            if (ageBigDecButton.contains(mx, my) || ageDecButton.contains(mx, my)
                    || ageIncButton.contains(mx, my) || ageBigIncButton.contains(mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
        }
        if (attributesPanel != null) {
            UiRect b = attributesPanel.bounds();
            for (int i = 0; i < STAT_COUNT; i++) {
                if (AttributeSliderRow.sliderContains(b, getStatRowY(i), mx, my)) {
                    return Cursor.SystemCursor.Hand;
                }
                if (statLabelBounds[i] != null && statLabelBounds[i].contains(mx, my)) {
                    return Cursor.SystemCursor.Hand;
                }
                if (statBigDecButtons[i].contains(mx, my) || statDecButtons[i].contains(mx, my)
                        || statIncButtons[i].contains(mx, my) || statBigIncButtons[i].contains(mx, my)) {
                    return Cursor.SystemCursor.Hand;
                }
            }
        }
        for (Button button : menuButtons) {
            if (button != null && button.contains(mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
        }
        return Cursor.SystemCursor.Arrow;
    }

    private void updateStatTooltip(int mouseX, int mouseY) {
        hoveredStatIndex = -1;

        for (int i = 0; i < STAT_COUNT; i++) {
            if (statLabelBounds[i] != null && statLabelBounds[i].contains(mouseX, mouseY)) {
                hoveredStatIndex = i;
                break;
            }
        }

        String tooltipText = hoveredStatIndex >= 0 && hoveredStatIndex < STAT_STEMS.length
                ? statFull(hoveredStatIndex).toUpperCase(GameTexts.getActiveLocale()) + "\n" + statDesc(hoveredStatIndex)
                : "";
        statTooltip.update(hoveredStatIndex >= 0, tooltipText);
    }

    private void updateGenderButtonColors() {
        for (int i = 0; i < genderButtons.length; i++) {
            boolean isSelected = (i == 0 && selectedGender == Character.Gender.MALE) ||
                                 (i == 1 && selectedGender == Character.Gender.FEMALE);
            genderButtons[i].setColors(new Color(12, 13, 16), isSelected ? selectedColor : new Color(42, 43, 48), new Color(32, 32, 32),
                                       isSelected ? selectedTextColor : textColor);
        }
    }

    private void updateAgeButtonColors() {
        setStatButtonEnabled(ageBigDecButton, age - 10 >= AGE_SLIDER_MIN);
        setStatButtonEnabled(ageDecButton, age > AGE_SLIDER_MIN);
        setStatButtonEnabled(ageIncButton, age < AGE_SLIDER_MAX);
        setStatButtonEnabled(ageBigIncButton, age + 10 <= AGE_SLIDER_MAX);
    }

    private void updateStatButtonColors() {
        for (int i = 0; i < STAT_COUNT; i++) {
            boolean atMinimum = getStatValue(i) <= Character.MIN_STAT_VALUE;
            boolean atMaximum = getStatValue(i) >= Character.MAX_STAT_VALUE;
            int remainingPoints = getRemainingPoints();
            setStatButtonEnabled(statBigDecButtons[i], !atMinimum && canAdjustStat(getStatValue(i), -10));
            setStatButtonEnabled(statDecButtons[i], !atMinimum);
            setStatButtonEnabled(statIncButtons[i], !atMaximum && remainingPoints >= 1);
            setStatButtonEnabled(statBigIncButtons[i], !atMaximum && canAdjustStat(getStatValue(i), 10));
        }
    }

    private void setStatButtonEnabled(Button button, boolean enabled) {
        if (enabled) {
            button.setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        } else {
            button.setColors(GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED_TEXT);
        }
    }

    private void updateCreateButtonColor() {
        if (getRemainingPoints() == 0) {
            GreatDreamerTheme.stylePrimaryButton(createButton, buttonFont);
        } else {
            createButton.setColors(GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED, GreatDreamerTheme.BUTTON_DISABLED_TEXT);
        }
    }

    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layout = GeneralMenuLayout.fromViewport(windowWidth, windowHeight);
        layoutY = layout.content.y;

        int leftX = layout.leftColumn.x;
        leftWidth = layout.leftRawWidth;
        int leftContentWidth = layout.leftColumn.width;
        int leftContentCenterX = layout.leftColumn.centerX();
        int genderButtonGroupWidth = GENDER_BUTTON_WIDTH * 2 + GENDER_BUTTON_GAP;
        portraitSize = Math.min(260, leftWidth - 160);
        portraitX = leftContentCenterX - portraitSize / 2;
        portraitY = layoutY + 100;

        genderButtons[0].x = leftContentCenterX - genderButtonGroupWidth / 2;
        genderButtons[0].y = portraitY + portraitSize + 22;
        genderButtons[1].x = genderButtons[0].x + GENDER_BUTTON_WIDTH + GENDER_BUTTON_GAP;
        genderButtons[1].y = genderButtons[0].y;

        int namePanelY = genderButtons[0].y + GENDER_BUTTON_HEIGHT + NAME_INPUT_CLEAR_BELOW_GENDER;
        int namePanelH = Math.max(TextInput.preferredOuterHeight(buttonFont), NAME_PANEL_MIN_HEIGHT);
        UiRect nameRect = new UiRect(leftX, namePanelY, leftContentWidth, namePanelH);
        if (namePanel == null) {
            namePanel = new MenuChipPanel(nameRect, GameTexts.tr("screen.character_create.panel.name"));
        } else {
            namePanel.setBounds(nameRect);
            namePanel.setChipLabel(GameTexts.tr("screen.character_create.panel.name"));
        }
        UiRect nameInner = MenuChipPanel.innerContentRectForBounds(nameRect);
        nameInput.setBounds(nameInner.x, nameInner.y, nameInner.width, nameInner.height);

        agePanelX = leftX;
        agePanelY = namePanelY + namePanelH + AGE_PANEL_GAP_BELOW_NAME;
        agePanelWidth = leftContentWidth;
        agePanelHeight = AGE_PANEL_HEIGHT;
        UiRect ageRect = new UiRect(agePanelX, agePanelY, agePanelWidth, agePanelHeight);
        if (agePanel == null) {
            agePanel = new MenuChipPanel(ageRect, GameTexts.tr("screen.character_create.panel.traits"));
        } else {
            agePanel.setBounds(ageRect);
            agePanel.setChipLabel(GameTexts.tr("screen.character_create.panel.traits"));
        }
        AttributeSliderRow.layout(agePanel.bounds(), getAgeRowY(), ageLabelBounds, ageBigDecButton, ageDecButton,
                ageIncButton, ageBigIncButton);

        rightX = layout.rightColumn.x;
        int rightWidth = layout.rightColumn.width;
        attrPanelX = rightX;
        attrPanelY = layoutY + ATTR_PANEL_TOP_OFFSET;
        // Match right edge to primary action bar (CREATE uses content.right() via rightAction).
        attrPanelWidth = rightWidth;
        attrPanelHeight = ATTR_PANEL_HEIGHT;

        UiRect attrRect = new UiRect(attrPanelX, attrPanelY, attrPanelWidth, attrPanelHeight);
        if (attributesPanel == null) {
            attributesPanel = new MenuChipPanel(attrRect, GameTexts.tr("screen.character_create.panel.attributes"));
        } else {
            attributesPanel.setBounds(attrRect);
            attributesPanel.setChipLabel(GameTexts.tr("screen.character_create.panel.attributes"));
        }
        UiRect attrBounds = attributesPanel.bounds();

        for (int i = 0; i < STAT_COUNT; i++) {
            int rowY = getStatRowY(i);
            AttributeSliderRow.layout(attrBounds, rowY, statLabelBounds[i], statBigDecButtons[i], statDecButtons[i],
                    statIncButtons[i], statBigIncButtons[i]);
        }

        MenuActionStrip.placeSecondaryBeforePrimary(layout, createButton, CREATE_BUTTON_WIDTH, backButton,
                BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, ACTION_BUTTON_GAP);
    }

    private static String statShort(int i) {
        return GameTexts.tr("stat." + STAT_STEMS[i] + ".short");
    }

    private static String statFull(int i) {
        return GameTexts.tr("stat." + STAT_STEMS[i] + ".full");
    }

    private static String statDesc(int i) {
        return GameTexts.tr("stat." + STAT_STEMS[i] + ".desc");
    }

    private int getStatRowY(int statIndex) {
        return attrPanelY + STAT_ROW_TOP_OFFSET + statIndex * STAT_ROW_SPACING;
    }

    private int getAgeRowY() {
        return agePanelY + AGE_ROW_TOP_OFFSET;
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);
        MenuScreenTitle.draw(g2d, layout.content, layout.leftColumn.right(), GameTexts.tr("screen.character_create.title"),
                GameTexts.archiveStrapline("archive.strapline.personal_record"),
                new MenuScreenTitle.RightMetric(String.valueOf(getRemainingPoints()),
                        GameTexts.tr("screen.character_create.metric.pts_remaining"), 20, 94));
        MenuScreenTitle.drawSecondaryHeadingAlignedToMetric(g2d, layout.content, rightX + 15,
                GameTexts.tr("screen.character_create.secondary_heading"));

        MenuPortrait.draw(g2d, portraitX, portraitY, portraitSize, selectedGender == Character.Gender.FEMALE ? femalePortrait : malePortrait);
        genderButtons[0].render(g2d);
        genderButtons[1].render(g2d);

        drawNamePanel(g2d);
        drawAgePanel(g2d);
        drawAttributesPanel(g2d);
        drawCreateStatus(g2d);
        drawStatTooltip(g2d, windowWidth, windowHeight);

        backButton.render(g2d);
        createButton.render(g2d);
    }

    private void drawNamePanel(Graphics2D g2d) {
        namePanel.drawPanelBody(g2d);
        nameInput.render(g2d);
        namePanel.drawChip(g2d);
    }

    private void drawStatTooltip(Graphics2D g2d, int windowWidth, int windowHeight) {
        statTooltip.render(g2d, Engine.instance().mouse.getX(), Engine.instance().mouse.getY(), windowWidth, windowHeight);
    }

    private void drawCreateStatus(Graphics2D g2d) {
        if (getRemainingPoints() == 0) {
            return;
        }

        g2d.setColor(mutedTextColor);
        g2d.setFont(smallFont);
        String status = GameTexts.tr("screen.character_create.status.spend_points");
        FontMetrics metrics = g2d.getFontMetrics();
        int statusX = createButton.x + (CREATE_BUTTON_WIDTH - metrics.stringWidth(status)) / 2;
        g2d.drawString(status, statusX, createButton.y + ACTION_BUTTON_HEIGHT + 18);
    }

    private void drawAgePanel(Graphics2D g2d) {
        agePanel.drawPanelBody(g2d);
        int rowY = getAgeRowY();
        UiRect ageBounds = agePanel.bounds();

        ageBigDecButton.render(g2d);
        ageDecButton.render(g2d);
        AttributeSliderRow.renderLabelsAndSliderRanged(g2d, ageBounds, rowY, GameTexts.tr("screen.character_create.age_label"), "", age, AGE_SLIDER_MIN,
                AGE_SLIDER_MAX);
        ageIncButton.render(g2d);
        ageBigIncButton.render(g2d);
        AttributeSliderRow.renderValue(g2d, ageBounds, rowY, age);
        agePanel.drawChip(g2d);
    }

    private void drawAttributesPanel(Graphics2D g2d) {
        attributesPanel.drawPanelBody(g2d);

        for (int i = 0; i < STAT_COUNT; i++) {
            drawAttributeRow(g2d, i);
        }
        attributesPanel.drawChip(g2d);
    }

    private void drawAttributeRow(Graphics2D g2d, int statIndex) {
        int statValue = getStatValue(statIndex);
        int rowY = getStatRowY(statIndex);
        UiRect attrBounds = attributesPanel.bounds();

        statBigDecButtons[statIndex].render(g2d);
        statDecButtons[statIndex].render(g2d);

        AttributeSliderRow.renderLabelsAndSlider(g2d, attrBounds, rowY, statFull(statIndex), statShort(statIndex),
                statValue);

        statIncButtons[statIndex].render(g2d);
        statBigIncButtons[statIndex].render(g2d);

        AttributeSliderRow.renderValue(g2d, attrBounds, rowY, statValue);
    }

}
