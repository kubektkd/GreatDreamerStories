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
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
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
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Choose your character";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 150;
        g2d.drawString(title, titleX, titleY);

        // Position and render buttons
        int buttonStartY = centerY - 50;
        
        backButton.x = centerX - 100;
        backButton.y = buttonStartY;
        backButton.render(g);
    }
}
