package atomiccode.cthulhuEngine.engineMain.engine;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.EmptyState;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window.Mode;
import atomiccode.greatDreamerStories.configs.Resolution;

public class EngineConfigs {

    // Default config values
    public Resolution resolution = Resolution.HD;
    public Resolution minResolution = Resolution.SMALL;
    public float uiSize = 1;

    public String windowTitle = "CthulhuEngine";
    public String language = "EN";
    /** Overridden by game bootstrap (e.g. {@code EngineConfigs_Build}); placeholder for generic configs. */
    public String version = "development";

    public Mode windowMode = Mode.WINDOWED;
    /** Foreground FPS cap for LibGDX; 0 = uncapped (vsync still limits swap when enabled). Non-zero caps e.g. for testing. */
    public int fps = 0;
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
