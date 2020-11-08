package tournament.run;

import engine.Config;
import engine.GameResult;
import engine.RunGame;


public class WarlightFightRound {
    
    private Config config;
    
    public WarlightFightRound(Config config) {
        this.config = config;
    }
    
    public synchronized GameResult run() {
        return new RunGame(config).go();
    }

    public Config getConfig() {
        return config;
    }
}
