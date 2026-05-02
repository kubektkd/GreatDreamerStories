package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.engineMain.engine.EngineRenderContext;
import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;

public enum GameState implements State {

    UI(),
    BATTLE(),
    NORMAL();

    @Override
    public int getPriority() {
        return super.ordinal();
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void update() {

    }

    @Override
    public void render(EngineRenderContext context) {

    }
}
