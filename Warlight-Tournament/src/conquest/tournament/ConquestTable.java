package conquest.tournament;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import conquest.game.Team;
import conquest.tournament.utils.CSV;
import conquest.tournament.utils.CSV.CSVRow;
import conquest.utils.LazyMap;

/**
 * Summarize table.csv that ConquestFight may incrementally output.
 * 
 * @author Jimmy
 */
public class ConquestTable {
    
    public static class FightsSummary {
        
        public int games;
        
        public int rounds;
        public double roundsAvg;
        
        public int wins;
        public double winsAvg;
        
        public int draws;
        public double drawsAvg;
        
        public int loses;
        public double losesAvg;
        
        public int regions;
        public double regionsAvg;
        
        public int armies;
        public double armiesAvg;
        
        public void addGame(int rounds, int winner, int regions, int armies) {
            ++games;
            
            this.rounds += rounds;
            switch (winner) {
            case 1: ++wins; break;
            case 0: ++draws; break;
            case 2: ++loses; break;
            default: throw new RuntimeException("Unhandled winner = " + winner);
            }

            this.regions += regions;
            this.armies += armies;
            
            roundsAvg  = ((double)this.rounds) / ((double)games);
            winsAvg     = ((double)wins) / ((double)games);
            drawsAvg     = ((double)draws) / ((double)games);
            losesAvg    = ((double)loses) / ((double)games);
            regionsAvg = ((double)this.regions) / ((double)games);
            armiesAvg  = ((double)this.armies) / ((double)games);
        }
        
        public String getCSVHeader() {
            return "games;wins;winsAvg;draws;drawsAvg;loses;losesAvg;rounds;roundsAvg;regions;regionsAvg;armies;armiesAvg";
        }
        
        public String getCSV() {
            return games + ";" + wins + ";" + winsAvg + ";" + draws + ";" + drawsAvg + ";" + loses + ";" + losesAvg + ";" + rounds + ";" + roundsAvg + ";" + regions + ";" + regionsAvg + ";" + armies + ";" + armiesAvg;
        }
        
    }
    
    public static class BotSummary extends FightsSummary {
        
        public String botId;
        
        public int matches;
        
        public int matchWins;
        public double matchWinsAvg;
        
        public int matchDraws;
        public double matchDrawsAvg;
        
        public int matchLoses;
        public double matchLosesAvg;
        
        public BotSummary(String botId) {
            this.botId = botId;
        }
        
        public void resetMatches() {
            matches = 0;
            matchWins = 0;
            matchDrawsAvg = 0;
            matchDraws = 0;
            matchDrawsAvg = 0;
            matchLoses = 0;
            matchLosesAvg = 0;
        }
        
        public void addMatch(int winner) {
            ++matches;
            
            switch (winner) {
            case 1:       ++matchWins; break;
            case 0:  ++matchDraws; break;
            case 2: ++matchLoses; break;            
            }
            
            matchWinsAvg  = ((double)matchWins)  / ((double)matches);
            matchDrawsAvg = ((double)matchDraws) / ((double)matches);
            matchLosesAvg = ((double)matchLoses) / ((double)matches);
        }

        public String getCSVHeader() {
            return "botId;matches;matchesWins;matchesWinsAvg;matchesDraws;matchesDrawsAvg;matchesLoses;matchesLosesAvg;" + super.getCSVHeader();
        }
        
        public String getCSV() {
            return botId + ";" + matches + ";" + matchWins + ";" + matchWinsAvg + ";" + matchDraws + ";" + matchDrawsAvg + ";" + matchLoses + ";" + matchLosesAvg + ";" + super.getCSV();
        }
        
    }
    
    public static class MatchSummary extends FightsSummary {
        
        public String bot1Id;
        public String bot2Id;
        
        public Team result = Team.NEUTRAL;
        
        public MatchSummary(String bot1Id, String bot2Id) {
            this.bot1Id = bot1Id;
            this.bot2Id = bot2Id;
        }

        public void addGame(int rounds, int winner, int regions, int armies) {
            super.addGame(rounds, winner, regions, armies);
            
            if (wins > loses) result = Team.PLAYER_1;
            else if (wins < loses) result = Team.PLAYER_2;
            else result = Team.NEUTRAL;
        }
        
