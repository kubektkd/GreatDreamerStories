package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import atomiccode.cthulhuEngine.engineMain.engine.EngineRenderContext;

public class EmptyState implements State {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
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
