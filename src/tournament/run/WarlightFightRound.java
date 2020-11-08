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
        RunGame game = new RunGame(config);
        return game.go();
    }

    public Config getConfig() {
        return config;
    }
}
