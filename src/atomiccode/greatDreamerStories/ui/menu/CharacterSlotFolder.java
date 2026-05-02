package atomiccode.greatDreamerStories.ui.menu;

import atomiccode.greatDreamerStories.character.Character;
import atomiccode.greatDreamerStories.ui.GreatDreamerTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * Case-file folder slot: occupied, available (next empty), or disabled (locked empty).
 */
public final class CharacterSlotFolder {

    private CharacterSlotFolder() {
    }

    public static void draw(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight,
                            Character character, int slotIndex, int firstAvailableSlot,
                            boolean hovered, boolean selected, Font slotFont, Font detailFont) {
        boolean isOccupied = character != null;
        boolean isAvailable = !isOccupied && slotIndex == firstAvailableSlot;

        Color folderColor;
        Color folderAccentColor;
        Color slotBorderColor;
        Color textColor;

        if (isOccupied) {
            folderColor = hovered ? new Color(191, 151, 88) : new Color(174, 133, 72);
            folderAccentColor = new Color(214, 182, 121);
            slotBorderColor = selected ? GreatDreamerTheme.SELECTED : GreatDreamerTheme.BORDER;
            textColor = new Color(38, 29, 18);
        } else if (isAvailable) {
            folderColor = hovered ? new Color(181, 139, 76) : new Color(157, 116, 61);
            folderAccentColor = new Color(201, 166, 105);
            slotBorderColor = selected ? GreatDreamerTheme.SELECTED : new Color(86, 92, 84);
            textColor = new Color(43, 31, 18);
        } else {
            folderColor = new Color(75, 61, 42);
            folderAccentColor = new Color(97, 80, 55);
            slotBorderColor = GreatDreamerTheme.MUTED_BORDER;
            textColor = new Color(118, 103, 78);
        }

        if (isOccupied) {
            drawFolderPapers(g2d, slotX, slotY, slotWidth, slotHeight);
        }

        drawFolderShape(g2d, slotX, slotY, slotWidth, slotHeight, folderColor, folderAccentColor, slotBorderColor, selected);

        if (isOccupied) {
            drawOccupiedSlot(g2d, character, slotX, slotY, slotWidth, slotHeight, textColor, slotFont, detailFont);
        } else {
            drawEmptySlot(g2d, slotX, slotY, slotWidth, slotHeight, textColor, isAvailable, slotFont, detailFont);
        }
    }

    private static void drawFolderPapers(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight) {
        int paperX = slotX + 18;
        int paperY = slotY + 14;
        int paperWidth = slotWidth - 34;
        int paperHeight = slotHeight - 42;

        g2d.setColor(new Color(224, 217, 194));
        g2d.fillRect(paperX + 8, paperY + 4, paperWidth, paperHeight);
        g2d.setColor(new Color(166, 153, 124));
        g2d.drawRect(paperX + 8, paperY + 4, paperWidth, paperHeight);

        g2d.setColor(new Color(236, 230, 206));
        g2d.fillRect(paperX, paperY, paperWidth, paperHeight);
        g2d.setColor(new Color(176, 162, 130));
        g2d.drawRect(paperX, paperY, paperWidth, paperHeight);

        g2d.setColor(new Color(125, 111, 84));
        for (int line = 0; line < 4; line++) {
            int y = paperY + 28 + line * 15;
            g2d.drawLine(paperX + 16, y, paperX + paperWidth - 16, y);
        }
    }

    private static void drawFolderShape(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight,
                                        Color folderColor, Color folderAccentColor, Color borderColor, boolean isSelected) {
        int tabHeight = 26;
        int tabWidth = Math.max(72, slotWidth / 3);
        int tabSlope = 16;
        int bodyTop = slotY + tabHeight;

        Polygon backTab = new Polygon();
        backTab.addPoint(slotX + 10, bodyTop);
        backTab.addPoint(slotX + 18, slotY + 7);
        backTab.addPoint(slotX + tabWidth, slotY + 7);
        backTab.addPoint(slotX + tabWidth + tabSlope, bodyTop);
        backTab.addPoint(slotX + slotWidth - 8, bodyTop);
        backTab.addPoint(slotX + slotWidth - 8, slotY + slotHeight - 8);
        backTab.addPoint(slotX + 10, slotY + slotHeight - 8);

        g2d.setColor(new Color(0, 0, 0, 70));
        g2d.fillPolygon(translatePolygon(backTab, 4, 5));

        g2d.setColor(folderAccentColor);
        g2d.fillPolygon(backTab);

        Polygon front = new Polygon();
        front.addPoint(slotX, bodyTop + 8);
        front.addPoint(slotX + slotWidth, bodyTop + 8);
        front.addPoint(slotX + slotWidth - 8, slotY + slotHeight);
        front.addPoint(slotX + 8, slotY + slotHeight);

        g2d.setColor(folderColor);
        g2d.fillPolygon(front);
        g2d.setColor(isSelected ? borderColor : GreatDreamerTheme.LINE);
        g2d.setStroke(new BasicStroke(isSelected ? 3f : 1f));
        g2d.drawPolygon(createFolderOutline(slotX, slotY, slotWidth, slotHeight, bodyTop, tabWidth, tabSlope));
        g2d.setStroke(new BasicStroke(1f));
    }

