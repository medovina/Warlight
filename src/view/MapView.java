package view;

import java.awt.Color;
import java.awt.Dimension;
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

    Game game;
    SVGDiagram diagram;
    
    Point[] regionPositions;
    boolean hasScroll;
    
    public MapView(Game game) {
        this.game = game;
        diagram = game.getWorld().getDiagram();
        setPreferredSize(new Dimension((int) diagram.getWidth(), (int) diagram.getHeight()));
            
        regionPositions = new Point[game.numRegions()];
        for (int i = 0 ; i < game.numRegions() ; ++i) {
            Region r = game.getRegion(i);
            Point2D p = r.getLabelPosition();
            regionPositions[i] = new Point((int) p.getX(), (int) p.getY());
        }

        hasScroll = (diagram.getElement("scroll_background") != null);
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
        return Util.toGlobal(e, bounds).getBounds();
    }

    void setOwner(int regionId, int player) {
        Region r = game.getRegion(regionId);
        RenderableElement e = r.svgElement;
        try {
            Color color = PlayerColors.getColor(player);
            String colorString = String.format(
                "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            StyleAttribute a = Util.getAttribute(e, "fill");
            if (a.getStringValue().equals(colorString))
                return;

            e.setAttribute("fill", AnimationElement.AT_AUTO, colorString);

            repaint(getBounds(e));
        } catch (SVGException ex) { throw new RuntimeException(ex); }
    }

    Region regionFromPoint(Point p) {
        for (List<SVGElement> path : Util.pick(diagram, p)) {
            RenderableElement e = (RenderableElement) path.get(path.size() - 1);
            Region r = game.getWorld().getRegion(e);
            if (r != null)
                return r;
        }

        return null;
    }

    Continent rewardFromPoint(Point p) {
        for (List<SVGElement> path : Util.pick(diagram, p)) {
            if (path.size() < 2)
                continue;
            SVGElement g = path.get(path.size() - 2);
            if (g instanceof Group) {
                Continent c = game.getWorld().getContinentWithReward((Group) g);
                if (c != null)
                    return c;
            }
        }

        return null;
    }
}
