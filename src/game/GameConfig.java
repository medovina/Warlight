package game;

public class GameConfig implements Cloneable {
    
    /**
     * Non-negative seed => use concrete seed.
     * Negative seed => pick random seed.
     */
    public int seed = -1;
    
    public boolean fullyObservableGame = true;
    
    public int startingArmies = 5;
    public int maxGameRounds = 100;
    
    public FightMode fight = FightMode.CONTINUAL_1_1_A60_D70;
    
    public boolean manualDistribution = false;
    public boolean warlords = false;

    public static GameConfig fromString(String line) {
        GameConfig result = new GameConfig();
        
        String[] parts = line.split(";");
        
        result.seed = Integer.parseInt(parts[0]);
        result.fullyObservableGame = Boolean.parseBoolean(parts[1]);
        result.startingArmies = Integer.parseInt(parts[2]);
        result.maxGameRounds = Integer.parseInt(parts[3]);
        result.fight = FightMode.valueOf(parts[4]);
        result.manualDistribution = Boolean.parseBoolean(parts[5]);
        result.warlords = Boolean.parseBoolean(parts[6]);

        return result;
    }

    public String getCSVHeader() {
        return "seed;fullyObservable;startingArmies;maxGameRounds;fightMode;" +
               "manualDistribution;warlords";         
    }
    
    public String getCSV() {
        return seed + ";" + fullyObservableGame + ";" +
               startingArmies + ";" + maxGameRounds + ";" + fight + ";" +
               manualDistribution + ";" + warlords;
    }
    
    public String asString() {
        return getCSV();
    }
}
