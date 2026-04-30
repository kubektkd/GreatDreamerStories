package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CharacterSelectState implements State {

    private Button backButton;
    private Button confirmDeleteButton;
    private Button cancelDeleteButton;
    private int selectedIndex = 0;
    private Button[] menuButtons;
    private SaveManager saveManager;
    private static final int SLOT_COLS = 4;
    private static final int SLOT_HEIGHT = 168;
    private static final int SLOT_SPACING_X = 20;
    private static final int SLOT_SPACING_Y = 24;
    private static final int ACTION_BUTTON_WIDTH = 130;
    private static final int ACTION_BUTTON_HEIGHT = 48;
    private static final int DELETE_CONFIRM_BUTTON_WIDTH = 160;
    private static final int DELETE_CONFIRM_BUTTON_HEIGHT = 48;
    
    // Slot interaction
    private int hoveredSlot = -1;
    private String tooltipText = "";
    private long tooltipStartTime = 0;
    private static final long TOOLTIP_DELAY = 1000; // 1 second delay before showing tooltip
    private boolean wasRightPressed = false;
    
    // Delete confirmation
    private boolean confirmingDelete = false;
    private int pendingDeleteSlot = -1;
    
    // Colors and fonts
    private final Color pageColor = new Color(8, 9, 11);
    private final Color lineBorderColor = new Color(31, 34, 40);
    private final Color borderColor = new Color(55, 58, 65);
    private final Color mutedBorderColor = new Color(155, 155, 155);
    private final Color textColor = new Color(235, 233, 225);
    private final Color mutedTextColor = new Color(116, 116, 116);
    private final Color selectedColor = new Color(245, 244, 238);
    private final Color dangerColor = new Color(125, 42, 42);
    private final Color dangerHoverColor = new Color(160, 58, 58);
    private Font buttonFont;
    private Font titleFont;
    private Font slotFont;
    private Font tooltipFont;
    private int layoutWidth;
    private int layoutHeight;
    private int layoutX;
    private int layoutY;
    private int gridStartX;
    private int gridStartY;
    private int gridWidth;
    private int slotWidth;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        // Character select initialization
        saveManager = SaveManager.getInstance();
        
        // Initialize fonts
        buttonFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        titleFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 30);
        slotFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 22);
        tooltipFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 12);

        // Initialize buttons
        backButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, "<  BACK");
        confirmDeleteButton = new Button(0, 0, DELETE_CONFIRM_BUTTON_WIDTH, DELETE_CONFIRM_BUTTON_HEIGHT, "DELETE");
        cancelDeleteButton = new Button(0, 0, DELETE_CONFIRM_BUTTON_WIDTH, DELETE_CONFIRM_BUTTON_HEIGHT, "CANCEL");

        // Set button colors
        backButton.setColors(new Color(12, 13, 16), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        confirmDeleteButton.setColors(dangerColor, dangerHoverColor, new Color(90, 30, 30), textColor);
        cancelDeleteButton.setColors(new Color(12, 13, 16), new Color(42, 43, 48), new Color(8, 8, 10), textColor);
        backButton.setFont(buttonFont);
        confirmDeleteButton.setFont(buttonFont);
        cancelDeleteButton.setFont(buttonFont);

        // Set button actions
        backButton.setOnClick(() -> {
            // State change will automatically trigger fade transition
            Engine.instance().stateProcessor.setState(new MainMenuState());
        });
        confirmDeleteButton.setOnClick(this::confirmDeleteSlot);
        cancelDeleteButton.setOnClick(this::cancelDeleteSlot);

        // Create button array for keyboard navigation
        menuButtons = new Button[]{backButton};
    }

    @Override
    public void onExit() {

    }

    @Override
    public void tick() {
        if (confirmingDelete) {
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
                confirmDeleteSlot();
            }
            if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
                cancelDeleteSlot();
            }
            return;
        }
        
        // Handle keyboard navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            if (selectedIndex == saveManager.getMaxSlots()) {
                backButton.click();
            } else {
                // Handle character slot selection
                handleSlotSelection(selectedIndex);
            }
        }
        
        // Handle arrow key navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_UP)) {
            if (selectedIndex == saveManager.getMaxSlots()) {
                selectedIndex = saveManager.getMaxSlots() - 1; // Move from back button to bottom row
            } else if (selectedIndex >= SLOT_COLS) {
                selectedIndex -= SLOT_COLS; // Move up one row
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DOWN)) {
            if (selectedIndex < saveManager.getMaxSlots() - SLOT_COLS) {
                selectedIndex += SLOT_COLS; // Move down one row
            } else if (selectedIndex < saveManager.getMaxSlots()) {
                selectedIndex = saveManager.getMaxSlots(); // Move from bottom row to back button
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
            if (selectedIndex < saveManager.getMaxSlots() && selectedIndex % SLOT_COLS > 0) {
                selectedIndex--;
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
            if (selectedIndex < saveManager.getMaxSlots() - 1 && selectedIndex % SLOT_COLS < SLOT_COLS - 1) {
                selectedIndex++;
            }
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DELETE)) {
            requestDeleteSlot(selectedIndex);
        }
        
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            backButton.click();
        }
    }

    @Override
    public void update() {
        updateLayout();
        
        // Update button states
        int mouseX = Engine.instance().mouse.getX();
        int mouseY = Engine.instance().mouse.getY();
        boolean mousePressed = Engine.instance().mouse.isLeftPressed();
        boolean rightPressed = Engine.instance().mouse.isRightPressed();
        
        if (confirmingDelete) {
            updateDeleteConfirmationButtons();
            confirmDeleteButton.update(mouseX, mouseY, mousePressed);
            cancelDeleteButton.update(mouseX, mouseY, mousePressed);
            backButton.setSelected(false);
            wasRightPressed = rightPressed;
            return;
        }
        
        for (Button button : menuButtons) {
            button.update(mouseX, mouseY, mousePressed);
        }
        
        // Back button is focused after the character slots in keyboard order.
        backButton.setSelected(selectedIndex == saveManager.getMaxSlots());
        
        // Check for mouse hover over character slots
        updateSlotHover(mouseX, mouseY);
        
        // Handle slot click
        if (mousePressed && hoveredSlot != -1) {
            handleSlotSelection(hoveredSlot);
        }
        
        if (rightPressed && !wasRightPressed && hoveredSlot != -1) {
            requestDeleteSlot(hoveredSlot);
        }
        wasRightPressed = rightPressed;
    }
    
    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layoutWidth = Math.min(windowWidth - 120, 1060);
        layoutHeight = Math.min(windowHeight - 90, 620);
        layoutX = (windowWidth - layoutWidth) / 2;
        layoutY = (windowHeight - layoutHeight) / 2;
        gridWidth = layoutWidth;
        slotWidth = (gridWidth - (SLOT_COLS - 1) * SLOT_SPACING_X) / SLOT_COLS;
        gridStartX = layoutX;
        gridStartY = layoutY + 120;
        
        backButton.x = layoutX + layoutWidth - ACTION_BUTTON_WIDTH;
        backButton.y = layoutY + layoutHeight - 62;
    }
    
    private void updateSlotHover(int mouseX, int mouseY) {
        int oldHoveredSlot = hoveredSlot;
        hoveredSlot = -1;
        
        for (int i = 0; i < saveManager.getMaxSlots(); i++) {
            int row = i / SLOT_COLS;
            int col = i % SLOT_COLS;
            int slotX = gridStartX + col * (slotWidth + SLOT_SPACING_X);
            int slotY = gridStartY + row * (SLOT_HEIGHT + SLOT_SPACING_Y);
            
            if (mouseX >= slotX && mouseX <= slotX + slotWidth &&
                mouseY >= slotY && mouseY <= slotY + SLOT_HEIGHT) {
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
    
    private void requestDeleteSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= saveManager.getMaxSlots()) {
            return;
        }
        if (saveManager.getCharacter(slotIndex) == null) {
            return;
        }
        
        pendingDeleteSlot = slotIndex;
        confirmingDelete = true;
    }
    
    private void confirmDeleteSlot() {
        if (pendingDeleteSlot >= 0 && pendingDeleteSlot < saveManager.getMaxSlots()) {
            saveManager.deleteCharacter(pendingDeleteSlot);
            if (hoveredSlot == pendingDeleteSlot) {
                updateTooltipText(hoveredSlot);
            }
        }
        
        cancelDeleteSlot();
    }
    
    private void cancelDeleteSlot() {
        confirmingDelete = false;
        pendingDeleteSlot = -1;
    }
    
    private void updateDeleteConfirmationButtons() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        int buttonY = windowHeight / 2 + 50;
        int spacing = 20;
        int totalButtonWidth = DELETE_CONFIRM_BUTTON_WIDTH * 2 + spacing;
        int startX = (windowWidth - totalButtonWidth) / 2;
        
        confirmDeleteButton.x = startX;
        confirmDeleteButton.y = buttonY;
        cancelDeleteButton.x = startX + DELETE_CONFIRM_BUTTON_WIDTH + spacing;
        cancelDeleteButton.y = buttonY;
    }

    @Override
    public void render(Graphics g) {
        updateLayout();
        
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);
        
        g2d.setColor(pageColor);
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        drawSubtleBackground(g2d, windowWidth, windowHeight);
        
        g2d.setColor(textColor);
        g2d.setFont(titleFont);
        g2d.drawString("CHOOSE INVESTIGATOR", layoutX, layoutY + 38);
        g2d.setFont(tooltipFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("STOKSJÖ POLICE ARCHIVE // CASE FILES", layoutX + 2, layoutY + 62);
        g2d.setColor(lineBorderColor);
        g2d.drawLine(layoutX, layoutY + 80, layoutX + gridStartX + gridWidth - layoutX, layoutY + 80);
        
        g2d.setColor(textColor);
        g2d.setFont(titleFont);
        String fileCount = String.valueOf(getOccupiedSlotCount());
        FontMetrics countMetrics = g2d.getFontMetrics();
        g2d.drawString(fileCount, layoutX + layoutWidth - countMetrics.stringWidth(fileCount) - 10, layoutY + 42);
        g2d.setFont(tooltipFont);
        g2d.setColor(mutedTextColor);
        g2d.drawString("ACTIVE CASES", layoutX + layoutWidth - 90, layoutY + 62);
        
        int slotCount = saveManager.getMaxSlots();
        for (int i = 0; i < slotCount; i++) {
            int row = i / SLOT_COLS;
            int col = i % SLOT_COLS;
            int slotX = gridStartX + col * (slotWidth + SLOT_SPACING_X);
            int slotY = gridStartY + row * (SLOT_HEIGHT + SLOT_SPACING_Y);

            drawCharacterSlot(g2d, i, slotX, slotY, slotWidth, SLOT_HEIGHT);
        }

        // Draw tooltip if hovering and enough time has passed
        if (hoveredSlot != -1 && !tooltipText.isEmpty() && 
            System.currentTimeMillis() - tooltipStartTime > TOOLTIP_DELAY) {
            drawTooltip(g2d);
        }

        g2d.setStroke(new BasicStroke(1f));
        backButton.render(g);
        
        if (confirmingDelete) {
            drawDeleteConfirmation(g2d, windowWidth, windowHeight);
        }
    }
    
    private int getOccupiedSlotCount() {
        int occupiedSlots = 0;
        for (int i = 0; i < saveManager.getMaxSlots(); i++) {
            if (saveManager.getCharacter(i) != null) {
                occupiedSlots++;
            }
        }
        return occupiedSlots;
    }
    
    private void drawSubtleBackground(Graphics2D g2d, int windowWidth, int windowHeight) {
        g2d.setColor(new Color(255, 255, 255, 8));
        for (int y = 0; y < windowHeight; y += 4) {
            g2d.drawLine(0, y, windowWidth, y);
        }
    }
    
    private void drawDeleteConfirmation(Graphics2D g2d, int windowWidth, int windowHeight) {
        Character character = saveManager.getCharacter(pendingDeleteSlot);
        String characterName = character != null ? character.getName() : "this character";
        
        g2d.setColor(new Color(0, 0, 0, 175));
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        int popupWidth = 520;
        int popupHeight = 220;
        int popupX = (windowWidth - popupWidth) / 2;
        int popupY = (windowHeight - popupHeight) / 2;
        
        g2d.setColor(new Color(11, 12, 15, 245));
        g2d.fillRect(popupX, popupY, popupWidth, popupHeight);
        
        g2d.setColor(dangerHoverColor);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRect(popupX, popupY, popupWidth, popupHeight);
        
        g2d.setColor(textColor);
        g2d.setFont(buttonFont);
        drawCenteredString(g2d, "DELETE CHARACTER?", popupX, popupY + 45, popupWidth);
        
        g2d.setFont(tooltipFont);
        g2d.setColor(mutedTextColor);
        drawCenteredString(g2d, "This will permanently delete \"" + characterName + "\".", popupX, popupY + 85, popupWidth);
        drawCenteredString(g2d, "Press Enter to delete or Esc to cancel.", popupX, popupY + 110, popupWidth);
        
        updateDeleteConfirmationButtons();
        g2d.setStroke(new BasicStroke(1f));
        confirmDeleteButton.render(g2d);
        cancelDeleteButton.render(g2d);
    }
    
    private void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width) {
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y);
    }
    
    private void drawCharacterSlot(Graphics2D g2d, int slotIndex, int slotX, int slotY, int slotWidth, int slotHeight) {
        Character character = saveManager.getCharacter(slotIndex);
        boolean isOccupied = character != null;
        boolean isAvailable = slotIndex == saveManager.getFirstAvailableSlot();
        boolean isHovered = hoveredSlot == slotIndex;
        boolean isSelected = selectedIndex == slotIndex;

        Color folderColor;
        Color folderAccentColor;
        Color slotBorderColor;
        Color textColor;
        
        if (isOccupied) {
            folderColor = isHovered ? new Color(191, 151, 88) : new Color(174, 133, 72);
            folderAccentColor = new Color(214, 182, 121);
            slotBorderColor = isSelected ? selectedColor : borderColor;
            textColor = new Color(38, 29, 18);
        } else if (isAvailable) {
            folderColor = isHovered ? new Color(181, 139, 76) : new Color(157, 116, 61);
            folderAccentColor = new Color(201, 166, 105);
            slotBorderColor = isSelected ? selectedColor : new Color(86, 92, 84);
            textColor = new Color(43, 31, 18);
        } else {
            folderColor = new Color(75, 61, 42);
            folderAccentColor = new Color(97, 80, 55);
            slotBorderColor = mutedBorderColor;
            textColor = new Color(118, 103, 78);
        }

        if (isOccupied) {
            drawFolderPapers(g2d, slotX, slotY, slotWidth, slotHeight);
        }

        drawFolderShape(g2d, slotX, slotY, slotWidth, slotHeight, folderColor, folderAccentColor, slotBorderColor, isSelected);

        if (isOccupied) {
            drawOccupiedSlot(g2d, character, slotX, slotY, slotWidth, slotHeight, textColor);
        } else {
            drawEmptySlot(g2d, slotX, slotY, slotWidth, slotHeight, textColor, isAvailable);
        }
    }

    private void drawFolderPapers(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight) {
        int paperX = slotX + 18;
        int paperY = slotY + 14;
        int paperWidth = slotWidth - 34;
        int paperHeight = slotHeight - 42;

        g2d.setColor(new Color(224, 217, 194));
        g2d.fillRect(paperX + 8, paperY + 4, paperWidth, paperHeight);
        g2d.setColor(new Color(166, 153, 124));
        g2d.drawRect(paperX + 8, paperY + 4, paperWidth, paperHeight);

        g2d.setColor(new Color(236, 230, 206));
        g2d.fillRect(paperX, paperY, paperWidth, paperHeight);
        g2d.setColor(new Color(176, 162, 130));
        g2d.drawRect(paperX, paperY, paperWidth, paperHeight);

        g2d.setColor(new Color(125, 111, 84));
        for (int line = 0; line < 4; line++) {
            int y = paperY + 28 + line * 15;
            g2d.drawLine(paperX + 16, y, paperX + paperWidth - 16, y);
        }
    }

    private void drawFolderShape(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight,
                                 Color folderColor, Color folderAccentColor, Color borderColor, boolean isSelected) {
        int tabHeight = 26;
        int tabWidth = Math.max(72, slotWidth / 3);
        int tabSlope = 16;
        int bodyTop = slotY + tabHeight;

        Polygon backTab = new Polygon();
        backTab.addPoint(slotX + 10, bodyTop);
        backTab.addPoint(slotX + 18, slotY + 7);
        backTab.addPoint(slotX + tabWidth, slotY + 7);
        backTab.addPoint(slotX + tabWidth + tabSlope, bodyTop);
        backTab.addPoint(slotX + slotWidth - 8, bodyTop);
        backTab.addPoint(slotX + slotWidth - 8, slotY + slotHeight - 8);
        backTab.addPoint(slotX + 10, slotY + slotHeight - 8);

        g2d.setColor(new Color(0, 0, 0, 70));
        g2d.fillPolygon(translatePolygon(backTab, 4, 5));

        g2d.setColor(folderAccentColor);
        g2d.fillPolygon(backTab);

        Polygon front = new Polygon();
        front.addPoint(slotX, bodyTop + 8);
        front.addPoint(slotX + slotWidth, bodyTop + 8);
        front.addPoint(slotX + slotWidth - 8, slotY + slotHeight);
        front.addPoint(slotX + 8, slotY + slotHeight);

        g2d.setColor(folderColor);
        g2d.fillPolygon(front);
        g2d.setColor(isSelected ? borderColor : lineBorderColor);
        g2d.setStroke(new BasicStroke(isSelected ? 3f : 1f));
        g2d.drawPolygon(createFolderOutline(slotX, slotY, slotWidth, slotHeight, bodyTop, tabWidth, tabSlope));
        g2d.setStroke(new BasicStroke(1f));
    }

    private Polygon createFolderOutline(int slotX, int slotY, int slotWidth, int slotHeight,
                                        int bodyTop, int tabWidth, int tabSlope) {
        Polygon outline = new Polygon();
        outline.addPoint(slotX, bodyTop + 8);
        outline.addPoint(slotX + 10, bodyTop);
        outline.addPoint(slotX + 18, slotY + 7);
        outline.addPoint(slotX + tabWidth, slotY + 7);
        outline.addPoint(slotX + tabWidth + tabSlope, bodyTop);
        outline.addPoint(slotX + slotWidth - 8, bodyTop);
        outline.addPoint(slotX + slotWidth, bodyTop + 8);
        outline.addPoint(slotX + slotWidth - 8, slotY + slotHeight);
        outline.addPoint(slotX + 8, slotY + slotHeight);
        return outline;
    }

    private Polygon translatePolygon(Polygon polygon, int dx, int dy) {
        Polygon translated = new Polygon();
        for (int i = 0; i < polygon.npoints; i++) {
            translated.addPoint(polygon.xpoints[i] + dx, polygon.ypoints[i] + dy);
        }
        return translated;
    }
    
    private void drawOccupiedSlot(Graphics2D g2d, Character character, int slotX, int slotY, 
                                 int slotWidth, int slotHeight, Color textColor) {
        g2d.setColor(textColor);
        
        // Character name (truncated if too long)
        g2d.setFont(slotFont);
        FontMetrics nameMetrics = g2d.getFontMetrics();
        String name = character.getName();
        if (nameMetrics.stringWidth(name) > slotWidth - 18) {
            // Truncate name if too long
            while (nameMetrics.stringWidth(name + "...") > slotWidth - 18 && name.length() > 3) {
                name = name.substring(0, name.length() - 1);
            }
            name += "...";
        }
        
        int nameX = slotX + (slotWidth - nameMetrics.stringWidth(name)) / 2;
        int nameY = slotY + 60;
        g2d.drawString(name, nameX, nameY);
        
        // Character level/progress indicator
        int completedStories = 0;
        for (boolean completed : character.getCompletedStories()) {
            if (completed) completedStories++;
        }
        
        String progressText = "Lvl " + (completedStories + 1);
        g2d.setFont(tooltipFont);
        FontMetrics detailMetrics = g2d.getFontMetrics();
        int progressX = slotX + (slotWidth - detailMetrics.stringWidth(progressText)) / 2;
        int progressY = slotY + 85;
        g2d.setColor(selectedColor);
        g2d.drawString(progressText, progressX, progressY);
        
        // Mini stats
        g2d.setColor(textColor);
        g2d.setFont(tooltipFont);
        String stats = String.format("STR:%d POW:%d EDU:%d CON:%d", 
                character.getStrength(), character.getPower(), character.getEducation(), character.getConstitution());
        FontMetrics statsMetrics = g2d.getFontMetrics();
        int statsX = slotX + (slotWidth - statsMetrics.stringWidth(stats)) / 2;
        int statsY = slotY + 110;
        g2d.drawString(stats, statsX, statsY);
        
        String stats2 = String.format("INT:%d APP:%d LCK:%d SIZ:%d DEX:%d", 
                character.getIntelligence(), character.getAppearance(), character.getLuck(), character.getSize(), character.getDexterity());
        int stats2X = slotX + (slotWidth - statsMetrics.stringWidth(stats2)) / 2;
        int stats2Y = slotY + 125;
        g2d.drawString(stats2, stats2X, stats2Y);
        
        // Last played info
        g2d.setColor(selectedColor);
        String playtime = String.format("Playtime: %dh", character.getTotalPlaytime() / 60);
        int playtimeX = slotX + (slotWidth - statsMetrics.stringWidth(playtime)) / 2;
        int playtimeY = slotY + 155;
        g2d.drawString(playtime, playtimeX, playtimeY);
    }
    
    private void drawEmptySlot(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight, 
                              Color textColor, boolean isAvailable) {
        // Draw plus sign
        g2d.setColor(textColor);
        g2d.setFont(slotFont);
        FontMetrics plusMetrics = g2d.getFontMetrics();
        String plusSign = isAvailable ? "+" : "X";
        int plusX = slotX + (slotWidth - plusMetrics.stringWidth(plusSign)) / 2;
        int plusY = slotY + slotHeight / 2 + plusMetrics.getAscent() / 2;
        g2d.drawString(plusSign, plusX, plusY);
        
        // Draw status text
        g2d.setFont(tooltipFont);
        FontMetrics statusMetrics = g2d.getFontMetrics();
        String statusText = isAvailable ? "NEW FILE" : "LOCKED";
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
        g2d.setColor(new Color(11, 12, 15, 245));
        g2d.fillRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
        
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
        
        // Draw tooltip text
        g2d.setColor(textColor);
        int lineY = tooltipY + tooltipMetrics.getAscent() + 5;
        for (String line : lines) {
            g2d.drawString(line, tooltipX + 10, lineY);
            lineY += tooltipMetrics.getHeight();
        }
    }
}
