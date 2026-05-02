package atomiccode.greatDreamerStories.decorations;

import atomiccode.cthulhuEngine.engineMain.engine.EngineFiles;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Mist {

    /** Shared mist textures — loaded once. Each Mist instance used to call ImageIO 6× on construction. */
    private static volatile BufferedImage[] SHARED_MIST_IMAGES;
    private static volatile boolean SHARED_LOAD_ATTEMPTED;
    private float x, y;
    private float velocityX;
    private float size;
    private float alpha;
    private float alphaVariation;
    private float sizeVariation;
    private boolean isActive;
    private float animationTime;
    
    // Transition state management
    private enum MistState {
        FADING_IN,
        ACTIVE,
        FADING_OUT
    }
    private MistState currentState;
    private float transitionAlpha;
    private float transitionTime;
    private static final float FADE_IN_DURATION = 2.0f;
    private static final float FADE_OUT_DURATION = 1.5f;
    
    private BufferedImage currentMistImage;
    private boolean imageLoaded = false;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    
    // Mist appearance constants
    private static final float MIN_SIZE = 350.0f;
    private static final float MAX_SIZE = 550.0f;
    private static final float MIN_VELOCITY_X = -16.0f;
    private static final float MAX_VELOCITY_X = 8.0f; // Allow positive velocity for rightward movement
    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 0.3f;
    private static final float ANIMATION_SPEED = 1.0f;
    
    public Mist(float x, float y) {
        this.x = x;
        this.y = y;
        
        ensureSharedMistImagesLoaded();
        
        // Randomize properties for organic variation
        this.velocityX = MIN_VELOCITY_X + (float) Math.random() * (MAX_VELOCITY_X - MIN_VELOCITY_X);
        this.size = MIN_SIZE + (float) Math.random() * (MAX_SIZE - MIN_SIZE);
        this.alpha = MIN_ALPHA + (float) Math.random() * (MAX_ALPHA - MIN_ALPHA);
        this.alphaVariation = 0.05f + (float) Math.random() * 0.1f;
        this.sizeVariation = 0.1f + (float) Math.random() * 0.2f;
        this.isActive = true;
        this.animationTime = (float) (Math.random() * Math.PI * 2); // Random starting phase
        
        // Initialize transition state
        this.currentState = MistState.FADING_IN;
        this.transitionAlpha = 0.0f;
        this.transitionTime = 0.0f;
        
        selectRandomMistImage();
        if (currentMistImage != null) {
            this.flipHorizontal = Math.random() < 0.3;
            this.flipVertical = Math.random() < 0.2;
        }
    }
    
    public void update(float deltaTime) {
        if (!isActive) return;
        
        // Update transition state
        updateTransitionState(deltaTime);
        
        // Update position with horizontal movement
        x += velocityX * deltaTime;
        
        // Update animation time for organic pulsing
        animationTime += deltaTime * ANIMATION_SPEED;
        
        // Add subtle vertical drift
        y += (float) (Math.sin(animationTime * 0.3) * 0.5 * deltaTime);
        
        // Organic size pulsing (calculated in render method)
        // Organic alpha pulsing (calculated in render method)
        
        // Start fade-out if moved too far off screen (handle both directions)
        if ((x < -size * 2 || x > 1920 + size * 2) && currentState != MistState.FADING_OUT) {
            startFadeOut();
        }
    }
    
    private void updateTransitionState(float deltaTime) {
        transitionTime += deltaTime;
        
        switch (currentState) {
            case FADING_IN:
                // Gradually increase alpha from 0 to 1 over FADE_IN_DURATION
                transitionAlpha = Math.min(1.0f, transitionTime / FADE_IN_DURATION);
                if (transitionTime >= FADE_IN_DURATION) {
                    currentState = MistState.ACTIVE;
                    transitionAlpha = 1.0f;
                }
                break;
                
            case ACTIVE:
                // Full alpha in active state
                transitionAlpha = 1.0f;
                break;
                
            case FADING_OUT:
                // Gradually decrease alpha from 1 to 0 over FADE_OUT_DURATION
                float fadeProgress = transitionTime / FADE_OUT_DURATION;
                transitionAlpha = Math.max(0.0f, 1.0f - fadeProgress);
                if (transitionTime >= FADE_OUT_DURATION) {
                    isActive = false;
                }
                break;
        }
    }
    
    public void startFadeOut() {
        if (currentState != MistState.FADING_OUT) {
            currentState = MistState.FADING_OUT;
            transitionTime = 0.0f;
        }
    }
    
    private static void ensureSharedMistImagesLoaded() {
        if (SHARED_LOAD_ATTEMPTED) {
            return;
        }
        synchronized (Mist.class) {
            if (SHARED_LOAD_ATTEMPTED) {
                return;
            }
            try {
                SHARED_MIST_IMAGES = new BufferedImage[]{
                    loadMistImage("misc/mist.png", "Mist image 1"),
                    loadMistImage("misc/mist2.png", "Mist image 2"),
                    loadMistImage("misc/mist3.png", "Mist image 3"),
                    loadMistImage("misc/mist4.png", "Mist image 4"),
                    loadMistImage("misc/mist5.png", "Mist image 5"),
                    loadMistImage("misc/mist6.png", "Mist image 6"),
                };
            } catch (Exception e) {
                System.err.println("Error loading mist images: " + e.getMessage());
                e.printStackTrace();
            }
            SHARED_LOAD_ATTEMPTED = true;
        }
    }
    
    private static BufferedImage loadMistImage(String path, String name) {
        try {
            File mistFile = EngineFiles.getResourceFile(path);
            if (mistFile.exists()) {
                return ImageIO.read(mistFile);
            } else {
                System.err.println(name + " not found: " + mistFile.getAbsolutePath());
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error loading " + name + ": " + e.getMessage());
            return null;
        }
    }
    
    private void selectRandomMistImage() {
        BufferedImage[] shared = SHARED_MIST_IMAGES;
        if (shared == null) {
            return;
        }

        java.util.List<BufferedImage> validImages = new java.util.ArrayList<>();
        for (BufferedImage img : shared) {
            if (img != null) {
                validImages.add(img);
            }
        }

        imageLoaded = !validImages.isEmpty();
        if (!validImages.isEmpty()) {
            int randomIndex = (int) (Math.random() * validImages.size());
            this.currentMistImage = validImages.get(randomIndex);
        }
    }
    
    public void render(Graphics2D g2d) {
        if (!isActive || !imageLoaded || currentMistImage == null) return;
        
        // Save current transform
        Graphics2D g = (Graphics2D) g2d.create();
        
        // Enable antialiasing for smooth mist
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Calculate organic size variation
        float sizePulse = (float) (Math.sin(animationTime) * sizeVariation);
        float currentSize = size + sizePulse;
        
        // Calculate organic alpha variation
        float alphaPulse = (float) (Math.sin(animationTime * 0.7 + 1.2) * alphaVariation);
        float baseAlpha = Math.max(0.05f, Math.min(0.4f, alpha + alphaPulse));
        
        // Apply transition alpha to the base alpha
        float currentAlpha = baseAlpha * transitionAlpha;
        
        // Set alpha for transparency
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
        
        // Calculate image dimensions
        int imageWidth = currentMistImage.getWidth();
        int imageHeight = currentMistImage.getHeight();
        
        // Scale image to desired size while maintaining aspect ratio
        float scale = currentSize / Math.max(imageWidth, imageHeight);
        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);
        
        // Calculate position (center the image)
        int drawX = (int) (x - scaledWidth / 2);
        int drawY = (int) (y - scaledHeight / 2);
        
        // Apply transformations if needed
        if (flipHorizontal || flipVertical) {
            // Calculate transformation matrix
            float scaleX = flipHorizontal ? -1.0f : 1.0f;
            float scaleY = flipVertical ? -1.0f : 1.0f;
            
            // Adjust position for flipping
            if (flipHorizontal) {
                drawX = (int) (x + scaledWidth / 2);
            }
            if (flipVertical) {
                drawY = (int) (y + scaledHeight / 2);
            }
            
            // Apply transformation
            g.scale(scaleX, scaleY);
        }
        
        // Draw the mist image
        g.drawImage(currentMistImage, drawX, drawY, scaledWidth, scaledHeight, null);
        
        // Restore transform
        g.dispose();
    }
    
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
}
