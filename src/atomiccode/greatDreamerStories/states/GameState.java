package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;

import java.awt.Graphics;

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
    public void render(Graphics g) {

    }
}
