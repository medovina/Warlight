package tournament;

import java.io.*;

import engine.*;

public class WarlightFight {
    Config config;
    int seed;
    int games;
    private File tableFile;
    
    public WarlightFight(Config config, int seed, int games, File tableFile) {
        this.config = config;
        this.seed = seed;
        this.games = games;
        this.tableFile = tableFile;
    }
    
    public TotalResults fight(String bot1Name, String bot1Init, String bot2Name, String bot2Init) {        
        config.player1Name = Sanitize.idify(bot1Name);
        config.bot1Init = bot1Init;
        config.player2Name = Sanitize.idify(bot2Name);
        config.bot2Init = bot2Init;
    
        TotalResults res = new TotalResults();
        boolean outputHeader = false;
    
        if (tableFile != null) {
            tableFile.getParentFile().mkdirs();
            outputHeader = !tableFile.exists();
        }

        try (PrintWriter writer =
                tableFile == null ? null :
                                    new PrintWriter(new FileOutputStream(tableFile, true))) {
            for (int i = 0; i < games; ++i) {
                config.gameConfig.seed = seed + i;
                GameResult result = new Engine(config).go();
                
                System.out.format(
                    "seed %d: %s won in %d rounds\n",
                    config.gameConfig.seed, result.getWinnerName(), result.round);
    
                switch (result.winner) {
                    case 1: res.victories1 += 1; break;
                    case 2: res.victories2 += 1; break;
                    default: break;
                }
            
                if (outputHeader) {
                    writer.println(result.getCSVHeader());
                    outputHeader = false;
                }
                if (tableFile != null)
                    writer.println(result.getCSV());
            }
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
        
        System.out.format("total victories: %s = %d (%.1f%%), %s = %d (%.1f%%)\n",
            bot1Name, res.victories1, 100.0 * res.victories1 / games, 
            bot2Name, res.victories2, 100.0 * res.victories2 / games);

        return res;        
    }
}
