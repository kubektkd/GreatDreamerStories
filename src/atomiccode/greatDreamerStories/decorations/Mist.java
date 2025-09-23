package atomiccode.greatDreamerStories.decorations;

import atomiccode.cthulhuEngine.engineMain.engine.EngineFiles;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Mist {
    private float x, y;
    private float velocityX;
    private float size;
    private float alpha;
    private float alphaVariation;
    private float sizeVariation;
    private boolean isActive;
    private float animationTime;
    
    // Mist images
    private BufferedImage mistImage1;
    private BufferedImage mistImage2;
    private BufferedImage mistImage3;
    private BufferedImage mistImage4;
    private BufferedImage mistImage5;
    private BufferedImage mistImage6;
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
        
        // Load mist images
        loadMistImages();
        
        // Randomize properties for organic variation
        this.velocityX = MIN_VELOCITY_X + (float) Math.random() * (MAX_VELOCITY_X - MIN_VELOCITY_X);
        this.size = MIN_SIZE + (float) Math.random() * (MAX_SIZE - MIN_SIZE);
        this.alpha = MIN_ALPHA + (float) Math.random() * (MAX_ALPHA - MIN_ALPHA);
        this.alphaVariation = 0.05f + (float) Math.random() * 0.1f;
        this.sizeVariation = 0.1f + (float) Math.random() * 0.2f;
        this.isActive = true;
        this.animationTime = (float) (Math.random() * Math.PI * 2); // Random starting phase
        
        // Choose random mist image from all available images
        if (imageLoaded) {
            selectRandomMistImage();
            // Randomly determine if this mist should be flipped
            this.flipHorizontal = Math.random() < 0.3; // 30% chance to flip horizontally
            this.flipVertical = Math.random() < 0.2; // 20% chance to flip vertically
        }
    }
    
    public void update(float deltaTime) {
        if (!isActive) return;
        
        // Update position with horizontal movement
        x += velocityX * deltaTime;
        
        // Update animation time for organic pulsing
        animationTime += deltaTime * ANIMATION_SPEED;
        
        // Add subtle vertical drift
        y += (float) (Math.sin(animationTime * 0.3) * 0.5 * deltaTime);
        
        // Organic size pulsing (calculated in render method)
        // Organic alpha pulsing (calculated in render method)
        
        // Deactivate if moved too far off screen (handle both directions)
        if (x < -size * 2 || x > 1920 + size * 2) { // Extended bounds for bidirectional movement
            isActive = false;
        }
    }
    
    private void loadMistImages() {
        try {
            // Load all mist images
            mistImage1 = loadMistImage("misc/mist.png", "Mist image 1");
            mistImage2 = loadMistImage("misc/mist2.png", "Mist image 2");
            mistImage3 = loadMistImage("misc/mist3.png", "Mist image 3");
            mistImage4 = loadMistImage("misc/mist4.png", "Mist image 4");
            mistImage5 = loadMistImage("misc/mist5.png", "Mist image 5");
            mistImage6 = loadMistImage("misc/mist6.png", "Mist image 6");
            
            // Check if at least some images loaded successfully
            imageLoaded = (mistImage1 != null || mistImage2 != null || mistImage3 != null || 
                          mistImage4 != null || mistImage5 != null || mistImage6 != null);
            
        } catch (Exception e) {
            System.err.println("Error loading mist images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private BufferedImage loadMistImage(String path, String name) {
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
        // Create array of available mist images
        BufferedImage[] availableImages = {mistImage1, mistImage2, mistImage3, mistImage4, mistImage5, mistImage6};
        
        // Find all non-null images
        java.util.List<BufferedImage> validImages = new java.util.ArrayList<>();
        for (BufferedImage img : availableImages) {
            if (img != null) {
                validImages.add(img);
            }
        }
        
        // Select random image from available ones
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
        float currentAlpha = Math.max(0.05f, Math.min(0.4f, alpha + alphaPulse));
        
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
