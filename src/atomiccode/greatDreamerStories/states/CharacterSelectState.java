package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.Resources;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.cthulhuEngine.ui.layout.UiGridLayout;
import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.character.SaveManager;
import atomiccode.greatDreamerStories.ui.GeneralMenuLayout;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;
import atomiccode.greatDreamerStories.ui.menu.CharacterSlotFolder;
import atomiccode.greatDreamerStories.ui.menu.MenuActionStrip;
import atomiccode.greatDreamerStories.ui.menu.MenuConfirmationModal;
import atomiccode.greatDreamerStories.ui.menu.MenuScreenTitle;
import atomiccode.greatDreamerStories.i18n.GameTexts;

import com.badlogic.gdx.graphics.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CharacterSelectState implements State {

    private static final String BACK_BUTTON_GRAPHIC_PREFIX = "<  ";

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
    private boolean wasRightPressed = false;
    
    // Delete confirmation
    private boolean confirmingDelete = false;
    private int pendingDeleteSlot = -1;
    
    private Font buttonFont;
    private Font slotFont;
    private Font tooltipFont;
    private GeneralMenuLayout layout;
    private UiRect[] slotRects = new UiRect[0];

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        // Character select initialization
        saveManager = SaveManager.getInstance();
        
        // Initialize fonts
        buttonFont = GreatDreamerTheme.archiveFont(14);
        slotFont = GreatDreamerTheme.archiveFont(22);
        tooltipFont = GreatDreamerTheme.archiveFont(12);

        // Initialize buttons
        backButton = new Button(0, 0, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT,
                BACK_BUTTON_GRAPHIC_PREFIX + GameTexts.tr("common.back"));
        confirmDeleteButton = new Button(0, 0, DELETE_CONFIRM_BUTTON_WIDTH, DELETE_CONFIRM_BUTTON_HEIGHT, GameTexts.tr("common.delete"));
        cancelDeleteButton = new Button(0, 0, DELETE_CONFIRM_BUTTON_WIDTH, DELETE_CONFIRM_BUTTON_HEIGHT, GameTexts.tr("common.cancel"));

        // Set button colors
        GreatDreamerTheme.styleSecondaryButton(backButton, buttonFont);
        GreatDreamerTheme.styleDangerButton(confirmDeleteButton, buttonFont);
        GreatDreamerTheme.styleSecondaryButton(cancelDeleteButton, buttonFont);

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

    @Override
    public Cursor.SystemCursor getUiSystemCursor(int mx, int my) {
        if (confirmingDelete) {
            if (confirmDeleteButton.contains(mx, my) || cancelDeleteButton.contains(mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
            return Cursor.SystemCursor.Arrow;
        }
        if (backButton.contains(mx, my)) {
            return Cursor.SystemCursor.Hand;
        }
        for (UiRect slot : slotRects) {
            if (slot != null && slot.contains(mx, my)) {
                return Cursor.SystemCursor.Hand;
            }
        }
        return Cursor.SystemCursor.Arrow;
    }
    
    private void updateLayout() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        layout = GeneralMenuLayout.fromViewport(windowWidth, windowHeight);
        UiGridLayout grid = new UiGridLayout(SLOT_COLS, SLOT_SPACING_X, SLOT_SPACING_Y, SLOT_HEIGHT);
        slotRects = grid.layout(layout.body, saveManager.getMaxSlots());

        MenuActionStrip.placePrimaryRight(layout, backButton, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
    }
    
    private void updateSlotHover(int mouseX, int mouseY) {
        hoveredSlot = -1;
        
        for (int i = 0; i < slotRects.length; i++) {
            if (slotRects[i].contains(mouseX, mouseY)) {
                hoveredSlot = i;
                break;
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
        MenuConfirmationModal.layoutButtonRow(windowWidth, windowHeight, DELETE_CONFIRM_BUTTON_WIDTH,
                DELETE_CONFIRM_BUTTON_HEIGHT, 20, new Button[]{confirmDeleteButton, cancelDeleteButton});
    }

    @Override
    public void render(Graphics g) {
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        Graphics2D g2d = (Graphics2D) g;
        Resources.enableAntialiasing(g2d);
        
        GeneralMenuRenderer.drawPage(g2d, windowWidth, windowHeight);
        GeneralMenuRenderer.drawSubtleBackground(g2d, windowWidth, windowHeight);
        
        MenuScreenTitle.draw(g2d, layout.content, layout.content.right(), GameTexts.tr("screen.character_select.title"),
                GameTexts.archiveStrapline("archive.strapline.case_files"),
                new MenuScreenTitle.RightMetric(String.valueOf(getOccupiedSlotCount()),
                        GameTexts.tr("screen.character_select.metric.active_agents"), 10, 90));
        
        for (int i = 0; i < slotRects.length; i++) {
            UiRect slotRect = slotRects[i];
            Character character = saveManager.getCharacter(i);
            CharacterSlotFolder.draw(g2d, slotRect.x, slotRect.y, slotRect.width, slotRect.height,
                    character, i, saveManager.getFirstAvailableSlot(),
                    hoveredSlot == i, selectedIndex == i, slotFont, tooltipFont);
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
    
    private void drawDeleteConfirmation(Graphics2D g2d, int windowWidth, int windowHeight) {
        Character character = saveManager.getCharacter(pendingDeleteSlot);
        String characterName = character != null ? character.getName() : GameTexts.tr("screen.character_select.placeholder_name");

        MenuConfirmationModal.drawScrim(g2d, windowWidth, windowHeight);
        MenuConfirmationModal.Geometry geo = MenuConfirmationModal.centered(windowWidth, windowHeight,
                MenuConfirmationModal.DEFAULT_WIDTH, MenuConfirmationModal.DEFAULT_HEIGHT);
        MenuConfirmationModal.drawFrame(g2d, geo);
        MenuConfirmationModal.drawText(g2d, geo, buttonFont, tooltipFont, GameTexts.tr("screen.character_select.delete.title"),
                new String[]{
                        GameTexts.trf("screen.character_select.delete.line1", characterName),
                        GameTexts.tr("screen.character_select.delete.line2")
                });

        g2d.setStroke(new BasicStroke(1f));
        confirmDeleteButton.render(g2d);
        cancelDeleteButton.render(g2d);
    }

}
