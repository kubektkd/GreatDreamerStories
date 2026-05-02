package atomiccode.greatDreamerStories.states;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Layout metrics for the main menu: button stack vertical anchor, logo–stack gap, and responsive logo bitmap size.
 * Width-led logo scaling with a minimum width vs buttons; height is applied only when it does not break that floor.
 */
final class MainMenuLayout {

    private MainMenuLayout() {
    }

    private static final int COMPACT_VIEWPORT_MAX_HEIGHT = 820;
    private static final int COMPACT_VIEWPORT_MAX_WIDTH = 1180;

    /** Multiplier relative to menu button width when deriving logo size from the button column. */
    private static final float MENU_BUTTON_WIDTH_MULTIPLIER_FOR_LOGO = 1.15f;

    private static final float LARGE_TARGET_WIDTH_OF_WINDOW = 0.44f;
    private static final float COMPACT_TARGET_WIDTH_OF_WINDOW = 0.36f;
    private static final float LARGE_BUTTON_BASED_STRETCH = 2.22f;
    private static final float COMPACT_BUTTON_BASED_STRETCH = 1.52f;

    private static final float COMPACT_MIN_LOGO_WIDTH_VS_BUTTON = 1.05f;
    private static final float LARGE_MIN_LOGO_WIDTH_VS_BUTTON = 1.2f;

    private static final int LARGE_LOGO_DRAW_WIDTH_MIN = 455;
    private static final int LOGO_DRAW_WIDTH_MAX = 640;

    private static final float LOGO_IDEAL_HEIGHT_OF_WINDOW = 0.345f;
    private static final float LOGO_SOFT_HEIGHT_CAP_OF_WINDOW = 0.48f;
    private static final int LOGO_DRAW_HEIGHT_MAX = 336;
    private static final int LOGO_HEIGHT_IDEAL_FLOOR_PX = 118;

    private static final float LOGO_ABS_MAX_HEIGHT_OF_WINDOW = 0.54f;
    private static final int LOGO_ABS_MAX_HEIGHT_PAD_PX = 24;

    private static final float MENU_STACK_BELOW_CENTER_FRACTION = 0.055f;

    private static final float LOGO_STACK_GAP_OF_WINDOW_HEIGHT = 0.069f;
    private static final int LOGO_STACK_GAP_MIN_PX = 70;
    private static final int LOGO_STACK_GAP_MAX_PX = 126;

    static final int LOGO_TOP_MIN_MARGIN_PX = 28;

    private static final int FALLBACK_FIRST_BUTTON_TOP_OFFSET_ABOVE_CENTER_PX = 80;

    /** Divisor for horizontal menu anchor: {@code windowWidth / divisor} (legacy one-fifth inset). */
    private static final int HORIZONTAL_ANCHOR_WIDTH_DIVISOR = 5;

    /** When the button stack is not ready, approximate the top of the first row for logo placement. */
    static int fallbackStackTopY(int viewportHeight) {
        return viewportHeight / 2 - FALLBACK_FIRST_BUTTON_TOP_OFFSET_ABOVE_CENTER_PX;
    }

    static int horizontalAnchorPx(int windowWidth) {
        return windowWidth / HORIZONTAL_ANCHOR_WIDTH_DIVISOR;
    }

    static int stackAreaOriginY(int viewportHeight) {
        int centerY = viewportHeight / 2;
        return centerY + Math.round(viewportHeight * MENU_STACK_BELOW_CENTER_FRACTION);
    }

    static int gapBetweenLogoBottomAndStackTop(int windowHeight) {
        return clamp(
                Math.round(windowHeight * LOGO_STACK_GAP_OF_WINDOW_HEIGHT),
                LOGO_STACK_GAP_MIN_PX,
                LOGO_STACK_GAP_MAX_PX);
    }

    /**
     * Draw size for the game logo art. {@code menuButtonWidth} is the width of the menu column (e.g. 200px).
     */
    static Dimension scaledLogoDrawSize(int windowWidth, int windowHeight, BufferedImage src, int menuButtonWidth) {
        int iw = src.getWidth();
        int ih = src.getHeight();
        if (iw < 1 || ih < 1) {
            return new Dimension(1, 1);
        }

        boolean compact = isCompactViewport(windowWidth, windowHeight);
        float minRendered = menuButtonWidth * (compact ? COMPACT_MIN_LOGO_WIDTH_VS_BUTTON : LARGE_MIN_LOGO_WIDTH_VS_BUTTON);

        float widthFrac = compact ? COMPACT_TARGET_WIDTH_OF_WINDOW : LARGE_TARGET_WIDTH_OF_WINDOW;
        float stretch = compact ? COMPACT_BUTTON_BASED_STRETCH : LARGE_BUTTON_BASED_STRETCH;
        float buttonBased = menuButtonWidth * MENU_BUTTON_WIDTH_MULTIPLIER_FOR_LOGO * stretch;
        float targetW = Math.max(windowWidth * widthFrac, buttonBased);

        int widthClampMin = compact ? Math.round(minRendered) : LARGE_LOGO_DRAW_WIDTH_MIN;
        int targetWidthPx = clamp(Math.round(targetW), widthClampMin, LOGO_DRAW_WIDTH_MAX);

        int heightIdeal = Math.round(windowHeight * LOGO_IDEAL_HEIGHT_OF_WINDOW);
        int heightCeiling = Math.min(LOGO_DRAW_HEIGHT_MAX, Math.round(windowHeight * LOGO_SOFT_HEIGHT_CAP_OF_WINDOW));
        int maxHeightPx = Math.max(LOGO_HEIGHT_IDEAL_FLOOR_PX, Math.min(heightIdeal, heightCeiling));

        float scale = targetWidthPx / (float) iw;
        if (iw * scale < minRendered) {
            scale = minRendered / (float) iw;
        }

        scale = shrinkIfTallerThan(scale, iw, ih, maxHeightPx, minRendered);

        int absMaxHeight = Math.min(Math.round(windowHeight * LOGO_ABS_MAX_HEIGHT_OF_WINDOW), heightCeiling + LOGO_ABS_MAX_HEIGHT_PAD_PX);
        scale = shrinkIfTallerThan(scale, iw, ih, absMaxHeight, minRendered);

        int dw = Math.max(1, Math.round(iw * scale));
        int dh = Math.max(1, Math.round(ih * scale));
        return new Dimension(dw, dh);
    }

    private static float shrinkIfTallerThan(float scale, int iw, int ih, int maxHeightPx, float minRenderedWidth) {
        if (ih * scale <= maxHeightPx) {
            return scale;
        }
        float fitHeight = maxHeightPx / (float) ih;
        if (iw * fitHeight >= minRenderedWidth) {
            return fitHeight;
        }
        return scale;
    }

    private static boolean isCompactViewport(int windowWidth, int windowHeight) {
        return windowHeight <= COMPACT_VIEWPORT_MAX_HEIGHT || windowWidth <= COMPACT_VIEWPORT_MAX_WIDTH;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
