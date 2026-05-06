package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.EngineFiles;
import atomiccode.greatDreamerStories.i18n.GameTexts;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SplashState implements State {

    private static final Font SPLASH_LOADING_FONT = new Font("Arial", Font.BOLD, 24);

    private static final int SINGLE_SPLASH_DURATION_MS = 2000; // 2 seconds per splash
    private static final int FADE_DURATION_MS = 500; // 0.5 seconds for fade transitions
    
    // Multiple splash images
    private String[] splashImagePaths = {"splash/splashscreen-logo.jpg", "splash/splashscreen.jpg"};
    private int currentImageIndex = 0;
    private long imageLoadTime;
    private boolean hasStarted = false;
    private BufferedImage splashImage;
    private boolean imageLoaded = false;
    
    // Fade transition state
    private boolean isTransitioning = false;
    private boolean isFadingOut = false;
    private boolean isFadingIn = false;
    private long fadeStartTime;
    private boolean fadeInStarted = false;
    
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
            File imageFile = EngineFiles.getResourceFile(imagePath);
            if (imageFile.exists()) {
                splashImage = ImageIO.read(imageFile);
                imageLoaded = true;
                imageLoadTime = System.currentTimeMillis();
            } else {
                System.err.println("Splash image not found: " + imageFile.getAbsolutePath());
                splashImage = null;
                imageLoaded = true;
                imageLoadTime = System.currentTimeMillis();
            }
        } catch (IOException e) {
            System.err.println("Error loading splash image: " + e.getMessage());
            e.printStackTrace();
            splashImage = null;
            imageLoaded = true;
            imageLoadTime = System.currentTimeMillis();
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
        if (hasStarted && !imageLoaded && currentImageIndex < splashImagePaths.length) {
            loadSplashImage();
        }
        if (hasStarted && imageLoaded) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - imageLoadTime; // Use image load time as reference
            
            if (!isTransitioning) {
                // Check if current splash is complete
                if (elapsedTime >= SINGLE_SPLASH_DURATION_MS) {
                    // Start fade transition to next image
                    startImageTransition();
                }
            } else {
                // Handle fade transition
                handleFadeTransition(currentTime);
            }
        }
    }
    
    private void startImageTransition() {
        if (isTransitioning) return; // Already transitioning
        
        currentImageIndex++;
        if (currentImageIndex < splashImagePaths.length) {
            // Start fade out to switch to next image
            isTransitioning = true;
            isFadingOut = true;
            isFadingIn = false;
            fadeStartTime = System.currentTimeMillis();
        } else {
            // All splash screens complete, transition to main menu
            Engine.instance().stateProcessor.setState(new MainMenuState());
        }
    }
    
    private void handleFadeTransition(long currentTime) {
        if (isFadingOut) {
            long fadeElapsed = currentTime - fadeStartTime;
            
            // Check if fade out is complete
            if (fadeElapsed >= FADE_DURATION_MS) {
                // Fade out complete, switch image
                isFadingOut = false;
                isFadingIn = true;
                fadeInStarted = false; // Reset fade in start flag
                
                // Load next image
                imageLoaded = false;
                loadSplashImage();
            }
        } else if (isFadingIn) {
            // Only start fade in when image is actually loaded
            if (imageLoaded && !fadeInStarted) {
                // Start fade in now that image is loaded
                fadeStartTime = currentTime;
                fadeInStarted = true;
            }
            
            if (isFadingIn && fadeInStarted) {
                long fadeElapsed = currentTime - fadeStartTime;
                
                // Check if fade in is complete
                if (fadeElapsed >= FADE_DURATION_MS) {
                    // Fade in complete, transition finished
                    isFadingIn = false;
                    isTransitioning = false;
                    fadeInStarted = false;
                }
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Get window dimensions from the engine
        int windowWidth = Engine.instance().getWindow().getCanvas().getWidth();
        int windowHeight = Engine.instance().getWindow().getCanvas().getHeight();

        // Draw splash image if loaded
        if (imageLoaded && splashImage != null) {
            // Draw black background first
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
            
            // Calculate fade alpha
            float alpha = 1.0f;
            if (isTransitioning) {
                long currentTime = System.currentTimeMillis();
                long fadeElapsed = currentTime - fadeStartTime;
                float progress = Math.min(1.0f, (float) fadeElapsed / FADE_DURATION_MS);
                
                if (isFadingOut) {
                    alpha = 1.0f - progress; // Fade from 1 to 0
                } else if (isFadingIn) {
                    if (fadeInStarted) {
                        alpha = progress; // Fade from 0 to 1
                    } else {
                        alpha = 0.0f; // Stay black while waiting for image to load
                    }
                }
            }
            
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
            g.setFont(SPLASH_LOADING_FONT);
            FontMetrics metrics = g.getFontMetrics(SPLASH_LOADING_FONT);
            String message = GameTexts.tr("splash.title");
            String loadingMessage = GameTexts.tr("splash.loading");
            int x = (windowWidth - metrics.stringWidth(message)) / 2;
            int y = windowHeight / 2;
            g.drawString(message, x, y);
            g.drawString(loadingMessage, x + 75, y + 35);
        }
    }
}
