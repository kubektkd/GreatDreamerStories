package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

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
    private final AssetManager assetManager = new AssetManager();
    private final Map<String, Image> images = new HashMap<>();

    public Texture getTexture(String path) {
        if (!assetManager.isLoaded(path, Texture.class)) {
            assetManager.load(path, Texture.class);
            assetManager.finishLoadingAsset(path);
        }
        return assetManager.get(path, Texture.class);
    }

    public FileHandle internal(String path) {
        return Gdx.files.internal(path);
    }

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
    
    /**
     * Applies high-quality defaults for legacy Java2D screens. Important for scaled
     * {@code drawImage} (backgrounds, logos): without interpolation/render-quality hints,
     * scaling stays effectively nearest-neighbour and looks harsher than typical Swing output.
     */
    public static void configureJava2DPipeline(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    }

    public static void enableAntialiasing(Graphics2D g2d) {
        configureJava2DPipeline(g2d);
    }

    public void dispose() {
        assetManager.dispose();
        images.clear();
    }

}
