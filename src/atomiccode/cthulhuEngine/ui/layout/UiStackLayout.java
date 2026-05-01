package atomiccode.cthulhuEngine.ui.layout;

public class UiStackLayout {
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private final Direction direction;
    private final int gap;
    private final UiAlign crossAxisAlign;
    private final UiAlign mainAxisAlign;

    public UiStackLayout(Direction direction, int gap, UiAlign crossAxisAlign, UiAlign mainAxisAlign) {
        this.direction = direction;
        this.gap = gap;
        this.crossAxisAlign = crossAxisAlign;
        this.mainAxisAlign = mainAxisAlign;
    }

    public UiRect[] layout(UiRect container, int itemWidth, int itemHeight, int count) {
        UiRect[] rects = new UiRect[count];
        if (count <= 0) {
            return rects;
        }

        boolean horizontal = direction == Direction.HORIZONTAL;
        int itemMainSize = horizontal ? itemWidth : itemHeight;
        int itemCrossSize = horizontal ? itemHeight : itemWidth;
        int availableMainSize = horizontal ? container.width : container.height;
        int totalMainSize = count * itemMainSize + (count - 1) * gap;
        int mainStart = alignStart(horizontal ? container.x : container.y, availableMainSize, totalMainSize, mainAxisAlign);

        for (int i = 0; i < count; i++) {
            int mainPosition = mainStart + i * (itemMainSize + gap);
            if (horizontal) {
                int y = alignStart(container.y, container.height, itemCrossSize, crossAxisAlign);
                int height = crossAxisAlign == UiAlign.STRETCH ? container.height : itemHeight;
                rects[i] = new UiRect(mainPosition, y, itemWidth, height);
            } else {
                int x = alignStart(container.x, container.width, itemCrossSize, crossAxisAlign);
                int width = crossAxisAlign == UiAlign.STRETCH ? container.width : itemWidth;
                rects[i] = new UiRect(x, mainPosition, width, itemHeight);
            }
        }

        return rects;
    }

    private int alignStart(int origin, int available, int needed, UiAlign align) {
        switch (align) {
            case CENTER:
                return origin + (available - needed) / 2;
            case END:
                return origin + available - needed;
            case STRETCH:
            case START:
            default:
                return origin;
        }
    }
}