        public String getCSVHeader() {
            return "bot1Id;bot2Id;result;" + super.getCSVHeader();
        }
        
        public String getCSV() {
            return bot1Id + ";" + bot2Id + ";" + result + ";" + super.getCSV();
        }
        
    }
    
    public static class TableSummary {
        
        public Map<String, BotSummary> bots = new LazyMap<String, BotSummary>() {

            private static final long serialVersionUID = 1L;

            @Override
            protected BotSummary create(String key) {
                return new BotSummary(key);
            }
            
        };
        
        /**
         * KEY1 won 'value' of games over KEY2
         */
        public LazyMap<String, Map<String, MatchSummary>> matches = new LazyMap<String, Map<String, MatchSummary>>() {

            private static final long serialVersionUID = 1L;

            @Override
            protected Map<String, MatchSummary> create(final String key1) {
                return new LazyMap<String, MatchSummary>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected MatchSummary create(final String key2) {                        
                        return new MatchSummary(key1, key2);
                    }
                    
                };
            }
            
        };
        
        public void addGame(String bot1Id, String bot2Id, Team winner, int rounds,
                            int bot1Regions, int bot1Armies, int bot2Regions, int bot2Armies) {
            
            int bot1Win, bot2Win;
            
            switch (winner) {
            case NEUTRAL:  bot1Win = 0; bot2Win = 0; break;
            case PLAYER_1: bot1Win = 1; bot2Win = 2; break;
            case PLAYER_2: bot1Win = 2; bot2Win = 1; break;
            default: throw new RuntimeException("Unhandled winner = " + winner);
            }
            
            BotSummary bot1 = bots.get(bot1Id);
            BotSummary bot2 = bots.get(bot2Id);
            
            bot1.addGame(rounds, bot1Win, bot1Regions, bot1Armies);
            bot2.addGame(rounds, bot2Win, bot2Regions, bot2Armies);
            
            if (bot1Id.compareTo(bot2Id) < 0) {
                matches.get(bot1Id).get(bot2Id).addGame(rounds, bot1Win, bot1Regions, bot1Armies);
            } else {
                matches.get(bot2Id).get(bot1Id).addGame(rounds, bot2Win, bot2Regions, bot2Armies);
            }            
        }
        
