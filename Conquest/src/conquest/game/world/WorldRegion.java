package conquest.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import conquest.game.GameMap;

public enum WorldRegion {
    
    // NORTH AMERICA
    Alaska("Alaska", 1, WorldContinent.North_America, true, 2, 4, 30), 
    Northwest_Territory("Northwest Terr.", 2, WorldContinent.North_America, false, 3, 4, 5),
    Greenland("Greenland", 3, WorldContinent.North_America, true, 5, 6, 14),
    Alberta("Alberta", 4, WorldContinent.North_America, false, 5, 7),
    Ontario("Ontario", 5, WorldContinent.North_America, false, 6, 7, 8), 
    Quebec("Quebec", 6, WorldContinent.North_America, false, 8), 
    Western_United_States("Western US", 7, WorldContinent.North_America, false, 8, 9), 
    Eastern_United_States("Eastern US", 8, WorldContinent.North_America, false, 9), 
    Central_America("Central America", 9, WorldContinent.North_America, true, 10),
    
    // SOUTH AMERICA
    Venezuela("Venezuela", 10, WorldContinent.South_America, true, 11, 12), 
    Peru("Peru", 11, WorldContinent.South_America, false, 12, 13), 
    Brazil("Brazil", 12, WorldContinent.South_America, true, 13, 21), 
    Argentina("Argentina", 13, WorldContinent.South_America, false),
    
    // EUROPE
    Iceland("Iceland", 14, WorldContinent.Europe, true, 15, 16), 
    Great_Britain("Great Britain", 15, WorldContinent.Europe, false, 16, 18 ,19), 
    Scandinavia("Scandinavia", 16, WorldContinent.Europe, false, 17), 
    Ukraine("Ukraine", 17, WorldContinent.Europe, true, 19, 20, 27, 32, 36), 
    Western_Europe("West. Eur", 18, WorldContinent.Europe, true, 19, 20, 21), 
    Northern_Europe("North. Eur", 19, WorldContinent.Europe, false, 20), 
    Southern_Europe("South. Eur", 20, WorldContinent.Europe, true, 21, 22, 36),
    
    // AFRIKA
    North_Africa("North Africa", 21, WorldContinent.Africa, true, 22, 23, 24), 
    Egypt("Egypt", 22, WorldContinent.Africa, true, 23, 36), 
    East_Africa("East Africa", 23, WorldContinent.Africa, true, 24, 25, 26, 36), 
    Congo("Congo", 24, WorldContinent.Africa, false, 25), 
    South_Africa("South Africa", 25, WorldContinent.Africa, false, 26), 
    Madagascar("Madagascar", 26, WorldContinent.Africa, false),
    
    // ASIA
    Ural("Ural", 27, WorldContinent.Asia, true, 28, 32, 33), 
    Siberia("Siberia", 28, WorldContinent.Asia, false, 29, 31, 33, 34), 
    Yakutsk("Yakutsk", 29, WorldContinent.Asia, false, 30, 31), 
    Kamchatka("Kamchatka", 30, WorldContinent.Asia, true, 31, 34, 35), 
    Irkutsk("Irkutsk", 31, WorldContinent.Asia, false, 34), 
    Kazakhstan("Kazakhstan", 32, WorldContinent.Asia, true, 33, 36, 37), 
    China("China", 33, WorldContinent.Asia, false, 34, 37, 38), 
    Mongolia("Mongolia", 34, WorldContinent.Asia, false, 35), 
    Japan("Japan", 35, WorldContinent.Asia, false), 
    Middle_East("Middle East", 36, WorldContinent.Asia, true, 37), 
    India("India", 37, WorldContinent.Asia, false, 38), 
    Siam("Siam", 38, WorldContinent.Asia, true, 39), 
    
    // AUSTRALIA
    Indonesia("Indonesia", 39, WorldContinent.Australia, true, 40, 41), 
    New_Guinea("New Guinea", 40, WorldContinent.Australia, false, 41, 42), 
    Western_Australia("West. Australia", 41, WorldContinent.Australia, false, 42), 
    Eastern_Australia("East. Australia", 42, WorldContinent.Australia, false);
    
