package atomiccode.cthulhuEngine.inputsOutputs.stateControl;

import java.util.Stack;

/**
 * Simple state stack/queue for managing state transitions
 * No rendering or update logic - just state management
 */
public class StateManager {

    private final Stack<State> stateStack = new Stack<>();
    private final State defaultState;

    public StateManager(State defaultState, State initialState) {
        this.defaultState = defaultState;
        if (initialState != null) {
            stateStack.push(initialState);
            initialState.onEnter(); // Call onEnter for initial state
        }
    }

    /**
     * Push a new state onto the stack
     */
    public void pushState(State state) {
        if (state != null) {
            stateStack.push(state);
            state.onEnter(); // Call onEnter when state is added
        }
    }

    /**
     * Pop the current state from the stack
     */
    public State popState() {
        if (stateStack.isEmpty()) {
            return defaultState;
        }
        State state = stateStack.pop();
        state.onExit();
        return state;
    }

    /**
     * Replace the current state
     */
    public void setState(State state) {
        if (!stateStack.isEmpty()) {
            State current = stateStack.pop();
            current.onExit();
        }
        if (state != null) {
            stateStack.push(state);
            state.onEnter(); // Call onEnter when state is added
        }
    }

    /**
     * Get the current state (top of stack)
     */
    public State getCurrentState() {
        if (stateStack.isEmpty()) {
            return defaultState;
        }
        return stateStack.peek();
    }

    /**
     * Check if stack is empty
     */
    public boolean isEmpty() {
        return stateStack.isEmpty();
    }

    /**
     * Get stack size
     */
    public int size() {
        return stateStack.size();
    }

    /**
     * Clear all states
     */
    public void clear() {
        while (!stateStack.isEmpty()) {
            stateStack.pop().onExit();
        }
        stateStack.clear();
    }
}