    private static Polygon createFolderOutline(int slotX, int slotY, int slotWidth, int slotHeight,
                                               int bodyTop, int tabWidth, int tabSlope) {
        Polygon outline = new Polygon();
        outline.addPoint(slotX, bodyTop + 8);
        outline.addPoint(slotX + 10, bodyTop);
        outline.addPoint(slotX + 18, slotY + 7);
        outline.addPoint(slotX + tabWidth, slotY + 7);
        outline.addPoint(slotX + tabWidth + tabSlope, bodyTop);
        outline.addPoint(slotX + slotWidth - 8, bodyTop);
        outline.addPoint(slotX + slotWidth, bodyTop + 8);
        outline.addPoint(slotX + slotWidth - 8, slotY + slotHeight);
        outline.addPoint(slotX + 8, slotY + slotHeight);
        return outline;
    }

    private static Polygon translatePolygon(Polygon polygon, int dx, int dy) {
        Polygon translated = new Polygon();
        for (int i = 0; i < polygon.npoints; i++) {
            translated.addPoint(polygon.xpoints[i] + dx, polygon.ypoints[i] + dy);
        }
        return translated;
    }

    private static void drawOccupiedSlot(Graphics2D g2d, Character character, int slotX, int slotY,
                                         int slotWidth, int slotHeight, Color textColor, Font slotFont, Font detailFont) {
        g2d.setColor(textColor);

        g2d.setFont(slotFont);
        FontMetrics nameMetrics = g2d.getFontMetrics();
        String name = character.getName();
        if (nameMetrics.stringWidth(name) > slotWidth - 18) {
            while (nameMetrics.stringWidth(name + "...") > slotWidth - 18 && name.length() > 3) {
                name = name.substring(0, name.length() - 1);
            }
            name += "...";
        }

        int nameX = slotX + (slotWidth - nameMetrics.stringWidth(name)) / 2;
        int nameY = slotY + 60;
        g2d.drawString(name, nameX, nameY);

        int completedStories = 0;
        for (boolean completed : character.getCompletedStories()) {
            if (completed) {
                completedStories++;
            }
        }

        String progressText = "Lvl " + (completedStories + 1);
        g2d.setFont(detailFont);
        FontMetrics detailMetrics = g2d.getFontMetrics();
        int progressX = slotX + (slotWidth - detailMetrics.stringWidth(progressText)) / 2;
        int progressY = slotY + 85;
        g2d.setColor(GreatDreamerTheme.SELECTED);
        g2d.drawString(progressText, progressX, progressY);

        g2d.setColor(textColor);
        g2d.setFont(detailFont);
        String stats = String.format("STR:%d POW:%d EDU:%d CON:%d",
                character.getStrength(), character.getPower(), character.getEducation(), character.getConstitution());
        FontMetrics statsMetrics = g2d.getFontMetrics();
        int statsX = slotX + (slotWidth - statsMetrics.stringWidth(stats)) / 2;
        int statsY = slotY + 110;
        g2d.drawString(stats, statsX, statsY);

        String stats2 = String.format("INT:%d APP:%d LCK:%d SIZ:%d DEX:%d",
                character.getIntelligence(), character.getAppearance(), character.getLuck(), character.getSize(),
                character.getDexterity());
        int stats2X = slotX + (slotWidth - statsMetrics.stringWidth(stats2)) / 2;
        int stats2Y = slotY + 125;
        g2d.drawString(stats2, stats2X, stats2Y);

        g2d.setColor(GreatDreamerTheme.SELECTED);
        String playtime = String.format("Playtime: %dh", character.getTotalPlaytime() / 60);
        int playtimeX = slotX + (slotWidth - statsMetrics.stringWidth(playtime)) / 2;
        int playtimeY = slotY + 155;
        g2d.drawString(playtime, playtimeX, playtimeY);
    }

    private static void drawEmptySlot(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight,
                                      Color textColor, boolean isAvailable, Font slotFont, Font detailFont) {
        g2d.setColor(textColor);
        g2d.setFont(slotFont);
        FontMetrics plusMetrics = g2d.getFontMetrics();
        String plusSign = isAvailable ? "+" : "X";
        int plusX = slotX + (slotWidth - plusMetrics.stringWidth(plusSign)) / 2;
        int plusY = slotY + slotHeight / 2 + plusMetrics.getAscent() / 2;
        g2d.drawString(plusSign, plusX, plusY);

        g2d.setFont(detailFont);
        FontMetrics statusMetrics = g2d.getFontMetrics();
        String statusText = isAvailable ? "NEW FILE" : "LOCKED";
        int statusX = slotX + (slotWidth - statusMetrics.stringWidth(statusText)) / 2;
        int statusY = slotY + slotHeight - 15;
        g2d.drawString(statusText, statusX, statusY);
    }
}
