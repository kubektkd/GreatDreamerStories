package atomiccode.greatDreamerStories;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;
import atomiccode.greatDreamerStories.gameManagement.GameManager;

/**
 * Game facade that provides a simplified interface to the game management system.
 * This class acts as a facade for GameManager, hiding the complexity of the underlying engine.
 */
public class Game {
    
    private GameManager gameManager;
    private String title;
    private int width, height;
    
    /**
     * Creates a new Game instance with the specified title and dimensions.
     * 
     * @param title The title of the game
     * @param width The width of the game window
     * @param height The height of the game window
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
     * Gets the game title.
     * 
     * @return The game title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the game width.
     * 
     * @return The game width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the game height.
     * 
     * @return The game height
     */
    public int getHeight() {
        return height;
    }
}
