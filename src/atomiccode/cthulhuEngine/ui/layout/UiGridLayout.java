package atomiccode.cthulhuEngine.ui.layout;

public class UiGridLayout {
    private final int columns;
    private final int gapX;
    private final int gapY;
    private final int preferredCellHeight;

    public UiGridLayout(int columns, int gapX, int gapY, int preferredCellHeight) {
        this.columns = Math.max(1, columns);
        this.gapX = Math.max(0, gapX);
        this.gapY = Math.max(0, gapY);
        this.preferredCellHeight = Math.max(1, preferredCellHeight);
    }

    public UiRect[] layout(UiRect container, int count) {
        UiRect[] rects = new UiRect[count];
        int cellWidth = (container.width - (columns - 1) * gapX) / columns;

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int col = i % columns;
            int x = container.x + col * (cellWidth + gapX);
            int y = container.y + row * (preferredCellHeight + gapY);
            rects[i] = new UiRect(x, y, cellWidth, preferredCellHeight);
        }

        return rects;
    }
}
