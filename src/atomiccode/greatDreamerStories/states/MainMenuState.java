package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.greatDreamerStories.Game;
import atomiccode.cthulhuEngine.ui.Button;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenuState implements State {
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean imageLoaded = false;
    
    // Logo image
    private BufferedImage logoImage;
    private boolean logoLoaded = false;
    
    // Menu buttons
    private Button startButton, settingsButton, exitButton;
    private int selectedIndex = 0;
    private Button[] menuButtons;
    
    
    // Logo animation
    private float logoAnimationTime = 0.0f;
    private float logoAnimationOffsetY = 0.0f; // Vertical floating offset
    private float logoAnimationOffsetX = 0.0f; // Horizontal swaying offset
    private final float LOGO_ANIMATION_SPEED = 1.0f; // Speed of the floating animation (half the original speed)
    private final float LOGO_ANIMATION_AMPLITUDE_Y = 10.0f; // How far up and down it moves
    private final float LOGO_ANIMATION_AMPLITUDE_X = 3.0f; // How far left and right it sways
    
    @Override
    public int getPriority() {
        return 1; // Lower priority than splash screen
    }
    
    @Override
    public void onEnter() {
        // Main menu initialization
        loadBackgroundImage();
        loadLogoImage();
        loadBackgroundMusic();
        
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
        Font buttonFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 18);
        startButton.setFont(buttonFont);
        settingsButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        
        // Set button actions
        startButton.setOnClick(() -> {
            // State change will automatically trigger fade transition
            Engine.instance().stateProcessor.setState(new CharacterSelectState());
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
    
    private void loadBackgroundImage() {
        try {
            File imageFile = new File("res/main-menu-bg.jpg");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
                imageLoaded = true;
            } else {
                System.err.println("Background image not found: " + imageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadLogoImage() {
        try {
            File imageFile = new File("res/game-logo-white.png");
            if (imageFile.exists()) {
                logoImage = ImageIO.read(imageFile);
                logoLoaded = true;
            } else {
                System.err.println("Logo image not found: " + imageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error loading logo image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadBackgroundMusic() {
        try {
            File audioFile = new File("sound/Barghest_Fell.wav");
            if (audioFile.exists()) {
                Engine.instance().audioManager.loadAudio("background_music", audioFile.getAbsolutePath());
                Engine.instance().audioManager.playMusicWithFade("background_music", 0.3f); // Set volume to 30% with fade
            } else {
                System.err.println("Background music file not found: " + audioFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onExit() {
        // Don't stop music here - let the new state handle music transitions
        // The centralized audio manager will handle smooth transitions
    }
    
    @Override
    public void tick() {
        // Update logo animation
        logoAnimationTime += Engine.instance().getDeltaSeconds() * LOGO_ANIMATION_SPEED;
        
        // Calculate smooth floating offset using simpler, gentler wave patterns
        // Vertical movement (up/down) - simple sine wave for smooth motion
        logoAnimationOffsetY = (float) Math.sin(logoAnimationTime) * LOGO_ANIMATION_AMPLITUDE_Y;
        
        // Horizontal movement (left/right) - gentle sway with different frequency
        logoAnimationOffsetX = (float) Math.sin(logoAnimationTime * 0.6 + 1.2) * LOGO_ANIMATION_AMPLITUDE_X;
        
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
        
        // Draw background image if loaded, otherwise fallback to solid color
        if (imageLoaded && backgroundImage != null) {
            // Scale image to fill entire window (stretch to fit)
            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();
            
            // Calculate scaling factors to fill the window
            float scaleX = (float) windowWidth / imageWidth;
            float scaleY = (float) windowHeight / imageHeight;
            
            // Use the larger scale to fill the entire window
            float scale = Math.max(scaleX, scaleY);
            
            int scaledWidth = (int) (imageWidth * scale);
            int scaledHeight = (int) (imageHeight * scale);
            
            // Center the image (it will be larger than window, so we center it)
            int imageX = (windowWidth - scaledWidth) / 2;
            int imageY = (windowHeight - scaledHeight) / 2;
            
            g.drawImage(backgroundImage, imageX, imageY, scaledWidth, scaledHeight, null);
        } else {
            // Fallback to solid color background
            g.setColor(new Color(20, 20, 40)); // Dark blue background
            g.fillRect(0, 0, windowWidth, windowHeight);
        }
        
        // Calculate center position
        int centerX = windowWidth / 5;
        int centerY = windowHeight / 2;
        
        // Draw main menu logo
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw logo if loaded, otherwise fallback to text
        if (logoLoaded && logoImage != null) {
            // Calculate logo size based on window size
            int maxLogoWidth = windowWidth / 3;
            int maxLogoHeight = windowHeight / 4;
            
            // Calculate scaling to fit within bounds while maintaining aspect ratio
            int logoWidth = logoImage.getWidth();
            int logoHeight = logoImage.getHeight();
            
            float scaleX = (float) maxLogoWidth / logoWidth;
            float scaleY = (float) maxLogoHeight / logoHeight;
            float scale = Math.min(scaleX, scaleY);
            
            int scaledLogoWidth = (int) (logoWidth * scale);
            int scaledLogoHeight = (int) (logoHeight * scale);
            
            // Position logo with floating animation (both X and Y movement)
            int baseLogoX = centerX - scaledLogoWidth / 2;
            int baseLogoY = centerY / 4;
            // Use pre-calculated smooth floating offsets for both X and Y
            int logoX = baseLogoX + Math.round(logoAnimationOffsetX);
            int logoY = baseLogoY + Math.round(logoAnimationOffsetY);
            
            g2d.drawImage(logoImage, logoX, logoY, scaledLogoWidth, scaledLogoHeight, null);
        } else {
            // Fallback to text title if logo fails to load
            Font titleFont = Engine.instance().resources.getFont("Milonga/Milonga-Regular.ttf", 48);
            g2d.setColor(Color.WHITE);
            g2d.setFont(titleFont);
            String title = "Great Dreamer Stories";
            int baseTitleX = 50;
            int baseTitleY = centerY / 4;
            // Use pre-calculated smooth floating offsets for both X and Y (text fallback)
            int titleX = baseTitleX + Math.round(logoAnimationOffsetX);
            int titleY = baseTitleY + Math.round(logoAnimationOffsetY);
            g2d.drawString(title, titleX, titleY);
        }
        
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
        Font infoFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(infoFont);
        String instructions = "Use mouse to click or arrow keys + Enter to navigate";
        FontMetrics instMetrics = g2d.getFontMetrics();
        int instX = Math.max(25, centerX - instMetrics.stringWidth(instructions) / 2);
        int instY = centerY + 175;
        g2d.drawString(instructions, instX, instY);

        // Draw version
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(infoFont);
        String version = Game.getInstance().getVersion();
        String versionText = "Version " + (version != null ? version : "Unknown");
        FontMetrics versionMetrics = g2d.getFontMetrics();
        int versionX = windowWidth - versionMetrics.stringWidth(versionText) - 15;
        int versionY = windowHeight - 15;
        g2d.drawString(versionText, versionX, versionY);
    }
}
