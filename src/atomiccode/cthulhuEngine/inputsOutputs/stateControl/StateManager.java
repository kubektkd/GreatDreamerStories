package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

public class StateManager {

    private final State defaultState;

    private State currentState;
    private List<QueuedState> stateQueue = new LinkedList<>();

    public StateManager(State defaultState, State initialState) {
        this.defaultState = defaultState;
        this.currentState = initialState;
        // Call onEnter for the initial state
        if (currentState != null) {
            currentState.onEnter();
        }
    }

    public void suggestState(State state, boolean waitForEndRequest) {
        QueuedState queuedState = new QueuedState(state, waitForEndRequest);
        sortStateIntoQueue(queuedState);
    }

    public void endState(State state) {
        Iterator<QueuedState> iterator = stateQueue.iterator();
        while (iterator.hasNext()) {
            QueuedState queuedState = iterator.next();
            if (queuedState.state == state) {
                iterator.remove();
                return;
            }
        }
    }

    public State getState() {
        return currentState;
    }
    
    public void setState(State newState) {
        suggestState(newState, false);
    }

    public void updateState() {
        State nextState = getNextState();
        cleanQueue();
        if (currentState == nextState) {
            return;
        }
        switchState(nextState);
    }

    private void switchState(State newState) {
        if (currentState != null) {
            currentState.onExit();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.onEnter();
        }
    }

    private State getNextState() {
        if (stateQueue.isEmpty()) {
            return currentState; // Don't switch if no state is queued
        }
        return stateQueue.getFirst().state;
    }

    private void cleanQueue() {
        Iterator<QueuedState> iterator = stateQueue.iterator();
        while (iterator.hasNext()) {
            QueuedState state = iterator.next();
            if (!state.waitForEndRequest && state.state != currentState) {
                iterator.remove();
            }
        }
    }

    private void sortStateIntoQueue(QueuedState newState) {
        for (int i = 0; i < stateQueue.size(); i++) {
            if (stateQueue.get(i).state.getPriority() > newState.state.getPriority()) {
                stateQueue.add(i, newState);
                return;
            }
        }
        stateQueue.add(newState);
    }

    public static class QueuedState {

        private final State state;
        private final boolean waitForEndRequest;

        private QueuedState(State state, boolean waitForEndRequest) {
            this.state = state;
            this.waitForEndRequest = waitForEndRequest;
        }

    }
}
