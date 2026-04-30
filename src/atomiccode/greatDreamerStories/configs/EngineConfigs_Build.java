package atomiccode.greatDreamerStories.configs;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window.Mode;
import atomiccode.greatDreamerStories.states.SplashState;
import atomiccode.greatDreamerStories.states.MainMenuState;

public class EngineConfigs_Build extends EngineConfigs {

    public EngineConfigs_Build(String version) {
        this.version = version;
        windowTitle = "Great Dreamer Stories - v" + version;
        resolution = Resolution.HD;
        minResolution = Resolution.HD;
        windowMode = Mode.WINDOWED;
        language = "PL";
        defaultState = new MainMenuState();
        initialState = new SplashState();
        // resources.colours.addPalette(loadColourPalette());
        // debugger = new DebuggerConfigs();
    }
}
