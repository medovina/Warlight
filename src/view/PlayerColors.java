package view;

import java.awt.Color;

public class PlayerColors {
    static final Color[] playerColors = {
         new Color(220, 220, 220),  // neutral
         new Color(160, 160, 255),
         new Color(255, 160, 160),
         new Color(160, 255, 160),
         new Color(160, 198, 208)
    };

    static final Color[] highlightColors = {
         new Color(245, 245, 245),
         new Color(180, 180, 255),
         new Color(255, 180, 180),
         new Color(180, 255, 180),
         new Color(180, 218, 228)
    };
    
    public static Color getColor(int player) {
        return playerColors[player];
    }
    
    public static Color getHighlightColor(int player) {
        return highlightColors[player];
    }
}
