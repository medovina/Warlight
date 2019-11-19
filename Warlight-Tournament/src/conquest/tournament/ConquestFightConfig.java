package conquest.tournament;

import conquest.engine.Config;

public class ConquestFightConfig {
    
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
    public ConquestFightConfig clone() {
        ConquestFightConfig result = new ConquestFightConfig();
        
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
