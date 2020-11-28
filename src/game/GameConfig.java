package game;

public class GameConfig implements Cloneable {
    
    /**
     * Non-negative seed => use concrete seed.
     * Negative seed => pick random seed.
     */
    public int seed = -1;
    
    public int numPlayers = 2;

    public int maxGameRounds = 100;
    
    public boolean manualDistribution = false;
    public boolean warlords = false;

    public String getCSVHeader() {
        return "seed;maxGameRounds;manualDistribution;warlords";         
    }
    
    public String getCSV() {
        return seed + ";" + maxGameRounds + ";" + manualDistribution + ";" + warlords;
    }
    
    public String asString() {
        return getCSV();
    }
}
