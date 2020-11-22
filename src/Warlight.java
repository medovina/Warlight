import static java.lang.System.out;

import java.io.File;
import java.util.*;

import engine.*;
import tournament.*;
import utils.Util;

public class Warlight {
    static String internalAgent(String name) { return "internal:" + name; }

    static void simulateGames(Config config, List<String> agents, int seed, int games, String resultdir) {
        if (agents.size() < 2) {
            out.println("must specify 2 agent names with -sim");
            return;
        }
        config.visualize = false;

        WarlightFight fight = new WarlightFight(config, seed > 0 ? seed : 0, games,
            resultdir == null ? null : new File(resultdir + "/games.csv")
            );
        fight.fight(Util.className(agents.get(0)), internalAgent(agents.get(0)),
                    Util.className(agents.get(1)), internalAgent(agents.get(1)));
}

    static void usage() {
        out.println("usage: warlight [<agent1-classname>] [<agent2-classname>] [<option>...]");
        out.println("options:");
        out.println("  -resultdir <path> : directory for results in CSV format");
        out.println("  -seed <num> : random seed");
        out.println("  -sim <count> : simulate a series of games without visualization");
        out.println("  -timeout <num> : agent time limit in ms");
        out.println();
        out.println("game configuration options:");
        out.println("  -manual : manual territory distribution");
        out.println("  -warlords : distribute only one territory from each continent");
    }

    public static void main(String[] args) {
        List<String> agents = new ArrayList<String>();
        String resultdir = null;
        int seed = -1;
        int sim = 0;

        Config config = new Config();

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            if (s.startsWith("-"))
                switch (s) {
                    case "-manual":
                        config.gameConfig.manualDistribution = true;
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
                        config.gameConfig.warlords = true;
                        break;
                    default:
                        usage();
                        System.exit(1);
                }
            else agents.add(args[i]);
        }

        if (sim > 0) {
            simulateGames(config, agents, seed, sim, resultdir);
        } else {
            if (agents.size() < 2) {
                config.setHuman(1);
                config.setAgentClass(2, agents.isEmpty() ? "agents.Attila" : agents.get(0));
            } else {
                config.setAgentClass(1, agents.get(0));
                config.setAgentClass(2, agents.get(1));
            }
            
            config.visualize = true;
            config.gameConfig.seed = seed;
            new Engine(config).go();
        }
        
        System.exit(0);
    }
}
