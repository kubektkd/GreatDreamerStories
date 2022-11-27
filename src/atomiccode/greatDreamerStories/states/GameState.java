package atomiccode.greatDreamerStories.states;

import atomiccode.cthulhuEngine.inputsOutputs.stateControl.State;

import java.awt.*;

public enum GameState implements State {

    SPLASH_SCREEN(),
    MAIN_MENU(),
    UI(),
    BATTLE(),
    NORMAL();

    @Override
    public int getPriority() {
        return super.ordinal();
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {

    }
}
