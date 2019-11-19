package conquest.bot.fight;

import java.util.HashMap;
import java.util.Map;

public class Fight {
    
    public static final double ATTACKERS_CHANCE_TO_KILL = 0.6;
    public static final double DEFENDERS_CHANCE_TO_KILL = 0.7;
    
    private static Map<Long, Long> factorialCache = new HashMap<Long, Long>();
    
    public static long factorial(long n) {
        Long result = factorialCache.get(n);
        if (result != null) return result;
        result = 1l;
        for (long i = 2; i <= n; ++i) {
            result *= i;
        }
        factorialCache.put(n, result);
        return result;
    }
    
    public static long NK(long n, long k) {
        if (n < k) return 0;
        if (k == 0 || n == k) return 1;        
        return (factorial(n)) / ((factorial(k)) * factorial(n-k));
    }
    
    /**
     * Chance to reach 'NUMBER' of killed soldiers in given 'ROUND' when you have 'CHANCE TO KILL'.
     * @param number
     * @param round
     * @param chanceToKill
     * @return
     */
    public static double KillChance(long number, long round, double chanceToKill) {
        if (round <= 0) return 0;
        if (number > round) return 0;
        return ((double)NK(round, number)) * Math.pow(chanceToKill, number) * Math.pow(1-chanceToKill, round - number);        
    }
    
    /**
     * Aggregated version of {@link #KillChance(long, long, double)}. Chance you will kill 'number' by the fight round 1-'round'.
     * @param number
     * @param round
     * @param chanceToKill
     * @return
     */
    public static double KillAtLeastChance(long number, long round, double chanceToKill) {
        if (round <= 0) return 0;
        if (number > round) return 0;
        double result = 0;
        for (long i = number; i <= round; ++i) {
            result += KillChance(i, round, chanceToKill);
        }
        return result;
    }
    
    public static void main(String[] args) {
        for (int i = 1; i <= 20; ++i) {
            System.out.println("Dead at least " + i + " in round 20 -> A:" + KillAtLeastChance(i, 20, DEFENDERS_CHANCE_TO_KILL) + " | D:" + KillAtLeastChance(i, 20, ATTACKERS_CHANCE_TO_KILL));
        }        
    }
    
}
