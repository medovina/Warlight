package warlight.bot.external;

import java.io.File;

import warlight.bot.Bot;
import warlight.bot.BotParser;

/**
 * This runs JAVA FQCN bot as external bot.
 * <br/><br/>
 * Can be used when running "JAVA BOTS" using "process" to avoid the use of bot's FQCN main method
 * that usually contains some "debug start" of the bot. Note that bot FQCN must be on Java classpath.
 * <br/><br/>
 * Usage: java ... warlight.bot.external.JavaBot <Bot FQCN> [ <log file ]
 *
 * @author Jimmy
 */
public class JavaBot {

    private String botFQCN;
    private File logFile;
    
    public JavaBot(String botFQCN) {
        this(botFQCN, null);
    }
    
    public JavaBot(String botFQCN, String logFile) {
        this.botFQCN = botFQCN;
        if (logFile != null) {
            this.logFile = new File(logFile);
        }
    }
    
    public void run() {
        BotParser parser = new BotParser(constructBot());
        if (logFile != null) {
            parser.setLogFile(logFile);
        }
        parser.start();
        
        try {
            parser.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        System.exit(0);
    }

    private Bot constructBot() {
        return BotParser.constructBot(null, botFQCN);
    }
    
    private static void fail(String msg) {        
        System.out.println("================");
        System.out.println("Warlight JavaBot");
        System.out.println("================");
        System.out.println();
        System.out.println(msg);
        System.out.println();
        System.out.println("Usage: java ... warlight.bot.external.JavaBot <Bot FQCN> [ <log file ]");
        System.out.println();
        
        System.exit(1);        
    }
    
    public static void test() {
        exec(new String[]{"warlight.bot.BotStarter"});
    }
    
    public static void exec(String[] args) {
        String botFQCN = null;
        String logFile = null;
        
        if (args.length > 0) {
            botFQCN = args[0];
        }
        if (args.length > 1) {
            logFile = args[1];
        }
        
        if (botFQCN == null) {
            fail("Bot FQCN not specified!");
        }
        
        JavaBot javaBot = new JavaBot(botFQCN, logFile);
        javaBot.run();
    }
    
    public static void main(String[] args) {
        exec(args);
    }
    
}
