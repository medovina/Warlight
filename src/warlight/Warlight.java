package warlight;

import java.io.File;

import warlight.engine.Config;
import warlight.engine.RunGame;

public class Warlight {
        
    /**
     * Example how to start bots "from command line"...
     */
    public static void startReplay(String replayFile) {
        Config config = new Config();
        
        config.visualize = true;
        
        RunGame run = new RunGame(config);
        run.goReplay(new File(replayFile));
        
        System.exit(0);
    }
    
    public static void main(String[] args) {
        Config config = new Config();
        
        config.bot1Init = "internal:warlight.bot.custom.AggressiveBot";
        config.bot2Init = "human";
        
        config.botCommandTimeoutMillis = 24*60*60*1000;
        config.visualize = true;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
}
