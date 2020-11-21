package game;

public enum RegionData {
    
    // NORTH AMERICA
    Alaska("Alaska", ContinentData.North_America, 2, 4, 30), 
    Northwest_Territory("Northwest Territories",
                        ContinentData.North_America, 3, 4, 5),
    Greenland("Greenland", ContinentData.North_America, 5, 6, 14),
    Alberta("Alberta", ContinentData.North_America, 5, 7),
    Ontario("Ontario", ContinentData.North_America, 6, 7, 8), 
    Quebec("Quebec", ContinentData.North_America, 8), 
    Western_United_States("Western US", ContinentData.North_America, 8, 9), 
    Eastern_United_States("Eastern US", ContinentData.North_America, 9), 
    Central_America("Central America", ContinentData.North_America, 10),
    
    // SOUTH AMERICA
    Venezuela("Venezuela", ContinentData.South_America, 11, 12), 
    Peru("Peru", ContinentData.South_America, 12, 13), 
    Brazil("Brazil", ContinentData.South_America, 13, 21), 
    Argentina("Argentina", ContinentData.South_America),
    
    // EUROPE
    Iceland("Iceland", ContinentData.Europe, 15, 16), 
    Great_Britain("Great Britain", ContinentData.Europe, 16, 18 ,19), 
    Scandinavia("Scandinavia", ContinentData.Europe, 17), 
    Ukraine("Ukraine", ContinentData.Europe, 19, 20, 27, 32, 36), 
    Western_Europe("Western Europe", ContinentData.Europe, 19, 20, 21), 
    Northern_Europe("Northern Europe", ContinentData.Europe, 20), 
    Southern_Europe("Southern Europe", ContinentData.Europe, 21, 22, 36),
    
    // AFRICA
    North_Africa("North Africa", ContinentData.Africa, 22, 23, 24), 
    Egypt("Egypt", ContinentData.Africa, 23, 36), 
    East_Africa("East Africa", ContinentData.Africa, 24, 25, 26, 36), 
    Congo("Congo", ContinentData.Africa, 25), 
    South_Africa("South Africa", ContinentData.Africa, 26), 
    Madagascar("Madagascar", ContinentData.Africa),
    
    // ASIA
    Ural("Ural", ContinentData.Asia, 28, 32, 33), 
    Siberia("Siberia", ContinentData.Asia, 29, 31, 33, 34), 
    Yakutsk("Yakutsk", ContinentData.Asia, 30, 31), 
    Kamchatka("Kamchatka", ContinentData.Asia, 31, 34, 35), 
    Irkutsk("Irkutsk", ContinentData.Asia, 34), 
    Kazakhstan("Kazakhstan", ContinentData.Asia, 33, 36, 37), 
    China("China", ContinentData.Asia, 34, 37, 38), 
    Mongolia("Mongolia", ContinentData.Asia, 35), 
    Japan("Japan", ContinentData.Asia), 
    Middle_East("Middle East", ContinentData.Asia, 37), 
    India("India", ContinentData.Asia, 38), 
    Siam("Siam", ContinentData.Asia, 39), 
    
    // AUSTRALIA
    Indonesia("Indonesia", ContinentData.Australia, 40, 41), 
    New_Guinea("New Guinea", ContinentData.Australia, 41, 42), 
    Western_Australia("Western Australia", ContinentData.Australia, 42), 
    Eastern_Australia("Eastern Australia", ContinentData.Australia);
    
    public final ContinentData continentData;
    public final String name;
    public final int[] forwardNeighbourIds;    
    
    private RegionData(String mapName, ContinentData continentData, int... forwardNeighbourIds) {        
        this.name = mapName;
        this.continentData = continentData;
        this.forwardNeighbourIds = forwardNeighbourIds;
    }
    
}
