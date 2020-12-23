package engine;

import java.util.ArrayList;

import game.GameConfig;
import utils.Util;

class AgentConfig {
    public String name;
    public String init;
    int extraArmies;

    public AgentConfig(String name, String init, int extraArmies) {
        this.name = name;
        this.init = init;
        this.extraArmies = extraArmies;
    }

    public String fullName() {
        String n = name;
        if (extraArmies > 0)
            n = n + "+" + extraArmies;
        return n;
    }
}

public class Config {
    ArrayList<AgentConfig> agentConfig = new ArrayList<AgentConfig>();

    public long timeoutMillis = 60_000;
    
    public boolean visualize = true;
    
    public GameConfig gameConfig = new GameConfig(0);
    
    public Config() {
        agentConfig.add(new AgentConfig("neutral", "neutral", 0));
    }

    public boolean isHuman(int player) {
        return agentInit(player).equals("human");
    }

    public void addAgent(String name) {
        String id = null;
        int extraArmies = 0;

        int i = name.indexOf('=');
        if (i >= 0) {
            id = name.substring(i + 1);
            name = name.substring(0, i);
        }
        i = name.indexOf('+');
        if (i >= 0) {
            extraArmies = Integer.parseInt(name.substring(i + 1));
            name = name.substring(0, i);
        }

        if (name.equals("me") || name.equals("human")) {
            if (id == null)
                id = "You";
            name = "human";
        } else {
            if (id == null)
                id = Util.className(name);
            name = "internal:" + name;
        }
        agentConfig.add(new AgentConfig(id, name, extraArmies));

        gameConfig.addPlayer(extraArmies);
    }

    public void addHuman() {
        addAgent("human");
    }

    public String playerName(int i) {
        return agentConfig.get(i).name;
    }

    public String fullName(int i) {
        return agentConfig.get(i).fullName();
    }

    public String agentInit(int i) {
        return agentConfig.get(i).init;
    }

    public int numPlayers() {
        return agentConfig.size() - 1;
    }

    public String getCSVHeader() {
        StringBuilder sb = new StringBuilder();
        for (int p = 1 ; p <= numPlayers() ; ++p)
            sb.append(";player" + p);
        
        return GameConfig.getCSVHeader() + ";timeoutMillis" + sb.toString();
    }
    
    public String getCSV() {
        StringBuilder sb = new StringBuilder();
        for (int p = 1 ; p <= numPlayers() ; ++p)
            sb.append(";" + fullName(p));

        return gameConfig.getCSV() + ";" + timeoutMillis + sb.toString();
    }    
}
