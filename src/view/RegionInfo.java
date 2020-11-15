package view;

import java.awt.*;

public class RegionInfo {
    private String text;
    private Color highlight;

    private int armies = 0;
    public int armiesPlus = 0;

    public static final Color
        Gray = new Color(180, 180, 180),
        Green = new Color(70, 189, 123);
    
    public void setText(String s) {
        text = s;
    }
    
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
            int thickness = 3;
            
            g.setColor(highlight);
            g.drawOval(x + 5 - thickness, y + 4 - thickness,
                       30 + thickness * 2, 30 + thickness * 2);
        }

        g.setColor(Color.BLACK);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        g.drawString(text, x + 20 - w / 2, y + 24);
    }
}
