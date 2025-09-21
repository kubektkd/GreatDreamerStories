package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import java.awt.*;

public class FadeState implements State {
    
    public enum FadeType {
        FADE_IN,    // From transparent to opaque
        FADE_OUT    // From opaque to transparent
    }
    
    private final FadeType fadeType;
    public final Color fadeColor;
    public final float duration;
    private final State targetState;
    
    private float currentTime;
    private boolean isComplete;
    
    public FadeState(FadeType fadeType, Color fadeColor, float duration, State targetState) {
        this.fadeType = fadeType;
        this.fadeColor = fadeColor;
        this.duration = duration;
        this.targetState = targetState;
        this.currentTime = 0.0f;
        this.isComplete = false;
    }
    
    @Override
    public boolean isOpaque() {
        return false; // Allow states behind to render
    }
    
    @Override
    public boolean blocksUpdate() {
        return false; // Don't block updates of other states
    }
    
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE; // Always render on top
    }
    
    @Override
    public void onEnter() {
        currentTime = 0.0f;
        isComplete = false;
    }
    
    @Override
    public void onExit() {
        // Nothing special needed
    }
    
    @Override
    public void tick() {
        // No deterministic logic needed for fade
    }
    
    @Override
    public void update() {
        if (isComplete) return;
        
        // Use deltaTime from the Engine instead of hardcoded 60 FPS
        float deltaTime = Engine.instance().getDeltaSeconds();
        currentTime += deltaTime;
        
        if (currentTime >= duration) {
            currentTime = duration;
            isComplete = true;
        }
    }
    
    @Override
    public void render(Graphics g) {
        if (isComplete && fadeType == FadeType.FADE_OUT) {
            return; // Don't render anything when fade out is complete
        }
        
        float progress = currentTime / duration;
        if (progress > 1.0f) progress = 1.0f;
        
        float alpha;
        if (fadeType == FadeType.FADE_IN) {
            alpha = progress; // 0 to 1
        } else {
            alpha = 1.0f - progress; // 1 to 0
        }
        
        
        // Create color with alpha
        Color fadeColorWithAlpha = new Color(
            fadeColor.getRed(),
            fadeColor.getGreen(),
            fadeColor.getBlue(),
            (int)(alpha * 255)
        );
        
        g.setColor(fadeColorWithAlpha);
        
        // Get window dimensions from Engine instead of relying on clip bounds
        int width = Engine.instance().getWindow().getCanvas().getWidth();
        int height = Engine.instance().getWindow().getCanvas().getHeight();
        g.fillRect(0, 0, width, height);
    }
    
    public boolean isComplete() {
        return isComplete;
    }
    
    public State getTargetState() {
        return targetState;
    }
    
    public float getProgress() {
        return currentTime / duration;
    }
    
    public FadeType getFadeType() {
        return fadeType;
    }
}
