package game;

import java.io.*;
import java.net.URI;

import com.kitfox.svg.*;

public class WarlightMap {
    SVGDiagram diagram;

    public WarlightMap() {
        SVGUniverse universe = new SVGUniverse();
        URI uri;
        try (InputStream s = this.getClass().getResourceAsStream("/images/earth.svg")) {
            uri = universe.loadSVG(s, "earth");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        diagram = universe.getDiagram(uri);
        /* SVGElement map = diagram.getElement("map");

           for (SVGElement c : map.getChildren(null)) {
            System.out.printf("found continent: %s\n", c.getId());
            for (SVGElement d : c.getChildren(null))
                System.out.printf("  child: %s of class %s\n", d.getId(),
                    d.getClass().getSimpleName());
        } */
    }

    public SVGDiagram getDiagram() {
        return diagram;
    }
}
