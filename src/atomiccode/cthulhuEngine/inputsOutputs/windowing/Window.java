package atomiccode.cthulhuEngine.inputsOutputs.windowing;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;

import java.awt.*;

public class Window {
    public enum Mode {
        FULLSCREEN,
        MAXIMIZED,
        WINDOWED
    }

    private final Canvas canvas;

    public Window(EngineConfigs configs) {
        canvas = new Canvas();
        canvas.setSize(configs.resolution.getWidth(), configs.resolution.getHeight());
    }

    public void setMode(Mode mode) {
        // Window mode is now controlled by LibGDX's Lwjgl3ApplicationConfiguration.
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void resize(int width, int height) {
        canvas.setSize(width, height);
    }

    public Object getFrame() {
        return null;
    }
}
