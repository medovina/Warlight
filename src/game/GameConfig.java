package game;

public class GameConfig implements Cloneable {
    
    /**
     * Non-negative seed => use concrete seed.
     * Negative seed => pick random seed.
     */
    public int seed = -1;
    
    public int maxGameRounds = 100;
    
    public boolean manualDistribution = false;
    public boolean warlords = false;

    public static GameConfig fromString(String line) {
        GameConfig result = new GameConfig();
        
        String[] parts = line.split(";");
        
        result.seed = Integer.parseInt(parts[0]);
        result.maxGameRounds = Integer.parseInt(parts[1]);
        result.manualDistribution = Boolean.parseBoolean(parts[2]);
        result.warlords = Boolean.parseBoolean(parts[3]);

        return result;
    }

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
