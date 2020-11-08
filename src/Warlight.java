import java.io.File;

import engine.Config;
import engine.RunGame;

public class Warlight {
    public static void main(String[] args) {
        Config config = new Config();
        
        config.bot1Init = "internal:bot.custom.AggressiveBot";
        config.bot2Init = "human";
        
        config.botCommandTimeoutMillis = 24*60*60*1000;
        config.visualize = true;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
}
