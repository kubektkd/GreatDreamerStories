package atomiccode.cthulhuEngine.ui;

import atomiccode.cthulhuEngine.engineMain.engine.Engine;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TextInput {
    private static final long BACKSPACE_REPEAT_INITIAL_DELAY = 350;
    private static final long BACKSPACE_REPEAT_INTERVAL = 45;
    private static final long CARET_MOVE_REPEAT_INITIAL_DELAY = 350;
    private static final long CARET_MOVE_REPEAT_INTERVAL = 45;

    public int x, y;
    private int width, height;
    private String label;
    private String text;
    private int maxLength;
    private boolean active;
    private boolean wasMousePressed;
    private int caretIndex;
    private long nextBackspaceRepeatTime;
    private long nextLeftCaretRepeatTime;
    private long nextRightCaretRepeatTime;

    private Font labelFont = new Font("Arial", Font.PLAIN, 12);
    private Font textFont = new Font("Arial", Font.PLAIN, 16);
    private Color backgroundColor = new Color(11, 12, 15, 210);
    private Color borderColor = new Color(31, 34, 40);
    private Color activeBorderColor = Color.WHITE;
    private Color textColor = Color.WHITE;
    private Color labelColor = new Color(116, 116, 116);
    private Color underlineColor = new Color(55, 58, 65);

    public TextInput(int x, int y, int width, int height, String label, String text, int maxLength) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.text = text;
        this.maxLength = maxLength;
        this.caretIndex = text.length();
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        caretIndex = clampCaretIndex(caretIndex);
    }

    public void setFonts(Font labelFont, Font textFont) {
        this.labelFont = labelFont;
        this.textFont = textFont;
    }

    public void setColors(Color background, Color border, Color activeBorder, Color text, Color label, Color underline) {
        this.backgroundColor = background;
        this.borderColor = border;
        this.activeBorderColor = activeBorder;
        this.textColor = text;
        this.labelColor = label;
        this.underlineColor = underline;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        caretIndex = this.text.length();
    }

    public String getText() {
        return text;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void update(int mouseX, int mouseY, boolean mousePressed) {
        if (mousePressed && !wasMousePressed) {
            active = contains(mouseX, mouseY);
            if (active) {
                updateCaretFromMouse(mouseX);
            }
        }
        wasMousePressed = mousePressed;
    }

    public void tick() {
        if (!active) {
            return;
        }

        caretIndex = clampCaretIndex(caretIndex);

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ENTER) ||
            Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            active = false;
            return;
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_HOME)) {
            caretIndex = 0;
            return;
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_END)) {
            caretIndex = text.length();
            return;
        }
        if (handleCaretMoveInput()) {
            return;
        }
        if (handleBackspaceInput()) {
            return;
        }

        java.lang.Character typedCharacter = getTypedCharacter();
        if (typedCharacter != null && text.length() < maxLength) {
            text = text.substring(0, caretIndex) + typedCharacter + text.substring(caretIndex);
            caretIndex++;
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(backgroundColor);
        g2d.fillRect(x, y, width, height);
        g2d.setColor(active ? activeBorderColor : borderColor);
        g2d.drawRect(x, y, width, height);

        g2d.setColor(labelColor);
        g2d.setFont(labelFont);
        g2d.drawString(label, x + 18, y + 24);

        g2d.setColor(textColor);
        g2d.setFont(textFont);
        int textX = x + 18;
        int textY = y + 58;
        g2d.drawString(text, textX, textY);

        if (active && System.currentTimeMillis() % 1000 < 500) {
            int caretX = textX + g2d.getFontMetrics().stringWidth(text.substring(0, clampCaretIndex(caretIndex)));
            g2d.drawLine(caretX, textY - 15, caretX, textY + 3);
        }

        g2d.setColor(underlineColor);
        g2d.drawLine(x + 18, y + 72, x + width - 18, y + 72);
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
               mouseY >= y && mouseY <= y + height;
    }

    private boolean handleCaretMoveInput() {
        long now = System.currentTimeMillis();
        boolean handled = false;

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_LEFT)) {
            moveCaret(-1);
            nextLeftCaretRepeatTime = now + CARET_MOVE_REPEAT_INITIAL_DELAY;
            handled = true;
        } else if (Engine.instance().keyboard.keyPressed(KeyEvent.VK_LEFT)) {
            if (nextLeftCaretRepeatTime > 0 && now >= nextLeftCaretRepeatTime) {
                moveCaret(-1);
                nextLeftCaretRepeatTime = now + CARET_MOVE_REPEAT_INTERVAL;
            }
            handled = true;
        } else {
            nextLeftCaretRepeatTime = 0;
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_RIGHT)) {
            moveCaret(1);
            nextRightCaretRepeatTime = now + CARET_MOVE_REPEAT_INITIAL_DELAY;
            handled = true;
        } else if (Engine.instance().keyboard.keyPressed(KeyEvent.VK_RIGHT)) {
            if (nextRightCaretRepeatTime > 0 && now >= nextRightCaretRepeatTime) {
                moveCaret(1);
                nextRightCaretRepeatTime = now + CARET_MOVE_REPEAT_INTERVAL;
            }
            handled = true;
        } else {
            nextRightCaretRepeatTime = 0;
        }

        return handled;
    }

    private boolean handleBackspaceInput() {
        long now = System.currentTimeMillis();

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_BACK_SPACE)) {
            deleteCharacterBeforeCaret();
            nextBackspaceRepeatTime = now + BACKSPACE_REPEAT_INITIAL_DELAY;
            return true;
        }

        if (!Engine.instance().keyboard.keyPressed(KeyEvent.VK_BACK_SPACE)) {
            nextBackspaceRepeatTime = 0;
            return false;
        }

        if (nextBackspaceRepeatTime > 0 && now >= nextBackspaceRepeatTime) {
            deleteCharacterBeforeCaret();
            nextBackspaceRepeatTime = now + BACKSPACE_REPEAT_INTERVAL;
            return true;
        }

        return true;
    }

    private void updateCaretFromMouse(int mouseX) {
        FontMetrics metrics = Engine.instance().getWindow().getCanvas().getFontMetrics(textFont);
        int textStartX = x + 18;
        int relativeX = Math.max(0, mouseX - textStartX);

        caretIndex = text.length();
        for (int i = 0; i <= text.length(); i++) {
            int leftWidth = metrics.stringWidth(text.substring(0, i));
            int rightWidth = i < text.length() ? metrics.stringWidth(text.substring(0, i + 1)) : leftWidth;
            int midpoint = leftWidth + (rightWidth - leftWidth) / 2;
            if (relativeX <= midpoint) {
                caretIndex = i;
                return;
            }
        }
    }

    private void moveCaret(int direction) {
        caretIndex = clampCaretIndex(caretIndex + direction);
    }

    private void deleteCharacterBeforeCaret() {
        if (caretIndex <= 0 || text.isEmpty()) {
            return;
        }

        text = text.substring(0, caretIndex - 1) + text.substring(caretIndex);
        caretIndex--;
    }

    private int clampCaretIndex(int caretIndex) {
        return Math.max(0, Math.min(caretIndex, text.length()));
    }

    private java.lang.Character getTypedCharacter() {
        boolean shift = Engine.instance().keyboard.keyPressed(KeyEvent.VK_SHIFT);

        for (int key = KeyEvent.VK_A; key <= KeyEvent.VK_Z; key++) {
            if (Engine.instance().keyboard.keyJustPressed(key)) {
                char typed = (char) ('a' + key - KeyEvent.VK_A);
                return shift ? java.lang.Character.toUpperCase(typed) : typed;
            }
        }

        for (int key = KeyEvent.VK_0; key <= KeyEvent.VK_9; key++) {
            if (Engine.instance().keyboard.keyJustPressed(key)) {
                return (char) ('0' + key - KeyEvent.VK_0);
            }
        }

        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_SPACE)) {
            return ' ';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_MINUS)) {
            return '-';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_QUOTE)) {
            return '\"';
        }
        if (Engine.instance().keyboard.keyJustPressed(KeyEvent.VK_PERIOD)) {
            return '.';
        }

        return null;
    }
}
