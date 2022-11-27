package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.timing.FrameTimer;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

public class EngineCreator {

    protected static Engine init(EngineConfigs configs) {
//        checkResFolderExists();
        Engine engine = initEngineInstance(configs);
        return engine;
    }

    private static void checkResFolderExists() {
        if (EngineFiles.RES_FOLDER.exists())
            return;
        System.err.println("Can't init engine - res folder not found.");
        System.exit(-1);
    }

    private static Engine initEngineInstance(EngineConfigs configs) {
        Window window = new Window(configs.windowTitle, configs.windowWidth, configs.windowHeight);
        Mouse mouseManager = new Mouse();
        Keyboard keyboardManager = new Keyboard();
        addInputListeners(window, mouseManager, keyboardManager);
        FrameTimer timer = new FrameTimer(configs.fps);
        StateManager stateManager = new StateManager(configs.defaultState, configs.initialState);
//        return new Engine(window, mouseManager, keyboardManager, timer, stateManager, configs.resources);
        return new Engine(window, mouseManager, keyboardManager, timer, stateManager);
    }

    private static void addInputListeners(Window window, Mouse mouseManager, Keyboard keyManager) {
        window.getFrame().addKeyListener(keyManager);
        window.getFrame().addMouseListener(mouseManager);
        window.getFrame().addMouseMotionListener(mouseManager);
        window.getCanvas().addMouseListener(mouseManager);
        window.getCanvas().addMouseMotionListener(mouseManager);
    }
}
