package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.*;
import java.net.URI;
import javax.swing.JPanel;

import com.kitfox.svg.*;

public class MapImage extends JPanel {
    private static final long serialVersionUID = 1L;

    SVGDiagram diagram;
    
    public MapImage(int width, int height) {
        SVGUniverse universe = new SVGUniverse();
        URI uri;
        try (InputStream s = this.getClass().getResourceAsStream("/images/warlight-map.svg")) {
            uri = universe.loadSVG(s, "warlight-map");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        diagram = universe.getDiagram(uri);
        diagram.setDeviceViewport(new Rectangle(0, 0, width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            diagram.render(g2);
        } catch (SVGException e) {
            throw new RuntimeException(e);
        }
    }
}
