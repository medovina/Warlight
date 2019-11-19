package conquest.tournament;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import conquest.engine.GameResult;
import conquest.tournament.run.ConquestFightRound;
import conquest.tournament.run.ConquestFightRoundGenerator;
import conquest.tournament.utils.Sanitize;

public class ConquestFight {
    
    private ConquestFightConfig fightConfig;
    
    private File tableFile;
    
    private File resultDirFile;
    
    private File replayDirFile;
    
    public ConquestFight(ConquestFightConfig prototypeConfig, File tableFile, File resultDirFile, File replayDirFile) {
        this.fightConfig = prototypeConfig;
        this.tableFile = tableFile;
        this.resultDirFile = resultDirFile;
        this.replayDirFile = replayDirFile;
    }
    
    private void log(String name, String msg) {
        System.out.println("[" + name + "] " + msg);
    }
    
    public void fight(String bot1Name, String bot1Init, String bot2Name, String bot2Init) {        
        bot1Name = Sanitize.idify(bot1Name);
        bot2Name = Sanitize.idify(bot2Name);
        
        String gameId = bot1Name + "-vs-" + bot2Name; 
        
        log(gameId, "FIGHT! GAMES: " + fightConfig.games);
        
        fightConfig.config.player1Name = bot1Name;
        fightConfig.config.bot1Init = bot1Init;
        fightConfig.config.player2Name = bot2Name;
        fightConfig.config.bot2Init = bot2Init;
        
        ConquestFightRound[] rounds = ConquestFightRoundGenerator.generateFightRounds(fightConfig.seed, fightConfig.config, fightConfig.games);
        
        GameResult[] results = new GameResult[rounds.length];
        
        replayDirFile.mkdirs();
                        
        for (int i = 0; i < rounds.length; ++i) {
            long start = System.currentTimeMillis();
            
            gameId = bot1Name + "-vs-" + bot2Name + "-" + i;
            
            log(gameId, "ROUND " + (i+1) + " / " + rounds.length);
            
            // SET REPLAY FILE
            int roundNumber = 0;
            while (true) {
                rounds[i].getConfig().replayLog = new File(replayDirFile, bot1Name + "-vs-" + bot2Name + "-Round-" + roundNumber + ".replay");
                if (!rounds[i].getConfig().replayLog.exists()) break;
                ++roundNumber;
            }
            
            GameResult result = rounds[i].run();
                        
            log(gameId, "ROUND " + (i+1) + " / " + rounds.length + " FINISHED: " + result.getHumanString());
            
            results[i] = result;
            
            log(gameId, "TIME: " + (System.currentTimeMillis() - start) + "ms");
        }
        
        gameId = bot1Name + "-vs-" + bot2Name; 
        
        log(gameId, "FIGHT FINISHED!");
        
        outputResults(rounds, results);        
    }

    private void outputResults(ConquestFightRound[] rounds, GameResult[] results) {
        String bot1Name = rounds[0].getConfig().player1Name;
        String bot2Name = rounds[0].getConfig().player2Name;
        
        String fileName;
        
        if (bot1Name.compareTo(bot2Name) < 0) {
            fileName = bot1Name + "-vs-" + bot2Name + ".csv";
        } else {
            fileName = bot2Name + "-vs-" + bot1Name + ".csv";
        }
        
        outputResults(new File(resultDirFile, fileName), rounds, results);
        
        outputResults(tableFile, rounds, results);
        
        int victories1 = 0, victories2 = 0;
        for (int i = 0 ; i < results.length ; ++i) {
            GameResult r = results[i];
            System.out.format("game %d: %s won in %d rounds (%d regions, %d armies)\n",
                    i + 1, r.getWinnerName(), r.round, r.getWinnerRegions(),r.getWinnerArmies());
            switch (r.winner) {
            case PLAYER_1: victories1 += 1; break;
            case PLAYER_2: victories2 += 1; break;
            default: break;
            }
        }
        
        System.out.format("total victories: %s = %d (%.1f%%), %s = %d (%.1f%%)\n",
            bot1Name, victories1, 100.0 * victories1 / results.length, 
            bot2Name, victories2, 100.0 * victories2 / results.length);
    }

    
    private void outputResults(File file, ConquestFightRound[] rounds, GameResult[] results) {
        file.getParentFile().mkdirs();
        
        PrintWriter writer = null;
        
        boolean outputHeader = !file.exists();
        
        try {
            writer = new PrintWriter(new FileOutputStream(file, true));
            
            if (outputHeader) writer.println(results[0].getCSVHeader() + ";replay");
            
            int index = 0;
            for (GameResult result : results) {
                writer.println(result.getCSV() + ";" + rounds[index].getConfig().replayLog.getAbsolutePath());
                ++index;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to write results into file: " + file.getAbsolutePath(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {                    
                }
            }
        }    
    }

}
