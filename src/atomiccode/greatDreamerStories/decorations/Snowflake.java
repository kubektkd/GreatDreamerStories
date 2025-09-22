package atomiccode.greatDreamerStories.decorations;

import java.awt.*;

public class Snowflake {
    private float x, y;
    private float velocityX, velocityY;
    private float rotation;
    private float rotationSpeed;
    private float size;
    private float alpha;
    private float fadeSpeed;
    private boolean isActive;
    
    // Snowflake appearance constants
    private static final float MIN_SIZE = 2.0f;
    private static final float MAX_SIZE = 6.0f;
    private static final float MIN_VELOCITY_Y = 20.0f;
    private static final float MAX_VELOCITY_Y = 60.0f;
    private static final float MIN_VELOCITY_X = -10.0f;
    private static final float MAX_VELOCITY_X = 10.0f;
    private static final float MIN_ROTATION_SPEED = -2.0f;
    private static final float MAX_ROTATION_SPEED = 2.0f;
    private static final float FADE_SPEED = 0.5f;
    
    public Snowflake(float x, float y) {
        this.x = x;
        this.y = y;
        
        // Randomize properties
        this.velocityX = MIN_VELOCITY_X + (float) Math.random() * (MAX_VELOCITY_X - MIN_VELOCITY_X);
        this.velocityY = MIN_VELOCITY_Y + (float) Math.random() * (MAX_VELOCITY_Y - MIN_VELOCITY_Y);
        this.rotation = (float) (Math.random() * 360);
        this.rotationSpeed = MIN_ROTATION_SPEED + (float) Math.random() * (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED);
        this.size = MIN_SIZE + (float) Math.random() * (MAX_SIZE - MIN_SIZE);
        this.alpha = 0.8f + (float) Math.random() * 0.2f; // Start with some variation in opacity
        this.fadeSpeed = FADE_SPEED;
        this.isActive = true;
    }
    
    public void update(float deltaTime) {
        if (!isActive) return;
        
        // Update position
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
        
        // Update rotation
        rotation += rotationSpeed * deltaTime;
        if (rotation > 360) rotation -= 360;
        if (rotation < 0) rotation += 360;
        
        // Add slight swaying motion
        velocityX += (float) (Math.sin(y * 0.01) * 5.0 * deltaTime);
        velocityX = Math.max(MIN_VELOCITY_X, Math.min(MAX_VELOCITY_X, velocityX));
        
        // Fade out when near bottom
        if (y > 400) { // Start fading when near bottom
            alpha -= fadeSpeed * deltaTime;
            if (alpha <= 0) {
                isActive = false;
            }
        }
    }
    
    public void render(Graphics2D g2d) {
        if (!isActive || alpha <= 0) return;
        
        // Save current transform
        Graphics2D g = (Graphics2D) g2d.create();
        
        // Set alpha for transparency
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        // Move to snowflake position and rotate
        g.translate(x, y);
        g.rotate(Math.toRadians(rotation));
        
        // Draw snowflake as a simple 6-pointed star
        g.setColor(Color.WHITE);
        drawSnowflake(g, size);
        
        // Restore transform
        g.dispose();
    }
    
    private void drawSnowflake(Graphics2D g, float size) {
        // Draw a simple 6-pointed snowflake
        int[] xPoints = new int[12];
        int[] yPoints = new int[12];
        
        float halfSize = size / 2;
        
        // Create 6-pointed star pattern
        for (int i = 0; i < 6; i++) {
            double angle1 = i * Math.PI / 3;
            double angle2 = (i + 0.5) * Math.PI / 3;
            
            xPoints[i * 2] = (int) (Math.cos(angle1) * size);
            yPoints[i * 2] = (int) (Math.sin(angle1) * size);
            
            xPoints[i * 2 + 1] = (int) (Math.cos(angle2) * halfSize);
            yPoints[i * 2 + 1] = (int) (Math.sin(angle2) * halfSize);
        }
        
        g.fillPolygon(xPoints, yPoints, 12);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public float getY() {
        return y;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
