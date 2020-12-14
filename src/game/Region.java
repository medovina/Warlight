package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.kitfox.svg.Path;

public class Region {
    public final Path svgElement;
    
    public final int id;
    public final Continent continent;
    private final String name;
    public Point labelPosition;     // in global coordinates

    private List<Region> neighbours = new ArrayList<Region>();    
    
    public Region(Path svgElement, String name, int id, Continent continent) {
        this.svgElement = svgElement;
        this.name = name;
        this.id = id;
        this.continent = continent;
    }

    public int getId() {
        return id;
    }

    public Continent getContinent() {
        return continent;
    }

    public String getName() {
       return name;
    }

    public void setLabelPosition(Point p) {
        labelPosition = p;
    }

    public Point getLabelPosition() {
        if (labelPosition == null)
            throw new Error("region '" + name + "' has no label position");

        return labelPosition;
    }

    public void addNeighbor(Region r) {
        if (!neighbours.contains(r))
            neighbours.add(r);
    }
    
    public List<Region> getNeighbors() {
        return neighbours;
    }
}
