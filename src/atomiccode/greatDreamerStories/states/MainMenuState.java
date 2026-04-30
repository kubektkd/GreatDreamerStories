package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.EngineFiles;
import atomiccode.greatDreamerStories.Game;
import atomiccode.cthulhuEngine.ui.Button;
import atomiccode.greatDreamerStories.decorations.Snowflake;
import atomiccode.greatDreamerStories.decorations.Mist;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainMenuState implements State {
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean imageLoaded = false;
    
    // Logo images
    private BufferedImage logoImage;
    private boolean logoLoaded = false;
    private BufferedImage cocLogoImage;
    private boolean cocLogoLoaded = false;
    
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
    
    // Snowflake animation
    private ArrayList<Snowflake> snowflakes;
    private float snowflakeSpawnTimer = 0.0f;
    private final float SNOWFLAKE_SPAWN_INTERVAL = 0.5f; // Spawn a snowflake every 0.5 seconds
    private final int MAX_SNOWFLAKES = 20; // Maximum number of snowflakes on screen
    
    // Mist animation
    private ArrayList<Mist> mistParticles;
    private float mistSpawnTimer = 0.0f;
    private final float MIST_SPAWN_INTERVAL = 2.0f; // Spawn mist every 2 seconds
    private final int MAX_MIST_PARTICLES = 25; // Maximum number of mist particles on screen
    
    @Override
    public int getPriority() {
        return 1; // Lower priority than splash screen
    }
    
    @Override
    public void onEnter() {
        // Main menu initialization
        loadBackgroundImage();
        loadLogoImages();
        loadBackgroundMusic();
        
        // Initialize snowflake system
        snowflakes = new ArrayList<>();
        
        // Initialize mist system
        mistParticles = new ArrayList<>();
        
        // Spawn initial mist particles across the window width
        spawnInitialMist();
        
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
        Font buttonFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 18);
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
        });
        
        exitButton.setOnClick(() -> {
            Engine.instance().requestClose();
        });
        
        // Create button array for keyboard navigation
        menuButtons = new Button[]{startButton, settingsButton, exitButton};
        
        // Initialize selection state - first button should be selected by default
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i].setSelected(i == selectedIndex);
        }
    }
    
    private void loadBackgroundImage() {
        try {
            File imageFile = EngineFiles.getResourceFile("backgrounds/menu/main-menu-bg.jpg");
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
    
    private void loadLogoImages() {
        try {
            File imageFile = EngineFiles.getResourceFile("logos/game-logo-white.png");
            if (imageFile.exists()) {
                logoImage = ImageIO.read(imageFile);
                logoLoaded = true;
            } else {
                System.err.println("Logo image not found: " + imageFile.getAbsolutePath());
            }
            imageFile = EngineFiles.getResourceFile("logos/coc-logo-white.png");
            if (imageFile.exists()) {
                cocLogoImage = ImageIO.read(imageFile);
                cocLogoLoaded = true;
            } else {
                System.err.println("CoC logo image not found: " + imageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error loading logo image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadBackgroundMusic() {
        try {
            File audioFile = EngineFiles.getResourceFile("sound/Barghest_Fell.ogg");
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
        float deltaTime = Engine.instance().getDeltaSeconds();
        
        // Update logo animation
        logoAnimationTime += deltaTime * LOGO_ANIMATION_SPEED;
        
        // Calculate smooth floating offset using simpler, gentler wave patterns
        // Vertical movement (up/down) - simple sine wave for smooth motion
        logoAnimationOffsetY = (float) Math.sin(logoAnimationTime) * LOGO_ANIMATION_AMPLITUDE_Y;
        
        // Horizontal movement (left/right) - gentle sway with different frequency
        logoAnimationOffsetX = (float) Math.sin(logoAnimationTime * 0.6 + 1.2) * LOGO_ANIMATION_AMPLITUDE_X;
        
        // Update snowflake system
        updateSnowflakes(deltaTime);
        
        // Update mist system
        updateMist(deltaTime);
        
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
    
    private void updateSnowflakes(float deltaTime) {
        // Spawn new snowflakes
        snowflakeSpawnTimer += deltaTime;
        if (snowflakeSpawnTimer >= SNOWFLAKE_SPAWN_INTERVAL && snowflakes.size() < MAX_SNOWFLAKES) {
            int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
            float spawnX = (float) (Math.random() * windowWidth);
            float spawnY = -20.0f; // Start above the screen
            snowflakes.add(new Snowflake(spawnX, spawnY));
            snowflakeSpawnTimer = 0.0f;
        }
        
        // Update existing snowflakes
        Iterator<Snowflake> iterator = snowflakes.iterator();
        while (iterator.hasNext()) {
            Snowflake snowflake = iterator.next();
            snowflake.update(deltaTime);
            
            // Remove inactive snowflakes
            if (!snowflake.isActive()) {
                iterator.remove();
            }
        }
    }
    
    private void spawnInitialMist() {
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Spawn 15-20 initial mist particles across the window width
        int initialMistCount = 15 + (int) (Math.random() * 6); // 15-20 particles
        
        for (int i = 0; i < initialMistCount; i++) {
            // Distribute mist particles with more concentration in the center
            float mistX;
            float random = (float) Math.random();
            
            if (random < 0.6f) {
                // 60% of mist in the center area (smaller zone to account for large image sizes)
                float centerStart = windowWidth * 0.4f;  // Start closer to center
                float centerEnd = windowWidth * 0.6f;    // End closer to center
                mistX = centerStart + (float) (Math.random() * (centerEnd - centerStart));
            } else if (random < 0.8f) {
                // 20% of mist in the left area (accounting for image size)
                float leftEnd = windowWidth * 0.25f;  // Smaller left zone
                mistX = (float) (Math.random() * leftEnd);
            } else {
                // 20% of mist in the right area (accounting for image size)
                float rightStart = windowWidth * 0.75f;  // Smaller right zone
                mistX = rightStart + (float) (Math.random() * (windowWidth - rightStart));
            }
            
            // Position mist in the bottom third of the screen with some variation
            float mistY = windowHeight - 100.0f + (float) (Math.random() * 100.0f);
            
            // Create mist particle
            Mist mist = new Mist(mistX, mistY);
            mistParticles.add(mist);
        }
    }
    
    private void updateMist(float deltaTime) {
        // Spawn new mist particles (less frequent since we have initial mist)
        mistSpawnTimer += deltaTime;
        if (mistSpawnTimer >= MIST_SPAWN_INTERVAL && mistParticles.size() < MAX_MIST_PARTICLES) {
            int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
            int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
            
            // Spawn mist from both sides randomly
            float spawnX;
            if (Math.random() < 0.5) {
                spawnX = 0.0f; // Start from the left side
            } else {
                spawnX = windowWidth; // Start from the right side
            }
            float spawnY = windowHeight - 50.0f + (float) (Math.random() * 50.0f); // Bottom area with variation
            mistParticles.add(new Mist(spawnX, spawnY));
            mistSpawnTimer = 0.0f;
        }
        
        // Update existing mist particles
        Iterator<Mist> iterator = mistParticles.iterator();
        while (iterator.hasNext()) {
            Mist mist = iterator.next();
            mist.update(deltaTime);
            
            // Remove inactive mist particles
            if (!mist.isActive()) {
                iterator.remove();
            }
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

        // Draw CoC logo
        if (cocLogoLoaded && cocLogoImage != null) {
            int logoWidth = cocLogoImage.getWidth();
            int logoHeight = cocLogoImage.getHeight();

            float scale = (float) 0.5f;

            int scaledLogoWidth = (int) (logoWidth * scale);
            int scaledLogoHeight = (int) (logoHeight * scale);

            g2d.drawImage(cocLogoImage, 20, windowHeight - scaledLogoHeight - 20, scaledLogoWidth, scaledLogoHeight, null);
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

        // Draw version
        Font infoFont = Engine.instance().resources.getFont("Special_Elite/SpecialElite-Regular.ttf", 14);
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(infoFont);
        String version = Game.getInstance().getVersion();
        String versionText = "Version " + (version != null ? version : "Unknown");
        FontMetrics versionMetrics = g2d.getFontMetrics();
        int versionX = windowWidth - versionMetrics.stringWidth(versionText) - 15;
        int versionY = windowHeight - 15;
        g2d.drawString(versionText, versionX, versionY);
        
        // Render snowflakes
        renderSnowflakes(g2d);
        
        // Render mist
        renderMist(g2d);
    }
    
    private void renderSnowflakes(Graphics2D g2d) {
        // Enable antialiasing for smooth snowflake rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render all active snowflakes
        for (Snowflake snowflake : snowflakes) {
            snowflake.render(g2d);
        }
    }
    
    private void renderMist(Graphics2D g2d) {
        // Enable antialiasing for smooth mist rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        // Render all active mist particles
        for (Mist mist : mistParticles) {
            mist.render(g2d);
        }
    }
}
