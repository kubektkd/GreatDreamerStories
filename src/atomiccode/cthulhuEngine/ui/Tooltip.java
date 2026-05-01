package atomiccode.cthulhuEngine.ui;

import java.awt.*;

public class Tooltip {
    private String text = "";
    private boolean hovered;
    private long hoverStartTime;
    private long delayMillis;

    private Font font = new Font("Arial", Font.PLAIN, 14);
    private Color backgroundColor = new Color(11, 12, 15, 245);
    private Color borderColor = new Color(55, 58, 65);
    private Color titleColor = Color.WHITE;
    private Color bodyColor = new Color(116, 116, 116);

    public Tooltip(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColors(Color background, Color border, Color title, Color body) {
        this.backgroundColor = background;
        this.borderColor = border;
        this.titleColor = title;
        this.bodyColor = body;
    }

    public void update(boolean hovered, String text) {
        String nextText = text != null ? text : "";
        if (this.hovered != hovered || !this.text.equals(nextText)) {
            hoverStartTime = System.currentTimeMillis();
        }

        this.hovered = hovered;
        this.text = nextText;
    }

    public void render(Graphics g, int anchorX, int anchorY, int viewportWidth, int viewportHeight) {
        if (!hovered || text.isEmpty()) {
            return;
        }
        if (System.currentTimeMillis() - hoverStartTime < delayMillis) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        String[] lines = text.split("\n");

        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, metrics.stringWidth(line));
        }

        int tooltipWidth = maxWidth + 20;
        int tooltipHeight = lines.length * metrics.getHeight() + 25;
        int tooltipX = anchorX + 15;
        int tooltipY = anchorY - tooltipHeight - 5;

        if (tooltipX + tooltipWidth > viewportWidth) {
            tooltipX = anchorX - tooltipWidth - 15;
        }
        if (tooltipY < 0) {
            tooltipY = anchorY + 20;
        }
        if (tooltipY + tooltipHeight > viewportHeight) {
            tooltipY = Math.max(0, viewportHeight - tooltipHeight - 5);
        }

        g2d.setColor(backgroundColor);
        g2d.fillRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);

        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);

        int lineY = tooltipY + metrics.getAscent() + 10;
        for (int i = 0; i < lines.length; i++) {
            g2d.setColor(i == 0 ? titleColor : bodyColor);
            g2d.drawString(lines[i], tooltipX + 10, lineY);
            lineY += metrics.getHeight() + 5;
        }
    }
}
