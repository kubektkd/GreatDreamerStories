package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CharacterSelectState implements State {

    private Button backButton;
    private int selectedIndex = 0;
    private Button[] menuButtons;
    private SaveManager saveManager;
    
    // Slot interaction
    private int hoveredSlot = -1;
    private String tooltipText = "";
    private long tooltipStartTime = 0;
    private static final long TOOLTIP_DELAY = 1000; // 1 second delay before showing tooltip
    
    // Colors and fonts
    private Font buttonFont;
    private Font titleFont;
    private Font slotFont;
    private Font tooltipFont;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        // Character select initialization
        saveManager = SaveManager.getInstance();
        
        // Initialize fonts
        buttonFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 18);
        titleFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 48);
        slotFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 24);
        tooltipFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 12);

        // Initialize buttons
        backButton = new Button(0, 0, 200, 50, "Back");

        // Set button colors
        Color normalColor = new Color(50, 50, 50, 200);
        Color hoverColor = new Color(70, 70, 70, 200);
        Color pressedColor = new Color(30, 30, 30, 200);
        Color textColor = Color.WHITE;
        
        backButton.setColors(normalColor, hoverColor, pressedColor, textColor);
        backButton.setFont(buttonFont);

        // Set button actions
        backButton.setOnClick(() -> {
            // State change will automatically trigger fade transition
            Engine.instance().stateProcessor.setState(new MainMenuState());
        });

        // Create button array for keyboard navigation
        menuButtons = new Button[]{backButton};
    }

    @Override
    public void onExit() {

    }

    @Override
    public void tick() {
        // Handle keyboard navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            if (selectedIndex == 0) {
                menuButtons[selectedIndex].click();
            } else {
                // Handle character slot selection
                int slotIndex = selectedIndex - 1;
                handleSlotSelection(slotIndex);
            }
        }
        
        // Handle arrow key navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_UP)) {
            if (selectedIndex >= 4) {
                selectedIndex -= 4; // Move up one row
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DOWN)) {
            if (selectedIndex <= 4) {
                selectedIndex += 4; // Move down one row
            } else if (selectedIndex > 8) {
                selectedIndex = 0; // Go to back button
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
            if (selectedIndex > 1) {
                selectedIndex--;
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
            if (selectedIndex >= 1 && selectedIndex < 8) {
                selectedIndex++;
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
        
        // Check for mouse hover over character slots
        updateSlotHover(mouseX, mouseY);
        
        // Handle slot click
        if (mousePressed && hoveredSlot != -1) {
            handleSlotSelection(hoveredSlot);
        }
    }
    
    private void updateSlotHover(int mouseX, int mouseY) {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        
        // Calculate slot positions (same as in render method)
        int slotRows = 2;
        int slotCols = 4;
        int slotWidth = 100;
        int slotHeight = 120;
        int slotSpacingX = 40;
        int slotSpacingY = 30;
        
        int gridWidth = slotCols * slotWidth + (slotCols - 1) * slotSpacingX;
        int gridHeight = slotRows * slotHeight + (slotRows - 1) * slotSpacingY;
        int gridStartX = centerX - gridWidth / 2;
        int gridStartY = centerY - gridHeight / 2 + 20;
        
        int oldHoveredSlot = hoveredSlot;
        hoveredSlot = -1;
        
        for (int i = 0; i < saveManager.getMaxSlots(); i++) {
            int row = i / slotCols;
            int col = i % slotCols;
            int slotX = gridStartX + col * (slotWidth + slotSpacingX);
            int slotY = gridStartY + row * (slotHeight + slotSpacingY);
            
            if (mouseX >= slotX && mouseX <= slotX + slotWidth &&
                mouseY >= slotY && mouseY <= slotY + slotHeight) {
                hoveredSlot = i;
                break;
            }
        }
        
        // Reset tooltip if hover changed
        if (hoveredSlot != oldHoveredSlot) {
            tooltipStartTime = System.currentTimeMillis();
            tooltipText = "";
            if (hoveredSlot != -1) {
                updateTooltipText(hoveredSlot);
            }
        }
    }
    
    private void updateTooltipText(int slotIndex) {
        Character character = saveManager.getCharacter(slotIndex);
        if (character != null) {
            tooltipText = character.getCharacterSummary();
        } else {
            if (slotIndex == saveManager.getFirstAvailableSlot()) {
                tooltipText = "Click to create a new character";
            } else {
                tooltipText = "Empty slot\n(Complete previous characters first)";
            }
        }
    }
    
    private void handleSlotSelection(int slotIndex) {
        Character character = saveManager.getCharacter(slotIndex);
        
        if (character != null) {
            // Existing character - start gameplay
            Engine.instance().stateProcessor.setState(new GameplayState(character, slotIndex));
        } else if (slotIndex == saveManager.getFirstAvailableSlot()) {
            // Create new character in first available slot
            Engine.instance().stateProcessor.setState(new CharacterCreationState(slotIndex, this));
        }
        // If it's not an available slot, do nothing
    }

    @Override
    public void render(Graphics g) {
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Set background color
        g.setColor(new Color(20, 20, 40)); // Dark blue background
        g.fillRect(0, 0, windowWidth, windowHeight);
        
        // Calculate center position
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        
        // Draw main menu title
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Choose your character";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 180;
        g2d.drawString(title, titleX, titleY);

        // Draw 8 character slots in two rows of 4, centered horizontally
        int slotRows = 2;
        int slotCols = 4;
        int slotCount = saveManager.getMaxSlots();
        int slotWidth = 100;
        int slotHeight = 120;
        int slotSpacingX = 40;
        int slotSpacingY = 30;

        // Calculate total width and height of the grid
        int gridWidth = slotCols * slotWidth + (slotCols - 1) * slotSpacingX;
        int gridHeight = slotRows * slotHeight + (slotRows - 1) * slotSpacingY;

        int gridStartX = centerX - gridWidth / 2;
        int gridStartY = centerY - gridHeight / 2 + 20;

        for (int i = 0; i < slotCount; i++) {
            int row = i / slotCols;
            int col = i % slotCols;
            int slotX = gridStartX + col * (slotWidth + slotSpacingX);
            int slotY = gridStartY + row * (slotHeight + slotSpacingY);

            drawCharacterSlot(g2d, i, slotX, slotY, slotWidth, slotHeight);
        }

        // Draw tooltip if hovering and enough time has passed
        if (hoveredSlot != -1 && !tooltipText.isEmpty() && 
            System.currentTimeMillis() - tooltipStartTime > TOOLTIP_DELAY) {
            drawTooltip(g2d);
        }

        // Position and render buttons
        int buttonStartY = centerY + gridHeight / 2 + 100;
        
        backButton.x = centerX - 100;
        backButton.y = buttonStartY;
        backButton.render(g);
    }
    
    private void drawCharacterSlot(Graphics2D g2d, int slotIndex, int slotX, int slotY, int slotWidth, int slotHeight) {
        Character character = saveManager.getCharacter(slotIndex);
        boolean isOccupied = character != null;
        boolean isAvailable = slotIndex == saveManager.getFirstAvailableSlot();
        boolean isHovered = hoveredSlot == slotIndex;
        boolean isSelected = selectedIndex == slotIndex + 1; // +1 because selectedIndex 0 is back button
        
        // Determine slot appearance
        Color backgroundColor;
        Color borderColor;
        Color textColor;
        
        if (isOccupied) {
            // Occupied slot - normal colors
            backgroundColor = isHovered ? new Color(60, 60, 80, 220) : new Color(40, 40, 60, 220);
            borderColor = isSelected ? new Color(120, 180, 255) : new Color(100, 100, 120);
            textColor = Color.WHITE;
        } else if (isAvailable) {
            // Available empty slot - highlighted
            backgroundColor = isHovered ? new Color(80, 120, 80, 220) : new Color(60, 100, 60, 220);
            borderColor = isSelected ? new Color(120, 255, 120) : new Color(120, 180, 120);
            textColor = Color.WHITE;
        } else {
            // Unavailable empty slot - grayed out
            backgroundColor = new Color(30, 30, 40, 220);
            borderColor = new Color(60, 60, 70);
            textColor = new Color(100, 100, 110);
        }
        
        // Draw slot background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(slotX, slotY, slotWidth, slotHeight, 18, 18);

        // Draw border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(isSelected ? 4f : 2f));
        g2d.drawRoundRect(slotX, slotY, slotWidth, slotHeight, 18, 18);

        // Draw slot content
        if (isOccupied) {
            drawOccupiedSlot(g2d, character, slotX, slotY, slotWidth, slotHeight, textColor);
        } else {
            drawEmptySlot(g2d, slotX, slotY, slotWidth, slotHeight, textColor, isAvailable);
        }
    }
    
    private void drawOccupiedSlot(Graphics2D g2d, Character character, int slotX, int slotY, 
                                 int slotWidth, int slotHeight, Color textColor) {
        g2d.setColor(textColor);
        
        // Character name (truncated if too long)
        g2d.setFont(tooltipFont);
        FontMetrics nameMetrics = g2d.getFontMetrics();
        String name = character.getName();
        if (nameMetrics.stringWidth(name) > slotWidth - 10) {
            // Truncate name if too long
            while (nameMetrics.stringWidth(name + "...") > slotWidth - 10 && name.length() > 3) {
                name = name.substring(0, name.length() - 1);
            }
            name += "...";
        }
        
        int nameX = slotX + (slotWidth - nameMetrics.stringWidth(name)) / 2;
        int nameY = slotY + 20;
        g2d.drawString(name, nameX, nameY);
        
        // Character level/progress indicator
        int completedStories = 0;
        for (boolean completed : character.getCompletedStories()) {
            if (completed) completedStories++;
        }
        
        String progressText = "Lvl " + (completedStories + 1);
        int progressX = slotX + (slotWidth - nameMetrics.stringWidth(progressText)) / 2;
        int progressY = slotY + 40;
        g2d.setColor(new Color(150, 255, 150));
        g2d.drawString(progressText, progressX, progressY);
        
        // Mini stats
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String stats = String.format("S%d D%d I%d", 
                character.getStrength(), character.getDexterity(), character.getIntelligence());
        FontMetrics statsMetrics = g2d.getFontMetrics();
        int statsX = slotX + (slotWidth - statsMetrics.stringWidth(stats)) / 2;
        int statsY = slotY + 60;
        g2d.drawString(stats, statsX, statsY);
        
        String stats2 = String.format("P%d C%d", 
                character.getPerception(), character.getCharisma());
        int stats2X = slotX + (slotWidth - statsMetrics.stringWidth(stats2)) / 2;
        int stats2Y = slotY + 75;
        g2d.drawString(stats2, stats2X, stats2Y);
        
        // Last played info
        g2d.setColor(new Color(180, 180, 180));
        String playtime = String.format("%dh", character.getTotalPlaytime() / 60);
        int playtimeX = slotX + (slotWidth - statsMetrics.stringWidth(playtime)) / 2;
        int playtimeY = slotY + 95;
        g2d.drawString(playtime, playtimeX, playtimeY);
    }
    
    private void drawEmptySlot(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight, 
                              Color textColor, boolean isAvailable) {
        // Draw plus sign
        g2d.setColor(textColor);
        g2d.setFont(slotFont);
        FontMetrics plusMetrics = g2d.getFontMetrics();
        String plusSign = "+";
        int plusX = slotX + (slotWidth - plusMetrics.stringWidth(plusSign)) / 2;
        int plusY = slotY + slotHeight / 2 + plusMetrics.getAscent() / 2;
        g2d.drawString(plusSign, plusX, plusY);
        
        // Draw status text
        g2d.setFont(tooltipFont);
        FontMetrics statusMetrics = g2d.getFontMetrics();
        String statusText = isAvailable ? "New" : "Locked";
        int statusX = slotX + (slotWidth - statusMetrics.stringWidth(statusText)) / 2;
        int statusY = slotY + slotHeight - 15;
        g2d.drawString(statusText, statusX, statusY);
    }
    
    private void drawTooltip(Graphics2D g2d) {
        if (tooltipText.isEmpty()) return;
        
        // Calculate tooltip size
        g2d.setFont(tooltipFont);
        FontMetrics tooltipMetrics = g2d.getFontMetrics();
        String[] lines = tooltipText.split("\n");
        
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, tooltipMetrics.stringWidth(line));
        }
        
        int tooltipWidth = maxWidth + 20;
        int tooltipHeight = lines.length * tooltipMetrics.getHeight() + 10;
        
        // Position tooltip near mouse
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        int tooltipX = mouseX + 15;
        int tooltipY = mouseY - tooltipHeight - 5;
        
        // Keep tooltip on screen
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        
        if (tooltipX + tooltipWidth > windowWidth) {
            tooltipX = mouseX - tooltipWidth - 15;
        }
        if (tooltipY < 0) {
            tooltipY = mouseY + 20;
        }
        
        // Draw tooltip background
        g2d.setColor(new Color(30, 30, 30, 240));
        g2d.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 8, 8);
        
        g2d.setColor(new Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 8, 8);
        
        // Draw tooltip text
        g2d.setColor(Color.WHITE);
        int lineY = tooltipY + tooltipMetrics.getAscent() + 5;
        for (String line : lines) {
            g2d.drawString(line, tooltipX + 10, lineY);
            lineY += tooltipMetrics.getHeight();
        }
    }
}
