package atomiccode.greatDreamerStories.gameManagement;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;
import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;

public class GameManager implements Runnable {
    private static Engine engine;

    private Thread thread;
    private boolean running = false;

    public void init(EngineConfigs configs) { // TODO take in game configs
        initSystems(configs);
    }

    private static void initSystems(EngineConfigs configs) {
        engine = Engine.init(configs);
    }

    @Override
    public void run() {
        while(running) {
            engine.update();
            engine.render();
        }
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start(); // this calls run method
    }

    public synchronized void stop() {
        if (!running)
            return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
