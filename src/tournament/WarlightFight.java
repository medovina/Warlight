package tournament;

import java.io.*;
import java.time.LocalDateTime;

import org.apache.commons.math3.stat.interval.*;

import engine.*;

public class WarlightFight {
    Config config;
    int baseSeed;
    int games;
    private String resultdir;
    
    public WarlightFight(Config config, int baseSeed, int games, String resultdir) {
        this.config = config;
        this.baseSeed = baseSeed;
        this.games = games;
        this.resultdir = resultdir;
    }

    PrintWriter open(String filename, String header) {
        File tableFile = new File(resultdir + "/" + filename);
        tableFile.getParentFile().mkdirs();
        boolean outputHeader = !tableFile.exists();
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(tableFile, true));
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
        if (outputHeader) {
            writer.println(header);
        }
        return writer;
    }

    void reportTotals(TotalResults res, long[] totalTime, int[] totalMoves, boolean verbose) {
        int numPlayers = config.numPlayers();
        
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

        if (resultdir == null)
            return;
        
        try (PrintWriter writer = open("matches.csv",
                "datetime;" + Config.getCSVHeader() +
                ";games;baseSeed;wonBy1;winRate1;wonBy2;winRate2;draws")) {
            writer.printf("%s;%s;%d;%d;%d;%.2f;%d;%.2f;%d\n",
                LocalDateTime.now(), config.getCSV(), games, baseSeed,
                res.victories[1], 1.0 * res.victories[1] / games,
                res.victories[2], 1.0 * res.victories[2] / games, res.victories[0]);
        }
    }
    
    public TotalResults fight(boolean verbose) {
        int numPlayers = config.numPlayers();
        TotalResults res = new TotalResults(numPlayers);
        int[] totalMoves = new int[numPlayers + 1];
        long[] totalTime = new long[numPlayers + 1];
        PrintWriter writer = null;

        if (resultdir != null)
            writer = open("games.csv",
                "datetime;" + Config.getCSVHeader() + ";seed;" + GameResult.getCSVHeader());

        for (int i = 0; i < games; ++i) {
            int seed = baseSeed + i;
            config.gameConfig.seed = seed;
            GameResult result = new Engine(config).run();
            for (int p = 1 ; p <= numPlayers ; ++p) {
                totalMoves[p] += result.totalMoves[p];
                totalTime[p] += result.totalTime[p];
            }
            
            System.out.printf(
                "seed %d: %s won in %d rounds\n", seed, result.getWinnerName(), result.round);

            res.victories[result.winner] += 1;
        
            if (writer != null)
                writer.println(LocalDateTime.now() + ";" + config.getCSV() + ";" + seed + ";" +
                               result.getCSV());
        }

        if (writer != null)
            writer.close();
        
        reportTotals(res, totalTime, totalMoves, verbose);
        return res;        
    }
}
