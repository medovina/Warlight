package engine;

import java.util.ArrayList;

import game.GameConfig;
import utils.Util;

public class Config implements Cloneable {
    ArrayList<String> playerNames = new ArrayList<String>();
    ArrayList<String> agentInit = new ArrayList<String>();

    public long timeoutMillis = 60_000;
    
    public boolean visualize = true;
    public Boolean visualizeContinual = null;
    public Integer visualizeContinualFrameTimeMillis = null;
    
    public GameConfig gameConfig = new GameConfig();
    
    public Config() {
        playerNames.add("neutral");
        agentInit.add("neutral");
    }

    public void addHuman() {
        playerNames.add("You");
        agentInit.add("human");
        gameConfig.numPlayers = numPlayers();
    }

    public boolean isHuman(int player) {
        return agentInit.get(player).equals("human");
    }

    public void addAgent(String name, String fqcn) {
        playerNames.add(name);
        agentInit.add("internal:" + fqcn);
        gameConfig.numPlayers = numPlayers();
    }

    public void addAgent(String fqcn) {
        if (fqcn.equals("human") || fqcn.equals("me"))
            addHuman();
        else
            addAgent(Util.className(fqcn), fqcn);
    }

    public String playerName(int i) {
        return playerNames.get(i);
    }

    public String agentInit(int i) {
        return agentInit.get(i);
    }

    public int numPlayers() {
        return agentInit.size() - 1;
    }

    public String asString() {
        return playerName(1) + ";" + playerName(2) + ";" +
               timeoutMillis + ";" +
               visualize + ";" + visualizeContinual + ";" + visualizeContinualFrameTimeMillis + ";" +
               gameConfig.asString();
    }
    
    public String getCSVHeader() {
        return "PlayerName1;PlayerName2;timeoutMillis;" + gameConfig.getCSVHeader();
    }
    
    public String getCSV() {
        return playerName(1) + ";" + playerName(2) + ";" +
               timeoutMillis + ";" + gameConfig.getCSV();
    }    
}
