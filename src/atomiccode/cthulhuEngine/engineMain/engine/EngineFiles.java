package atomiccode.cthulhuEngine.engineMain.engine;

import java.io.File;

public class EngineFiles {

    private static final File PROJECT_ROOT_FOLDER = new File("./");
    public static final File RES_FOLDER = findResFolder();
    
    private static File findResFolder() {
        // First try the current directory (for development)
        File resFolder = new File(PROJECT_ROOT_FOLDER, "res");
        if (resFolder.exists()) {
            return resFolder;
        }
        
        // If not found, try the app directory (for jpackage distribution)
        resFolder = new File(PROJECT_ROOT_FOLDER, "app/res");
        if (resFolder.exists()) {
            return resFolder;
        }
        
        // Fallback to current directory
        return new File(PROJECT_ROOT_FOLDER, "res");
    }
    
    /**
     * Gets the correct path for a resource file, handling both development and distribution environments.
     * @param resourcePath The path relative to the res folder (e.g., "backgrounds/menu/main-menu-bg.jpg")
     * @return File object pointing to the correct resource location
     */
    public static File getResourceFile(String resourcePath) {
        return new File(RES_FOLDER, resourcePath);
    }
}
