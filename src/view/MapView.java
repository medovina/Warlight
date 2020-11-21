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

import game.*;
import utils.Util;

public class MapView extends JPanel {
    private static final long serialVersionUID = 1L;

    GameState game;
    SVGDiagram diagram;
    AffineTransform viewportTransform;
    Rectangle2D imageBounds;
    
    Point[] regionPositions;
    
    public MapView(GameState game, int width, int height) {
        this.game = game;
        diagram = game.getWorld().getDiagram();
        Rectangle viewport = new Rectangle(0, 0, width, height);
        diagram.setDeviceViewport(viewport);

        regionPositions = new Point[game.numRegions() + 1];
        SVGRoot root = diagram.getRoot();
        viewportTransform = root.calcViewportTransform(viewport);
        imageBounds = Util.getBoundingBox(root);
            
        for (int i = 1 ; i <= game.numRegions() ; ++i) {
            MapRegion r = game.getMapRegion(i);
            Point2D p = viewportTransform.transform(r.getLabelPosition(), null);
            regionPositions[i] = new Point((int) p.getX(), (int) p.getY());
        }
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

    Rectangle getBounds(RenderableElement e) {
        Rectangle2D bounds = Util.getBoundingBox(e);
        bounds = Util.toGlobal(e, bounds).getBounds2D();
        
        double xScale = getWidth() / imageBounds.getWidth();
        double yScale = getHeight() / imageBounds.getHeight();
        return new Rectangle(
            (int) (xScale * bounds.getX()), (int) (yScale * bounds.getY()),
            (int) (xScale * bounds.getWidth()), (int) (yScale * bounds.getHeight())
        );
    }

    void setOwner(int regionId, int player) {
        MapRegion r = game.getMapRegion(regionId);
        RenderableElement e = r.svgElement;
        try {
            Color color = PlayerColors.getColor(player);
            String colorString = String.format(
                "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            StyleAttribute a = Util.getAttribute(e, "fill");
            if (a.getStringValue().equals(colorString))
                return;

            e.setAttribute("fill", AnimationElement.AT_XML, colorString);

            repaint(getBounds(e));
        } catch (SVGException ex) { throw new RuntimeException(ex); }
    }

    MapRegion regionFromPoint(Point p) {
        List<List<SVGElement>> elements = new ArrayList<List<SVGElement>>();
        try {
            diagram.pick(p, elements);
        } catch (SVGException e) { throw new RuntimeException(e); }

        for (List<SVGElement> path : elements) {
            RenderableElement e = (RenderableElement) path.get(path.size() - 1);
            MapRegion r = game.getWorld().getMapRegion(e);
            if (r != null)
                return r;
        }

        return null;
    }

}
