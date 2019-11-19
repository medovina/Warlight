package conquest.tournament;

import java.io.*;
import java.util.*;

// Read results.csv and output a summary table showing how many times each bot won/lost versus every other bot.

class Bot {
    public int totalWins, totalLosses;
    
    public Map<String, Integer> winsVersus = new HashMap<>();
    public Map<String, Integer> lossesVersus = new HashMap<>();
}

public class Summarize {
    Map<String, Bot> botMap = new HashMap<>();
    
    Bot getBot(String name) {
        Bot b = botMap.get(name);
        if (b == null) {
            b = new Bot();
            botMap.put(name, b);
        }
        return b;
    }
    
    static int get(Map<String, Integer> map, String s) {
        Integer i = map.get(s);
        if (i == null)
            i = 0;
        return i;
    }
    
    static void increment(Map<String, Integer> map, String s) {
        map.put(s, get(map, s) + 1);
    }
    
    static String fix(String s) {
        if (s.charAt(1) == '_')
            s = s.substring(2);
        return s;
    }
    
    void run(BufferedReader in) throws IOException {
        while (true) {
            String line = in.readLine();
            if (line == null) break;
            String[] fields = line.split(";");
            String winner = fix(fields[0]), loser = fix(fields[1]);
            Bot winnerBot = getBot(winner), loserBot = getBot(loser);
            winnerBot.totalWins += 1;
            increment(winnerBot.winsVersus, loser);
            loserBot.totalLosses += 1;
            increment(loserBot.lossesVersus, winner);
        }
        in.close();
        
        List<String> bots = new ArrayList<String>(botMap.keySet());
        bots.sort( (b, c) -> - Integer.compare(getBot(b).totalWins, getBot(c).totalWins) );
        
        PrintWriter out = new PrintWriter("summary.csv");
        PrintWriter out2 = new PrintWriter("summary.html");
        out2.println("<style>table { border-collapse: collapse; } table, th, td { border: 1px solid black; } </style>");
        out2.println("<table>\n<tr><td></td>");
        
        for (String s : bots) {
            out.print(";" + s);
            out2.format("<td>%s</td>", s);
        }
        out.println();
        out.println("</tr>");
        
        for (String bot2 : bots) {
            out.print(bot2 + ";");
            out2.format("<tr><td>%s</td>", bot2);
            
            for (String bot1 : bots) {
                if (bot1.equals(bot2)) {
                    out.print("---;");
                    out2.print("<td>---</td>");
                    continue;
                }
                
                Bot b = getBot(bot1);
                String s = String.format("%d - %d", get(b.winsVersus, bot2), get(b.lossesVersus, bot2));
                out.print(s + ";");
                out2.format("<td>%s</td>", s);
            }
            
            out.println();
            out2.println("</tr>");
        }
        
        out.print("TOTAL;");
        out2.print("<tr><td>TOTAL</td>");
        for (String bot : bots) {
            Bot b = getBot(bot);
            String s = String.format("%d - %d", b.totalWins, b.totalLosses);
            out.print(s + ";");
            out2.format("<td>%s</td>", s);
        }
        out.println();
        out2.println("</tr>\n</table>");
        out.close();
        out2.close();
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("results.csv"));
        
        String header = in.readLine();
        if (!header.startsWith("winnerName;loserName;")) {
            System.err.println("unexpected format");
            in.close();
            return;
        }
        
        new Summarize().run(in);
    }
}
