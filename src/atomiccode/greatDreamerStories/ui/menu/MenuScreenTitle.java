package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.cthulhuEngine.ui.layout.UiRect;
import atomiccode.greatDreamerStories.ui.GeneralMenuRenderer;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Screen header: title, subtitle, rule line, and optional right-aligned metric (archive typography).
 */
public final class MenuScreenTitle {

    public static final int TITLE_FONT_SIZE = 30;
    public static final int SUBTITLE_FONT_SIZE = 12;

    /** Optional right column metric; pass {@code null} to omit. */
    public record RightMetric(String value, String label, int valueRightInset, int labelRightInset) {
        public RightMetric(String value, String label) {
            this(value, label, 12, 96);
        }
    }

    private MenuScreenTitle() {
    }

    public static Font titleFont() {
        return GreatDreamerTheme.archiveFont(TITLE_FONT_SIZE);
    }

    public static Font subtitleFont() {
        return GreatDreamerTheme.archiveFont(SUBTITLE_FONT_SIZE);
    }

    /**
     * @param lineRight x-coordinate where the header rule ends (often {@link UiRect#right()} or a column edge)
     */
    public static void draw(Graphics2D g2d, UiRect content, int lineRight, String title, String subtitle,
                            RightMetric metric) {
        Font tf = titleFont();
        Font sf = subtitleFont();
        GeneralMenuRenderer.drawHeader(g2d, content, lineRight, title, subtitle, tf, sf);
        if (metric != null) {
            GeneralMenuRenderer.drawRightMetric(g2d, content, metric.value(), metric.label(),
                    metric.valueRightInset(), metric.labelRightInset(), tf, sf);
        }
    }

    /**
     * Aligns a secondary heading to the bottom of the metric label line (e.g. "SUBJECT'S PROFILE" on creation).
     */
    public static void drawSecondaryHeadingAlignedToMetric(Graphics2D g2d, UiRect content, int titleLeftX, String title) {
        GeneralMenuRenderer.drawTitleAlignedToRightMetricLabelBottom(g2d, content, titleLeftX, title,
                GreatDreamerTheme.archiveFont(15), subtitleFont());
    }
}
