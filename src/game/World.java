package game;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.*;
import java.net.URI;
import java.util.*;

import com.kitfox.svg.*;
import com.kitfox.svg.animation.AnimationElement;

import utils.Util;

public class World {
    SVGDiagram diagram;

    ArrayList<Continent> continents = new ArrayList<Continent>();
    ArrayList<Region> regions = new ArrayList<Region>();

    public World() {
        SVGUniverse universe = new SVGUniverse();
        URI uri;
        try (InputStream s = this.getClass().getResourceAsStream("/images/earth.svg")) {
            uri = universe.loadSVG(s, "earth");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        diagram = universe.getDiagram(uri);

        SVGElement map = diagram.getElement("map");
        String mapDesc = createRegions(map);

        findNeighbors();

        if (mapDesc != null)
            for (String line : mapDesc.split("\n")) {
                String[] w = line.split(" +");
                if (w[0].strip().equals("adj")) {
                    Region r = getRegion(w[1].strip());
                    Region s = getRegion(w[2].strip());
                    r.addNeighbor(s);
                    s.addNeighbor(r);
                } else throw new Error("unknown keyword in map description");
            }

        findLabels();
    }

    String createRegions(SVGElement map) {
        String mapDesc = null;

        for (SVGElement e : map.getChildren(null))
            if (e instanceof Desc) {
                mapDesc = ((Desc) e).getText();
            } else {
                Continent c = new Continent(Util.decamel(e.getId()), continents.size());
                continents.add(c);

                for (SVGElement d : e.getChildren(null)) {
                    if (d instanceof Path) {
                        Path p = (Path) d;
                        Region r = new Region(p, Util.decamel(p.getId()), regions.size(), c);
                        regions.add(r);
                        c.addRegion(r);
                    } else if (d instanceof Desc) {
                        String s = ((Desc) d).getText();
                        String[] w = s.split(" +");
                        if (w[0].strip().equals("bonus"))
                            c.setReward(Integer.parseInt(w[1]));
                        else throw new Error("unknown keyword in continent description");
                    }
            }
        }
        return mapDesc;
    }

    void findNeighbors() {
        final int Dist = 3;
        double[] coords = new double[6];
        for (Region r : regions) {
            Path rp = r.svgElement;
            for (Region s : regions) {
                if (r.id >= s.id)
                    continue;
                Path sp = s.svgElement;
                if (Util.getBoundingBox(rp).intersects(Util.getBoundingBox(sp))) {
                    for (PathIterator pi = rp.getShape().getPathIterator(null);
                         !pi.isDone(); pi.next()) {
                        int i;
                        switch (pi.currentSegment(coords)) {
                            case PathIterator.SEG_MOVETO:
                            case PathIterator.SEG_LINETO:
                                i = 0;
                                break;
                            case PathIterator.SEG_QUADTO:
                                i = 2;
                                break;
                            case PathIterator.SEG_CUBICTO:
                                i = 4;
                                break;
                            case PathIterator.SEG_CLOSE:
                                continue;
                            default:
                                throw new Error("unknown segment type");
                        }
                        if (sp.getShape().intersects(coords[i] - Dist, coords[i + 1] - Dist, 2 * Dist, 2 * Dist)) {
                            r.addNeighbor(s);
                            s.addNeighbor(r);
                            break;
                        }
                    }
                }
            }
        }
    }

    void findLabels() {
        SVGElement text = diagram.getElement("text");
        for (SVGElement e : text.getChildren(null)) {
            Text t = (Text) e;
            Point p = new Point(Util.getAttribute(t, "x").getIntValue(), Util.getAttribute(t, "y").getIntValue());
            p = Util.toGlobal(t, p);
            for (Region r : regions) {
                Shape s = Util.toGlobal(r.svgElement, r.svgElement.getShape());
                if (s.contains(p)) {
                    r.setLabelPosition(p);
                    break;
                }
            }
            try {
                t.addAttribute("display", AnimationElement.AT_CSS, "none");
            } catch (SVGElementException ex) { throw new Error(ex); }
        }
    }

    public SVGDiagram getDiagram() {
        return diagram;
    }

    public int numContinents() {
        return continents.size();
    }

    public List<Continent> getContinents() {
        return continents;
    }

    public Continent getContinent(int id) {
        return continents.get(id);
    }

    public int numRegions() {
        return regions.size();
    }

    public List<Region> getRegions() {
        return regions;
    }

    public Region getRegion(int i) {
        return regions.get(i);
    }

    public Region getRegion(String id) {
        for (Region r : regions)
            if (r.svgElement.getId().equals(id))
                return r;

        throw new Error("no region with id '" + id + "'");
    }

    public Region getRegion(SVGElement e) {
        for (Region r : regions)
            if (r.svgElement == e)
                return r;
                
        return null;
    }
}
