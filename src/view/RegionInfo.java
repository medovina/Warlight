package view;

import java.awt.*;

public class RegionInfo {
    private Color highlight;

    private int armies = 0;
    public int armiesPlus = 0;

    public static final Color
        Gray = new Color(180, 180, 180),
        Green = new Color(70, 189, 123);
    
    public void setHighlight(Color c) {
        this.highlight = c;
    }
        
    public void setHighlight(boolean state) {
        setHighlight(state ? Color.WHITE : null);
    }
    
    public int getArmies() {
        return armies;
    }
    
    public void setArmies(int armies) {
        this.armies = armies;
    }
    
    protected void draw(Graphics g, int x, int y) {
        if (highlight != null) {
            g.setColor(highlight);
            g.drawOval(x - 16, y - 22, 36, 36);
        }

        g.setColor(Color.BLACK);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);

        String text = "" + armies;
        if (armiesPlus > 0)
            text += "+" + armiesPlus;
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        g.drawString(text, x + 2 - w / 2, y);
    }
}
