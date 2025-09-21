package atomiccode.cthulhuEngine.ui;

import java.awt.*;

public class Button {
    public int x, y;
    private int width, height;
    private String text;
    private boolean hovered, pressed, selected;
    private Color normalColor, hoverColor, pressedColor, textColor;
    private Font font;
    private Runnable onClick;
    
    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.hovered = false;
        this.pressed = false;
        this.selected = false;
        
        // Default colors
        this.normalColor = new Color(50, 50, 50);
        this.hoverColor = new Color(70, 70, 70);
        this.pressedColor = new Color(30, 30, 30);
        this.textColor = Color.WHITE;
        this.font = new Font("Arial", Font.PLAIN, 16);
    }
    
    public void setColors(Color normal, Color hover, Color pressed, Color text) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        this.textColor = text;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
    
    public void update(int mouseX, int mouseY, boolean mousePressed) {
        // Check if mouse is over button
        hovered = (mouseX >= x && mouseX <= x + width && 
                  mouseY >= y && mouseY <= y + height);
        
        // Check if button is being pressed
        if (hovered && mousePressed) {
            pressed = true;
        } else if (!mousePressed && pressed && hovered) {
            // Button was released while hovering - trigger click
            if (onClick != null) {
                onClick.run();
            }
            pressed = false;
        } else if (!mousePressed) {
            pressed = false;
        }
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Determine button color based on state
        Color buttonColor = normalColor;
        if (selected) {
            buttonColor = hoverColor;
        } else if (pressed) {
            buttonColor = pressedColor;
        } else if (hovered) {
            buttonColor = hoverColor;
        }
        
        // Draw button background
        g2d.setColor(buttonColor);
        g2d.fillRect(x, y, width, height);
        
        // Draw button border
        g2d.setColor(selected ? Color.CYAN : Color.GRAY);
        g2d.drawRect(x, y, width, height);
        
        // Draw text
        g2d.setColor(textColor);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);
    }
    
    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }
    
    public void click() {
        if (onClick != null) {
            onClick.run();
        }
    }
}
