package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

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

    void render(Graphics g);
}
