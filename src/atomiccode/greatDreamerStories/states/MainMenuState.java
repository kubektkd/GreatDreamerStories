package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.ui.Button;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MainMenuState implements State {
    
    private static final int FADE_DURATION_MS = 1000; // 1 second fade
    private long startTime;
    private boolean hasStarted = false;
    
    // Menu buttons
    private Button startButton, settingsButton, exitButton;
    private int selectedIndex = 0;
    private Button[] menuButtons;
    
    @Override
    public int getPriority() {
        return 1; // Lower priority than splash screen
    }
    
    @Override
    public void onEnter() {
        // Main menu initialization
        startTime = System.currentTimeMillis();
        hasStarted = true;
        
        // Initialize buttons (will be positioned in render method)
        startButton = new Button(0, 0, 200, 50, "Start Game");
        settingsButton = new Button(0, 0, 200, 50, "Settings");
        exitButton = new Button(0, 0, 200, 50, "Exit");
        
        // Set button colors
        Color normalColor = new Color(50, 50, 50, 200);
        Color hoverColor = new Color(70, 70, 70, 200);
        Color pressedColor = new Color(30, 30, 30, 200);
        Color textColor = Color.WHITE;
        
        startButton.setColors(normalColor, hoverColor, pressedColor, textColor);
        settingsButton.setColors(normalColor, hoverColor, pressedColor, textColor);
        exitButton.setColors(normalColor, hoverColor, pressedColor, textColor);
        
        // Set button fonts
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        startButton.setFont(buttonFont);
        settingsButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        
        // Set button actions
        startButton.setOnClick(() -> {
            Engine.instance().stateManager.setState(new CharacterSelectState());
        });
        
        settingsButton.setOnClick(() -> {
            // TODO: Implement settings state
            System.out.println("Settings clicked - not implemented yet");
        });
        
        exitButton.setOnClick(() -> {
            Engine.instance().requestClose();
        });
        
        // Create button array for keyboard navigation
        menuButtons = new Button[]{startButton, settingsButton, exitButton};
    }
    
    @Override
    public void onExit() {
        // Cleanup when leaving main menu
    }
    
    @Override
    public void tick() {
        // Handle keyboard navigation
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_UP)) {
            selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuButtons.length;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER)) {
            menuButtons[selectedIndex].click();
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            Engine.instance().requestClose();
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
        
        // Calculate fade alpha
        float alpha = 1.0f;
        if (hasStarted) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            if (elapsedTime < FADE_DURATION_MS) {
                alpha = (float) elapsedTime / FADE_DURATION_MS;
            }
        }
        
        // Calculate center position
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        
        // Draw main menu title with alpha
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Great Dreamer Stories";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 150;
        g2d.drawString(title, titleX, titleY);
        
        // Position and render buttons
        int buttonSpacing = 70;
        int buttonStartY = centerY - 50;
        
        startButton.x = centerX - 100;
        startButton.y = buttonStartY;
        startButton.render(g);
        
        settingsButton.x = centerX - 100;
        settingsButton.y = buttonStartY + buttonSpacing;
        settingsButton.render(g);
        
        exitButton.x = centerX - 100;
        exitButton.y = buttonStartY + buttonSpacing * 2;
        exitButton.render(g);
        
        // Draw instructions
        g2d.setColor(new Color(200, 200, 200, (int)(alpha * 255)));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String instructions = "Use mouse to click or arrow keys + Enter to navigate";
        FontMetrics instMetrics = g2d.getFontMetrics();
        int instX = centerX - instMetrics.stringWidth(instructions) / 2;
        int instY = centerY + 200;
        g2d.drawString(instructions, instX, instY);
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
