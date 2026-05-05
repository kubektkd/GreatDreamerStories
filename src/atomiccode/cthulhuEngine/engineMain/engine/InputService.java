package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputService {
    private int scrollYAccumulator;

    public InputService() {
        installScrollListener();
    }

    private void installScrollListener() {
        InputAdapter scrollListener = new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                scrollYAccumulator += Math.round(amountY);
                return false;
            }
        };

        InputProcessor existing = Gdx.input.getInputProcessor();
        if (existing == null) {
            Gdx.input.setInputProcessor(scrollListener);
            return;
        }

        if (existing instanceof InputMultiplexer multiplexer) {
            multiplexer.addProcessor(scrollListener);
            return;
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(existing);
        multiplexer.addProcessor(scrollListener);
        Gdx.input.setInputProcessor(multiplexer);
    }

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

    /** Signed wheel steps since last consume call (positive means scroll down on desktop wheels). */
    public int consumeScrollY() {
        int value = scrollYAccumulator;
        scrollYAccumulator = 0;
        return value;
    }
}
