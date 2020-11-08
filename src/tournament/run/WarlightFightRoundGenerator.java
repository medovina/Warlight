package tournament.run;

import engine.Config;

public class WarlightFightRoundGenerator {
    public static Config[] generateConfigs(int randomSeed, Config prototypeOptions, int games) {
        
        Config[] configs = new Config[games];
        
        for (int i = 0; i < games; ++i) {
            Config config = prototypeOptions.clone();
            config.game.seed = randomSeed + i;

            configs[i] = config;
        }
        
        return configs;
    }
    
    public static WarlightFightRound[] generateFightRounds(int randomSeed, Config prototypeOptions, int games) {
        Config[] configs = generateConfigs(randomSeed, prototypeOptions, games);
        WarlightFightRound[] result = new WarlightFightRound[games];
        for (int i = 0; i < games; ++i) {
            result[i] = new WarlightFightRound(configs[i]);
        }
        return result;
    }
    
}
