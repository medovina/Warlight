package utils;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.*;

import com.kitfox.svg.*;
import com.kitfox.svg.xml.StyleAttribute;

public class Util {
    public static String capitalize(String s) {
        String[] words = s.split(" +");

        for (int i = 0 ; i < words.length ; ++i) {
            String w = words[i];
            if (!w.equals("of") && !w.equals("and") && !w.startsWith("d'"))
                words[i] = Character.toUpperCase(w.charAt(0)) + w.substring(1);
        }

        return String.join(" ", words);
    }

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

	public static StyleAttribute getAttribute(SVGElement e, String name) {
	    try {
	        StyleAttribute a = new StyleAttribute(name);
	        if (!e.getStyle(a))
	            throw new RuntimeException(String.format("can't get attribute '%s'", name));
	        return a;
	    } catch (SVGException ex) { throw new RuntimeException(ex); }
    }
    
	public static Rectangle2D getBoundingBox(RenderableElement e) {
	    try {
	        return e.getBoundingBox();
	    } catch (SVGException ex) {
	        throw new Error(ex);
	    }
    }
    
    public static List<List<SVGElement>> pick(SVGDiagram diagram, Point p) {
        List<List<SVGElement>> elements = new ArrayList<List<SVGElement>>();
        try {
            diagram.pick(p, elements);
            return elements;
        } catch (SVGException e) { throw new Error(e); }
    }

    public static Point toGlobal(RenderableElement e, Point p) {
        while (true) {
            e = (RenderableElement) e.getParent();
            if (e == null)
                break;
            AffineTransform t = e.getXForm();
            if (t != null)
                t.transform(p, p);
        }
        return p;
    }

    public static Shape toGlobal(RenderableElement e, Shape s) {
        while (true) {
            e = (RenderableElement) e.getParent();
            if (e == null)
                break;
            AffineTransform t = e.getXForm();
            if (t != null)
                s = t.createTransformedShape(s);
        }
        return s;
    }

}