        public void computeMatchWins() {
            for (BotSummary bot : bots.values()) { 
                bot.resetMatches();
            }
            
            for (String botId1 : matches.keySet()) {
                for (String botId2 : matches.get(botId1).keySet()) {
                    MatchSummary match = matches.get(botId1).get(botId2);
                    
                    int bot1Win, bot2Win;
                    
                    switch (match.result) {
                    case NEUTRAL:  bot1Win = 0; bot2Win = 0; break;
                    case PLAYER_1: bot1Win = 1; bot2Win = 2; break;
                    case PLAYER_2: bot1Win = 2; bot2Win = 1; break;
                    default: throw new RuntimeException("Unhandled match.result = " + match.result);
                    } 
                    
                    bots.get(botId1).addMatch(bot1Win);
                    bots.get(botId2).addMatch(bot2Win);
                }
            }
        }
        
    }

    private File tableFileIn;
    private File summaryFileOut;
    
    public ConquestTable(File tableFileIn, File summaryFileOut) {
        this.tableFileIn = tableFileIn;
        this.summaryFileOut = summaryFileOut;
    }
    
    public void run() {
        if (tableFileIn.isFile()) {
            // PROCESS SINGLE FILE
            System.out.println("Reading: " + tableFileIn.getAbsolutePath());            
            TableSummary summary = ConquestTable.readTable(tableFileIn);
            System.out.println("Writing: " + summaryFileOut.getAbsolutePath());
            ConquestTable.writeSummary(summary, summaryFileOut);
        } else 
        if (tableFileIn.isDirectory()) {
            // PROCESS ALL .csv WITHIN THE DIR
            TableSummary summary = new TableSummary();
            
            for (File file : tableFileIn.listFiles()) {
                if (file.getName().endsWith(".csv")) {
                    System.out.println("Reading: " + file.getAbsolutePath());                                
                    ConquestTable.readTable(file, summary);
                }
            }
            
            System.out.println("Summarizing matches...");
            summary.computeMatchWins();
            
            System.out.println("Writing: " + summaryFileOut.getAbsolutePath());
            ConquestTable.writeSummary(summary, summaryFileOut);
        } else {
            throw new RuntimeException("Table file is invalid, neither FILE nor DIRECTORY: " + tableFileIn.getAbsolutePath());
        }
    }
    
    public static TableSummary readTable(File tableFileIn) {
        return readTable(tableFileIn, null);
    }
    
    /**
     * Read another file with results appending data into 'result'.
     * @param tableFileIn
     * @param result if null will be created
     * @return
     */
    public static TableSummary readTable(File tableFileIn, TableSummary result) {
        if (result == null) result = new TableSummary();
        
        try {
            CSV csv = new CSV(tableFileIn, ";", true);
            
            for (CSVRow row : csv.rows) {
                
                Team winner = Team.valueOf(row.getString("winner"));
                
                String bot1Id = row.getString("Bot1");
                String bot2Id = row.getString("Bot2");
                                
                int rounds = row.getInt("round");
                
                int bot1Regions = row.getInt("player1Regions");                
                int bot1Armies = row.getInt("player1Armies");
                
                int bot2Regions = row.getInt("player2Regions");                
                int bot2Armies = row.getInt("player2Armies");
                
                result.addGame(bot1Id, bot2Id, winner, rounds, bot1Regions, bot1Armies, bot2Regions, bot2Armies);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to read table from: " + tableFileIn.getAbsolutePath(), e);
        }
        
        return result;
    }
    
    public static void writeSummary(final TableSummary summary, File summaryFileOut) {
        
        if (summaryFileOut.exists()) summaryFileOut.delete();
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(new FileOutputStream(summaryFileOut));
            
            List<String> botIds = new ArrayList<String>( summary.bots.keySet() );
            Collections.sort(botIds, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int result = summary.bots.get(o2).matchWins - summary.bots.get(o1).matchWins;
                    if (result != 0) return result;
                    MatchSummary match;
                    if (o1.compareTo(o2) < 0) {
                        match = summary.matches.get(o1).get(o2);
                        result = match.loses - match.wins;                        
                    } else {
                        match = summary.matches.get(o2).get(o1);
                        result = match.wins - match.loses;
                    }
                    if (result != 0) return result;
                    result = summary.bots.get(o2).wins - summary.bots.get(o1).wins;
                    if (result != 0) return result;
                    return summary.bots.get(o2).rounds - summary.bots.get(o1).rounds;
                }
            });
            
            // HEADER
            
            writer.print("botId;matchWins;gameWins");
            
            for (String botId : botIds) {
                writer.print(";");
                writer.print(botId);
            }
            
            writer.print(";");
            writer.print(summary.bots.values().iterator().next().getCSVHeader());
            
            writer.println();
            
            // VALUES
            
            for (String bot1Id : botIds) {
                
                BotSummary bot = summary.bots.get(bot1Id);
                
                writer.print(bot1Id);
                writer.print(";");
                writer.print(bot.matchWins);
                writer.print(";");
                writer.print(bot.wins);
                
                for (String bot2Id : botIds) {
                    writer.print(";");                    
                    if (bot1Id.equals(bot2Id)) writer.print("x");
                    else {
                        if (bot1Id.compareTo(bot2Id) < 0) {
                            writer.print("[" + summary.matches.get(bot1Id).get(bot2Id).wins + " : " + summary.matches.get(bot1Id).get(bot2Id).loses + "]");
                        } else {
                            writer.print("[" + summary.matches.get(bot2Id).get(bot1Id).loses + " : " + summary.matches.get(bot2Id).get(bot1Id).wins + "]");
                        }
                    }
                }
                
                writer.print(";");
                writer.print(bot.getCSV());
                
                writer.println();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to output table summary into: " + summaryFileOut.getAbsolutePath(), e);
        } finally {
            if (writer != null) writer.close();
        }
        
    }
    
}
