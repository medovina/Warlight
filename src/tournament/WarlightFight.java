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
    
    public TotalResults fight() {
        TotalResults res = new TotalResults(config.numPlayers());
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
                GameResult result = new Engine(config).run();
                
                System.out.format(
                    "seed %d: %s won in %d rounds\n",
                    config.gameConfig.seed, result.getWinnerName(), result.round);
    
                res.victories[result.winner] += 1;
            
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
        
        System.out.format("total victories: ");
        for (int p = 1 ; p <= config.numPlayers() ; ++p) {
            if (p > 1)
                System.out.print(", ");
            System.out.format("%s = %d (%.1f%%)",
                config.playerName(p), res.victories[p], 100.0 * res.victories[p] / games);
        }
        System.out.println();

        return res;        
    }
}
