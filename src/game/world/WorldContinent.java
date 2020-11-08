package game.world;

import java.util.*;

public enum WorldContinent {
    
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
    
    private List<WorldRegion> regions = null;
    
    private WorldContinent(String mapName, int id, int reward) {
        this.mapName = mapName;
        this.id = id;
        this.reward = reward;    
    }
    
    public List<WorldRegion> getRegions() {
        if (regions == null) {
            synchronized(this) {
                if (regions == null) {
                    List<WorldRegion> regions = new ArrayList<WorldRegion>();
                    for (WorldRegion regionName : WorldRegion.values()) {
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
    
    private static Map<Integer, WorldContinent> id2Continent = null;
    
    public static WorldContinent forId(int id) {
        if (id2Continent == null) {
            id2Continent = new HashMap<Integer, WorldContinent>();
            for (WorldContinent continent : WorldContinent.values()) {
                id2Continent.put(continent.id, continent);
            }
        }
        return id2Continent.get(id);
    }
}
