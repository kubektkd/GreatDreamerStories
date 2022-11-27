package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.awt.*;

public interface State {

    public int getPriority();

    public abstract void tick();

    public void render(Graphics g);
}
