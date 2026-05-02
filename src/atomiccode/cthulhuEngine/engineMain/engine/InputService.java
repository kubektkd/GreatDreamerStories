package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.Gdx;

public class InputService {
    public void update() {
        // LibGDX tracks key/mouse state internally. This hook keeps the engine
        // service shape stable for future custom input routing.
    }

    public boolean keyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }

    public boolean keyJustPressed(int keyCode) {
        return Gdx.input.isKeyJustPressed(keyCode);
    }

    public int getMouseX() {
        return Gdx.input.getX();
    }

    public int getMouseY() {
        // Match Java2D / UI code: origin top-left, Y increases downward.
        return Gdx.input.getY();
    }

    public boolean isLeftPressed() {
        return Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT);
    }

    public boolean isRightPressed() {
        return Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.RIGHT);
    }
}
