package game;

import java.util.*;

public class MapContinent {
    public final int id;
    public int reward;
    public final String mapName;
    
    private List<MapRegion> regions = new ArrayList<MapRegion>();
    
    public MapContinent(String mapName, int id) {
        this.mapName = mapName;
        this.id = id;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void addRegion(MapRegion r) {
        regions.add(r);
    }
    
    public List<MapRegion> getRegions() {
        return regions;
    }
}
