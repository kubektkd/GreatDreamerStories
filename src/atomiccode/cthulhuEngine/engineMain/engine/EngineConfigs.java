package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.EmptyState;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window.Mode;
import atomiccode.greatDreamerStories.configs.Resolution;

public class EngineConfigs {

    public Resolution resolution = Resolution.HD;
    public Resolution minResolution = Resolution.SMALL;
    public float uiSize = 1;

    public String windowTitle = "CthulhuEngine";
    public String language = "EN";

    public Mode windowMode = Mode.WINDOWED;
    public int fps = 60;
    public boolean vsync = true;
    public boolean msaa = true;

    // public UiResources uiResources = new UiResources();
    // public Resources resources = new Resources();
    
    public State initialState = new EmptyState();
    public State defaultState = new EmptyState();

    // public DebugFormat debugger = null;

    public static EngineConfigs getDefaultConfigs(){
        return new EngineConfigs();
    }

    protected EngineConfigs() {}
}
