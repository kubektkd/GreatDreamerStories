package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main gameplay state where the story unfolds.
 * Currently serves as a placeholder for the actual visual novel/RPG gameplay.
 */
public class GameplayState implements State {
    
    private final Character selectedCharacter;
    private final int characterSlot;
    
    private Button backButton;
    private Button[] menuButtons;
    private int selectedIndex = 0;
    
    // Display info
    private Font titleFont;
    private Font textFont;
    private Font buttonFont;
    
    public GameplayState(Character character, int characterSlot) {
        this.selectedCharacter = character;
        this.characterSlot = characterSlot;
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public void onEnter() {
        // Initialize fonts
        titleFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 36);
        textFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 16);
        buttonFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 18);
        
        // Initialize buttons
        backButton = new Button(0, 0, 200, 50, "Return to Characters");
        
        // Set button colors
        Color normalColor = new Color(50, 50, 50, 200);
        Color hoverColor = new Color(70, 70, 70, 200);
        Color pressedColor = new Color(30, 30, 30, 200);
        Color textColor = Color.WHITE;
        
        backButton.setColors(normalColor, hoverColor, pressedColor, textColor);
        backButton.setFont(buttonFont);
        
        // Set button action
        backButton.setOnClick(() -> {
            // Update character's last played time before returning
            selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());
            SaveManager.getInstance().saveCharacter(selectedCharacter, characterSlot);
            
            // Return to character select
            Engine.instance().stateProcessor.setState(new CharacterSelectState());
        });
        
        // Create button array for keyboard navigation
        menuButtons = new Button[]{backButton};
        
        // Update character's last played time
        selectedCharacter.setLastPlayedAt(java.time.LocalDateTime.now());
    }
    
    @Override
    public void onExit() {
        // Save character data when exiting
        if (selectedCharacter != null) {
            SaveManager.getInstance().saveCharacter(selectedCharacter, characterSlot);
        }
    }
    
    @Override
    public void tick() {
        // Handle keyboard navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            if (selectedIndex < menuButtons.length) {
                menuButtons[selectedIndex].click();
            }
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            backButton.click();
        }
    }
    
    @Override
    public void update() {
        // Update button states
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();
        
        for (Button button : menuButtons) {
            button.update(mouseX, mouseY, mousePressed);
        }
        
        // Update selection state for keyboard navigation
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i].setSelected(i == selectedIndex);
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Get window dimensions
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Set background color - dark, atmospheric
        g.setColor(new Color(15, 15, 25)); // Very dark blue-black
        g.fillRect(0, 0, windowWidth, windowHeight);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Calculate center position
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        
        // Draw main title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Great Dreamer Stories";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 200;
        g2d.drawString(title, titleX, titleY);
        
        // Draw subtitle
        g2d.setFont(textFont);
        g2d.setColor(new Color(200, 200, 200));
        FontMetrics subtitleMetrics = g2d.getFontMetrics();
        String subtitle = "The Nordic Noir Mystery Begins...";
        int subtitleX = centerX - subtitleMetrics.stringWidth(subtitle) / 2;
        int subtitleY = titleY + 50;
        g2d.drawString(subtitle, subtitleX, subtitleY);
        
        // Draw character info panel
        drawCharacterInfoPanel(g2d, centerX, centerY);
        
        // Draw placeholder story content
        drawStoryPlaceholder(g2d, centerX, centerY + 100);
        
        // Position and render buttons
        backButton.x = centerX - 100;
        backButton.y = windowHeight - 150;
        backButton.render(g);
        
        // Draw instructions
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(textFont);
        String instructions = "This is a placeholder for the main gameplay. Press ESC or click the button to return.";
        FontMetrics instrMetrics = g2d.getFontMetrics();
        int instrX = centerX - instrMetrics.stringWidth(instructions) / 2;
        g2d.drawString(instructions, instrX, windowHeight - 40);
    }
    
    private void drawCharacterInfoPanel(Graphics2D g2d, int centerX, int centerY) {
        // Character info panel
        int panelWidth = 400;
        int panelHeight = 145;
        int panelX = centerX - panelWidth / 2;
        int panelY = centerY - 100;
        
        // Panel background
        g2d.setColor(new Color(40, 40, 60, 200));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        g2d.setColor(new Color(100, 100, 120));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Character name and title
        g2d.setColor(Color.WHITE);
        g2d.setFont(textFont);
        FontMetrics textMetrics = g2d.getFontMetrics();
        
        String characterInfo = selectedCharacter.getName() + " (" + selectedCharacter.getGender().getDisplayName() + ")";
        int charInfoX = panelX + (panelWidth - textMetrics.stringWidth(characterInfo)) / 2;
        g2d.drawString(characterInfo, charInfoX, panelY + 25);
        
        String occupation = selectedCharacter.getOccupation();
        int occupationX = panelX + (panelWidth - textMetrics.stringWidth(occupation)) / 2;
        g2d.setColor(new Color(180, 180, 255));
        g2d.drawString(occupation, occupationX, panelY + 45);
        
        // Stats display
        g2d.setColor(Color.WHITE);
        String stats = String.format("STR:%d  POW:%d  EDU:%d  CON:%d  INT:%d  APP:%d  LCK:%d  SIZ:%d  DEX:%d", 
                selectedCharacter.getStrength(),
                selectedCharacter.getPower(),
                selectedCharacter.getEducation(),
                selectedCharacter.getConstitution(),
                selectedCharacter.getIntelligence(),
                selectedCharacter.getAppearance(),
                selectedCharacter.getLuck(),
                selectedCharacter.getSize(),
                selectedCharacter.getDexterity());
        int statsX = panelX + (panelWidth - textMetrics.stringWidth(stats)) / 2;
        g2d.drawString(stats, statsX, panelY + 70);

        String skills = selectedCharacter.getKeySkillSummary();
        int skillsX = panelX + (panelWidth - textMetrics.stringWidth(skills)) / 2;
        g2d.setColor(new Color(180, 220, 255));
        g2d.drawString(skills, skillsX, panelY + 95);
        
        // Story progress
        int completedStories = 0;
        for (boolean completed : selectedCharacter.getCompletedStories()) {
            if (completed) completedStories++;
        }
        
        String progress = String.format("Stories completed: %d/%d", completedStories, Character.MAX_STORIES);
        int progressX = panelX + (panelWidth - textMetrics.stringWidth(progress)) / 2;
        g2d.setColor(new Color(150, 255, 150));
        g2d.drawString(progress, progressX, panelY + 120);
    }
    
    private void drawStoryPlaceholder(Graphics2D g2d, int centerX, int centerY) {
        // Story content placeholder
        int contentWidth = 600;
        int contentHeight = 200;
        int contentX = centerX - contentWidth / 2;
        int contentY = centerY;
        
        // Content background
        g2d.setColor(new Color(25, 25, 35, 180));
        g2d.fillRoundRect(contentX, contentY, contentWidth, contentHeight, 10, 10);
        
        g2d.setColor(new Color(80, 80, 100));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(contentX, contentY, contentWidth, contentHeight, 10, 10);
        
        // Placeholder story text
        g2d.setColor(Color.WHITE);
        g2d.setFont(textFont);
        
        String[] storyLines = {
            "The fog rolls in from the fjord as you arrive at the station.",
            "Your first day as Chief of Police in this small Nordic town",
            "begins with reports of strange disappearances...",
            "",
            "The mysteries of the Great Dreamer await your investigation.",
            "",
            " Future implementation: Visual novel storytelling,",
            " dialogue choices, investigation mechanics, and",
            " turn-based combat encounters will appear here"
        };
        
        int lineY = contentY + 25;
        for (String line : storyLines) {
            if (line.startsWith(" ")) {
                g2d.setColor(new Color(150, 150, 150));
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(line, contentX + 20, lineY);
            lineY += 20;
        }
    }
}


