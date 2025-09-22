package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CharacterSelectState implements State {

    private Button backButton;
    private int selectedIndex = 0;
    private Button[] menuButtons;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        // Character select initialization

        // Initialize buttons
        backButton = new Button(0, 0, 200, 50, "Back");

        // Set button colors
        Color normalColor = new Color(50, 50, 50, 200);
        Color hoverColor = new Color(70, 70, 70, 200);
        Color pressedColor = new Color(30, 30, 30, 200);
        Color textColor = Color.WHITE;
        
        backButton.setColors(normalColor, hoverColor, pressedColor, textColor);

        // Set button fonts
        Font buttonFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 18);
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
            menuButtons[selectedIndex].click();
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
        Font titleFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 48);
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
        int slotCount = 8;
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

            // Draw slot background
            g2d.setColor(new Color(40, 40, 60, 220));
            g2d.fillRoundRect(slotX, slotY, slotWidth, slotHeight, 18, 18);

            // Draw border (highlight if selected)
            if (i == selectedIndex) {
                g2d.setColor(new Color(120, 180, 255));
                g2d.setStroke(new java.awt.BasicStroke(4f));
            } else {
                g2d.setColor(new Color(100, 100, 120));
                g2d.setStroke(new java.awt.BasicStroke(2f));
            }
            g2d.drawRoundRect(slotX, slotY, slotWidth, slotHeight, 18, 18);

            // Draw character number or placeholder
            String charLabel = "Slot " + (i + 1);
            Font charFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 18);
            g2d.setFont(charFont);
            FontMetrics charMetrics = g2d.getFontMetrics();
            int labelX = slotX + (slotWidth - charMetrics.stringWidth(charLabel)) / 2;
            int labelY = slotY + slotHeight / 2 + charMetrics.getAscent() / 2;
            g2d.setColor(Color.WHITE);
            g2d.drawString(charLabel, labelX, labelY);
        }

        // Position and render buttons
        int buttonStartY = centerY + gridHeight - 50;
        
        backButton.x = centerX - 100;
        backButton.y = buttonStartY;
        backButton.render(g);
    }
}
