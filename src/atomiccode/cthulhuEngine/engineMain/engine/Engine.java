package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateProcessor;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.inputsOutputs.timing.FrameTimer;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static Engine currentInstance;
    private final Window window;
    public final Keyboard keyboard;
    public final Mouse mouse;
    // public final Resources resources;
    public final StateManager stateManager;
    private final StateProcessor stateProcessor;

    private final FrameTimer timer;

    private boolean closeFlag = false;

    private BufferStrategy bs;
    private Graphics g;

    // protected Engine(Window window, Mouse mouse, Keyboard keyboard, FrameTimer timer, StateManager stateManager, Resources resources) {
    protected Engine(Window window, Mouse mouse, Keyboard keyboard, FrameTimer timer, StateManager stateManager) {
        this.window = window;
        this.mouse = mouse;
        this.keyboard = keyboard;
        this.timer = timer;
        this.stateManager = stateManager;
        
        // Initialize StateProcessor with current state
        List<State> initialStates = new ArrayList<>();
        if (stateManager.getState() != null) {
            initialStates.add(stateManager.getState());
        }
        this.stateProcessor = new StateProcessor(initialStates);
        // this.resources = resources;
    }

    public static Engine instance() {
        if (currentInstance == null)
            return init();
        return currentInstance;
    }

    public void update() {
        // Ui.update(getDeltaSeconds());
        keyboard.update();
        mouse.update();
        // window.update();
        timer.update();
        
        // Update StateManager for state transitions
        stateManager.updateState();
        
        // Update current state
        if (stateManager.getState() != null) {
            stateManager.getState().tick();
            stateManager.getState().update();
        }
        
        // Update StateProcessor (handles fade animations only)
        stateProcessor.update(getDeltaSeconds());
    }

    public void render() {
        bs = window.getCanvas().getBufferStrategy();
        if (bs == null) {
            window.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();

        // Clear screen to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getCanvas().getWidth(), window.getCanvas().getHeight());

        // Begin drawing
        if (stateManager.getState() != null)
            stateManager.getState().render(g);
        
        // Render fade overlay if active
        Graphics2D g2d = (Graphics2D) g;
        stateProcessor.render(g2d);
        // End drawing

        bs.show();
        g.dispose();
    }

    public float getDeltaSeconds() {
        return timer.getDelta();
    }

    public float getCurrentTime() {
        return timer.getTime();
    }

    public boolean isCloseRequested() {
        return closeFlag;
    }

    public void requestClose() {
        this.closeFlag = true;
    }
    
    public Window getWindow() {
        return window;
    }
    
    /**
     * Start a fade transition to a new state
     * @param newState The state to transition to
     * @param fadeColor The color to fade to/from
     * @param fadeDuration Duration of the fade in seconds
     */
    public void startFadeTransition(State newState, Color fadeColor, float fadeDuration) {
        stateProcessor.startFadeTransition(newState, fadeColor, fadeDuration);
    }
    
    /**
     * Check if currently transitioning between states
     * @return true if a fade transition is in progress
     */
    public boolean isTransitioning() {
        return stateProcessor.isTransitioning();
    }
    
    /**
     * Get the current fade state (for debugging or advanced usage)
     * @return The current FadeState or null if not transitioning
     */
    public atomiccode.cthulhuEngine.inputsOutputs.stateControl.FadeState getCurrentFade() {
        return stateProcessor.getCurrentFade();
    }


    public static Engine init() {
        return init(EngineConfigs.getDefaultConfigs());
    }

    public static Engine init(EngineConfigs configs) {
        if (currentInstance != null) {
            throw new IllegalStateException("Engine has already been initialized!");
        }
        currentInstance = EngineCreator.init(configs);
        return currentInstance;
    }
}
