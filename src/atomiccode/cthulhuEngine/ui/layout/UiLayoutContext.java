package atomiccode.cthulhuEngine.ui.layout;

public class UiLayoutContext {
    public static final int BASE_WIDTH = 1280;
    public static final int BASE_HEIGHT = 720;

    public final int viewportWidth;
    public final int viewportHeight;
    public final float scale;

    public UiLayoutContext(int viewportWidth, int viewportHeight) {
        this.viewportWidth = Math.max(1, viewportWidth);
        this.viewportHeight = Math.max(1, viewportHeight);
        this.scale = Math.max(1.0f, Math.min(this.viewportWidth / (float) BASE_WIDTH, this.viewportHeight / (float) BASE_HEIGHT));
    }

    public UiRect viewport() {
        return new UiRect(0, 0, viewportWidth, viewportHeight);
    }

    public int scale(int value) {
        return Math.round(value * scale);
    }

    public UiRect centeredRect(int maxWidth, int maxHeight, int horizontalMargin, int verticalMargin) {
        int resolvedWidth = Math.min(viewportWidth - horizontalMargin * 2, maxWidth);
        int resolvedHeight = Math.min(viewportHeight - verticalMargin * 2, maxHeight);
        return viewport().center(resolvedWidth, resolvedHeight);
    }
}
