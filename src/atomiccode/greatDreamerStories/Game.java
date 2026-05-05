package atomiccode.greatDreamerStories;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;

import org.lwjgl.glfw.GLFW;

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

    /**
     * Fullscreen vs window vs maximized for UI highlighting ( GLFW maximized attrib when applicable).
     */
    public static Window.Mode getDetectedWindowMode() {
        if (Gdx.graphics.isFullscreen()) {
            return Window.Mode.FULLSCREEN;
        }
        if (Gdx.graphics instanceof Lwjgl3Graphics lg) {
            long handle = lg.getWindow().getWindowHandle();
            boolean maximized = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE;
            if (maximized) {
                return Window.Mode.MAXIMIZED;
            }
        }
        return Window.Mode.WINDOWED;
    }

    /**
     * Applies saved SFX multiplier and saved window mode after {@link Engine} exists (LibGDX graphics ready).
     */
    public static void applyPersistedDisplayAndAudio() {
        Engine engine = Engine.instance();
        engine.audioManager.setMasterSfxVolume(GamePreferences.getSfxVolume());
        Window.Mode saved = GamePreferences.getWindowMode();
        applyWindowModeToOs(saved, false);
    }

    /**
     * Persists and applies window mode. LWJGL3 only for maximized; other backends get windowed/fullscreen.
     */
    public static void setPersistedWindowMode(Window.Mode mode) {
        applyWindowModeToOs(mode, true);
    }

    /** Applies window mode for this session only (does not write to {@link GamePreferences}). */
    public static void setSessionWindowMode(Window.Mode mode) {
        applyWindowModeToOs(mode, false);
    }

    private static void applyWindowModeToOs(Window.Mode mode, boolean persist) {
        Engine engine = Engine.instance();
        EngineConfigs c = engine.getConfigs();

        switch (mode) {
            case WINDOWED:
                exitFullscreenPreserveWindow();
                restoreLwjglMaximizedThenSize(c);
                Gdx.graphics.setWindowedMode(c.resolution.getWidth(), c.resolution.getHeight());
                break;
            case MAXIMIZED:
                exitFullscreenPreserveWindow();
                if (Gdx.graphics instanceof Lwjgl3Graphics lg) {
                    Lwjgl3Window w = lg.getWindow();
                    w.restoreWindow();
                    Gdx.graphics.setWindowedMode(c.resolution.getWidth(), c.resolution.getHeight());
                    w.maximizeWindow();
                }
                break;
            case FULLSCREEN:
                restoreLwjglForModeChange();
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                break;
            default:
                break;
        }

        c.windowMode = mode;
        if (persist) {
            GamePreferences.setWindowMode(mode);
        }
    }

    private static void restoreLwjglForModeChange() {
        if (Gdx.graphics instanceof Lwjgl3Graphics lg) {
            lg.getWindow().restoreWindow();
        }
    }

    private static void restoreLwjglMaximizedThenSize(EngineConfigs c) {
        if (Gdx.graphics instanceof Lwjgl3Graphics lg) {
            lg.getWindow().restoreWindow();
            Gdx.graphics.setWindowedMode(c.resolution.getWidth(), c.resolution.getHeight());
        }
    }

    private static void exitFullscreenPreserveWindow() {
        if (!Gdx.graphics.isFullscreen()) {
            return;
        }
        EngineConfigs c = Engine.instance().getConfigs();
        Gdx.graphics.setWindowedMode(c.resolution.getWidth(), c.resolution.getHeight());
    }

    @Override
    public void create() {
        engine = Engine.init(configs);
        applyPersistedDisplayAndAudio();
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
