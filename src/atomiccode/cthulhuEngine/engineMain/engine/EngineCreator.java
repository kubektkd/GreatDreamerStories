package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.timing.FrameTimer;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

public class EngineCreator {

    protected static Engine init(EngineConfigs configs) {
        checkResFolderExists();
        return initEngineInstance(configs);
    }

    private static void checkResFolderExists() {
        if (!EngineFiles.RES_FOLDER.exists()) {
            throw new IllegalStateException("Resource folder not found: " + EngineFiles.RES_FOLDER.getAbsolutePath());
        }
    }

    private static Engine initEngineInstance(EngineConfigs configs) {
        if (configs == null) {
            throw new IllegalArgumentException("EngineConfigs cannot be null");
        }

        Window window = new Window(configs.windowTitle, configs.resolution.getWidth(), configs.resolution.getHeight(), configs.windowMode);
        Mouse mouseManager = new Mouse();
        Keyboard keyboardManager = new Keyboard();

        addInputListeners(window, mouseManager, keyboardManager);

        FrameTimer timer = new FrameTimer(configs.fps);
        StateManager stateManager = new StateManager(configs.defaultState, configs.initialState);
        Resources resources = new Resources();

        return new Engine(window, mouseManager, keyboardManager, timer, stateManager, resources);
    }

    private static void addInputListeners(Window window, Mouse mouseManager, Keyboard keyManager) {
        window.getFrame().addKeyListener(keyManager);
        window.getFrame().addMouseListener(mouseManager);
        window.getFrame().addMouseMotionListener(mouseManager);
        window.getCanvas().addMouseListener(mouseManager);
        window.getCanvas().addMouseMotionListener(mouseManager);
    }
}
