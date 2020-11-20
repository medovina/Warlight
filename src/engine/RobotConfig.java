package engine;

import engine.replay.GameLog;
import view.GUI;

public class RobotConfig {
    
    public final int player;
    
    public final String playerName;
    
    public final long timeoutMillis;
    
    public final GameLog gameLog;
    
    public final boolean logToConsole;
    
    public final GUI gui;

    public RobotConfig(int player, String playerName, long timeoutMillis,
                       GameLog gameLog, boolean logToConsole, GUI gui) {
        super();
        this.player = player;
        this.playerName = playerName;
        this.timeoutMillis = timeoutMillis;
        this.gameLog = gameLog;
        this.logToConsole = logToConsole;
        this.gui = gui;
    }
    
}
