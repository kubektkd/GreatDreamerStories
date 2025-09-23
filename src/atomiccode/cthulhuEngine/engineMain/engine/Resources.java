package atomiccode.cthulhuEngine.engineMain.engine;

import javax.imageio.*;
import java.awt.Image;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
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

    public Font getFont(String fontPath, int size) {
        try {
            File fontFile = EngineFiles.getResourceFile("fonts/" + fontPath);
            if (fontFile.exists()) {
                InputStream fontStream = new FileInputStream(fontFile);
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                return customFont.deriveFont(Font.PLAIN, size);
            } else {
                System.err.println("Font file not found: " + fontFile.getAbsolutePath() + ", using Arial fallback");
                return new Font("Arial", Font.PLAIN, size);
            }
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage() + ", using Arial fallback");
            return new Font("Arial", Font.PLAIN, size);
        }
    }
    
    public static void enableAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    // TODO: implement a caching and lazy load mechanisms

}
