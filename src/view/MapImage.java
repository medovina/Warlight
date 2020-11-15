package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;

import com.kitfox.svg.*;

import game.world.WorldRegion;

public class MapImage extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    GUI gui;
    SVGDiagram diagram;
    
    public MapImage(GUI gui, int width, int height) {
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

    @Override
    public void mousePressed(MouseEvent e)
    {
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
        List<List<SVGElement>> elements = new ArrayList<List<SVGElement>>();
        try {
            diagram.pick(event.getPoint(), elements);
        } catch (SVGException e) { throw new RuntimeException(e); }

        for (List<SVGElement> path : elements) {
            RenderableElement e = (RenderableElement) path.get(path.size() - 1);
            String id = e.getId();
            if (id.startsWith("region")) {
                int regionId = Integer.parseInt(id.substring(6));
                return WorldRegion.forId(regionId).getName();
            }
        }

        return "";
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        Point p = e.getPoint();
        return new Point(p.x + 20, p.y + 10);
    }

}
