package game;

import java.util.ArrayList;

public class GameConfig {
    public String mapName;
    public int seed = -1;       // -1 will use random seed
    
    public int numPlayers;
    public ArrayList<Integer> extraArmies;

    public int maxGameRounds = 100;
    
    public boolean manualDistribution = false;
    public boolean warlords = false;

    public GameConfig(int numPlayers) {
        this.numPlayers = numPlayers;

        extraArmies = new ArrayList<Integer>();
        for (int i = 0 ; i < numPlayers + 1; ++i)
            extraArmies.add(0);
    }

    public GameConfig() { this(2); }

    public void addPlayer(int extra) {
        numPlayers += 1;
        extraArmies.add(extra);
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
