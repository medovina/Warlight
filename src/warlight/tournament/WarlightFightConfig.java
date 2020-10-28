package warlight.tournament;

import warlight.engine.Config;

public class WarlightFightConfig {
    
    public Config config;

    /**
     * Seed to use for round generation.
     */
    public int seed;
    
    /**
     * Number of games to play
     */
    public int games;

    @Override
    public WarlightFightConfig clone() {
        WarlightFightConfig result = new WarlightFightConfig();
        
        result.config = (Config) config.clone();
        
        result.seed = seed;
        result.games = games;
        
        return result;
    }
    
    public String getCSVHeader() {
        return config.getCSVHeader() + ";games;fightSeed";
    }
    
    public String getCSV() {
        return config.getCSV() + ";" + games + ";" + seed;
    }
    

}
