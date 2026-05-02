package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import com.badlogic.gdx.graphics.Color;

import atomiccode.cthulhuEngine.engineMain.engine.EngineRenderContext;

/**
 * Handles all state processing: rendering, updates, and transitions
 */
public class StateProcessor {

    private static final float DEFAULT_FADE_DURATION = 0.5f;
    private final StateManager stateManager;
    private FadeState currentFade;
    private State pendingState;
    private boolean isTransitioning;

    public StateProcessor(StateManager stateManager) {
        this.stateManager = stateManager;
        this.currentFade = null;
        this.pendingState = null;
        this.isTransitioning = false;
    }

    /**
     * Update all states and handle transitions
     */
    public void update(float deltaTime) {
        // Update fade animation if active
        if (currentFade != null) {
            currentFade.update();
            
            // Check if fade is complete
            if (currentFade.isComplete()) {
                if (currentFade.getFadeType() == FadeState.FadeType.FADE_IN) {
                    // Fade out complete, switch to pending state and start fade in
                    if (pendingState != null) {
                        switchToState(pendingState);
                        startFadeIn();
                    }
                } else {
                    // Fade in complete, transition finished
                    finishTransition();
                }
            }
        }
        
        // Update current state (allow updates during transitions for states like SplashState)
        State currentState = stateManager.getCurrentState();
        if (currentState != null) {
            currentState.tick();
            currentState.update();
        }
    }

    /**
     * Render all states with fade overlay
     */
    public void render(EngineRenderContext context) {
        // Render current state
        State currentState = stateManager.getCurrentState();
        if (currentState != null) {
            currentState.render(context);
        }
        
        // Render fade overlay if active
        if (currentFade != null) {
            currentFade.render(context);
        }
    }
    
    /**
     * Set state with automatic fade transition
     */
    public void setState(State newState) {
        if (isTransitioning) {
            return; // Ignore if already transitioning
        }
        
        if (newState == null || newState == stateManager.getCurrentState()) {
            return; // No change needed
        }
        
        // Start fade transition
        startFadeTransition(newState, Color.BLACK, DEFAULT_FADE_DURATION);
    }
    
    /**
     * Push state onto stack with fade transition
     */
    public void pushState(State newState) {
        if (isTransitioning) {
            return; // Ignore if already transitioning
        }
        
        if (newState == null) {
            return;
        }
        
        // Start fade transition
        startFadeTransition(newState, Color.BLACK, DEFAULT_FADE_DURATION);
    }
    
    /**
     * Pop state from stack with fade transition
     */
    public void popState() {
        if (isTransitioning) {
            return; // Ignore if already transitioning
        }
        
        if (stateManager.size() <= 1) {
            return; // Can't pop the last state
        }
        
        // Start fade transition to previous state
        State previousState = stateManager.getCurrentState();
        stateManager.popState(); // Remove current state
        State targetState = stateManager.getCurrentState();
        stateManager.pushState(previousState); // Restore for now
        
        startFadeTransition(targetState, Color.BLACK, DEFAULT_FADE_DURATION);
    }
    
    /**
     * Start a fade transition to a new state
     */
    private void startFadeTransition(State newState, Color fadeColor, float fadeDuration) {
        if (isTransitioning) {
            return; // Already transitioning
        }
        
        this.pendingState = newState;
        this.isTransitioning = true;
        startFadeOut(fadeColor, fadeDuration);
    }
    
    /**
     * Start fade out animation (current state to black)
     */
    private void startFadeOut(Color fadeColor, float duration) {
        currentFade = new FadeState(FadeState.FadeType.FADE_IN, fadeColor, duration, null);
        currentFade.onEnter();
    }
    
    /**
     * Start fade in animation (black to new state)
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
        if (newState != null) {
            stateManager.setState(newState);
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
     * Get the current state
     */
    public State getCurrentState() {
        return stateManager.getCurrentState();
    }

    public void resize(int width, int height) {
        State currentState = stateManager.getCurrentState();
        if (currentState != null) {
            currentState.resize(width, height);
        }
    }

    public void dispose() {
        State currentState = stateManager.getCurrentState();
        if (currentState != null) {
            currentState.dispose();
        }
    }
}