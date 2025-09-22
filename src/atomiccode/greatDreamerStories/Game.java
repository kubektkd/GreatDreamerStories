package atomiccode.greatDreamerStories;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.greatDreamerStories.gameManagement.GameManager;

/**
 * Game facade that provides a simplified interface to the game management system.
 * This class acts as a facade for GameManager, hiding the complexity of the underlying engine.
 */
public class Game {
    
    private GameManager gameManager;
    private String version;
    
    /**
     * Creates a new Game instance.
     */
    public Game() {
        this.gameManager = new GameManager();
    }
    
    /**
     * Initializes the game with the provided engine configuration.
     * 
     * @param configs The engine configuration to use
     */
    public void init(EngineConfigs configs) {
        this.version = configs.version;
        gameManager.init(configs);
    }
    
    /**
     * Starts the game loop.
     */
    public void start() {
        gameManager.start();
    }
    
    /**
     * Stops the game loop.
     */
    public void stop() {
        gameManager.stop();
    }

    /**
     * Gets the game version.
     * 
     * @return The game version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the singleton instance of the Game.
     * 
     * @return The singleton Game instance
     */
    private static Game instance;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
}
