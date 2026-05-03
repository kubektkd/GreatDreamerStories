package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateManager;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.StateProcessor;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Keyboard;
import atomiccode.cthulhuEngine.inputsOutputs.userInputs.Mouse;
import atomiccode.cthulhuEngine.inputsOutputs.windowing.Window;

public class Engine {

    private static Engine currentInstance;

    public final Keyboard keyboard;
    public final Mouse mouse;
    public final Resources resources;
    public final StateProcessor stateProcessor;
    public final AudioManager audioManager;
    public final InputService input;

    private final EngineConfigs configs;
    private final Window window;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final EngineRenderContext renderContext;
    private float currentTime;
    private Cursor.SystemCursor lastUiSystemCursor = Cursor.SystemCursor.Arrow;

    protected Engine(EngineConfigs configs) {
        this.configs = configs;
        this.window = new Window(configs);
        this.input = new InputService();
        this.keyboard = new Keyboard(input);
        this.mouse = new Mouse(input);
        this.resources = new Resources();
        this.audioManager = AudioManager.getInstance();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.renderContext = new EngineRenderContext(batch, shapeRenderer, resources);

        StateManager stateManager = new StateManager(configs.defaultState, configs.initialState);
        this.stateProcessor = new StateProcessor(stateManager);
    }

    public static Engine instance() {
        if (currentInstance == null) {
            return init();
        }
        return currentInstance;
    }

    public void update() {
        float deltaSeconds = getDeltaSeconds();
        currentTime += deltaSeconds;
        input.update();
        keyboard.update();
        mouse.update();
        audioManager.update(deltaSeconds);
        stateProcessor.update(deltaSeconds);
        applyUiSystemCursor();
    }

    private void applyUiSystemCursor() {
        State state = stateProcessor.getCurrentState();
        Cursor.SystemCursor desired = state != null
                ? state.getUiSystemCursor(mouse.getX(), mouse.getY())
                : Cursor.SystemCursor.Arrow;
        if (desired != lastUiSystemCursor) {
            Gdx.graphics.setSystemCursor(desired);
            lastUiSystemCursor = desired;
        }
    }

    public void render() {
        int w = getWidth();
        int h = getHeight();
        // Snipping Tool / overlays can transiently report a 0×0 framebuffer via GLFWFramebufferSizeCallback.
        if (w <= 0 || h <= 0) {
            return;
        }
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        // Maximize / fullscreen can leave a stale glViewport (initial window size); redraw must cover the full draw surface.
        HdpiUtils.glViewport(0, 0, w, h);
        renderContext.beginFrame(getDeltaSeconds(), w, h);
        syncWindowCanvasToGraphics(w, h);
        stateProcessor.render(renderContext);
    }

    /** Keeps the legacy AWT canvas size aligned with LibGDX (skipped when framebuffer size is invalid). */
    private void syncWindowCanvasToGraphics(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        window.resize(width, height);
    }

    public float getDeltaSeconds() {
        return Gdx.graphics.getDeltaTime();
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public boolean isCloseRequested() {
        return false;
    }

    public void requestClose() {
        Gdx.app.exit();
    }

    public void cleanup() {
        stateProcessor.dispose();
        audioManager.cleanup();
        resources.dispose();
        renderContext.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        currentInstance = null;
    }

    public void resize(int width, int height) {
        stateProcessor.resize(width, height);
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public EngineConfigs getConfigs() {
        return configs;
    }

    public Window getWindow() {
        syncWindowCanvasToGraphics(getWidth(), getHeight());
        return window;
    }

    public static Engine init() {
        return init(EngineConfigs.getDefaultConfigs());
    }

    public static Engine init(EngineConfigs configs) {
        if (currentInstance != null) {
            throw new IllegalStateException("Engine has already been initialized!");
        }
        currentInstance = EngineCreator.init(configs);
        return currentInstance;
    }
}
