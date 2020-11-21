package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import com.kitfox.svg.*;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;

import game.MapRegion;
import game.WarlightMap;
import utils.Util;

public class MapView extends JPanel {
    private static final long serialVersionUID = 1L;

    GUI gui;
    SVGDiagram diagram;
    AffineTransform viewportTransform;
    Rectangle2D imageBounds;
    
    Point[] regionPositions = new Point[MapRegion.NUM_REGIONS + 1];
    
    public MapView(GUI gui, int width, int height) {
        diagram = new WarlightMap().getDiagram();
        Rectangle viewport = new Rectangle(0, 0, width, height);
        diagram.setDeviceViewport(viewport);

        SVGRoot root = diagram.getRoot();
        viewportTransform = root.calcViewportTransform(viewport);
        try {
            imageBounds = root.getBoundingBox();
            
            for (int i = 1 ; i <= MapRegion.NUM_REGIONS ; ++i) {
                Text t = (Text) diagram.getElement("region" + i + "Text");
                Point p = new Point(getAttribute(t, "x").getIntValue(),
                                    getAttribute(t, "y").getIntValue());
                regionPositions[i] = localToViewport(t, p);
                // System.out.printf("region %d: x = %d, y = %d",i, regionPositions[i].x, regionPositions[i].y);
                t.addAttribute("display", AnimationElement.AT_CSS, "none");
            }
        } catch (SVGException e) { throw new RuntimeException(e); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Graphics2D g2 = (Graphics2D) g;
            Util.renderNicely(g2);
            // System.out.print("rendering...");
            // long start = System.currentTimeMillis();
            diagram.render(g2);
            // long elapsed = System.currentTimeMillis() - start;
            // System.out.printf("done in %d ms\n", elapsed);
        } catch (SVGException e) {
            throw new RuntimeException(e);
        }
    }

    StyleAttribute getAttribute(SVGElement e, String name) {
        try {
            StyleAttribute a = new StyleAttribute(name);
            if (!e.getStyle(a))
                throw new RuntimeException(String.format("can't get attribute '%s'", name));
            return a;
        } catch (SVGException ex) { throw new RuntimeException(ex); }
    }

    Point localToViewport(RenderableElement e, Point p) {
        while (true) {
            e = (RenderableElement) e.getParent();
            if (e == null)
                break;
            AffineTransform t = e.getXForm();
            if (t != null)
                t.transform(p, p);
        }
        viewportTransform.transform(p, p);
        return p;
    }

    Rectangle getBounds(RenderableElement e) {
        Rectangle2D bounds;
        
        try {
            bounds = e.getBoundingBox();
        } catch (SVGException ex) { throw new RuntimeException(ex); }
        while (true) {
            e = (RenderableElement) e.getParent();
            if (e == null)
                break;
            AffineTransform t = e.getXForm();
            if (t != null)
                bounds = t.createTransformedShape(bounds).getBounds2D();
        }

        double xScale = getWidth() / imageBounds.getWidth();
        double yScale = getHeight() / imageBounds.getHeight();
        return new Rectangle(
            (int) (xScale * bounds.getX()), (int) (yScale * bounds.getY()),
            (int) (xScale * bounds.getWidth()), (int) (yScale * bounds.getHeight())
        );
    }

    void setOwner(int regionId, int player) {
        RenderableElement e = (RenderableElement) diagram.getElement("region" + regionId);
        try {
            Color color = PlayerColors.getColor(player);
            String colorString = String.format(
                "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            StyleAttribute a = getAttribute(e, "fill");
            if (a.getStringValue().equals(colorString))
                return;

            e.setAttribute("fill", AnimationElement.AT_XML, colorString);

            repaint(getBounds(e));
        } catch (SVGException ex) { throw new RuntimeException(ex); }
    }

    int regionFromPoint(Point p) {
        List<List<SVGElement>> elements = new ArrayList<List<SVGElement>>();
        try {
            diagram.pick(p, elements);
        } catch (SVGException e) { throw new RuntimeException(e); }

        for (List<SVGElement> path : elements) {
            RenderableElement e = (RenderableElement) path.get(path.size() - 1);
            String id = e.getId();
            if (id.startsWith("region")) {
                return Integer.parseInt(id.substring(6));
            }
        }

        return -1;
    }

}
