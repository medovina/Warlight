import static java.lang.System.out;

import java.io.File;
import java.util.*;

import engine.Config;
import engine.RunGame;
import tournament.*;
import utils.Util;

public class Warlight {
    static String internalBot(String name) { return "internal:" + name; }

    static void usage() {
        out.println("usage: warlight [<bot1-classname>] [<bot2-classname>] [<option>...]");
        out.println("options:");
        out.println("  -resultdir <path> : directory for results in CSV format");
        out.println("  -seed <num> : random seed");
        out.println("  -sim <count> : simulate a series of games without visualization");
        out.println("  -timeout <num> : bot time limit in ms");
    }

    public static void main(String[] args) {
        List<String> bots = new ArrayList<String>();
        String resultdir = null;
        int seed = -1;
        int sim = 0;
        int timeout = -1;

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            if (s.startsWith("-"))
                switch (s) {
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
                        timeout = Integer.parseInt(args[++i]);
                        break;
                    default:
                        usage();
                        System.exit(1);
                }
            else bots.add(args[i]);
        }

        Config config = new Config();
        if (timeout >= 0)
            config.botCommandTimeoutMillis = timeout;
        
        if (sim > 0) {
            if (bots.size() < 2) {
                out.println("must specify 2 bot names with -sim");
                return;
            }
            config.visualize = false;
            WarlightFightConfig fc = new WarlightFightConfig();
            fc.config = config;
            fc.games = sim;
            fc.seed = seed > 0 ? seed : 0;
            WarlightFight fight = new WarlightFight(fc,
                resultdir == null ? null : new File(resultdir + "/all-results.csv"),
                resultdir == null ? null : new File(resultdir + "/fights"),
                resultdir == null ? null : new File(resultdir + "/replays")
                );
            fight.fight(Util.className(bots.get(0)), internalBot(bots.get(0)),
                        Util.className(bots.get(1)), internalBot(bots.get(1)));
    
        } else {
            if (bots.size() < 2) {
                config.bot1Init = "human";
                config.bot2Init = internalBot(bots.isEmpty() ? "bots.AggressiveBot" : bots.get(0));
            } else {
                config.bot1Init = internalBot(bots.get(0));
                config.bot2Init = internalBot(bots.get(1));
            }
            
            config.visualize = true;
            config.game.seed = seed;
            new RunGame(config).go();
        }
        
        System.exit(0);
    }
}
