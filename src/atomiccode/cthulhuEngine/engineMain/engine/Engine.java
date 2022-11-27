package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.timing.FrameTimer;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Engine {

    private static Engine currentInstance;
    private final Window window;
    public final Keyboard keyboard;
    public final Mouse mouse;
//    public final Resources resources;
    public final StateManager stateManager;

    private final FrameTimer timer;

    private boolean closeFlag = false;

    private BufferStrategy bs;
    private Graphics g;

//    protected Engine(Window window, Mouse mouse, Keyboard keyboard, FrameTimer timer, StateManager stateManager, Resources resources) {
    protected Engine(Window window, Mouse mouse, Keyboard keyboard, FrameTimer timer, StateManager stateManager) {
        this.window = window;
        this.mouse = mouse;
        this.keyboard = keyboard;
        this.timer = timer;
        this.stateManager = stateManager;
//        this.resources = resources;
    }

    public static Engine instance() {
        if (currentInstance == null)
            return init();
        return currentInstance;
    }

    public void update() {
//        Ui.update(getDeltaSeconds());
        keyboard.update();
//        mouse.update();
//        window.update();
        timer.update();
        stateManager.updateState();
    }

    private void render() {
        bs = window.getCanvas().getBufferStrategy();
        if (bs == null) {
            window.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();

        // Clear screen
        g.clearRect(0, 0, window.getCanvas().getWidth(), window.getCanvas().getHeight());

        // Begin drawing
        if (stateManager.getState() != null)
            stateManager.getState().render(g);
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


    public static Engine init() {
        return init(EngineConfigs.getDefaultConfigs());
    }

    public static Engine init(EngineConfigs configs) {
        if (currentInstance != null) {
            System.err.println("Engine has already been initialized!");
        } else {
            currentInstance = EngineCreator.init(configs);
        }
        return currentInstance;
    }
}
