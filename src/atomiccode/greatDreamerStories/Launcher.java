package atomiccode.greatDreamerStories;

import atomiccode.greatDreamerStories.configs.EngineConfigs_Build;
import atomiccode.greatDreamerStories.gameManagement.GameManager;

public class Launcher {

    private static final String VERSION = "0.0.1";

    public static void main(String[] args) {
//        Game game = new Game("Tiled Game", 640, 480);
//        game.start();

        GameManager game = new GameManager();

        game.init(new EngineConfigs_Build(VERSION));
        game.start();
    }
}