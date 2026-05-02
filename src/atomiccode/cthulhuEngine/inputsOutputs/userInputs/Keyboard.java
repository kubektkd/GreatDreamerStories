package atomiccode.cthulhuEngine.inputsOutputs.userInputs;

import com.badlogic.gdx.Input;

import atomiccode.cthulhuEngine.engineMain.engine.InputService;

import java.awt.event.KeyEvent;

public class Keyboard {

    private final InputService input;
    public boolean up, down, left, right;

    public Keyboard(InputService input) {
        this.input = input;
    }

    public void update() {
        up = input.keyPressed(Input.Keys.W) || input.keyPressed(Input.Keys.UP);
        down = input.keyPressed(Input.Keys.S) || input.keyPressed(Input.Keys.DOWN);
        left = input.keyPressed(Input.Keys.A) || input.keyPressed(Input.Keys.LEFT);
        right = input.keyPressed(Input.Keys.D) || input.keyPressed(Input.Keys.RIGHT);
    }

    public boolean keyJustPressed(int keyCode) {
        int mappedKey = mapKeyCode(keyCode);
        if (mappedKey == Input.Keys.SHIFT_LEFT) {
            return input.keyJustPressed(Input.Keys.SHIFT_LEFT) || input.keyJustPressed(Input.Keys.SHIFT_RIGHT);
        }
        return input.keyJustPressed(mappedKey);
    }

    public boolean keyPressed(int keyCode) {
        int mappedKey = mapKeyCode(keyCode);
        if (mappedKey == Input.Keys.SHIFT_LEFT) {
            return input.keyPressed(Input.Keys.SHIFT_LEFT) || input.keyPressed(Input.Keys.SHIFT_RIGHT);
        }
        return input.keyPressed(mappedKey);
    }

    private int mapKeyCode(int keyCode) {
        if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return Input.Keys.A + (keyCode - KeyEvent.VK_A);
        }
        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) {
            return Input.Keys.NUM_0 + (keyCode - KeyEvent.VK_0);
        }

        switch (keyCode) {
            case KeyEvent.VK_UP:
                return Input.Keys.UP;
            case KeyEvent.VK_DOWN:
                return Input.Keys.DOWN;
            case KeyEvent.VK_LEFT:
                return Input.Keys.LEFT;
            case KeyEvent.VK_RIGHT:
                return Input.Keys.RIGHT;
            case KeyEvent.VK_ENTER:
                return Input.Keys.ENTER;
            case KeyEvent.VK_ESCAPE:
                return Input.Keys.ESCAPE;
            case KeyEvent.VK_BACK_SPACE:
                return Input.Keys.BACKSPACE;
            case KeyEvent.VK_DELETE:
                return Input.Keys.FORWARD_DEL;
            case KeyEvent.VK_HOME:
                return Input.Keys.HOME;
            case KeyEvent.VK_END:
                return Input.Keys.END;
            case KeyEvent.VK_SPACE:
                return Input.Keys.SPACE;
            case KeyEvent.VK_MINUS:
                return Input.Keys.MINUS;
            case KeyEvent.VK_QUOTE:
                return Input.Keys.APOSTROPHE;
            case KeyEvent.VK_PERIOD:
                return Input.Keys.PERIOD;
            case KeyEvent.VK_SHIFT:
                return Input.Keys.SHIFT_LEFT;
            default:
                return keyCode;
        }
    }
}
