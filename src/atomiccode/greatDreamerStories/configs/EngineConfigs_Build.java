package atomiccode.greatDreamerStories.configs;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.greatDreamerStories.states.GameState;

public class EngineConfigs_Build extends EngineConfigs {

    public EngineConfigs_Build(String version) {
        windowTitle = "Great Dreamer Stories - v"+version;
        vsync = true;
        msaa = true;
        fps = 60;
        uiSize = 1;
        fullscreen = true;
        windowMinHeight = 0;
        windowMinWidth = 0;
        windowWidth = 1280;
        windowHeight = 720;
        language = "PL";
//        resources.colours.addPalette(loadColourPalette());
        defaultState = GameState.NORMAL;
        initialState = GameState.SPLASH_SCREEN;
//        debugger = new DebuggerConfigs();
    }
}
