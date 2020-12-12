package tournament;

import java.io.*;

import org.apache.commons.math3.stat.interval.*;

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
    
    public TotalResults fight(boolean verbose) {
        int numPlayers = config.numPlayers();
        TotalResults res = new TotalResults(numPlayers);
        int[] totalMoves = new int[numPlayers + 1];
        long[] totalTime = new long[numPlayers + 1];
        boolean outputHeader = false;
        PrintWriter writer = null;

        try {
            if (tableFile != null) {
                tableFile.getParentFile().mkdirs();
                outputHeader = !tableFile.exists();
                writer = new PrintWriter(new FileOutputStream(tableFile, true));
            }
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }

        for (int i = 0; i < games; ++i) {
            config.gameConfig.seed = seed + i;
            GameResult result = new Engine(config).run();
            for (int p = 1 ; p <= numPlayers ; ++p) {
                totalMoves[p] += result.totalMoves[p];
                totalTime[p] += result.totalTime[p];
            }
            
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

        if (writer != null)
            writer.close();
        
        System.out.format("total victories: ");
        for (int p = 1 ; p <= numPlayers ; ++p) {
            if (p > 1)
                System.out.print(", ");
            System.out.format("%s = %d (%.1f%%)",
                config.playerName(p), res.victories[p], 100.0 * res.victories[p] / games);
        }
        System.out.println();

        for (int p = 1 ; p <= numPlayers ; ++p) {
            if (p > 1)
                System.out.print(", ");
            System.out.format("%s took %.1f ms/move", config.playerName(p),
                1.0 * totalTime[p] / totalMoves[p]);
        }
        System.out.println();

        if (verbose) {
            int confidence = 98;
            System.out.printf("with %d%% confidence:\n", confidence);
            for (int p = 1 ; p <= numPlayers ; ++p) {
                ConfidenceInterval ci =
                    IntervalUtils.getWilsonScoreInterval(
                        games, res.victories[p], confidence / 100.0);
                double lo = ci.getLowerBound() * 100, hi = ci.getUpperBound() * 100;
                System.out.printf("  %s wins %.1f%% - %.1f%%\n", config.playerName(p), lo, hi);
            }
        }

        return res;        
    }
}
