package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;

import java.awt.*;

public class CharacterSelectState implements State {


    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnter() {
        // Character select initialization
    }

    @Override
    public void onExit() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void update() {

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
    }
}
