package game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.GameMap;

public enum WorldRegion {
    
    // NORTH AMERICA
    Alaska("Alaska", "AK", 1, WorldContinent.North_America, true, 2, 4, 30), 
    Northwest_Territory("Northwest Territories", "NT",
                        2, WorldContinent.North_America, false, 3, 4, 5),
    Greenland("Greenland", "GR", 3, WorldContinent.North_America, true, 5, 6, 14),
    Alberta("Alberta", "AL", 4, WorldContinent.North_America, false, 5, 7),
    Ontario("Ontario", "ON", 5, WorldContinent.North_America, false, 6, 7, 8), 
    Quebec("Quebec", "QU", 6, WorldContinent.North_America, false, 8), 
    Western_United_States("Western US", "WU", 7, WorldContinent.North_America, false, 8, 9), 
    Eastern_United_States("Eastern US", "EU", 8, WorldContinent.North_America, false, 9), 
    Central_America("Central America", "CA", 9, WorldContinent.North_America, true, 10),
    
    // SOUTH AMERICA
    Venezuela("Venezuela", "VZ", 10, WorldContinent.South_America, true, 11, 12), 
    Peru("Peru", "PR", 11, WorldContinent.South_America, false, 12, 13), 
    Brazil("Brazil", "BZ", 12, WorldContinent.South_America, true, 13, 21), 
    Argentina("Argentina", "AR", 13, WorldContinent.South_America, false),
    
    // EUROPE
    Iceland("Iceland", "IC", 14, WorldContinent.Europe, true, 15, 16), 
    Great_Britain("Great Britain", "GB", 15, WorldContinent.Europe, false, 16, 18 ,19), 
    Scandinavia("Scandinavia", "SC", 16, WorldContinent.Europe, false, 17), 
    Ukraine("Ukraine", "UK", 17, WorldContinent.Europe, true, 19, 20, 27, 32, 36), 
    Western_Europe("Western Europe", "WE", 18, WorldContinent.Europe, true, 19, 20, 21), 
    Northern_Europe("Northern Europe", "NE", 19, WorldContinent.Europe, false, 20), 
    Southern_Europe("Southern Europe", "SE", 20, WorldContinent.Europe, true, 21, 22, 36),
    
    // AFRIKA
    North_Africa("North Africa", "NA", 21, WorldContinent.Africa, true, 22, 23, 24), 
    Egypt("Egypt", "EG", 22, WorldContinent.Africa, true, 23, 36), 
    East_Africa("East Africa", "EA", 23, WorldContinent.Africa, true, 24, 25, 26, 36), 
    Congo("Congo", "CG", 24, WorldContinent.Africa, false, 25), 
    South_Africa("South Africa", "SA", 25, WorldContinent.Africa, false, 26), 
    Madagascar("Madagascar", "MG", 26, WorldContinent.Africa, false),
    
    // ASIA
    Ural("Ural", "UR", 27, WorldContinent.Asia, true, 28, 32, 33), 
    Siberia("Siberia", "SB", 28, WorldContinent.Asia, false, 29, 31, 33, 34), 
    Yakutsk("Yakutsk", "YK", 29, WorldContinent.Asia, false, 30, 31), 
    Kamchatka("Kamchatka", "KA", 30, WorldContinent.Asia, true, 31, 34, 35), 
    Irkutsk("Irkutsk", "IR", 31, WorldContinent.Asia, false, 34), 
    Kazakhstan("Kazakhstan", "KZ", 32, WorldContinent.Asia, true, 33, 36, 37), 
    China("China", "CH", 33, WorldContinent.Asia, false, 34, 37, 38), 
    Mongolia("Mongolia", "MG", 34, WorldContinent.Asia, false, 35), 
    Japan("Japan", "JA", 35, WorldContinent.Asia, false), 
    Middle_East("Middle East", "ME", 36, WorldContinent.Asia, true, 37), 
    India("India", "IN", 37, WorldContinent.Asia, false, 38), 
    Siam("Siam", "SI", 38, WorldContinent.Asia, true, 39), 
    
    // AUSTRALIA
    Indonesia("Indonesia", "IS", 39, WorldContinent.Australia, true, 40, 41), 
    New_Guinea("New Guinea", "NG", 40, WorldContinent.Australia, false, 41, 42), 
    Western_Australia("Western Australia", "WS", 41, WorldContinent.Australia, false, 42), 
    Eastern_Australia("Eastern Australia", "ES", 42, WorldContinent.Australia, false);
    
    public static final int LAST_ID = 42;
            
    /**
     * Must be 1-based!
     */
    public final int id;
    public final WorldContinent worldContinent;
    private final String name;
    public final String abbrev;
    /**
     * Whether this region makes the border for the continent.
     */
    public final boolean continentBorder;
    
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
    
    private WorldRegion(String mapName, String abbrev, int id, WorldContinent superRegion, boolean continentBorder, int... forwardNeighbourIds) {        
        this.name = mapName;
        this.abbrev = abbrev;
        this.id = id;
        this.worldContinent = superRegion;
        this.continentBorder = continentBorder;
        this.forwardNeighbourIds = forwardNeighbourIds;
    }

    public String getName() {
       return name;
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
    
}
