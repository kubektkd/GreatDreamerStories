package atomiccode.cthulhuEngine.engineMain.engine;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class EngineRenderContext {
    public final SpriteBatch batch;
    public final ShapeRenderer shapes;
    public final Resources resources;

    private float deltaSeconds;
    private int width;
    private int height;
    private BufferedImage java2dImage;
    private Pixmap java2dPixmap;
    private Texture java2dTexture;

    EngineRenderContext(SpriteBatch batch, ShapeRenderer shapes, Resources resources) {
        this.batch = batch;
        this.shapes = shapes;
        this.resources = resources;
    }

    void beginFrame(float deltaSeconds, int width, int height) {
        this.deltaSeconds = deltaSeconds;
        this.width = width;
        this.height = height;
    }

    public float getDeltaSeconds() {
        return deltaSeconds;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void renderJava2D(Consumer<Graphics2D> renderer) {
        ensureJava2DTarget();

        Graphics2D graphics = java2dImage.createGraphics();
        try {
            Resources.configureJava2DPipeline(graphics);
            renderer.accept(graphics);
        } finally {
            graphics.dispose();
        }

        copyJava2DImageToPixmap();
        java2dTexture.draw(java2dPixmap, 0, 0);

        batch.begin();
        batch.draw(java2dTexture, 0, 0, width, height);
        batch.end();
    }

    public void dispose() {
        if (java2dTexture != null) {
            java2dTexture.dispose();
        }
        if (java2dPixmap != null) {
            java2dPixmap.dispose();
        }
    }

    private void ensureJava2DTarget() {
        if (java2dImage != null && java2dImage.getWidth() == width && java2dImage.getHeight() == height) {
            return;
        }

        if (java2dTexture != null) {
            java2dTexture.dispose();
        }
        if (java2dPixmap != null) {
            java2dPixmap.dispose();
        }

        java2dImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java2dPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        java2dTexture = new Texture(width, height, Pixmap.Format.RGBA8888);
        java2dTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void copyJava2DImageToPixmap() {
        int[] argbPixels = ((DataBufferInt) java2dImage.getRaster().getDataBuffer()).getData();
        ByteBuffer pixels = java2dPixmap.getPixels();
        pixels.clear();

        for (int argb : argbPixels) {
            pixels.put((byte) ((argb >> 16) & 0xff));
            pixels.put((byte) ((argb >> 8) & 0xff));
            pixels.put((byte) (argb & 0xff));
            pixels.put((byte) ((argb >> 24) & 0xff));
        }

        pixels.flip();
    }
}
