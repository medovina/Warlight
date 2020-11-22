package tournament;

import engine.*;

public class WarlightFightRound {
    
    private Config config;
    
    public WarlightFightRound(Config config) {
        this.config = config;
    }
    
    public synchronized GameResult run() {
        return new Engine(config).go();
    }

    public Config getConfig() {
        return config;
    }
}
