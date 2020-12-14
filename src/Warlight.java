import static java.lang.System.out;

import java.io.File;
import java.util.*;

import engine.*;
import tournament.*;

public class Warlight {
    static String internalAgent(String name) { return "internal:" + name; }

    static void simulateGames(Config config, List<String> agents, int seed, int games,
                              String resultdir, boolean verbose) {
        if (agents.size() < 2) {
            out.println("must specify at least 2 agent names with -sim");
            return;
        }

        config.visualize = false;
        for (String s : agents)
            config.addAgent(s);

        WarlightFight fight = new WarlightFight(config, seed > 0 ? seed : 0, games,
            resultdir == null ? null : new File(resultdir + "/games.csv")
            );
        fight.fight(verbose);
}

    static void usage() {
        out.println("usage: warlight [<agent-classname>[<agent-opts>] ...] [<option> ...]");
        out.println("options:");
        out.println("  -map <name> : map for game");
        out.println("  -resultdir <path> : directory for results in CSV format");
        out.println("  -seed <num> : random seed");
        out.println("  -sim <count> : simulate a series of games without visualization");
        out.println("  -timeout <num> : agent time limit in ms");
        out.println("  -v : verbose output");
        out.println();
        out.println("game configuration options:");
        out.println("  -manual : manual territory distribution");
        out.println("  -warlords : distribute only one territory from each continent");
        out.println();
        out.println("agent options:");
        out.println("  +<num> : give agent <num> extra armies on each starting territory");
    }

    public static void main(String[] args) {
        List<String> agents = new ArrayList<String>();
        String resultdir = null;
        int seed = -1;
        int sim = 0;
        boolean verbose = false;

        Config config = new Config();

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            if (s.startsWith("-"))
                switch (s) {
                    case "-manual":
                        config.gameConfig.manualDistribution = true;
                        break;
                    case "-map":
                        config.gameConfig.mapName = args[++i];
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
                    case "-v":
                        verbose = true;
                        break;
                    case "-warlords":
                        config.gameConfig.warlords = true;
                        break;
                    default:
                        usage();
                        System.exit(1);
                }
            else agents.add(args[i]);
        }

        if (agents.size() > 4) {
            System.out.println("too many players (at most 4 are allowed)");
            System.exit(1);
        }

        if (sim > 0) {
            simulateGames(config, agents, seed, sim, resultdir, verbose);
        } else {
            if (agents.size() < 2) {
                config.addHuman();
                config.addAgent(agents.isEmpty() ? "agents.Attila" : agents.get(0));
            } else {
                for (String s : agents)
                    config.addAgent(s);
            }
            
            config.visualize = true;
            config.gameConfig.seed = seed;
            new Engine(config).run();
        }
        
        System.exit(0);
    }
}
