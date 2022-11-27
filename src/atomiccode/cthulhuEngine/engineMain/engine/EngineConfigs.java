package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.EmptyState;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;

public class EngineConfigs {

    public String language = "EN";

    public int windowWidth = 1280;
    public int windowHeight = 720;

    public int windowMinWidth = 600;
    public int windowMinHeight = 350;
    public float uiSize = 1;

    public boolean fullscreen = false;
    public String windowTitle = "The Game";
    public int fps = 60;
    public boolean vsync = true;
    public boolean msaa = true;

//    public UiResources uiResources = new UiResources();
//
//    public Resources resources = new Resources();
//
    public State initialState = new EmptyState();
    public State defaultState = new EmptyState();
//
//    public DebugFormat debugger = null;

    public static EngineConfigs getDefaultConfigs(){
        return new EngineConfigs();
    }

    protected EngineConfigs() {}
}
