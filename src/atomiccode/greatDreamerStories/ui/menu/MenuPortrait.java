package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

/** Square portrait with theme border. */
public final class MenuPortrait {

    private MenuPortrait() {
    }

    public static void draw(Graphics2D g2d, int x, int y, int size, Image image) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, size, size);
        g2d.setColor(GreatDreamerTheme.BORDER);
        g2d.drawRect(x, y, size, size);

        if (image != null) {
            g2d.drawImage(image, x + 1, y + 1, size - 2, size - 2, null);
        }
    }
}
