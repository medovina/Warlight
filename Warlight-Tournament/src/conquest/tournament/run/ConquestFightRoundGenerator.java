package conquest.tournament.run;

import java.util.Random;

import conquest.engine.Config;


public class ConquestFightRoundGenerator {
    
    public static int[] generateSeeds(int randomSeed, int count) {
        Random random = new Random(randomSeed);
        int[] seeds = new int[count];
        
        for (int i = 0; i < count; ++i) {
            seeds[i] = random.nextInt();
            while (seeds[i] <= 0) {
                seeds[i] += Integer.MAX_VALUE;
            }            
        }
        
        return seeds;
    }
    
    public static Config[] generateConfigs(int randomSeed, Config prototypeOptions, int games) {
        
        int[] seeds = generateSeeds(randomSeed, games);
                
        Config[] configs = new Config[games];
        
        for (int i = 0; i < games; ++i) {
            Config config = prototypeOptions.clone();
            
            config.game.seed = seeds[i];

            configs[i] = config;
        }
        
        return configs;
    }
    
    public static ConquestFightRound[] generateFightRounds(int randomSeed, Config prototypeOptions, int games) {
        Config[] configs = generateConfigs(randomSeed, prototypeOptions, games);
        ConquestFightRound[] result = new ConquestFightRound[games];
        for (int i = 0; i < games; ++i) {
            result[i] = new ConquestFightRound(configs[i]);
        }
        return result;
    }
    
}
