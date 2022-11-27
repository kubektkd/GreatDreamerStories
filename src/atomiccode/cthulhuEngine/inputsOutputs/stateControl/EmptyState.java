package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.awt.*;

public class EmptyState implements State {

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {

    }

}
