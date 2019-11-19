package conquest.engine;

import java.io.File;

import conquest.game.GameConfig;

public class Config implements Cloneable {
    
    public String gameId = "GAME";
    
    /**
     * Human-readable name of player 1 to display during visualization or to report into CSV.
     */
    public String player1Name = "Bot1";
    /**
     * Human-readable name of player 2 to display during visualization or to report into CSV.
     */
    public String player2Name = "Bot2";
    
    public String bot1Init;
    public String bot2Init;
    
    public long botCommandTimeoutMillis = 2000;
    
    public boolean visualize = true;
    
    public Boolean visualizeContinual = null;
    
    public Integer visualizeContinualFrameTimeMillis = null;
    
    public boolean logToConsole = true;
    
    public File replayLog = null;
    
    public GameConfig game = new GameConfig();
    
    public String asString() {
        return gameId + ";" + player1Name + ";" + player2Name + ";" +
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
        return "ID;PlayerName1;PlayerName2;timeoutMillis;" + game.getCSVHeader();
    }
    
    public String getCSV() {
        return gameId + ";" + player1Name + ";" + player2Name + ";" +
               botCommandTimeoutMillis + ";" + game.getCSV();
    }
    
    public static Config fromString(String line) {
        
        String[] parts = line.split(";");
        
        Config result = new Config();

        result.gameId = parts[0];
        result.player1Name = parts[1];
        result.player2Name = parts[2];
        result.botCommandTimeoutMillis = Integer.parseInt(parts[3]);
        result.visualize = Boolean.parseBoolean(parts[4]);
        result.visualizeContinual = (parts[5].toLowerCase().equals("null") ? null : Boolean.parseBoolean(parts[5]));
        result.visualizeContinualFrameTimeMillis = (parts[6].toLowerCase().equals("null") ? null : Integer.parseInt(parts[6]));
        result.logToConsole = Boolean.parseBoolean(parts[7]);
        
        int engineConfigStart = 0;
        for (int i = 0; i < 8; ++i) {
            engineConfigStart = line.indexOf(";", engineConfigStart);
            ++engineConfigStart;
        }
        
        result.game = GameConfig.fromString(line.substring(engineConfigStart));
        
        return result;
    }
    
}