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

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Character creation popup state for creating new characters.
 * Allows players to distribute skill points, select gender, and name their character.
 */
public class CharacterCreationState implements State {
    private static final int MAX_NAME_LENGTH = 24;
    private static final int STAT_COUNT = 9;
    private static final int ATTR_PANEL_TOP_OFFSET = 80;
    private static final int ATTR_PANEL_HEIGHT = 410;
    private static final int STAT_ROW_TOP_OFFSET = 34;
    private static final int STAT_ROW_SPACING = 42;
    private static final int CREATE_BUTTON_WIDTH = 210;
    private static final int BACK_BUTTON_WIDTH = 130;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int ACTION_BUTTON_GAP = 16;
    private static final int GENDER_BUTTON_WIDTH = 130;
    private static final int GENDER_BUTTON_HEIGHT = 38;
    private static final int GENDER_BUTTON_GAP = 12;
    /** Space from gender button bottom to name-panel top (~chip bleed above panel top + margin). */
    private static final int NAME_INPUT_CLEAR_BELOW_GENDER = 22;
    private static final long STAT_TOOLTIP_DELAY = 300;
    private static final String[] STAT_NAMES = {"Strength", "Power", "Education", "Constitution", "Intelligence", "Appearance", "Luck", "Size", "Dexterity"};
    private static final String[] STAT_CODES = {"STR", "POW", "EDU", "CON", "INT", "APP", "LCK", "SIZ", "DEX"};
    private static final String[] STAT_DESCRIPTIONS = {
        "Physical force.\nHelps with restraints, forced entry, and close confrontations.",
        "Willpower and nerve.\nHelps resist fear, pressure, and occult influence.",
        "Formal knowledge.\nHelps with records, procedure, research, and expert context.",
        "Endurance and health.\nHelps survive injury, fatigue, poison, and harsh conditions.",
        "Reasoning and deduction.\nHelps connect clues, solve puzzles, and understand motives.",
        "Presence and first impression.\nHelps with charm, disguise, and social access.",
        "Good fortune.\nHelps when events are uncertain or outside direct control.",
        "Body mass and reach.\nHelps with intimidation, carrying, pushing, and physical scale.",
        "Speed and precision.\nHelps with stealth, firearms, dodging, and delicate actions."
    };
    private static final String MALE_PORTRAIT = "characters/main-officer-male.jfif";
    private static final String FEMALE_PORTRAIT = "characters/main-officer-female.jfif";
    private static final String FALLBACK_PORTRAIT = "characters/test.jfif";
    private static final String DEFAULT_CHARACTER_NAME = "Officer Björn";
    
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
    
    // UI components
    private Button[] genderButtons;
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
    private MenuChipPanel attributesPanel;
    private int layoutY;
    private int leftWidth;
    private int rightX;
    private int portraitSize;
    private int portraitX;
    private int portraitY;
    private int attrPanelX;
    private int attrPanelY;
    private int attrPanelWidth;
    private int attrPanelHeight;
    
    // Input handling
    private int selectedIndex = 0;
    private int hoveredStatIndex = -1;
    private int draggingStatIndex = -1;
    
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
        nameInput = new TextInput(0, 0, 0, 0, "SUBJECT NAME", DEFAULT_CHARACTER_NAME, MAX_NAME_LENGTH);
        GreatDreamerTheme.styleTextInput(nameInput, smallFont, buttonFont);

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
        genderButtons = new Button[]{new Button(0, 0, GENDER_BUTTON_WIDTH, GENDER_BUTTON_HEIGHT, "MALE OFFICER"),
                                     new Button(0, 0, GENDER_BUTTON_WIDTH, GENDER_BUTTON_HEIGHT, "FEMALE OFFICER")};
        Character.Gender[] selectableGenders = {Character.Gender.MALE, Character.Gender.FEMALE};
        for (int i = 0; i < genderButtons.length; i++) {
            Character.Gender gender = selectableGenders[i];
            genderButtons[i].setColors(new Color(12, 13, 16), selectedColor, new Color(32, 32, 32), textColor);
            genderButtons[i].setFont(buttonFont);
            final Character.Gender selectedGender = gender;
            genderButtons[i].setOnClick(() -> this.selectedGender = selectedGender);
        }
        
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
        createButton = new Button(0, 0, CREATE_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "CREATE CHARACTER  >");
        GreatDreamerTheme.stylePrimaryButton(createButton, buttonFont);
        createButton.setOnClick(this::createCharacter);

        backButton = new Button(0, 0, BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "<  BACK");
        GreatDreamerTheme.styleArchiveButton(backButton, buttonFont);
        backButton.setOnClick(this::cancelCreation);

