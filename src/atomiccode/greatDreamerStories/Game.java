package atomiccode.greatDreamerStories;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

/**
 * LibGDX application facade. LibGDX owns the low-level loop; cthulhuEngine
 * owns the reusable game-specific services and state processing above it.
 */
public class Game extends ApplicationAdapter {

    private static Game instance;
    private final EngineConfigs configs;
    private Engine engine;

    private Game(EngineConfigs configs) {
        this.configs = configs;
        instance = this;
    }

    public static void launch(EngineConfigs configs) {
        Lwjgl3ApplicationConfiguration appConfig = new Lwjgl3ApplicationConfiguration();
        appConfig.setTitle(configs.windowTitle);
        appConfig.setWindowedMode(configs.resolution.getWidth(), configs.resolution.getHeight());
        appConfig.setWindowSizeLimits(configs.minResolution.getWidth(), configs.minResolution.getHeight(), -1, -1);
        appConfig.useVsync(configs.vsync);
        appConfig.setForegroundFPS(configs.fps);
        appConfig.setResizable(true);

        if (configs.windowMode == Window.Mode.FULLSCREEN) {
            appConfig.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        } else if (configs.windowMode == Window.Mode.MAXIMIZED) {
            appConfig.setMaximized(true);
        }

        new Lwjgl3Application(new Game(configs), appConfig);
    }

    public static Game getInstance() {
        return instance;
    }

    @Override
    public void create() {
        engine = Engine.init(configs);
    }

    @Override
    public void render() {
        engine.update();
        engine.render();
    }

    @Override
    public void resize(int width, int height) {
        if (engine != null) {
            engine.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (engine != null) {
            engine.cleanup();
        }
    }

    public String getVersion() {
        return configs.version;
    }
}
