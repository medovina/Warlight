package game;

public enum ContinentData {
    
    North_America("North America", 1, 5),
    South_America("South America", 2, 2),
    Europe("Europe", 3, 5),
    Africa("Africa", 4, 3),    
    Asia("Asia", 5, 7),
    Australia("Australia", 6, 2);
    
    public final int id;
    public final int reward;
    public final String mapName;
    
    private ContinentData(String mapName, int id, int reward) {
        this.mapName = mapName;
        this.id = id;
        this.reward = reward;    
    }
}
