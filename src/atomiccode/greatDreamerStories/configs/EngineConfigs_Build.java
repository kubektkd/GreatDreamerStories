package atomiccode.greatDreamerStories.configs;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.greatDreamerStories.states.SplashState;
import atomiccode.greatDreamerStories.states.MainMenuState;

public class EngineConfigs_Build extends EngineConfigs {

    public EngineConfigs_Build(String version) {
        windowTitle = "Great Dreamer Stories - v"+version;
        minResolution = Resolution.SMALL;
        resolution = Resolution.HD;
        language = "PL";
        defaultState = new MainMenuState();
        initialState = new SplashState();
        // resources.colours.addPalette(loadColourPalette());
        // debugger = new DebuggerConfigs();
    }
}
