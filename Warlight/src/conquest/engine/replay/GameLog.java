package conquest.engine.replay;

import conquest.engine.Config;
import conquest.engine.GameResult;

public abstract class GameLog {
    
    public static enum Way {
        ENGINE_TO_BOT("-->"),
        BOT_TO_ENGINE("<--");
        
        public final String mark;

        private Way(String mark) {
            this.mark = mark;
        }
    }
    
    public static class LogLine {
        
        boolean comment = false;
        
        int player;
        Way way;
        String line;
        
        /**
         * Log COMMENT
         * @param who
         * @param line
         */
        public LogLine(int who, String msg) {
            comment = true;
            player = who;
            line = msg;
        }
        
        /**
         * Log ACTION
         * @param player
         * @param way
         * @param line
         */
        public LogLine(int player, Way way, String line) {
            this.player = player;
            this.way = way;
            this.line = line;
        }

        public String asString() {
            if (comment) {
                return "#" + player + ":" + line;
            } else {
                return player + "|" + way.mark + "|" + line;
            }
        }
        
        public static LogLine fromString(String line) {
            if (line.startsWith("#")) {
                line = line.substring(1);
                int whoEndIndex = line.indexOf(":");
                int who = Integer.parseInt(line.substring(0, whoEndIndex));
                String msg = line.substring(whoEndIndex+1);
                return new LogLine(who, msg);
            } else {
                String[] parts = line.split("\\|");
                if (parts.length < 2 || parts.length > 3) {
                    throw new RuntimeException("Invalid line, split into less/more than 2 or 3 parts ... 2 or 3 != " + parts.length + ": " + line);
                }
                int player = Integer.parseInt(parts[0]);
                Way way = parts[1].equals(Way.ENGINE_TO_BOT.mark) ? Way.ENGINE_TO_BOT : Way.BOT_TO_ENGINE;
                String log = "";
                if (parts.length == 3) {
                    log = parts[2]; 
                }
                return new LogLine(player, way, log);
            }
        }
        
    }
    
    public abstract void start(Config config);
    
    public abstract void finish(GameResult result);
    
    public void logComment(int who, String line) {
        log(new LogLine(who, line));
    }
    
    public void logEngineToBot(int player, String line) {
        log(new LogLine(player, Way.ENGINE_TO_BOT, line));
    }
    
    public void logBotToEngine(int player, String line) {
        log(new LogLine(player, Way.BOT_TO_ENGINE, line));
    }
    
    protected abstract void log(LogLine line);

}
