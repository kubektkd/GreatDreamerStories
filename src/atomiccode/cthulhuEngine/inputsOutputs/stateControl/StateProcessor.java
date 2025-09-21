package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.awt.Graphics2D;
import java.util.List;
import java.util.ListIterator;

public class StateProcessor {

    private final List<State> states;

    public StateProcessor(List<State> states) {
        this.states = states;
    }

    public void update(float deltaTime) {
        ListIterator<State> it = states.listIterator(states.size());
        while (it.hasPrevious()) {
            State state = it.previous();
            state.update();
            if (state.blocksUpdate()) break;
        }
    }

    public void render(Graphics2D g) {
        for (State state : states) {
            state.render(g);
            if (state.isOpaque()) break;
        }
    }
}
