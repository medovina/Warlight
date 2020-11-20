package engine;

import java.io.File;

import game.GameConfig;
import utils.Util;

public class Config implements Cloneable {
    public String player1Name, player2Name;
    
    public String bot1Init, bot2Init;

    public long botCommandTimeoutMillis = 60_000;
    
    public boolean visualize = true;
    public Boolean visualizeContinual = null;
    public Integer visualizeContinualFrameTimeMillis = null;
    
    public boolean logToConsole = true;
    
    public File replayLog = null;
    
    public GameConfig game = new GameConfig();
    
    public void setHuman(int player) {
        if (player == 1) {
            bot1Init = "human";
            player1Name = "You";
        } else {
            bot2Init = "human";
            player2Name = "You";
        }
    }

    public boolean isHuman(int player) {
        return player == 1 ? bot1Init.equals("human") : bot2Init.equals("human");
    }

    public void setBotClass(int player, String fqcn) {
        if (player == 1) {
            bot1Init = "internal:" + fqcn;
            player1Name = Util.className(fqcn);
        } else {
            bot2Init = "internal:" + fqcn;
            player2Name = Util.className(fqcn);
        }
    }

    public String playerName(int i) {
        return i == 1 ? player1Name : player2Name;
    }

    public String asString() {
        return player1Name + ";" + player2Name + ";" +
               botCommandTimeoutMillis + ";" +
               visualize + ";" + visualizeContinual + ";" + visualizeContinualFrameTimeMillis + ";" +
               logToConsole + ";" + game.asString();
    }
    
    @Override
    public Config clone() {
        Config result = fromString(asString());
        
        result.replayLog = replayLog;
        result.bot1Init = bot1Init;
        result.bot2Init = bot2Init;
        
        return result;
    }
    
    public String getCSVHeader() {
        return "PlayerName1;PlayerName2;timeoutMillis;" + game.getCSVHeader();
    }
    
    public String getCSV() {
        return player1Name + ";" + player2Name + ";" +
               botCommandTimeoutMillis + ";" + game.getCSV();
    }
    
    public static Config fromString(String line) {
        
        String[] parts = line.split(";");
        
        Config result = new Config();

        result.player1Name = parts[0];
        result.player2Name = parts[1];
        result.botCommandTimeoutMillis = Integer.parseInt(parts[2]);
        result.visualize = Boolean.parseBoolean(parts[3]);
        result.visualizeContinual = (parts[4].toLowerCase().equals("null") ? null : Boolean.parseBoolean(parts[4]));
        result.visualizeContinualFrameTimeMillis = (parts[5].toLowerCase().equals("null") ? null : Integer.parseInt(parts[5]));
        result.logToConsole = Boolean.parseBoolean(parts[6]);
        
        int engineConfigStart = 0;
        for (int i = 0; i < 7; ++i) {
            engineConfigStart = line.indexOf(";", engineConfigStart);
            ++engineConfigStart;
        }
        
        result.game = GameConfig.fromString(line.substring(engineConfigStart));
        
        return result;
    }
    
}
