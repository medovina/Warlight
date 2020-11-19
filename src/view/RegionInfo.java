package view;

import java.awt.*;

public class RegionInfo {
    public Color highlight;

    public int armies = 0;
    public int armiesPlus = 0;

    public static final Color
        Gray = Color.gray,
        Green = Color.green.darker().darker();
    
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
}
