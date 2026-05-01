package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.TextInput;
import atomiccode.cthulhuEngine.ui.Tooltip;
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
    private TextInput nameInput;
    private Tooltip statTooltip;
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
    private int selectedIndex = 0;
    private int hoveredStatIndex = -1;
    
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
        
        initializeInputs();
        initializeButtons();
        resetCharacterData();
    }

    private void initializeInputs() {
        nameInput = new TextInput(0, 0, 0, 0, "SUBJECT NAME", DEFAULT_CHARACTER_NAME, MAX_NAME_LENGTH);
        nameInput.setFonts(smallFont, buttonFont);
        nameInput.setColors(panelColor, mutedBorderColor, selectedColor, textColor, mutedTextColor, borderColor);

        statTooltip = new Tooltip(STAT_TOOLTIP_DELAY);
        statTooltip.setFont(tooltipFont);
        statTooltip.setColors(new Color(11, 12, 15, 245), borderColor, textColor, mutedTextColor);
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

        nameInput.setBounds(leftX, genderButtons[0].y + 82, leftContentWidth, 92);

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

}
