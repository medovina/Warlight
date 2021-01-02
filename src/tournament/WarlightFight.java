package tournament;

import java.io.*;
import static java.lang.System.out;
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
        boolean outputHeader = false;
        
        if (tableFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
                String line = reader.readLine();
                if (line == null)   // empty file
                    outputHeader = true;
                else if (!line.equals(header))
                    throw new Error("incompatible file format");
            } catch (IOException e) { throw new Error(e); }
        } else outputHeader = true;

        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(tableFile, true));
        } catch (FileNotFoundException e) { throw new Error(e); }
        if (outputHeader) {
            writer.println(header);
        }
        return writer;
    }

    void reportTotals(TotalResults res, long[] totalTime, int[] totalMoves, boolean verbose) {
        int numPlayers = config.numPlayers();
        
        out.print("total victories: ");
        for (int p = 1 ; p <= numPlayers ; ++p) {
            if (p > 1)
                out.print(", ");
            out.printf("%s = %d (%.1f%%)",
                config.fullName(p), res.totalVictories[p], 100.0 * res.totalVictories[p] / games);
        }
        out.println();

        if (numPlayers > 2 && verbose) {
            out.print("average score: ");
            for (int p = 1 ; p <= numPlayers ; ++p) {
                if (p > 1)
                    out.print(", ");
                out.printf("%s = %.2f", config.fullName(p), 1.0 * res.totalScore[p] / games);
            }
            out.println();
        }

        for (int p = 1 ; p <= numPlayers ; ++p) {
            if (p > 1)
                out.print(", ");
            out.format("%s took %.1f ms/move", config.fullName(p),
                1.0 * totalTime[p] / totalMoves[p]);
        }
        out.println();

        if (games == 1)
            return;

        if (verbose) {
            int confidence = 98;
            out.printf("with %d%% confidence:\n", confidence);
            for (int p = 1 ; p <= numPlayers ; ++p) {
                ConfidenceInterval ci =
                    IntervalUtils.getWilsonScoreInterval(
                        games, res.totalVictories[p], confidence / 100.0);
                double lo = ci.getLowerBound() * 100, hi = ci.getUpperBound() * 100;
                out.printf("  %s wins %.1f%% - %.1f%%\n", config.fullName(p), lo, hi);
            }
        }

        if (resultdir == null)
            return;
        
        try (PrintWriter writer = open("matches.csv",
                "datetime;" + config.getCSVHeader() + ";baseSeed;" + res.getCSVHeader())) {
                    
            writer.printf("%s;%s;%d;%s\n",
                LocalDateTime.now(), config.getCSV(), baseSeed, res.getCSV());
        }
    }
    
    public TotalResults fight(boolean verbose) {
        int numPlayers = config.numPlayers();
        TotalResults totalResults = new TotalResults(numPlayers, games);
        int[] totalMoves = new int[numPlayers + 1];
        long[] totalTime = new long[numPlayers + 1];
        PrintWriter writer = null;

        if (resultdir != null)
            writer = open("games.csv",
                "datetime;" + config.getCSVHeader() + ";seed;" + GameResult.getCSVHeader(numPlayers));

        for (int game = 0; game < games; ++game) {
            int seed = baseSeed + game;
            config.gameConfig.seed = seed;
            GameResult result = new Engine(config).run(verbose);
            for (int p = 1 ; p <= numPlayers ; ++p) {
                totalMoves[p] += result.totalMoves[p];
                totalTime[p] += result.totalTime[p];
            }
            
            out.printf("seed %d: %s won in %d rounds",
                      seed, config.fullName(result.winner), result.round);
            if (verbose && numPlayers > 2) {
                out.print(" (");
                for (int i = 2 ; i <= numPlayers ; ++i) {
                    if (i > 2)
                        out.print(", ");
                    int p = 1;
                    while (true) {
                        if (result.score[p] == numPlayers - i)
                            break;
                        p += 1;
                    }
                    out.printf("#%d = %s", i, config.fullName(p));
                }
                out.print(")");
            }
            out.println();

            totalResults.totalVictories[result.winner] += 1;
            for (int p = 1 ; p <= numPlayers ; ++p)
                totalResults.totalScore[p] += result.score[p];
        
            if (writer != null)
                writer.println(LocalDateTime.now() + ";" + config.getCSV() + ";" + seed + ";" +
                               result.getCSV());
        }

        if (writer != null)
            writer.close();
        
        reportTotals(totalResults, totalTime, totalMoves, verbose);
        return totalResults;        
    }
}
