package atomiccode.cthulhuEngine.inputsOutputs.userInputs;

import atomiccode.cthulhuEngine.engineMain.engine.InputService;

public class Mouse {

    private final InputService input;

    public Mouse(InputService input) {
        this.input = input;
    }

    public void update() {
        // LibGDX keeps mouse state updated globally.
    }

    public int getX() { return input.getMouseX(); }
    public int getY() { return input.getMouseY(); }
    public boolean isLeftPressed() { return input.isLeftPressed(); }
    public boolean isRightPressed() { return input.isRightPressed(); }
    public int consumeScrollY() { return input.consumeScrollY(); }
}
