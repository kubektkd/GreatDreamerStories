package atomiccode.greatDreamerStories;

import atomiccode.greatDreamerStories.configs.EngineConfigs_Build;

public class Launcher {

    private static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        // Create game instance using the singleton pattern
        Game game = Game.getInstance();
        
        // Initialize and start the game
        game.init(new EngineConfigs_Build(VERSION));
        game.start();
    }
}