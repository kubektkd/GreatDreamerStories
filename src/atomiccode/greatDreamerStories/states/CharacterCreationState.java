package atomiccode.greatDreamerStories.states;

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
    
    private final int targetSlot;
    private final State returnState;
    
    // Character creation data
    private String characterName = "";
    private Character.Gender selectedGender = Character.Gender.MALE;
    private int strength = Character.MIN_STAT_VALUE;
    private int dexterity = Character.MIN_STAT_VALUE;
    private int intelligence = Character.MIN_STAT_VALUE;
    private int perception = Character.MIN_STAT_VALUE;
    private int charisma = Character.MIN_STAT_VALUE;
    
    // UI components
    private Button[] genderButtons;
    private Button[] statIncButtons;
    private Button[] statDecButtons;
    private Button createButton;
    private Button cancelButton;
    private Button[] menuButtons;
    
    // Input handling
    private boolean isTypingName = false;
    private int selectedIndex = 0;
    
    // Colors and fonts
    private Color normalColor = new Color(50, 50, 50, 200);
    private Color hoverColor = new Color(70, 70, 70, 200);
    private Color pressedColor = new Color(30, 30, 30, 200);
    private Color textColor = Color.WHITE;
    private Color selectedColor = new Color(100, 150, 255, 200);
    private Font buttonFont;
    private Font titleFont;
    private Font labelFont;
    
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
        return false; // Allow background to show through
    }
    
    @Override
    public void onEnter() {
        // Initialize fonts
        buttonFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 16);
        titleFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 24);
        labelFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        
        initializeButtons();
        resetCharacterData();
    }
    
    private void initializeButtons() {
        // Gender selection buttons
        genderButtons = new Button[Character.Gender.values().length];
        for (int i = 0; i < Character.Gender.values().length; i++) {
            Character.Gender gender = Character.Gender.values()[i];
            genderButtons[i] = new Button(0, 0, 80, 30, gender.getDisplayName());
            genderButtons[i].setColors(normalColor, hoverColor, pressedColor, textColor);
            genderButtons[i].setFont(buttonFont);
            final Character.Gender selectedGender = gender;
            genderButtons[i].setOnClick(() -> this.selectedGender = selectedGender);
        }
        
        // Stat adjustment buttons
        statIncButtons = new Button[5];
        statDecButtons = new Button[5];
        
        for (int i = 0; i < 5; i++) {
            statIncButtons[i] = new Button(0, 0, 25, 25, "+");
            statIncButtons[i].setColors(normalColor, hoverColor, pressedColor, textColor);
            statIncButtons[i].setFont(buttonFont);
            
            statDecButtons[i] = new Button(0, 0, 25, 25, "-");
            statDecButtons[i].setColors(normalColor, hoverColor, pressedColor, textColor);
            statDecButtons[i].setFont(buttonFont);
            
            final int statIndex = i;
            statIncButtons[i].setOnClick(() -> incrementStat(statIndex));
            statDecButtons[i].setOnClick(() -> decrementStat(statIndex));
        }
        
        // Action buttons
        createButton = new Button(0, 0, 100, 40, "Create");
        createButton.setColors(new Color(50, 120, 50, 200), new Color(70, 140, 70, 200), 
                              new Color(30, 100, 30, 200), textColor);
        createButton.setFont(buttonFont);
        createButton.setOnClick(this::createCharacter);
        
        cancelButton = new Button(0, 0, 100, 40, "Cancel");
        cancelButton.setColors(new Color(120, 50, 50, 200), new Color(140, 70, 70, 200), 
                              new Color(100, 30, 30, 200), textColor);
        cancelButton.setFont(buttonFont);
        cancelButton.setOnClick(this::cancelCreation);
        
        // Setup menu buttons array for navigation
        menuButtons = new Button[genderButtons.length + statIncButtons.length + statDecButtons.length + 2];
        int index = 0;
        System.arraycopy(genderButtons, 0, menuButtons, index, genderButtons.length);
        index += genderButtons.length;
        System.arraycopy(statIncButtons, 0, menuButtons, index, statIncButtons.length);
        index += statIncButtons.length;
        System.arraycopy(statDecButtons, 0, menuButtons, index, statDecButtons.length);
        index += statDecButtons.length;
        menuButtons[index++] = createButton;
        menuButtons[index] = cancelButton;
    }
    
    private void resetCharacterData() {
        characterName = "Officer Smith";
        selectedGender = Character.Gender.MALE;
        strength = Character.MIN_STAT_VALUE;
        dexterity = Character.MIN_STAT_VALUE;
        intelligence = Character.MIN_STAT_VALUE;
        perception = Character.MIN_STAT_VALUE;
        charisma = Character.MIN_STAT_VALUE;
    }
    
    private void incrementStat(int statIndex) {
        if (getRemainingPoints() <= 0) return;
        
        switch (statIndex) {
            case 0: if (strength < Character.MAX_STAT_VALUE) strength++; break;
            case 1: if (dexterity < Character.MAX_STAT_VALUE) dexterity++; break;
            case 2: if (intelligence < Character.MAX_STAT_VALUE) intelligence++; break;
            case 3: if (perception < Character.MAX_STAT_VALUE) perception++; break;
            case 4: if (charisma < Character.MAX_STAT_VALUE) charisma++; break;
        }
    }
    
    private void decrementStat(int statIndex) {
        switch (statIndex) {
            case 0: if (strength > Character.MIN_STAT_VALUE) strength--; break;
            case 1: if (dexterity > Character.MIN_STAT_VALUE) dexterity--; break;
            case 2: if (intelligence > Character.MIN_STAT_VALUE) intelligence--; break;
            case 3: if (perception > Character.MIN_STAT_VALUE) perception--; break;
            case 4: if (charisma > Character.MIN_STAT_VALUE) charisma--; break;
        }
    }
    
    private int getRemainingPoints() {
        int usedPoints = strength + dexterity + intelligence + perception + charisma;
        return Character.INITIAL_SKILL_POINTS - usedPoints;
    }
    
    private void createCharacter() {
        if (characterName.trim().isEmpty()) {
            characterName = "Officer Smith"; // Default name
        }
        
        if (getRemainingPoints() != 0) {
            return; // Must use all points
        }
        
        try {
            Character newCharacter = new Character(characterName.trim(), selectedGender, 
                                                 strength, dexterity, intelligence, perception, charisma);
            
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
            
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_SPACE)) {
                isTypingName = true;
            }
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            cancelCreation();
        }
    }
    
    private void handleNameInput() {
        // Simple text input handling
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            isTypingName = false;
            return;
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            isTypingName = false;
            return;
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_BACK_SPACE)) {
            if (characterName.length() > 0) {
                characterName = characterName.substring(0, characterName.length() - 1);
            }
        }
        
        // Note: For full text input, you'd want to implement proper key-to-character mapping
        // This is a simplified version
    }
    
    @Override
    public void update() {
        // Update all buttons
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();
        
        for (Button button : menuButtons) {
            if (button != null) {
                button.update(mouseX, mouseY, mousePressed);
            }
        }
        
        // Update selection highlighting
        for (int i = 0; i < menuButtons.length; i++) {
            if (menuButtons[i] != null) {
                menuButtons[i].setSelected(i == selectedIndex);
            }
        }
        
        // Update gender button highlighting
        for (int i = 0; i < genderButtons.length; i++) {
            Character.Gender gender = Character.Gender.values()[i];
            genderButtons[i].setSelected(gender == selectedGender);
        }
    }
    
    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Get window dimensions
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Draw semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        // Draw popup background
        int popupWidth = 500;
        int popupHeight = 600;
        int popupX = (windowWidth - popupWidth) / 2;
        int popupY = (windowHeight - popupHeight) / 2;
        
        g2d.setColor(new Color(30, 30, 50, 240));
        g2d.fillRoundRect(popupX, popupY, popupWidth, popupHeight, 20, 20);
        
        g2d.setColor(new Color(100, 100, 120));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(popupX, popupY, popupWidth, popupHeight, 20, 20);
        
        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Create New Character";
        int titleX = popupX + (popupWidth - titleMetrics.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, popupY + 40);
        
        // Current Y position for UI elements
        int currentY = popupY + 80;
        
        // Draw name input
        g2d.setFont(labelFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Name:", popupX + 30, currentY);
        
        // Name input box
        int nameBoxX = popupX + 90;
        int nameBoxY = currentY - 15;
        int nameBoxWidth = 200;
        int nameBoxHeight = 25;
        
        Color nameBoxColor = isTypingName ? selectedColor : normalColor;
        g2d.setColor(nameBoxColor);
        g2d.fillRect(nameBoxX, nameBoxY, nameBoxWidth, nameBoxHeight);
        g2d.setColor(Color.GRAY);
        g2d.drawRect(nameBoxX, nameBoxY, nameBoxWidth, nameBoxHeight);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(buttonFont);
        g2d.drawString(characterName, nameBoxX + 5, nameBoxY + 17);
        
        currentY += 50;
        
        // Draw gender selection
        g2d.setFont(labelFont);
        g2d.drawString("Gender:", popupX + 30, currentY);
        
        for (int i = 0; i < genderButtons.length; i++) {
            genderButtons[i].x = popupX + 90 + i * 90;
            genderButtons[i].y = currentY - 15;
            genderButtons[i].render(g);
        }
        
        currentY += 60;
        
        // Draw occupation (fixed)
        g2d.setColor(Color.WHITE);
        g2d.setFont(labelFont);
        g2d.drawString("Occupation: Chief Police Officer", popupX + 30, currentY);
        
        currentY += 40;
        
        // Draw stats section
        g2d.setFont(labelFont);
        g2d.drawString("Distribute Skill Points:", popupX + 30, currentY);
        
        currentY += 25;
        
        g2d.setFont(buttonFont);
        String pointsText = "Remaining Points: " + getRemainingPoints();
        g2d.setColor(getRemainingPoints() == 0 ? Color.GREEN : Color.YELLOW);
        g2d.drawString(pointsText, popupX + 30, currentY);
        
        currentY += 30;
        
        // Draw stat controls
        String[] statNames = {"Strength", "Dexterity", "Intelligence", "Perception", "Charisma"};
        int[] statValues = {strength, dexterity, intelligence, perception, charisma};
        
        for (int i = 0; i < 5; i++) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(labelFont);
            g2d.drawString(statNames[i] + ":", popupX + 30, currentY + 17);
            
            // Decrease button
            statDecButtons[i].x = popupX + 180;
            statDecButtons[i].y = currentY;
            statDecButtons[i].render(g);
            
            // Stat value
            g2d.setFont(buttonFont);
            String valueText = String.valueOf(statValues[i]);
            FontMetrics valueMetrics = g2d.getFontMetrics();
            int valueX = popupX + 220 - valueMetrics.stringWidth(valueText) / 2;
            g2d.drawString(valueText, valueX, currentY + 17);
            
            // Increase button
            statIncButtons[i].x = popupX + 235;
            statIncButtons[i].y = currentY;
            statIncButtons[i].render(g);
            
            currentY += 35;
        }
        
        currentY += 20;
        
        // Draw action buttons
        createButton.x = popupX + popupWidth - 220;
        createButton.y = currentY;
        createButton.render(g);
        
        cancelButton.x = popupX + popupWidth - 110;
        cancelButton.y = currentY;
        cancelButton.render(g);
        
        // Draw instructions
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(labelFont);
        String instructions = "TAB: Edit name | ESC: Cancel | Use all " + Character.INITIAL_SKILL_POINTS + " points to create";
        FontMetrics instrMetrics = g2d.getFontMetrics();
        int instrX = popupX + (popupWidth - instrMetrics.stringWidth(instructions)) / 2;
        g2d.drawString(instructions, instrX, popupY + popupHeight - 20);
    }
}
