package warlight.tournament.run;

import warlight.engine.Config;
import warlight.engine.GameResult;
import warlight.engine.RunGame;


public class WarlightFightRound {
    
    private Config config;
    
    public WarlightFightRound(Config config) {
        this.config = config;
    }
    
    public synchronized GameResult run() {
        RunGame game = new RunGame(config);
        
        GameResult result = game.go();
            
        System.out.println("GAME FINISHED - Winner: " + result.winner);
        
        return result;        
    }

    public Config getConfig() {
        return config;
    }
        
}
