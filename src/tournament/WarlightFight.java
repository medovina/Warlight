package tournament;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import engine.GameResult;

public class WarlightFight {
    
    private WarlightFightConfig fightConfig;
    
    private File tableFile;
    
    private File resultDirFile;
    
    private File replayDirFile;
    
    public WarlightFight(WarlightFightConfig prototypeConfig, File tableFile, File resultDirFile, File replayDirFile) {
        this.fightConfig = prototypeConfig;
        this.tableFile = tableFile;
        this.resultDirFile = resultDirFile;
        this.replayDirFile = replayDirFile;
    }
    
    public TotalResults fight(String bot1Name, String bot1Init, String bot2Name, String bot2Init) {        
        bot1Name = Sanitize.idify(bot1Name);
        bot2Name = Sanitize.idify(bot2Name);
        
        fightConfig.config.player1Name = bot1Name;
        fightConfig.config.bot1Init = bot1Init;
        fightConfig.config.player2Name = bot2Name;
        fightConfig.config.bot2Init = bot2Init;
        
        WarlightFightRound[] rounds =
            WarlightFightRoundGenerator.generateFightRounds(
                fightConfig.seed, fightConfig.config, fightConfig.games);
        
        GameResult[] results = new GameResult[rounds.length];

        if (replayDirFile != null)
            replayDirFile.mkdirs();
                        
        for (int i = 0; i < rounds.length; ++i) {
            if (replayDirFile != null) {
                // SET REPLAY FILE
                int roundNumber = 0;
                while (true) {
                    rounds[i].getConfig().replayLog = new File(replayDirFile, bot1Name + "-vs-" + bot2Name + "-Round-" + roundNumber + ".replay");
                    if (!rounds[i].getConfig().replayLog.exists()) break;
                    ++roundNumber;
                }
            }
            
            GameResult result = rounds[i].run();
            results[i] = result;
            
            System.out.format(
                "seed %d: %s won in %d rounds\n",
                rounds[i].getConfig().game.seed, result.getWinnerName(), result.round);
        }
        
        return outputResults(rounds, results);        
    }

    private TotalResults outputResults(WarlightFightRound[] rounds, GameResult[] results) {
        String bot1Name = rounds[0].getConfig().player1Name;
        String bot2Name = rounds[0].getConfig().player2Name;
        
        if (resultDirFile != null) {
            String fileName;
            if (bot1Name.compareTo(bot2Name) < 0) {
                fileName = bot1Name + "-vs-" + bot2Name + ".csv";
            } else {
                fileName = bot2Name + "-vs-" + bot1Name + ".csv";
            }
            outputResults(new File(resultDirFile, fileName), rounds, results);
        }

        if (tableFile != null)
            outputResults(tableFile, rounds, results);
        
        TotalResults res = new TotalResults();
        for (int i = 0 ; i < results.length ; ++i) {
            GameResult r = results[i];
            switch (r.winner) {
            case PLAYER_1: res.victories1 += 1; break;
            case PLAYER_2: res.victories2 += 1; break;
            default: break;
            }
        }
        
        System.out.format("total victories: %s = %d (%.1f%%), %s = %d (%.1f%%)\n",
            bot1Name, res.victories1, 100.0 * res.victories1 / results.length, 
            bot2Name, res.victories2, 100.0 * res.victories2 / results.length);

        return res;
    }

    
    private void outputResults(File file, WarlightFightRound[] rounds, GameResult[] results) {
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
