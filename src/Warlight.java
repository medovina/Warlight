import static java.lang.System.out;

import java.io.File;
import java.util.*;

import engine.*;
import tournament.*;
import utils.Util;

public class Warlight {
    static String internalBot(String name) { return "internal:" + name; }

    static void simulateGames(Config config, List<String> bots, int seed, int games, String resultdir) {
        if (bots.size() < 2) {
            out.println("must specify 2 bot names with -sim");
            return;
        }
        config.visualize = false;

        WarlightFightConfig fc = new WarlightFightConfig();
        fc.config = config;
        fc.games = games;
        fc.seed = seed > 0 ? seed : 0;

        WarlightFight fight = new WarlightFight(fc,
            resultdir == null ? null : new File(resultdir + "/all-results.csv"),
            resultdir == null ? null : new File(resultdir + "/fights")
            );
        fight.fight(Util.className(bots.get(0)), internalBot(bots.get(0)),
                    Util.className(bots.get(1)), internalBot(bots.get(1)));
}

    static void usage() {
        out.println("usage: warlight [<bot1-classname>] [<bot2-classname>] [<option>...]");
        out.println("options:");
        out.println("  -resultdir <path> : directory for results in CSV format");
        out.println("  -seed <num> : random seed");
        out.println("  -sim <count> : simulate a series of games without visualization");
        out.println("  -timeout <num> : bot time limit in ms");
        out.println();
        out.println("game configuration options:");
        out.println("  -manual : manual territory distribution");
        out.println("  -warlords : distribute only one territory from each continent");
    }

    public static void main(String[] args) {
        List<String> bots = new ArrayList<String>();
        String resultdir = null;
        int seed = -1;
        int sim = 0;

        Config config = new Config();

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            if (s.startsWith("-"))
                switch (s) {
                    case "-manual":
                        config.game.manualDistribution = true;
                        break;
                    case "-resultdir":
                        resultdir = args[++i];
                        break;
                    case "-seed":
                        seed = Integer.parseInt(args[++i]);
                        break;
                    case "-sim":
                        sim = Integer.parseInt(args[++i]);
                        break;
                    case "-timeout":
                        config.timeoutMillis = Integer.parseInt(args[++i]);
                        break;
                    case "-warlords":
                        config.game.warlords = true;
                        break;
                    default:
                        usage();
                        System.exit(1);
                }
            else bots.add(args[i]);
        }

        if (sim > 0) {
            simulateGames(config, bots, seed, sim, resultdir);
        } else {
            if (bots.size() < 2) {
                config.setHuman(1);
                config.setBotClass(2, bots.isEmpty() ? "bots.AggressiveBot" : bots.get(0));
            } else {
                config.setBotClass(1, bots.get(0));
                config.setBotClass(2, bots.get(1));
            }
            
            config.visualize = true;
            config.game.seed = seed;
            new Engine(config).go();
        }
        
        System.exit(0);
    }
}