        // Setup menu buttons array for navigation
        menuButtons = new Button[genderButtons.length + statBigIncButtons.length + statIncButtons.length +
                                 statBigDecButtons.length + statDecButtons.length + 2];
        int index = 0;
        System.arraycopy(genderButtons, 0, menuButtons, index, genderButtons.length);
        index += genderButtons.length;
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
        characterName = DEFAULT_CHARACTER_NAME;
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
        UiRect b = attributesPanel.bounds();
        if (!mousePressed) {
            draggingStatIndex = -1;
            return;
        }
        if (draggingStatIndex < 0) {
            for (int i = 0; i < STAT_COUNT; i++) {
                if (AttributeSliderRow.sliderContains(b, getStatRowY(i), mouseX, mouseY)) {
                    draggingStatIndex = i;
                    applyStatFromSliderMouse(i, mouseX);
                    return;
                }
            }
        } else {
            applyStatFromSliderMouse(draggingStatIndex, mouseX);
        }
    }
    
    private void createCharacter() {
        characterName = nameInput.getText();
        if (characterName.trim().isEmpty()) {
            characterName = DEFAULT_CHARACTER_NAME; // Default name
            nameInput.setText(characterName);
        }
        
        if (getRemainingPoints() != 0) {
            return; // Must use all points
        }
        
        try {
            Character newCharacter = new Character(characterName.trim(), selectedGender, 
                                                 strength, power, education, constitution, intelligence, appearance, luck, size, dexterity);
            
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

        String tooltipText = hoveredStatIndex >= 0 && hoveredStatIndex < STAT_DESCRIPTIONS.length
                ? STAT_NAMES[hoveredStatIndex].toUpperCase() + "\n" + STAT_DESCRIPTIONS[hoveredStatIndex]
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
        int namePanelH = TextInput.preferredArchiveOuterHeight(buttonFont);
        nameInput.setBounds(leftX, namePanelY, leftContentWidth, namePanelH);

        rightX = layout.rightColumn.x;
        int rightWidth = layout.rightColumn.width;
        attrPanelX = rightX;
        attrPanelY = layoutY + ATTR_PANEL_TOP_OFFSET;
        // Match right edge to primary action bar (CREATE uses content.right() via rightAction).
        attrPanelWidth = rightWidth;
        attrPanelHeight = ATTR_PANEL_HEIGHT;

        attributesPanel = new MenuChipPanel(new UiRect(attrPanelX, attrPanelY, attrPanelWidth, attrPanelHeight), "ATTRIBUTES");
        UiRect attrBounds = attributesPanel.bounds();

        for (int i = 0; i < STAT_COUNT; i++) {
            int rowY = getStatRowY(i);
            AttributeSliderRow.layout(attrBounds, rowY, statLabelBounds[i], statBigDecButtons[i], statDecButtons[i],
                    statIncButtons[i], statBigIncButtons[i]);
        }

        MenuActionStrip.placeSecondaryBeforePrimary(layout, createButton, CREATE_BUTTON_WIDTH, backButton,
                BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, ACTION_BUTTON_GAP);
    }

    private int getStatRowY(int statIndex) {
        return attrPanelY + STAT_ROW_TOP_OFFSET + statIndex * STAT_ROW_SPACING;
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);

        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();

        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);
        MenuScreenTitle.draw(g2d, layout.content, layout.leftColumn.right(), "NEW INVESTIGATOR",
                "STOKSJÖ POLICE ARCHIVE // PERSONAL RECORD",
                new MenuScreenTitle.RightMetric(String.valueOf(getRemainingPoints()), "PTS REMAINING", 20, 94));
        MenuScreenTitle.drawSecondaryHeadingAlignedToMetric(g2d, layout.content, rightX + 15, "SUBJECT'S PROFILE");

        MenuPortrait.draw(g2d, portraitX, portraitY, portraitSize, selectedGender == Character.Gender.FEMALE ? femalePortrait : malePortrait);
        genderButtons[0].render(g2d);
        genderButtons[1].render(g2d);

        nameInput.render(g2d);
        drawAttributesPanel(g2d);
        drawCreateStatus(g2d);
        drawStatTooltip(g2d, windowWidth, windowHeight);

        backButton.render(g2d);
        createButton.render(g2d);
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
        String status = "USE ALL REMAINING POINTS TO CONTINUE";
        FontMetrics metrics = g2d.getFontMetrics();
        int statusX = createButton.x + (CREATE_BUTTON_WIDTH - metrics.stringWidth(status)) / 2;
        g2d.drawString(status, statusX, createButton.y + ACTION_BUTTON_HEIGHT + 18);
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

        AttributeSliderRow.renderLabelsAndSlider(g2d, attrBounds, rowY, STAT_NAMES[statIndex], STAT_CODES[statIndex],
                statValue);

        statIncButtons[statIndex].render(g2d);
        statBigIncButtons[statIndex].render(g2d);

        AttributeSliderRow.renderValue(g2d, attrBounds, rowY, statValue);
    }

}
