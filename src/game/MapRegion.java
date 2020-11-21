package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.kitfox.svg.Path;

public class MapRegion {
    public final Path svgElement;
    
    public final int id;
    public final MapContinent mapContinent;
    private final String name;
    public Point labelPosition;     // in global coordinates

    private List<MapRegion> neighbours = new ArrayList<MapRegion>();    
    
    public MapRegion(Path svgElement, String name, int id, MapContinent continent) {
        this.svgElement = svgElement;
        this.name = name;
        this.id = id;
        this.mapContinent = continent;
    }

    public String getName() {
       return name;
    }

    public void setLabelPosition(Point p) {
        labelPosition = p;
    }

    public Point getLabelPosition() {
        return labelPosition;
    }

    public void addNeighbor(MapRegion r) {
        if (!neighbours.contains(r))
            neighbours.add(r);
    }
    
    public List<MapRegion> getNeighbours() {
        return neighbours;
    }
}
