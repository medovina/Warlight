package warlight;

import java.io.File;

import warlight.engine.Config;
import warlight.engine.RunGame;

public class Warlight {
    
    /**
     * Example how to start internal bots...
     */
    public static void startInternalBots(boolean visualize) {
        Config config = new Config();
        
        config.bot1Init = "internal:warlight.bot.BotStarter";
        config.bot2Init = "internal:warlight.bot.BotStarter";
                
        config.visualize = visualize;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
    
    /**
     * Example how to start bots "from command line"...
     */
    public static void startProcessBots(boolean visualize) {
        Config config = new Config();
        
        config.bot1Init = "process:java -cp bin warlight.bot.BotStarter";
        config.bot2Init = "dir;process:./bin;java warlight.bot.BotStarter";
                
        config.visualize = visualize;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
    
    /**
     * Example how to start bots "from command line"...
     */
    public static void startReplay(String replayFile) {
        Config config = new Config();
        
        config.visualize = true;
        
        RunGame run = new RunGame(config);
        run.goReplay(new File(replayFile));
        
        System.exit(0);
    }
    
    private static void header() {
        System.out.println("---------------");
        System.out.println("WARLIGHT ENGINE");
        System.out.println("---------------");
    }
    
    private static void error() {        
        System.out.println();
        System.out.println("Invalid arguments passed. Expected 1, 5 or 6 arguemnts");
        System.out.println();
        System.out.println("To start the game (5 or 6 arguments):");
        System.out.println("java ... warlight.Warlight <max rounds:int> <bot command timeout:long> <bot1:init string> <bot2:init string> <visualization:boolean> [ <replay file:string> ]");
        System.out.println("    -- possible bot init strings:");
        System.out.println("        -- internal:fqcn ~ to start bot directly on Java classpath");
        System.out.println("        -- process:command ~ to execute the bot from command line");
        System.out.println("        -- dir;process:dir;command ~ to execute the bot from command line from specfici directory");
        System.out.println("        -- human ~ human player");
        System.out.println();
        System.out.println("To replay the game (1 argument):");
        System.out.println("java ... warlight.Warlight <replay file:string>");
        System.out.println();        
        System.exit(1);
    }

    public static void main(String[] args) {
        
        //
        // TEST ARGUMENTS
        // -- REPLAY
        //args = new String[]{ "replay.log" };
        
        // -- TWO INTERNAL BOTS + visualization + logging replay
        //args = new String[]{ "100", "5000", "internal:warlight.bot.BotStarter", "internal:warlight.bot.BotStarter", "true", "replay.log" };
        
        // -- TWO PROCESS BOTS + visualization + logging replay
        //args = new String[]{ "100", "5000", "process:java -cp bin warlight.bot.BotStarter", "process:java -cp bin warlight.bot.BotStarter", "true", "replay.log" };
        
        if (args == null || (args.length != 1 && args.length != 5 && args.length != 6)) {
            header();
            error();
        }
        
        if (args.length == 1) {
            try {
                startReplay(args[0]);
            } catch (Exception e) {
                header();
                System.out.println();
                e.printStackTrace();
            }
        }
        
        if (args.length == 5 || args.length == 6) {
            try {
                Config config = new Config();
                
                config.game.maxGameRounds = Integer.parseInt(args[0]);
                config.botCommandTimeoutMillis = Long.parseLong(args[1]);
                
                config.bot1Init = args[2];
                config.bot2Init = args[3];
                
                config.visualize = Boolean.parseBoolean(args[4]);
                
                if (args.length == 6) {
                    config.replayLog = new File(args[5]);
                }
                
                RunGame run = new RunGame(config);
                run.go();
                
                System.exit(0);                
            } catch (Exception e) {
                header();
                System.out.println();
                e.printStackTrace();
            }
        }        
        
    }
    
}
