package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import atomiccode.cthulhuEngine.engineMain.engine.EngineRenderContext;

import java.awt.Graphics;

public interface State {

    default boolean isOpaque() { return true; }
    default boolean blocksUpdate() { return true; }

    int getPriority();

    void onEnter();
    void onExit();

    // Deterministic game logic (e.g., combat, dialogue progression)
    void tick();

    // Frame-based updates (e.g., animations, transitions)
    void update();

    default void render(EngineRenderContext context) {
        context.renderJava2D(this::render);
    }

    default void render(Graphics g) {}

    default void resize(int width, int height) {}

    default void dispose() {}
}
