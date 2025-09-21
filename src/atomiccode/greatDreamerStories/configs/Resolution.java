package atomiccode.greatDreamerStories.configs;

public enum Resolution {
    SMALL(640, 360),
    MEDIUM(800, 450),
    LARGE(1024, 576),
    HD(1280, 720),
    HD_READY(1366, 768),
    HD_PLUS(1600, 900),
    FULLHD(1920, 1080),
    QHD(2560, 1440);

    private final int width;
    private final int height;

    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
