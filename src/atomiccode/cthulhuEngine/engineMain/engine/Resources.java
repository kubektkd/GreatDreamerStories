package atomiccode.cthulhuEngine.engineMain.engine;

import javax.imageio.*;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class Resources {
    private final Map<String, Image> images = new HashMap<>();
//    public final ColourRepository colours = new ColourRepository();

    public Image getImage(String path) {
        return images.computeIfAbsent(path, p -> loadImage(p));
    }

    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(EngineFiles.RES_FOLDER, path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }

    // Audio methods removed - use Engine.instance().audioManager instead

    // TODO: add methods for loading fonts, etc.
    // TODO: implement a caching and lazy load mechanisms

}
