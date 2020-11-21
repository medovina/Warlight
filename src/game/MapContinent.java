package game;

import java.util.*;

public enum MapContinent {
    
    North_America("North America", 1, 5),
    South_America("South America", 2, 2),
    Europe("Europe", 3, 5),
    Africa("Africa", 4, 3),    
    Asia("Asia", 5, 7),
    Australia("Australia", 6, 2);
    
    public static final int LAST_ID = 6;
    
    /**
     * Must be 1-based!
     */
    public final int id;
    public final int reward;
    public final String mapName;
    
    private List<MapRegion> regions = null;
    
    private MapContinent(String mapName, int id, int reward) {
        this.mapName = mapName;
        this.id = id;
        this.reward = reward;    
    }
    
    public List<MapRegion> getRegions() {
        if (regions == null) {
            synchronized(this) {
                if (regions == null) {
                    List<MapRegion> regions = new ArrayList<MapRegion>();
                    for (MapRegion regionName : MapRegion.values()) {
                        if (regionName.worldContinent == this) {
                            regions.add(regionName);
                        }
                    }
                    this.regions = regions;
                }
            }
        }
        return regions;
    }
    
    private static Map<Integer, MapContinent> id2Continent = null;
    
    public static MapContinent forId(int id) {
        if (id2Continent == null) {
            id2Continent = new HashMap<Integer, MapContinent>();
            for (MapContinent continent : MapContinent.values()) {
                id2Continent.put(continent.id, continent);
            }
        }
        return id2Continent.get(id);
    }
}
