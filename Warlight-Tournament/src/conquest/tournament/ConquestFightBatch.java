package conquest.tournament;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * This file takes as input a property file with botId=botInit + {@link ConquestFightConfig} and performs 
 * fights between one bot vs. other bots. 
 * 
 * @author Jimmy
 */
public class ConquestFightBatch {
    
    private Properties bots;
    
    private ConquestFightConfig fightConfig;
    
    public ConquestFightBatch(File botsPropertyFile, ConquestFightConfig fightConfig) {
        this.bots = new Properties();
        
        try {
            InputStream stream = new FileInputStream(botsPropertyFile);
            bots.load(stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read bots property file: " + botsPropertyFile.getAbsolutePath(), e);
        }
        
        this.fightConfig = fightConfig;
    }
    
    public ConquestFightBatch(Properties bots, ConquestFightConfig fightConfig) {
        this.bots = bots;
        this.fightConfig = fightConfig;
    }
    
    public void fight(String botId1, String botId2, boolean reverseGames, File tableFile, File resultDirFile, File replayDirFile) {
        System.out.println("------");
        System.out.println("------");
        System.out.println("CONQUEST FIGHT BATCH: " + botId1 + " vs. " + botId2);
        System.out.println("------");
        System.out.println("------");
        
        ConquestFight fight = new ConquestFight(fightConfig, tableFile, resultDirFile, replayDirFile);
        
        fight.fight(botId1, bots.getProperty(botId1), botId2, bots.getProperty(botId2));    
        
        if (reverseGames) {
            fight.fight(botId2, bots.getProperty(botId2), botId1, bots.getProperty(botId1));
        }
    }
    
    public void fight(String botId, boolean reverseGames, File tableFile, File resultDirFile, File replayDirFile) {
        Object[] keys = bots.keySet().toArray();
        Arrays.sort(keys);
        
        if (botId.equals("*")) {    // all vs. all
            for (Object key1 : keys)
                for (Object key2 : keys) {
                    String botId1 = key1.toString(), botId2 = key2.toString();
                    if (botId1.compareTo(botId2) < 0)
                        fight(botId1, botId2, reverseGames, tableFile, resultDirFile, replayDirFile);
                }
            return;
        }
        
        if (!bots.containsKey(botId)) throw new RuntimeException(
            "Cannot execute fights for '" + botId +
            "' as it does not have bot init specified within the property file (key does not exist).");
        
        for (Object key : keys) {
            String otherBotId = key.toString();
            
            if (!botId.equals(otherBotId))
                fight(botId, otherBotId, reverseGames, tableFile, resultDirFile, replayDirFile);
        }
        
    }

}
