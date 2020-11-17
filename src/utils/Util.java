package utils;

import java.awt.*;

public class Util {
    public static String className(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static void drawCentered(Graphics g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        g.drawString(text, x - width / 2, y);
    }
}
