package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class StateProcessor {

    private FadeState currentFade;
    private State pendingState;
    private boolean isTransitioning;

    public StateProcessor(List<State> states) {
        this.currentFade = null;
        this.pendingState = null;
        this.isTransitioning = false;
    }

    public void update(float deltaTime) {
        // Update fade animation if active
        if (currentFade != null) {
            currentFade.update();
            
            // Check if fade is complete
            if (currentFade.isComplete()) {
                if (currentFade.getFadeType() == FadeState.FadeType.FADE_IN) {
                    // Fade out complete (used FADE_IN), switch to pending state and start fade in
                    if (pendingState != null) {
                        switchToState(pendingState);
                        startFadeIn();
                    }
                } else {
                    // Fade in complete (used FADE_OUT), transition finished
                    finishTransition();
                }
            }
        }
    }

    public void render(Graphics2D g) {
        // Render fade overlay if active
        if (currentFade != null) {
            currentFade.render(g);
        }
    }
    
    /**
     * Start a fade transition to a new state
     */
    public void startFadeTransition(State newState, Color fadeColor, float fadeDuration) {
        if (isTransitioning) {
            return; // Already transitioning
        }
        
        this.pendingState = newState;
        this.isTransitioning = true;
        startFadeOut(fadeColor, fadeDuration);
    }
    
    /**
     * Start fade out animation (current state to black)
     * Use FADE_IN to make overlay go from transparent to opaque (normal to black)
     */
    private void startFadeOut(Color fadeColor, float duration) {
        currentFade = new FadeState(FadeState.FadeType.FADE_IN, fadeColor, duration, null);
        currentFade.onEnter();
    }
    
    /**
     * Start fade in animation (black to new state)
     * Use FADE_OUT to make overlay go from opaque to transparent (black to normal)
     */
    private void startFadeIn() {
        if (currentFade != null) {
            Color fadeColor = currentFade.fadeColor;
            float duration = currentFade.duration;
            currentFade = new FadeState(FadeState.FadeType.FADE_OUT, fadeColor, duration, null);
            currentFade.onEnter();
        }
    }
    
    /**
     * Switch to a new state (called during fade out)
     */
    private void switchToState(State newState) {
        // Use StateManager to handle the state transition
        if (newState != null) {
            // Get the StateManager from Engine and switch states
            atomiccode.cthulhuEngine.engineMain.engine.Engine.instance().stateManager.setState(newState);
        }
    }
    
    /**
     * Finish the transition
     */
    private void finishTransition() {
        currentFade = null;
        pendingState = null;
        isTransitioning = false;
    }
    
    /**
     * Check if currently transitioning
     */
    public boolean isTransitioning() {
        return isTransitioning;
    }
    
    /**
     * Get the current fade state
     */
    public FadeState getCurrentFade() {
        return currentFade;
    }
    
}
