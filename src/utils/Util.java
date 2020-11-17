package utils;

import java.awt.*;

public class Util {
    public static String className(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static void renderNicely(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static void drawCentered(Graphics g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        g.drawString(text, x - width / 2, y);
    }
}
