package game;

import java.util.ArrayList;
import java.util.List;

public class MapRegion {
    public final int id;
    public final MapContinent mapContinent;
    private final String name;
    private List<MapRegion> neighbours = new ArrayList<MapRegion>();    
    
    public MapRegion(String name, int id, MapContinent continent) {        
        this.name = name;
        this.id = id;
        this.mapContinent = continent;
    }

    public String getName() {
       return name;
    }

    public void addNeighbor(MapRegion r) {
        neighbours.add(r);
    }
    
    public List<MapRegion> getNeighbours() {
        return neighbours;
    }
}
