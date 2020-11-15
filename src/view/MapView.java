package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;

import com.kitfox.svg.*;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;

import game.Team;
import game.world.WorldRegion;

public class MapView extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    GUI gui;
    SVGDiagram diagram;
    Rectangle2D imageBounds;
    
    public MapView(GUI gui, int width, int height) {
        this.gui = gui;

        SVGUniverse universe = new SVGUniverse();
        URI uri;
        try (InputStream s = this.getClass().getResourceAsStream("/images/warlight-map.svg")) {
            uri = universe.loadSVG(s, "warlight-map");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        diagram = universe.getDiagram(uri);
        diagram.setDeviceViewport(new Rectangle(0, 0, width, height));

        SVGRoot root = diagram.getRoot();
        try {
            imageBounds = root.getBoundingBox();
        } catch (SVGException e) { throw new RuntimeException(e); }

        hideLabels(root);

        addMouseListener(this);

        ToolTipManager m = ToolTipManager.sharedInstance();
        m.setDismissDelay(10000);
        m.setInitialDelay(10);
        m.setReshowDelay(10);

        setToolTipText("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // System.out.print("rendering...");
            // long start = System.currentTimeMillis();
            diagram.render(g2);
            // long elapsed = System.currentTimeMillis() - start;
            // System.out.printf("done in %d ms\n", elapsed);
        } catch (SVGException e) {
            throw new RuntimeException(e);
        }
    }

    void setOwner(int regionId, int player) {
        RenderableElement e = (RenderableElement) diagram.getElement("region" + regionId);
        try {
            Color color = TeamView.getColor(Team.getTeam(player));
            String colorString = String.format(
                "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            StyleAttribute a = new StyleAttribute("fill");
            if (!e.getStyle(a))
                throw new RuntimeException("can't get fill attribute");
            if (a.getStringValue().equals(colorString))
                return;

            e.setAttribute("fill", AnimationElement.AT_XML, colorString);

            Rectangle2D bounds = e.getBoundingBox();
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
            Rectangle deviceBounds = new Rectangle(
                (int) (xScale * bounds.getX()), (int) (yScale * bounds.getY()),
                (int) (xScale * bounds.getWidth()), (int) (yScale * bounds.getHeight())
            );

            repaint(deviceBounds);
        } catch (SVGException ex) { throw new RuntimeException(ex); }
    }

    void hideLabels(SVGElement e) {
        if (e.getId().endsWith("Text")) {
            try {
                e.addAttribute("display", AnimationElement.AT_CSS, "none");
            } catch (SVGElementException ex) { throw new RuntimeException(ex); }
        }
        for (SVGElement e1 : e.getChildren(null))
            hideLabels(e1);
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

    @Override
    public void mousePressed(MouseEvent e)
    {
        int id = regionFromPoint(e.getPoint());
        if (id >= 0)
            gui.regionClicked(id, e.getButton() == MouseEvent.BUTTON1);
        else
            gui.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gui.mouseReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int id = regionFromPoint(event.getPoint());
        return id == -1 ? "" : WorldRegion.forId(id).getName();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        Point p = e.getPoint();
        return new Point(p.x + 20, p.y + 10);
    }

}
