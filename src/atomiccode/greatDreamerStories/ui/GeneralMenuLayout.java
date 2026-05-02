package atomiccode.greatDreamerStories.ui;

import atomiccode.cthulhuEngine.ui.layout.UiLayoutContext;
import atomiccode.cthulhuEngine.ui.layout.UiRect;

/** Shared centered card + columns for menu flows (character select, settings, gameplay dossier shell, etc.). */
public class GeneralMenuLayout {
    public static final int MAX_WIDTH = 1060;
    public static final int MAX_HEIGHT = 620;
    public static final int HORIZONTAL_MARGIN = 60;
    public static final int VERTICAL_MARGIN = 45;
    public static final int HEADER_LINE_Y = 80;
    public static final int BODY_TOP = 120;
    public static final int ACTION_BOTTOM_OFFSET = 62;
    public static final int COLUMN_GAP = 40;

    public final UiLayoutContext context;
    public final UiRect content;
    public final UiRect body;
    public final UiRect leftColumn;
    public final UiRect rightColumn;
    public final int leftRawWidth;

    public GeneralMenuLayout(UiLayoutContext context) {
        this.context = context;
        this.content = context.centeredRect(MAX_WIDTH, MAX_HEIGHT, HORIZONTAL_MARGIN, VERTICAL_MARGIN);
        this.body = new UiRect(content.x, content.y + BODY_TOP, content.width, content.height - BODY_TOP);
        this.leftRawWidth = Math.max(360, content.width * 44 / 100);
        this.leftColumn = new UiRect(content.x, content.y, Math.max(0, leftRawWidth - 30), content.height);

        int rightX = content.x + leftRawWidth + COLUMN_GAP;
        this.rightColumn = new UiRect(rightX, content.y, content.right() - rightX, content.height);
    }

    public static GeneralMenuLayout fromViewport(int viewportWidth, int viewportHeight) {
        return new GeneralMenuLayout(new UiLayoutContext(viewportWidth, viewportHeight));
    }

    public int headerLineY() {
        return content.y + HEADER_LINE_Y;
    }

    public int actionY(int actionHeight) {
        return content.bottom() - ACTION_BOTTOM_OFFSET;
    }

    public UiRect rightAction(int width, int height) {
        return new UiRect(content.right() - width, actionY(height), width, height);
    }

    public UiRect before(UiRect rect, int width, int gap) {
        return new UiRect(rect.x - gap - width, rect.y, width, rect.height);
    }
}
