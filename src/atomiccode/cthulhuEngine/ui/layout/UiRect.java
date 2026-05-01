package atomiccode.cthulhuEngine.ui.layout;

import java.awt.Rectangle;

public class UiRect {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public UiRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }

    public UiRect inset(int amount) {
        return inset(amount, amount, amount, amount);
    }

    public UiRect inset(int horizontal, int vertical) {
        return inset(horizontal, vertical, horizontal, vertical);
    }

    public UiRect inset(int left, int top, int right, int bottom) {
        return new UiRect(x + left, y + top, width - left - right, height - top - bottom);
    }

    public UiRect withY(int newY) {
        return new UiRect(x, newY, width, height);
    }

    public UiRect withHeight(int newHeight) {
        return new UiRect(x, y, width, newHeight);
    }

    public UiRect center(int childWidth, int childHeight) {
        return new UiRect(x + (width - childWidth) / 2, y + (height - childHeight) / 2, childWidth, childHeight);
    }

    public UiRect align(int childWidth, int childHeight, UiAlign horizontal, UiAlign vertical) {
        int childX = alignAxis(x, width, childWidth, horizontal);
        int childY = alignAxis(y, height, childHeight, vertical);
        int resolvedWidth = horizontal == UiAlign.STRETCH ? width : childWidth;
        int resolvedHeight = vertical == UiAlign.STRETCH ? height : childHeight;
        return new UiRect(childX, childY, resolvedWidth, resolvedHeight);
    }

    public UiRect left(int leftWidth) {
        return new UiRect(x, y, Math.min(width, Math.max(0, leftWidth)), height);
    }

    public UiRect rightOf(int leftWidth, int gap) {
        int rightX = x + leftWidth + gap;
        return new UiRect(rightX, y, right() - rightX, height);
    }

    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX <= right() && pointY >= y && pointY <= bottom();
    }

    public Rectangle toRectangle() {
        return new Rectangle(x, y, width, height);
    }

    private int alignAxis(int origin, int available, int childSize, UiAlign align) {
        switch (align) {
            case CENTER:
                return origin + (available - childSize) / 2;
            case END:
                return origin + available - childSize;
            case STRETCH:
            case START:
            default:
                return origin;
        }
    }
}
