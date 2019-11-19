package conquest.bot.fight;

import java.io.Serializable;

public class FightResult implements Serializable {
    /**
     * AUTO-GENERATED
     */
    private static final long serialVersionUID = 5072973284660981315L;
    
    /**
     * Probability that attackers will win.
     */
    public double attackersWinChance;
    /**
     * Probability that defenders will win.
     */
    public double defendersWinChance;
    /**
     * Expected attacker loss count.
     */
    public double expectedAttackersDeaths;
    /**
     * Expected defender loss count.
     */
    public double expectedDefendersDeaths;
    
    public void println() {
        System.out.println("ATTACKERS WIN -> " + FightUtils.formatterChance.format(attackersWinChance));
        System.out.println("DEFENDERS WIN -> " + FightUtils.formatterChance.format(defendersWinChance));
        System.out.println("EXPECTED ATTACKERS DEATHS -> " + FightUtils.formatterNumber.format(expectedAttackersDeaths));
        System.out.println("EXPECTED DEFENDERS DEATHS -> " + FightUtils.formatterNumber.format(expectedDefendersDeaths));            
    }
}
