package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MapRegion {
    
    // NORTH AMERICA
    Alaska("Alaska", 1, MapContinent.North_America, 2, 4, 30), 
    Northwest_Territory("Northwest Territories",
                        2, MapContinent.North_America, 3, 4, 5),
    Greenland("Greenland", 3, MapContinent.North_America, 5, 6, 14),
    Alberta("Alberta", 4, MapContinent.North_America, 5, 7),
    Ontario("Ontario", 5, MapContinent.North_America, 6, 7, 8), 
    Quebec("Quebec", 6, MapContinent.North_America, 8), 
    Western_United_States("Western US", 7, MapContinent.North_America, 8, 9), 
    Eastern_United_States("Eastern US", 8, MapContinent.North_America, 9), 
    Central_America("Central America", 9, MapContinent.North_America, 10),
    
    // SOUTH AMERICA
    Venezuela("Venezuela", 10, MapContinent.South_America, 11, 12), 
    Peru("Peru", 11, MapContinent.South_America, 12, 13), 
    Brazil("Brazil", 12, MapContinent.South_America, 13, 21), 
    Argentina("Argentina", 13, MapContinent.South_America),
    
    // EUROPE
    Iceland("Iceland", 14, MapContinent.Europe, 15, 16), 
    Great_Britain("Great Britain", 15, MapContinent.Europe, 16, 18 ,19), 
    Scandinavia("Scandinavia",16, MapContinent.Europe, 17), 
    Ukraine("Ukraine", 17, MapContinent.Europe, 19, 20, 27, 32, 36), 
    Western_Europe("Western Europe", 18, MapContinent.Europe, 19, 20, 21), 
    Northern_Europe("Northern Europe", 19, MapContinent.Europe, 20), 
    Southern_Europe("Southern Europe", 20, MapContinent.Europe, 21, 22, 36),
    
    // AFRICA
    North_Africa("North Africa", 21, MapContinent.Africa, 22, 23, 24), 
    Egypt("Egypt", 22, MapContinent.Africa, 23, 36), 
    East_Africa("East Africa", 23, MapContinent.Africa, 24, 25, 26, 36), 
    Congo("Congo", 24, MapContinent.Africa, 25), 
    South_Africa("South Africa", 25, MapContinent.Africa, 26), 
    Madagascar("Madagascar", 26, MapContinent.Africa),
    
    // ASIA
    Ural("Ural", 27, MapContinent.Asia, 28, 32, 33), 
    Siberia("Siberia", 28, MapContinent.Asia, 29, 31, 33, 34), 
    Yakutsk("Yakutsk", 29, MapContinent.Asia, 30, 31), 
    Kamchatka("Kamchatka", 30, MapContinent.Asia, 31, 34, 35), 
    Irkutsk("Irkutsk", 31, MapContinent.Asia, 34), 
    Kazakhstan("Kazakhstan", 32, MapContinent.Asia, 33, 36, 37), 
    China("China", 33, MapContinent.Asia, 34, 37, 38), 
    Mongolia("Mongolia", 34, MapContinent.Asia, 35), 
    Japan("Japan", 35, MapContinent.Asia), 
    Middle_East("Middle East", 36, MapContinent.Asia, 37), 
    India("India", 37, MapContinent.Asia, 38), 
    Siam("Siam", 38, MapContinent.Asia, 39), 
    
    // AUSTRALIA
    Indonesia("Indonesia", 39, MapContinent.Australia, 40, 41), 
    New_Guinea("New Guinea", 40, MapContinent.Australia, 41, 42), 
    Western_Australia("Western Australia", 41, MapContinent.Australia, 42), 
    Eastern_Australia("Eastern Australia", 42, MapContinent.Australia);
    
    public static final int NUM_REGIONS = 42;
            
    /**
     * Must be 1-based!
     */
    public final int id;
    public final MapContinent worldContinent;
    private final String name;
    
    /**
     * DO NOT USE, contains only "forward" neighbours. Use {@link #getNeighbours()} to obtain ALL neighbours.
     * Used for {@link GameMap} initialization only.
     */
    private final int[] forwardNeighbourIds;    
    /**
     * DO NOT USE, contains only "forward" neighbours. Use {@link #getNeighbours()} to obtain ALL neighbours.
     * Used for {@link GameMap} initialization only.
     */
    private List<MapRegion> forwardNeighbours = null;
    
    /**
     * List of all neighbour regions.
     */
    private List<MapRegion> allNeighbours = null;    
    
    private MapRegion(String mapName, int id, MapContinent superRegion, int... forwardNeighbourIds) {        
        this.name = mapName;
        this.id = id;
        this.worldContinent = superRegion;
        this.forwardNeighbourIds = forwardNeighbourIds;
    }

    public String getName() {
       return name;
    }
    
    /**
     * All neighbour {@link MapRegion}s.
     * @return
     */
    public List<MapRegion> getNeighbours() {
        if (allNeighbours == null) {
            synchronized(this) {
                if (allNeighbours == null) {
                    // FIND MY NEIGHBOUR
                    List<MapRegion> neighbours = new ArrayList<MapRegion>();
                    for (int i = 0; i < forwardNeighbourIds.length; ++i) {
                        for (MapRegion region : MapRegion.values()) {
                            if (region.id == forwardNeighbourIds[i]) {
                                neighbours.add(region);
                                break;
                            }
                        }
                    }
                    
                    // FIND ME IN NEIGHBOURS OF OTHERS
                    for (MapRegion region : MapRegion.values()) {
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
    public List<MapRegion> getForwardNeighbours() {
        if (forwardNeighbours == null) {
            synchronized(this) {
                if (forwardNeighbours == null) {
                    // FIND MY FORWARD NEIGHBOURS
                    List<MapRegion> neighbours = new ArrayList<MapRegion>();
                    for (int i = 0; i < forwardNeighbourIds.length; ++i) {
                        for (MapRegion region : MapRegion.values()) {
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
    
    private static Map<Integer, MapRegion> id2Region = null;
    
    public static MapRegion forId(int id) {
        if (id2Region == null) {
            id2Region = new HashMap<Integer, MapRegion>();
            for (MapRegion region : MapRegion.values()) {
                id2Region.put(region.id, region);
            }
        }
        return id2Region.get(id);
    }
    
}
