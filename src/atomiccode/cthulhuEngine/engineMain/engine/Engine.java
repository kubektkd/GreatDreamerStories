package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateProcessor;
import atomiccode.cthulhuEngine.inputsOutputs.timing.FrameTimer;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

public class Engine {

    private static Engine currentInstance;
    private final Window window;
    public final Keyboard keyboard;
    public final Mouse mouse;
    public final Resources resources;
    public final StateProcessor stateProcessor;
    public final AudioManager audioManager;

    private final FrameTimer timer;

    private boolean closeFlag = false;

    private BufferStrategy bs;
    private Graphics g;

    protected Engine(Window window, Mouse mouse, Keyboard keyboard, FrameTimer timer, StateManager stateManager, Resources resources) {
        this.window = window;
        this.mouse = mouse;
        this.keyboard = keyboard;
        this.timer = timer;
        
        // Initialize StateProcessor with StateManager
        this.stateProcessor = new StateProcessor(stateManager);
        this.resources = resources;
        this.audioManager = AudioManager.getInstance();
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
        
        // Update audio manager for smooth transitions
        audioManager.update(getDeltaSeconds());
        
        // Update StateProcessor (handles everything: states, updates, transitions)
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

        // Begin drawing - StateProcessor handles all rendering
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing globally for all states
        Resources.enableAntialiasing(g2d);
        
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
    
    public void cleanup() {
        if (audioManager != null) {
            audioManager.cleanup();
        }
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
