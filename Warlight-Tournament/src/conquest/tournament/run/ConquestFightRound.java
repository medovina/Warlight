package conquest.tournament.run;

import conquest.engine.Config;
import conquest.engine.GameResult;
import conquest.engine.RunGame;


public class ConquestFightRound {
    
    private Config config;
    
    public ConquestFightRound(Config config) {
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
