import java.util.*;

import engine.Config;
import engine.RunGame;

public class Warlight {
    static String internalBot(String name) { return "internal:" + name; }

    public static void main(String[] args) {
        List<String> bots = new ArrayList<String>();

        for (int i = 0 ; i < args.length ; ++i)
            bots.add(args[i]);

        Config config = new Config();
        
        if (bots.size() < 2) {
            config.bot1Init = "human";
            config.bot2Init = internalBot(bots.isEmpty() ? "bots.AggressiveBot" : bots.get(0));
        } else {
            config.bot1Init = internalBot(bots.get(0));
            config.bot2Init = internalBot(bots.get(1));
        }
        
        config.visualize = true;
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
}
