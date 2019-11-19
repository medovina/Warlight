package warlight.tournament;

import java.io.File;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import warlight.engine.Config;

public class WarlightFightConsole {
    
    private static final char ARG_SEED_SHORT = 's';
    
    private static final String ARG_SEED_LONG = "seed";
    
    private static final char ARG_GAME_CONFIG_SHORT = 'o';
    
    private static final String ARG_GAME_CONFIG_LONG = "game-config";
    
    private static final char ARG_GAMES_COUNT_SHORT = 'g';
    
    private static final String ARG_GAMES_COUNT_LONG = "games-count";
    
    private static final char ARG_REVERSE_GAMES_SHORT = 'r';
    
    private static final String ARG_REVERSE_GAMES_LONG = "reverse-games";
    
    private static final char ARG_BOT1_NAME_SHORT = 'a';
    
    private static final String ARG_BOT1_NAME_LONG = "bot1-name";
    
    private static final char ARG_BOT1_INIT_SHORT = 'b';
    
    private static final String ARG_BOT1_INIT_LONG = "bot1-init";
    
    private static final char ARG_BOT2_NAME_SHORT = 'c';
    
    private static final String ARG_BOT2_NAME_LONG = "bot2-name";
    
    private static final char ARG_BOT2_INIT_SHORT = 'd';
    
    private static final String ARG_BOT2_INIT_LONG = "bot2-init";
    
    private static final char ARG_BOT_ID_BATCH_SHORT = 'e';
    
    private static final String ARG_BOT_ID_BATCH_LONG = "bot-id-batch";
    
    private static final char ARG_BOTS_BATCH_PROPERTIES_SHORT = 'f';
    
    private static final String ARG_BOTS_BATCH_PROPERTIES_LONG = "bots-property-file-batch";
    
    private static final char ARG_RESULT_DIR_SHORT = 'u';
    
    private static final String ARG_RESULT_DIR_LONG = "result-dir";
    
    private static final char ARG_REPLAY_DIR_SHORT = 'y';
    
    private static final String ARG_REPLAY_DIR_LONG = "replay-dir";
    
    private static final char ARG_TABLE_FILE_SHORT = 't';
    
    private static final String ARG_TABLE_FILE_LONG = "table-file";
    
    private static JSAP jsap;

    private static int seed = 0;

    private static String roundConfig;
    
    private static int gamesCount;
    
    private static boolean reverseGames;
    
    private static String bot1Name;
    
    private static String bot1Init;
    
    private static String bot2Name;
    
    private static String bot2Init;
    
    private static String botIdBatch;
    
    private static String botsBatchPropertyFileName;
    
    private static File botsBatchPropertyFile;
        
    private static String resultDir;
    
    private static File resultDirFile;
    
    private static String replayDir;
    
    private static File replayDirFile;
    
    private static String tableFileName;
    
    private static File tableFile;
    
    private static boolean batchFight;

    private static boolean headerOutput = false;

    private static JSAPResult config;

    private static void fail(String errorMessage) {
        fail(errorMessage, null);
    }