    public static final int LAST_ID = 42;
            
    /**
     * Must be 1-based!
     */
    public final int id;
    public final WorldContinent worldContinent;
    public final String mapName;
    /**
     * Whether this region makes the border for the continent.
     */
    public final boolean continentBorder;
    
    /**
     * Region flag.
     */
    public final long regionFlag;
    
    /**
     * DO NOT USE, contains only "forward" neighbours. Use {@link #getNeighbours()} to obtain ALL neighbours.
     * Used for {@link GameMap} initialization only.
     */
    private final int[] forwardNeighbourIds;    
    /**
     * DO NOT USE, contains only "forward" neighbours. Use {@link #getNeighbours()} to obtain ALL neighbours.
     * Used for {@link GameMap} initialization only.
     */
    private List<WorldRegion> forwardNeighbours = null;
    
    /**
     * List of all neighbour regions.
     */
    private List<WorldRegion> allNeighbours = null;    
    
    private WorldRegion(String mapName, int id, WorldContinent superRegion, boolean continentBorder, int... forwardNeighbourIds) {        
        this.mapName = mapName;
        this.id = id;
        this.worldContinent = superRegion;
        this.continentBorder = continentBorder;
        this.regionFlag = ((long)1) << (id-1);
        this.forwardNeighbourIds = forwardNeighbourIds;
    }
    
    /**
     * All neighbour {@link WorldRegion}s.
     * @return
     */
    public List<WorldRegion> getNeighbours() {
        if (allNeighbours == null) {
            synchronized(this) {
                if (allNeighbours == null) {
                    // FIND MY NEIGHBOUR
                    List<WorldRegion> neighbours = new ArrayList<WorldRegion>();
                    for (int i = 0; i < forwardNeighbourIds.length; ++i) {
                        for (WorldRegion region : WorldRegion.values()) {
                            if (region.id == forwardNeighbourIds[i]) {
                                neighbours.add(region);
                                break;
                            }
                        }
                    }
                    
                    // FIND ME IN NEIGHBOURS OF OTHERS
                    for (WorldRegion region : WorldRegion.values()) {
                        for (int id : region.forwardNeighbourIds) {
                            if (id == this.id) {
                                neighbours.add(region);
                                break;
                            }
                        }
                    }
                    
                    // SET THE LIST
                    this.allNeighbours = neighbours;
                }
            }    
        }
        return allNeighbours;
    }
    
    /**
     * USED ONLY FOR THE MAP INITIALIZATION ... for the full list of neighbours use {@link #getNeighbours()}.
     * @return
     */
    public List<WorldRegion> getForwardNeighbours() {
        if (forwardNeighbours == null) {
            synchronized(this) {
                if (forwardNeighbours == null) {
                    // FIND MY FORWARD NEIGHBOURS
                    List<WorldRegion> neighbours = new ArrayList<WorldRegion>();
                    for (int i = 0; i < forwardNeighbourIds.length; ++i) {
                        for (WorldRegion region : WorldRegion.values()) {
                            if (region.id == forwardNeighbourIds[i]) {
                                neighbours.add(region);
                                break;
                            }
                        }
                    }
                    
                    this.forwardNeighbours = neighbours;
                }
            }    
        }
        return forwardNeighbours;
    }
    
    private static Map<Integer, WorldRegion> id2Region = null;
    
    public static WorldRegion forId(int id) {
        if (id2Region == null) {
            id2Region = new HashMap<Integer, WorldRegion>();
            for (WorldRegion region : WorldRegion.values()) {
                id2Region.put(region.id, region);
            }
        }
        return id2Region.get(id);
    }
    
    private static Map<Long, WorldRegion> flagToRegion = null;
        
    public static WorldRegion fromFlag(long regionFlag) {
        if (flagToRegion == null) {
            flagToRegion = new HashMap<Long, WorldRegion>();
            for (WorldRegion region : WorldRegion.values()) {
                flagToRegion.put(region.regionFlag, region);
            }
        }
        return flagToRegion.get(regionFlag);
    }

}
