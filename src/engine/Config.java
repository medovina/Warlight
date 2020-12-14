package engine;

import java.util.ArrayList;

import game.GameConfig;
import utils.Util;

class AgentConfig {
    public String name;
    public String init;

    public AgentConfig(String name, String init) {
        this.name = name;
        this.init = init;
    }
}

public class Config {
    ArrayList<AgentConfig> agentConfig = new ArrayList<AgentConfig>();

    public long timeoutMillis = 60_000;
    
    public boolean visualize = true;
    
    public GameConfig gameConfig = new GameConfig(0);
    
    public Config() {
        agentConfig.add(new AgentConfig("neutral", "neutral"));
    }

    public boolean isHuman(int player) {
        return agentInit(player).equals("human");
    }

    public void addAgent(String name) {
        int extraArmies = 0;

        int i = name.indexOf('+');
        if (i >= 0) {
            extraArmies = Integer.parseInt(name.substring(i + 1));
            name = name.substring(0, i);
        }

        if (name.equals("me") || name.equals("human"))
            agentConfig.add(new AgentConfig("You", "human"));
        else {
            String displayName = Util.className(name);
            if (extraArmies > 0)
                displayName = displayName + "+" + extraArmies;
            agentConfig.add(new AgentConfig(displayName, "internal:" + name));
        }

        gameConfig.addPlayer(extraArmies);
    }

    public void addHuman() {
        addAgent("human");
    }

    public String playerName(int i) {
        return agentConfig.get(i).name;
    }

    public String agentInit(int i) {
        return agentConfig.get(i).init;
    }

    public int numPlayers() {
        return agentConfig.size() - 1;
    }

    public String asString() {
        return playerName(1) + ";" + playerName(2) + ";" +
               timeoutMillis + ";" +
               visualize + ";" +
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