    private static void fail(String errorMessage, Throwable e) {
        header();
        System.out.println("ERROR: " + errorMessage);
        System.out.println();
        if (e != null) {
            e.printStackTrace();
            System.out.println("");
        }        
        System.out.println("Usage: java -jar warlight-tournament.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
    }

    private static void header() {
        if (headerOutput) return;
        System.out.println();
        System.out.println("==============");
        System.out.println("Warlight Fight");
        System.out.println("==============");
        System.out.println();
        headerOutput = true;
    }
        
    private static void initJSAP() throws JSAPException {
        jsap = new JSAP();
        
        FlaggedOption opt1 = new FlaggedOption(ARG_BOT1_NAME_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOT1_NAME_SHORT)
            .setLongFlag(ARG_BOT1_NAME_LONG);    
        opt1.setHelp("Bot 1 ID.");
    
        jsap.registerParameter(opt1);
        
        FlaggedOption opt11 = new FlaggedOption(ARG_BOT1_INIT_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOT1_INIT_SHORT)
            .setLongFlag(ARG_BOT1_INIT_LONG);    
        opt11.setHelp("Bot 1 INIT string, e.g.: internal:warlight.bot.BotStarter");
    
        jsap.registerParameter(opt11);
        
        FlaggedOption opt2 = new FlaggedOption(ARG_BOT2_NAME_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOT2_NAME_SHORT)
            .setLongFlag(ARG_BOT2_NAME_LONG);    
        opt2.setHelp("Bot 2 ID.");
        
        jsap.registerParameter(opt2);
        
        FlaggedOption opt22 = new FlaggedOption(ARG_BOT2_INIT_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOT2_INIT_SHORT)
            .setLongFlag(ARG_BOT2_INIT_LONG);    
        opt22.setHelp("Bot 2 INIT string, e.g.: process:java -cp bin warlight.bot.BotStarter");
        
        jsap.registerParameter(opt22);
        
        FlaggedOption opt23 = new FlaggedOption(ARG_BOT_ID_BATCH_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOT_ID_BATCH_SHORT)
            .setLongFlag(ARG_BOT_ID_BATCH_LONG);    
        opt23.setHelp("Bot ID to use for BATCH fights agains other bots specified within batch property file.");
        
        jsap.registerParameter(opt23);
        
        FlaggedOption opt24 = new FlaggedOption(ARG_BOTS_BATCH_PROPERTIES_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false) 
            .setShortFlag(ARG_BOTS_BATCH_PROPERTIES_SHORT)
            .setLongFlag(ARG_BOTS_BATCH_PROPERTIES_LONG);    
        opt24.setHelp("Property file with botId=botInit entries to use for batch fights.");
        
        jsap.registerParameter(opt24);
        
        
        FlaggedOption opt3 = new FlaggedOption(ARG_REVERSE_GAMES_LONG)
            .setStringParser(JSAP.BOOLEAN_PARSER)
            .setRequired(false)
            .setDefault("true")
            .setShortFlag(ARG_REVERSE_GAMES_SHORT)
            .setLongFlag(ARG_REVERSE_GAMES_LONG);    
        opt3.setHelp("Whether we should also generate rounds for 'reversed games', that is, play another 'games count' rounds where Bot2 plays as Player1 and Bot1 as Player2.");
        
        jsap.registerParameter(opt3);
        
        FlaggedOption opt31 = new FlaggedOption(ARG_GAME_CONFIG_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(true) 
            .setShortFlag(ARG_GAME_CONFIG_SHORT)
            .setLongFlag(ARG_GAME_CONFIG_LONG);    
        opt31.setHelp("List of simulator options, see Config.fromString()");
    
        jsap.registerParameter(opt31);
        
        FlaggedOption opt32 = new FlaggedOption(ARG_RESULT_DIR_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setDefault("./results/fights")
            .setShortFlag(ARG_RESULT_DIR_SHORT)
            .setLongFlag(ARG_RESULT_DIR_LONG);    
        opt32.setHelp("Directory where to output results, will be created if not exist.");
        
        jsap.registerParameter(opt32);
        
        FlaggedOption opt321 = new FlaggedOption(ARG_REPLAY_DIR_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setDefault("./results/replays")
            .setShortFlag(ARG_REPLAY_DIR_SHORT)
            .setLongFlag(ARG_REPLAY_DIR_LONG);    
        opt321.setHelp("Directory where to output replays, will be created if not exist.");
        
        jsap.registerParameter(opt321);
        
        FlaggedOption opt322 = new FlaggedOption(ARG_TABLE_FILE_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setDefault("./results/all-results.csv")
            .setShortFlag(ARG_TABLE_FILE_SHORT)
            .setLongFlag(ARG_TABLE_FILE_LONG);    
        opt322.setHelp("File where to collect results of all fights (we incrementally append results here).");
        
        jsap.registerParameter(opt322);
        
        FlaggedOption opt33 = new FlaggedOption(ARG_GAMES_COUNT_LONG)
            .setStringParser(JSAP.INTEGER_PARSER)
            .setRequired(false)
            .setDefault("5")
            .setShortFlag(ARG_GAMES_COUNT_SHORT)
            .setLongFlag(ARG_GAMES_COUNT_LONG);    
        opt33.setHelp("How many fight rounds (full games) bots should fight each other.");
    
        jsap.registerParameter(opt33);
    
        FlaggedOption opt6 = new FlaggedOption(ARG_SEED_LONG)
            .setStringParser(JSAP.INTEGER_PARSER)
            .setRequired(false)
            .setDefault("0")
            .setShortFlag(ARG_SEED_SHORT)
            .setLongFlag(ARG_SEED_LONG);    
        opt6.setHelp("Seed to be used when generating seeds for respective levels.");
    
        jsap.registerParameter(opt6);
       }

    private static void readConfig(String[] args) {
        System.out.println("Parsing command arguments.");
        
        try {
            config = jsap.parse(args);
        } catch (Exception e) {
            fail(e.getMessage());
            System.out.println("");
            e.printStackTrace();
            throw new RuntimeException("FAILURE!");
        }
        
        if (!config.success()) {
            String error = "Invalid arguments specified.";
            
            @SuppressWarnings("unchecked")
            Iterator<String> errorIter = config.getErrorMessageIterator();
            if (!errorIter.hasNext()) {
                error += "\n-- No details given.";
            } else {
                while (errorIter.hasNext()) {
                    error += "\n-- " + errorIter.next();
                }
            }
            fail(error);
        }

        seed = config.getInt(ARG_SEED_LONG);

        roundConfig = config.getString(ARG_GAME_CONFIG_LONG);
        
        gamesCount = config.getInt(ARG_GAMES_COUNT_LONG);
        
        reverseGames = config.getBoolean(ARG_REVERSE_GAMES_LONG);
        
        resultDir = config.getString(ARG_RESULT_DIR_LONG);
        
        replayDir = config.getString(ARG_REPLAY_DIR_LONG);
        
        tableFileName = config.getString(ARG_TABLE_FILE_LONG);
        
        bot1Name = config.getString(ARG_BOT1_NAME_LONG, null);
        
        bot1Init = config.getString(ARG_BOT1_INIT_LONG, null);
        
        bot2Name = config.getString(ARG_BOT2_NAME_LONG, null);
        
        bot2Init = config.getString(ARG_BOT2_INIT_LONG, null);
        
        botIdBatch = config.getString(ARG_BOT_ID_BATCH_LONG, null);
        
        botsBatchPropertyFileName = config.getString(ARG_BOTS_BATCH_PROPERTIES_LONG, null);
    }
    
    private static void sanityChecks() {
        System.out.println("Sanity checks...");
        
        System.out.println("-- seed: " + seed);
        System.out.println("-- game config: " + roundConfig);
        System.out.println("-- #games: " + gamesCount);
        System.out.println("-- play reversed games: " + reverseGames);
        
        resultDirFile = new File(resultDir);
        System.out.println("-- result dir: " + resultDir + " --> " + resultDirFile.getAbsolutePath());
        
        if (!resultDirFile.exists()) {
            System.out.println("---- result dir does not exist, creating!");
            resultDirFile.mkdirs();
        }
        if (!resultDirFile.exists()) {
            fail("Result dir does not exists. Parsed as: " + resultDir + " --> " + resultDirFile.getAbsolutePath());
        }
        if (!resultDirFile.isDirectory()) {
            fail("Result dir is not a directory. Parsed as: " + resultDir + " --> " + resultDirFile.getAbsolutePath());
        }
        System.out.println("---- result directory exists, ok");
        
        replayDirFile = new File(replayDir);
        System.out.println("-- replay dir: " + replayDir + " --> " + replayDirFile.getAbsolutePath());
        
        if (!replayDirFile.exists()) {
            System.out.println("---- replay dir does not exist, creating!");
            replayDirFile.mkdirs();
        }
        if (!replayDirFile.exists()) {
            fail("Replay dir does not exists. Parsed as: " + replayDir + " --> " + replayDirFile.getAbsolutePath());
        }
        if (!replayDirFile.isDirectory()) {
            fail("Replay dir is not a directory. Parsed as: " + replayDir + " --> " + replayDirFile.getAbsolutePath());
        }
        System.out.println("---- replay directory exists, ok");
        
        tableFile = new File(tableFileName);
        System.out.println("-- table file: " + tableFileName + " --> " + tableFile.getAbsolutePath());
        
        if (tableFile.exists() && !tableFile.isFile()) {
            fail("Table file exists and is not a file. Parsed as: " + tableFileName + " --> " + tableFile.getAbsolutePath());
        }
        
        if (bot1Name != null && bot2Name != null && bot1Init != null && bot2Init != null) {
            batchFight = false;
            System.out.println("-- Bot 1 & 2 ids / inits specified, will execute 1v1 fights");
            System.out.println("---- bot1: " + bot1Name + " / " + bot1Init);
            System.out.println("---- bot2: " + bot2Name + " / " + bot2Init);
        } else 
        if (botIdBatch != null && botsBatchPropertyFileName != null) {
            batchFight = true;                
            System.out.println("-- Bot batch ID + Bots batch property file name specified, will execute batch fights");
            
            System.out.println("---- Bot ID for batch fights: " + botIdBatch);
            
            botsBatchPropertyFile = new File(botsBatchPropertyFileName);
            System.out.println("---- Bots property file for batch fights: " + botsBatchPropertyFileName + " --> " + botsBatchPropertyFile.getAbsolutePath());
            
            if (!botsBatchPropertyFile.exists()) {
                fail("------ File does not exist: " + botsBatchPropertyFileName + " --> " + botsBatchPropertyFile.getAbsolutePath());
            }
            if (!botsBatchPropertyFile.isFile()) {
                fail("------ File is not a file: " + botsBatchPropertyFileName + " --> " + botsBatchPropertyFile.getAbsolutePath());
            }
            System.out.println("------ Bots property file exists, ok");
            
        } else {
            fail("Invalid specification, you either have to specify Bot 1 Id+Init and Bot 2 Id+Init for 1v1 fights, or Bot Id for batch fights together with property files with botId=botInit pairs.");
        }
        
        System.out.println("Sanity checks OK!");
    }
    
    private static void fight() {
        
        if (batchFight) {
            batchFight();
        } else {
            fight1v1();
        }
    }
    
    private static void fight1v1() {
    
        System.out.println("EXECUTING 1v1 FIGHT!");
        
        WarlightFightConfig config = new WarlightFightConfig();
        
        config.config = Config.fromString(roundConfig);
        config.seed = seed;
        config.games = gamesCount;
        
        WarlightFight fight = new WarlightFight(config, tableFile, resultDirFile, replayDirFile);
        fight.fight(bot1Name, bot1Init, bot2Name, bot2Init);
        
        if (reverseGames) {
            fight.fight(bot2Name, bot2Init, bot1Name, bot1Init);
        }
    }
    
    private static void batchFight() {
        System.out.println("EXECUTING BATCH FIGHTS!");
        
        WarlightFightConfig config = new WarlightFightConfig();
        
        config.config = Config.fromString(roundConfig);
        config.seed = seed;
        config.games = gamesCount;
        
        WarlightFightBatch batch = new WarlightFightBatch(botsBatchPropertyFile, config);
        
        batch.fight(botIdBatch, reverseGames, tableFile, resultDirFile, replayDirFile);
    }
        
    // ==============
    // TEST ARGUMENTS
    // ==============
    
    public static String[] getTestArgs_1v1() {
        return new String[] {
                  "-s", "20"     // seed
                , "-o", "GAME;x;x;5000;false;false;200;false;-1;true;5;100;CONTINUAL_1_1_A60_D70"   // game-config
                , "-g", "10"      // games-count
                , "-r", "false"   // reverse-games
                , "-a", "MyBot"                              // bot1-id
                , "-b", "internal:MyBot" // bot1-init
                , "-c", "AggressiveBot"                                 // bot2-id
                , "-d", "internal:warlight.bot.custom.AggressiveBot"           // bot2-init
                , "-u", "./results/fights"              // result-dir
                , "-y", "./results/replays"           // replay-dir
                , "-t", "./results/all-results.csv"   // single results file
        };
        
        // engine config:
        //        result.gameId = parts[0];                                        // should be always: GAME
        //        result.player1Name = parts[1];                                   // will be auto-changed
        //        result.player2Name = parts[2];                                   // will be auto-changed
        //        result.botCommandTimeoutMillis = Integer.parseInt(parts[3]);
        //        result.visualize = Boolean.parseBoolean(parts[4]);
        //        result.visualizeContinual = (parts[5].toLowerCase().equals("null") ? null : Boolean.parseBoolean(parts[5]));
        //        result.visualizeContinualFrameTimeMillis = (parts[6].toLowerCase().equals("null") ? null : Integer.parseInt(parts[6]));
        //        result.logToConsole = Boolean.parseBoolean(parts[7]);

        // followed by game config:
        //        result.seed = Integer.parseInt(parts[0]);                        // will be auto-changed according to master seed above
        //        result.fullyObservableGame = Boolean.parseBoolean(parts[1]);
        //        result.startingArmies = Integer.parseInt(parts[2]);
        //        result.maxGameRounds = Integer.parseInt(parts[3]);
        //        result.fight = FightMode.valueOf(parts[4]);                      // see FightMode for strings
    }
    
    public static String[] getTestArgs_Batch() {
        return new String[] {
                  "-s", "20"     // seed
                , "-o", "GAME;x;x;5000;false;null;null;false;-1;true;5;100;CONTINUAL_1_1_A60_D70"   // game-config
                , "-g", "3"      // games-count
                , "-r", "true"   // reverse-games
                , "-e", "AggressiveBot"               // bot-id that will perform fights against all other bots within batch property file
                , "-f", "batch-fight.properties"       // batch property file
                , "-u", "./results/fights"            // result-dir
                , "-y", "./results/replays"           // replay-dir
                , "-t", "./results/all-results.csv"   // single results file
        };
    }    
    
    public static void main(String[] args) throws JSAPException {
        // -----------
        // FOR TESTING
        // -----------
        args = getTestArgs_1v1();        
        //args = getTestArgs_Batch();
        
        // --------------
        // IMPLEMENTATION
        // --------------
        
        initJSAP();
        
        header();
        
        readConfig(args);
        
        sanityChecks();
        
        fight();
        
        System.out.println("---// FINISHED //---");
        
        System.exit(0);
    }

}
