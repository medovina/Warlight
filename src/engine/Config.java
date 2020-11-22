package engine;

import game.GameConfig;
import utils.Util;

public class Config implements Cloneable {
    public String player1Name, player2Name;
    
    public String agent1Init, agent2Init;

    public long timeoutMillis = 60_000;
    
    public boolean visualize = true;
    public Boolean visualizeContinual = null;
    public Integer visualizeContinualFrameTimeMillis = null;
    
    public GameConfig gameConfig = new GameConfig();
    
    public void setHuman(int player) {
        if (player == 1) {
            agent1Init = "human";
            player1Name = "You";
        } else {
            agent2Init = "human";
            player2Name = "You";
        }
    }

    public boolean isHuman(int player) {
        return player == 1 ? agent1Init.equals("human") : agent2Init.equals("human");
    }

    public void setAgentClass(int player, String fqcn) {
        if (player == 1) {
            agent1Init = "internal:" + fqcn;
            player1Name = Util.className(fqcn);
        } else {
            agent2Init = "internal:" + fqcn;
            player2Name = Util.className(fqcn);
        }
    }

    public String playerName(int i) {
        return i == 1 ? player1Name : player2Name;
    }

    public String asString() {
        return player1Name + ";" + player2Name + ";" +
               timeoutMillis + ";" +
               visualize + ";" + visualizeContinual + ";" + visualizeContinualFrameTimeMillis + ";" +
               gameConfig.asString();
    }
    
    public String getCSVHeader() {
        return "PlayerName1;PlayerName2;timeoutMillis;" + gameConfig.getCSVHeader();
    }
    
    public String getCSV() {
        return player1Name + ";" + player2Name + ";" +
               timeoutMillis + ";" + gameConfig.getCSV();
    }    
}
