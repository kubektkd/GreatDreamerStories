package atomiccode.greatDreamerStories;

import atomiccode.greatDreamerStories.configs.EngineConfigs_Build;

public class Launcher {

    private static final String VERSION = "0.0.1";
    private static final String GAME_TITLE = "Great Dreamer Stories";
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;

    public static void main(String[] args) {
        // Create game instance using the facade
        Game game = new Game(GAME_TITLE, GAME_WIDTH, GAME_HEIGHT);
        
        // Initialize and start the game
        game.init(new EngineConfigs_Build(VERSION));
        game.start();
    }
}