package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;

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
    private static final long STAT_TOOLTIP_DELAY = 300;
    private static final long BACKSPACE_REPEAT_INITIAL_DELAY = 350;
    private static final long BACKSPACE_REPEAT_INTERVAL = 45;
    private static final long CARET_MOVE_REPEAT_INITIAL_DELAY = 350;
    private static final long CARET_MOVE_REPEAT_INTERVAL = 45;
    private static final Color BUTTON_DISABLED_COLOR = new Color(13, 14, 17);
    private static final Color BUTTON_DISABLED_TEXT_COLOR = new Color(65, 66, 70);
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
    private Rectangle nameBoxBounds = new Rectangle();
    private Rectangle[] statLabelBounds = new Rectangle[STAT_COUNT];
    private int layoutWidth;
    private int layoutHeight;
    private int layoutX;
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
    private boolean isTypingName = false;
    private int selectedIndex = 0;
    private boolean wasMousePressed = false;
    private int nameCaretIndex = 0;
    private long nextBackspaceRepeatTime = 0;
    private long nextLeftCaretRepeatTime = 0;
    private long nextRightCaretRepeatTime = 0;
    private int hoveredStatIndex = -1;
    private long statTooltipStartTime = 0;
    
    // Colors and fonts
    private final Color pageColor = new Color(8, 9, 11);
    private final Color panelColor = new Color(11, 12, 15, 210);
    private final Color borderColor = new Color(55, 58, 65);
    private final Color mutedBorderColor = new Color(31, 34, 40);
    private final Color textColor = new Color(235, 233, 225);
    private final Color mutedTextColor = new Color(116, 116, 116);
    private final Color selectedColor = new Color(245, 244, 238);
    private final Color selectedTextColor = new Color(18, 18, 18);
    private Font buttonFont;
    private Font titleFont;
    private Font labelFont;
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
        buttonFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        titleFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 30);
        labelFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 12);
        smallFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 10);
        tooltipFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        malePortrait = loadPortrait(MALE_PORTRAIT);
        femalePortrait = loadPortrait(FEMALE_PORTRAIT);
        
        initializeButtons();
        resetCharacterData();
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

            statBigIncButtons[i] = new Button(0, 0, 26, 22, "++");
            statBigIncButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statBigIncButtons[i].setFont(smallFont);
            
            statIncButtons[i] = new Button(0, 0, 22, 22, "+");
            statIncButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statIncButtons[i].setFont(smallFont);
            
            statBigDecButtons[i] = new Button(0, 0, 26, 22, "--");
            statBigDecButtons[i].setColors(new Color(18, 20, 24), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
            statBigDecButtons[i].setFont(smallFont);
            
            statDecButtons[i] = new Button(0, 0, 22, 22, "-");
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
        createButton.setColors(selectedColor, new Color(210, 208, 198), new Color(165, 163, 154), selectedTextColor);
        createButton.setFont(buttonFont);
        createButton.setOnClick(this::createCharacter);

        backButton = new Button(0, 0, BACK_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "<  BACK");
        backButton.setColors(new Color(12, 13, 16), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        backButton.setFont(buttonFont);
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
        nameCaretIndex = characterName.length();
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
    
    private void createCharacter() {
        if (characterName.trim().isEmpty()) {
            characterName = DEFAULT_CHARACTER_NAME; // Default name
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
        // Handle keyboard input for name typing
        if (isTypingName) {
            handleNameInput();
        } else {
            // Handle navigation
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
    
    private void handleNameInput() {
        nameCaretIndex = clampNameCaretIndex(nameCaretIndex);

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            isTypingName = false;
            return;
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            isTypingName = false;
            return;
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_HOME)) {
            nameCaretIndex = 0;
            return;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_END)) {
            nameCaretIndex = characterName.length();
            return;
        }
        if (handleCaretMoveInput()) {
            return;
        }

        if (handleBackspaceInput()) {
            return;
        }

        java.lang.Character typedCharacter = getTypedCharacter();
        if (typedCharacter != null && characterName.length() < MAX_NAME_LENGTH) {
            characterName = characterName.substring(0, nameCaretIndex) + typedCharacter + characterName.substring(nameCaretIndex);
            nameCaretIndex++;
        }
    }

    private boolean handleCaretMoveInput() {
        long now = System.currentTimeMillis();
        boolean handled = false;

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
            moveNameCaret(-1);
            nextLeftCaretRepeatTime = now + CARET_MOVE_REPEAT_INITIAL_DELAY;
            handled = true;
        } else if (Engine.instance().keyboard.keyPressed(KeyEvent.VK_LEFT)) {
            if (nextLeftCaretRepeatTime > 0 && now >= nextLeftCaretRepeatTime) {
                moveNameCaret(-1);
                nextLeftCaretRepeatTime = now + CARET_MOVE_REPEAT_INTERVAL;
            }
            handled = true;
        } else {
            nextLeftCaretRepeatTime = 0;
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
            moveNameCaret(1);
            nextRightCaretRepeatTime = now + CARET_MOVE_REPEAT_INITIAL_DELAY;
            handled = true;
        } else if (Engine.instance().keyboard.keyPressed(KeyEvent.VK_RIGHT)) {
            if (nextRightCaretRepeatTime > 0 && now >= nextRightCaretRepeatTime) {
                moveNameCaret(1);
                nextRightCaretRepeatTime = now + CARET_MOVE_REPEAT_INTERVAL;
            }
            handled = true;
        } else {
            nextRightCaretRepeatTime = 0;
        }

        return handled;
    }

    private void moveNameCaret(int direction) {
        nameCaretIndex = clampNameCaretIndex(nameCaretIndex + direction);
    }

    private boolean handleBackspaceInput() {
        long now = System.currentTimeMillis();

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_BACK_SPACE)) {
            deleteCharacterBeforeCaret();
            nextBackspaceRepeatTime = now + BACKSPACE_REPEAT_INITIAL_DELAY;
            return true;
        }

        if (!Engine.instance().keyboard.keyPressed(KeyEvent.VK_BACK_SPACE)) {
            nextBackspaceRepeatTime = 0;
            return false;
        }

        if (nextBackspaceRepeatTime > 0 && now >= nextBackspaceRepeatTime) {
            deleteCharacterBeforeCaret();
            nextBackspaceRepeatTime = now + BACKSPACE_REPEAT_INTERVAL;
            return true;
        }

        return true;
    }

    private void deleteCharacterBeforeCaret() {
        if (nameCaretIndex <= 0 || characterName.isEmpty()) {
            return;
        }

        characterName = characterName.substring(0, nameCaretIndex - 1) + characterName.substring(nameCaretIndex);
        nameCaretIndex--;
    }

    private int clampNameCaretIndex(int caretIndex) {
        return Math.max(0, Math.min(caretIndex, characterName.length()));
    }
    
    @Override
    public void update() {
        updateLayout();
        
        // Update all buttons
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();

        if (mousePressed && !wasMousePressed) {
            isTypingName = nameBoxBounds.contains(mouseX, mouseY);
            if (isTypingName) {
                updateNameCaretFromMouse(mouseX);
            }
        }
        wasMousePressed = mousePressed;
        updateStatTooltip(mouseX, mouseY);
        
        for (Button button : menuButtons) {
            if (button != null) {
                button.update(mouseX, mouseY, mousePressed);
            }
        }
        
        // Update selection highlighting
        for (int i = 0; i < menuButtons.length; i++) {
            if (menuButtons[i] != null) {
                menuButtons[i].setSelected(!isTypingName && i == selectedIndex);
            }
        }
        
        // Update gender button highlighting
        genderButtons[0].setSelected(selectedGender == Character.Gender.MALE);
        genderButtons[1].setSelected(selectedGender == Character.Gender.FEMALE);
        updateGenderButtonColors();
        updateStatButtonColors();
        updateCreateButtonColor();
    }

    private void updateStatTooltip(int mouseX, int mouseY) {
        int oldHoveredStatIndex = hoveredStatIndex;
        hoveredStatIndex = -1;

        for (int i = 0; i < STAT_COUNT; i++) {
            if (statLabelBounds[i] != null && statLabelBounds[i].contains(mouseX, mouseY)) {
                hoveredStatIndex = i;
                break;
            }
        }

        if (hoveredStatIndex != oldHoveredStatIndex) {
            statTooltipStartTime = System.currentTimeMillis();
        }
    }

    private void updateNameCaretFromMouse(int mouseX) {
        FontMetrics metrics = Engine.instance().getWindow().getCanvas().getFontMetrics(buttonFont);
        int textStartX = nameBoxBounds.x + 18;
        int relativeX = Math.max(0, mouseX - textStartX);

        nameCaretIndex = characterName.length();
        for (int i = 0; i <= characterName.length(); i++) {
            int leftWidth = metrics.stringWidth(characterName.substring(0, i));
            int rightWidth = i < characterName.length() ? metrics.stringWidth(characterName.substring(0, i + 1)) : leftWidth;
            int midpoint = leftWidth + (rightWidth - leftWidth) / 2;
            if (relativeX <= midpoint) {
                nameCaretIndex = i;
                return;
            }
        }
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
            button.setColors(BUTTON_DISABLED_COLOR, BUTTON_DISABLED_COLOR, BUTTON_DISABLED_COLOR, BUTTON_DISABLED_TEXT_COLOR);
        }
    }

    private void updateCreateButtonColor() {
        if (getRemainingPoints() == 0) {
            createButton.setColors(selectedColor, new Color(210, 208, 198), new Color(165, 163, 154), selectedTextColor);
        } else {
            createButton.setColors(BUTTON_DISABLED_COLOR, BUTTON_DISABLED_COLOR, BUTTON_DISABLED_COLOR, BUTTON_DISABLED_TEXT_COLOR);
        }
    }

    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layoutWidth = Math.min(windowWidth - 120, 1060);
        layoutHeight = Math.min(windowHeight - 90, 620);
        layoutX = (windowWidth - layoutWidth) / 2;
        layoutY = (windowHeight - layoutHeight) / 2;

        int leftX = layoutX;
        leftWidth = Math.max(360, layoutWidth * 44 / 100);
        int leftContentWidth = leftWidth - 30;
        int leftContentCenterX = leftX + leftContentWidth / 2;
        int genderButtonGroupWidth = GENDER_BUTTON_WIDTH * 2 + GENDER_BUTTON_GAP;
        portraitSize = Math.min(260, leftWidth - 160);
        portraitX = leftContentCenterX - portraitSize / 2;
        portraitY = layoutY + 100;

        genderButtons[0].x = leftContentCenterX - genderButtonGroupWidth / 2;
        genderButtons[0].y = portraitY + portraitSize + 22;
        genderButtons[1].x = genderButtons[0].x + GENDER_BUTTON_WIDTH + GENDER_BUTTON_GAP;
        genderButtons[1].y = genderButtons[0].y;

        nameBoxBounds.setBounds(leftX, genderButtons[0].y + 82, leftContentWidth, 92);

        rightX = layoutX + leftWidth + 40;
        int rightWidth = layoutX + layoutWidth - rightX;
        attrPanelX = rightX;
        attrPanelY = layoutY + ATTR_PANEL_TOP_OFFSET;
        attrPanelWidth = Math.max(330, rightWidth - 10);
        attrPanelHeight = ATTR_PANEL_HEIGHT;

        for (int i = 0; i < STAT_COUNT; i++) {
            int rowY = getStatRowY(i);
            statLabelBounds[i].setBounds(attrPanelX + 16, rowY - 12, 102, 34);
            statBigDecButtons[i].x = attrPanelX + 118;
            statBigDecButtons[i].y = rowY - 4;
            statDecButtons[i].x = attrPanelX + 148;
            statDecButtons[i].y = rowY - 4;
            statIncButtons[i].x = attrPanelX + attrPanelWidth - 102;
            statIncButtons[i].y = rowY - 4;
            statBigIncButtons[i].x = attrPanelX + attrPanelWidth - 74;
            statBigIncButtons[i].y = rowY - 4;
        }

        createButton.x = attrPanelX + attrPanelWidth - CREATE_BUTTON_WIDTH;
        createButton.y = layoutY + layoutHeight - 62;
        backButton.x = createButton.x - ACTION_BUTTON_GAP - BACK_BUTTON_WIDTH;
        backButton.y = createButton.y;
    }

    private int getStatRowY(int statIndex) {
        return attrPanelY + STAT_ROW_TOP_OFFSET + statIndex * STAT_ROW_SPACING;
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);
        updateLayout();

        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();

        g2d.setColor(pageColor);
        g2d.fillRect(0, 0, windowWidth, windowHeight);

        drawSubtleBackground(g2d, windowWidth, windowHeight);

        g2d.setColor(textColor);
        g2d.setFont(titleFont);
        g2d.drawString("NEW INVESTIGATOR", layoutX, layoutY + 38);
        g2d.setFont(smallFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("STOKSJÖ POLICE ARCHIVE // CLASSIFIED", layoutX + 2, layoutY + 62);
        g2d.setColor(mutedBorderColor);
        g2d.drawLine(layoutX, layoutY + 80, layoutX + leftWidth - 30, layoutY + 80);

        g2d.setColor(textColor);
        g2d.setFont(labelFont);
        g2d.drawString("SUBJECT'S PROFILE", rightX, layoutY + 38);
        g2d.setFont(smallFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("ATTRIBUTES", rightX, layoutY + 62);

        g2d.setFont(titleFont);
        g2d.setColor(textColor);
        String remaining = String.valueOf(getRemainingPoints());
        FontMetrics remainingMetrics = g2d.getFontMetrics();
        g2d.drawString(remaining, layoutX + layoutWidth - remainingMetrics.stringWidth(remaining) - 20, layoutY + 45);
        g2d.setFont(smallFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("PTS REMAINING", layoutX + layoutWidth - 94, layoutY + 62);

        drawPortrait(g2d, portraitX, portraitY, portraitSize);
        genderButtons[0].render(g2d);
        genderButtons[1].render(g2d);

        drawNameBox(g2d);
        drawAttributesPanel(g2d);
        drawCreateStatus(g2d);
        drawStatTooltip(g2d);

        backButton.render(g2d);
        createButton.render(g2d);
    }

    private void drawStatTooltip(Graphics2D g2d) {
        if (hoveredStatIndex < 0 || hoveredStatIndex >= STAT_DESCRIPTIONS.length) {
            return;
        }
        if (System.currentTimeMillis() - statTooltipStartTime < STAT_TOOLTIP_DELAY) {
            return;
        }

        drawTooltip(g2d, STAT_NAMES[hoveredStatIndex].toUpperCase() + "\n" + STAT_DESCRIPTIONS[hoveredStatIndex]);
    }

    private void drawTooltip(Graphics2D g2d, String tooltipText) {
        if (tooltipText.isEmpty()) {
            return;
        }

        g2d.setFont(tooltipFont);
        FontMetrics tooltipMetrics = g2d.getFontMetrics();
        String[] lines = tooltipText.split("\n");

        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, tooltipMetrics.stringWidth(line));
        }

        int tooltipWidth = maxWidth + 20;
        int tooltipHeight = lines.length * tooltipMetrics.getHeight() + 25;
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        int tooltipX = mouseX + 15;
        int tooltipY = mouseY - tooltipHeight - 5;
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();

        if (tooltipX + tooltipWidth > windowWidth) {
            tooltipX = mouseX - tooltipWidth - 15;
        }
        if (tooltipY < 0) {
            tooltipY = mouseY + 20;
        }

        g2d.setColor(new Color(11, 12, 15, 245));
        g2d.fillRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);

        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);

        int lineY = tooltipY + tooltipMetrics.getAscent() + 10;
        for (int i = 0; i < lines.length; i++) {
            g2d.setColor(i == 0 ? textColor : mutedTextColor);
            g2d.drawString(lines[i], tooltipX + 10, lineY);
            lineY += tooltipMetrics.getHeight() + 5;
        }
    }

    private void drawCreateStatus(Graphics2D g2d) {
        if (getRemainingPoints() == 0) {
            return;
        }

        g2d.setColor(mutedTextColor);
        g2d.setFont(smallFont);
        String status = "USE ALL REMAINING POINTS TO CREATE";
        FontMetrics metrics = g2d.getFontMetrics();
        int statusX = createButton.x + (CREATE_BUTTON_WIDTH - metrics.stringWidth(status)) / 2;
        g2d.drawString(status, statusX, createButton.y - 12);
    }

    private void drawSubtleBackground(Graphics2D g2d, int windowWidth, int windowHeight) {
        g2d.setColor(new Color(255, 255, 255, 8));
        for (int y = 0; y < windowHeight; y += 4) {
            g2d.drawLine(0, y, windowWidth, y);
        }
    }

    private void drawPortrait(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, size, size);
        g2d.setColor(borderColor);
        g2d.drawRect(x, y, size, size);

        Image portrait = selectedGender == Character.Gender.FEMALE ? femalePortrait : malePortrait;
        if (portrait != null) {
            g2d.drawImage(portrait, x + 1, y + 1, size - 2, size - 2, null);
        }
    }

    private void drawNameBox(Graphics2D g2d) {
        g2d.setColor(panelColor);
        g2d.fillRect(nameBoxBounds.x, nameBoxBounds.y, nameBoxBounds.width, nameBoxBounds.height);
        g2d.setColor(isTypingName ? selectedColor : mutedBorderColor);
        g2d.drawRect(nameBoxBounds.x, nameBoxBounds.y, nameBoxBounds.width, nameBoxBounds.height);

        g2d.setColor(mutedTextColor);
        g2d.setFont(smallFont);
        g2d.drawString("SUBJECT NAME", nameBoxBounds.x + 18, nameBoxBounds.y + 24);

        g2d.setColor(textColor);
        g2d.setFont(buttonFont);
        int textX = nameBoxBounds.x + 18;
        int textY = nameBoxBounds.y + 58;
        g2d.drawString(characterName, textX, textY);

        if (isTypingName && System.currentTimeMillis() % 1000 < 500) {
            int caretX = textX + g2d.getFontMetrics().stringWidth(characterName.substring(0, clampNameCaretIndex(nameCaretIndex)));
            g2d.drawLine(caretX, textY - 15, caretX, textY + 3);
        }

        g2d.setColor(borderColor);
        g2d.drawLine(nameBoxBounds.x + 18, nameBoxBounds.y + 72,
                     nameBoxBounds.x + nameBoxBounds.width - 18, nameBoxBounds.y + 72);
    }

    private void drawAttributesPanel(Graphics2D g2d) {
        g2d.setColor(panelColor);
        g2d.fillRect(attrPanelX, attrPanelY, attrPanelWidth, attrPanelHeight);
        g2d.setColor(borderColor);
        g2d.drawRect(attrPanelX, attrPanelY, attrPanelWidth, attrPanelHeight);

        for (int i = 0; i < STAT_COUNT; i++) {
            drawAttributeRow(g2d, i);
        }
    }

    private void drawAttributeRow(Graphics2D g2d, int statIndex) {
        int statValue = getStatValue(statIndex);
        int rowY = getStatRowY(statIndex);
        int sliderX = attrPanelX + 186;
        int sliderWidth = attrPanelWidth - 306;
        int sliderY = rowY + 7;
        int min = 0;
        int max = 99;
        int knobX = sliderX + ((statValue - min) * sliderWidth) / (max - min);

        g2d.setColor(textColor);
        g2d.setFont(labelFont);
        g2d.drawString(STAT_NAMES[statIndex].toUpperCase(), attrPanelX + 20, rowY + 4);
        g2d.setFont(smallFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString(STAT_CODES[statIndex], attrPanelX + 20, rowY + 18);

        statBigDecButtons[statIndex].render(g2d);
        statDecButtons[statIndex].render(g2d);

        g2d.setColor(new Color(52, 54, 60));
        g2d.drawLine(sliderX, sliderY, sliderX + sliderWidth, sliderY);
        g2d.setColor(new Color(55, 55, 58));
        int tickX = sliderX + sliderWidth / 2;
        g2d.drawLine(tickX, sliderY - 5, tickX, sliderY + 5);
        g2d.setFont(smallFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("0", sliderX - 3, sliderY + 16);
        g2d.drawString("99", sliderX + sliderWidth - 8, sliderY + 16);
        g2d.setColor(selectedColor);
        g2d.drawLine(sliderX, sliderY, knobX, sliderY);
        g2d.fillRect(knobX - 2, sliderY - 7, 4, 14);

        statIncButtons[statIndex].render(g2d);
        statBigIncButtons[statIndex].render(g2d);

        g2d.setColor(textColor);
        g2d.setFont(buttonFont);
        String valueText = String.valueOf(statValue);
        FontMetrics valueMetrics = g2d.getFontMetrics();
        g2d.drawString(valueText, attrPanelX + attrPanelWidth - 20 - valueMetrics.stringWidth(valueText), rowY + 12);
    }

    private java.lang.Character getTypedCharacter() {
        boolean shift = Engine.instance().keyboard.keyPressed(KeyEvent.VK_SHIFT);

        for (int key = KeyEvent.VK_A; key <= KeyEvent.VK_Z; key++) {
            if (Engine.instance().keyboard.keyJustPressed(key)) {
                char typed = (char) ('a' + key - KeyEvent.VK_A);
                return shift ? java.lang.Character.toUpperCase(typed) : typed;
            }
        }

        for (int key = KeyEvent.VK_0; key <= KeyEvent.VK_9; key++) {
            if (Engine.instance().keyboard.keyJustPressed(key)) {
                return (char) ('0' + key - KeyEvent.VK_0);
            }
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_SPACE)) {
            return ' ';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_MINUS)) {
            return '-';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_QUOTE)) {
            return '\"';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_PERIOD)) {
            return '.';
        }

        return null;
    }
}
