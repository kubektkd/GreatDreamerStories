package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;

import java.awt.*;

public class MainMenuState implements State {
    
    private static final int FADE_DURATION_MS = 1000; // 1 second fade
    private long startTime;
    private boolean hasStarted = false;
    
    @Override
    public int getPriority() {
        return 1; // Lower priority than splash screen
    }
    
    @Override
    public void onEnter() {
        // Main menu initialization
        startTime = System.currentTimeMillis();
        hasStarted = true;
    }
    
    @Override
    public void onExit() {
        // Cleanup when leaving main menu
    }
    
    @Override
    public void tick() {
        // No deterministic logic needed for main menu
    }
    
    @Override
    public void update() {
        // Handle main menu updates
    }
    
    @Override
    public void render(Graphics g) {
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Set background color
        g.setColor(new Color(0, 0, 0)); // Darker background
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
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Main Menu";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 100;
        g2d.drawString(title, titleX, titleY);
        
        // Draw menu options
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        FontMetrics menuMetrics = g2d.getFontMetrics();
        String[] menuItems = {"Start Game", "Settings", "Exit"};
        
        for (int i = 0; i < menuItems.length; i++) {
            String item = menuItems[i];
            int itemX = centerX - menuMetrics.stringWidth(item) / 2;
            int itemY = centerY - 20 + (i * 40);
            g2d.drawString(item, itemX, itemY);
        }
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
