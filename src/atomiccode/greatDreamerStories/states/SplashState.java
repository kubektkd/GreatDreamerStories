package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SplashState implements State {
    
    private static final int FADE_IN_DURATION_MS = 500; // 1 second fade in
    private static final int VISIBLE_DURATION_MS = 3000; // 3 seconds fully visible
    private static final int FADE_OUT_DURATION_MS = 500; // 1 second fade out
    private static final int SINGLE_SPLASH_DURATION_MS = FADE_IN_DURATION_MS + VISIBLE_DURATION_MS + FADE_OUT_DURATION_MS; // 5 seconds per splash
    
    // Multiple splash images
    private String[] splashImagePaths = {"res/ac-logo.png", "res/splashscreen.jpg"};
    private int currentImageIndex = 0;
    private long imageLoadTime;
    private boolean hasStarted = false;
    private boolean isFadingOut = false;
    private long fadeStartTime;
    private BufferedImage splashImage;
    private boolean imageLoaded = false;
    
    @Override
    public int getPriority() {
        return 0; // High priority for splash screen
    }
    
    @Override
    public void onEnter() {
        hasStarted = true;
        loadSplashImage();
    }
    
    private void loadSplashImage() {
        if (currentImageIndex >= splashImagePaths.length) {
            return; // No more images to load
        }
        
        try {
            String imagePath = splashImagePaths[currentImageIndex];
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                splashImage = ImageIO.read(imageFile);
                imageLoaded = true;
                imageLoadTime = System.currentTimeMillis();
            } else {
                System.err.println("Splash image not found: " + imageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error loading splash image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onExit() {
        hasStarted = false;
    }
    
    @Override
    public void tick() {
        // No deterministic logic needed for splash screen
    }
    
    @Override
    public void update() {
        if (hasStarted && imageLoaded) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - imageLoadTime; // Use image load time as reference
            
            // Start fade out after fade in + visible duration
            if (elapsedTime >= FADE_IN_DURATION_MS + VISIBLE_DURATION_MS && !isFadingOut) {
                isFadingOut = true;
                fadeStartTime = currentTime;
            }
            
            // Check if current splash is complete
            if (elapsedTime >= SINGLE_SPLASH_DURATION_MS) {
                // Move to next image or finish
                currentImageIndex++;
                if (currentImageIndex < splashImagePaths.length) {
                    // Load next image
                    imageLoaded = false;
                    isFadingOut = false;
                    loadSplashImage();
                } else {
                    // All splash screens complete, transition to main menu
                    Engine.instance().stateManager.suggestState(new MainMenuState(), false);
                }
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();
        
        // Try to load image if not already loaded
        if (!imageLoaded) {
            loadSplashImage();
        }
        
        // Draw splash image if loaded
        if (imageLoaded && splashImage != null) {
            
            // Calculate fade alpha based on time since image was loaded
            float alpha = 1.0f;
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - imageLoadTime; // Use image load time as reference
            
            if (elapsedTime < FADE_IN_DURATION_MS) {
                // Fade in effect - smoothly transition from 0 to 1
                alpha = (float) elapsedTime / FADE_IN_DURATION_MS;
            } else if (elapsedTime < FADE_IN_DURATION_MS + VISIBLE_DURATION_MS) {
                // Fully visible during the visible duration (3 seconds)
                alpha = 1.0f;
            } else if (isFadingOut) {
                // Fade out effect - smoothly transition from 1 to 0
                long fadeElapsed = currentTime - fadeStartTime;
                alpha = Math.max(0.0f, 1.0f - (float) fadeElapsed / FADE_OUT_DURATION_MS);
            } else {
                // Fallback to fully visible
                alpha = 1.0f;
            }
            
            // Draw black background first to ensure proper fade-in
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, windowWidth, windowHeight);
            
            // Scale image to fill entire window (stretch to fit)
            int imageWidth = splashImage.getWidth();
            int imageHeight = splashImage.getHeight();
            
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
            
            // Draw the scaled image with alpha
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(splashImage, imageX, imageY, scaledWidth, scaledHeight, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            // Show loading screen while image is being loaded
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, windowWidth, windowHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics metrics = g.getFontMetrics();
            String message = "Great Dreamer Stories";
            String loadingMessage = "Loading...";
            int x = (windowWidth - metrics.stringWidth(message)) / 2;
            int y = windowHeight / 2;
            g.drawString(message, x, y);
            g.drawString(loadingMessage, x + 75, y + 35);
        }
    }
}
