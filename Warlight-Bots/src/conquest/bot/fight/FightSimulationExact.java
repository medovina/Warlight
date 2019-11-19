package conquest.bot.fight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FightSimulationExact {
    
    
    public static class FightExactResults implements Serializable {
        
        /**
         * AUTO-GENERATED 
         */
        private static final long serialVersionUID = -2574698112470137019L;
        
        public FightExactResult[][] results;
        
        /**
         * Chance that an attacker will be killed during one fight round.
         */
        public double attackerDieChance;
        /**
         * Chance that a defender will be killed during one fight round.
         */
        public double defenderDieChance;
        
        public FightExactResults(int attackersMax, int defendersMax, double attackerDieChance, double defenderDieChance) {
            results = new FightExactResult[attackersMax+1][defendersMax+1];
            this.attackerDieChance = attackerDieChance;
            this.defenderDieChance = defenderDieChance;
        }
        
        public FightExactResult getResult(int attackers, int defenders) {
            return results[attackers][defenders];
        }
        
        public int getAttackersMax() {
            return results.length-1;
        }
        
        public int getDefendersMax() {
            return results[0].length-1;
        }
        
        public void saveToFile(File file) {
            ObjectOutputStream stream = null;
            try {
                stream = new ObjectOutputStream(new FileOutputStream(file));
                stream.writeObject(this);
                stream.close();
            } catch (Exception e) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e1) {                        
                    }
                }
                throw new RuntimeException("Failed to save FightExactResults into: " + file.getAbsolutePath(), e);
            }            
        }
        
        public static FightExactResults loadFromFile(File file) {
            ObjectInputStream stream = null;
            FightExactResults result = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                result = (FightExactResults)stream.readObject();
                stream.close();
            } catch (Exception e) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e1) {                        
                    }
                }
                throw new RuntimeException("Failed to load FightExactResults from: " + file.getAbsolutePath(), e);
            }        
            return result;
        }
        
    }
        
    public static class FightExactResult extends FightResult implements Serializable {
        
        /**
         * AUTO-GENERATED 
         */
        private static final long serialVersionUID = -6208454356167476112L;
        
        // ================
        // INITIAL POSITION
        // ================        
        
        /**
         * How many attackers have attacked.
         */
        public int attackers;
        /**
         * How many defenders have attacked.
         */
        public int defenders;
        
        // ======
        // RESULT
        // ======
        
        /**
         * Length of 'attackers+1' ... index ~ number of attackers remained at the end of the fight
         * [0] -> null (defender wins in this situation), [1] -> 1 attacker remained, [2] -> 2 attackers remained, ...
         */
        public BigDecimal[] attackersWinChances;
        
        /**
         * Length of 'defenders+1' ... index ~ number of defenders remained at the end of the fight
         * [0] -> all defenders dead, [1] -> 1 defender remained, [2] -> 2 defenders remained, ... 
         */
        public BigDecimal[] defendersWinChances;
    }
    
    public static class State {
        BigDecimal probability = new BigDecimal(0);
    }
    
    public State[][] states;
    
    public FightExactResult result;
    
    private int attackersMax;
    private int defendersMax;
    private double attackerDieChance;
    private double defenderDieChance;
    
    private BigDecimal a1d1;
    private BigDecimal a1d0;
    private BigDecimal a0d1;
    
    public FightSimulationExact(int attackers, int defenders, double defenderDieChance, double attackerDieChance) {
        this.attackersMax = attackers;
        this.defendersMax = defenders;
        this.attackerDieChance = defenderDieChance;
        this.defenderDieChance = attackerDieChance;
        
        states = new State[attackers+1][defenders+1];
        for (int i = 0; i <= attackers; ++i) {
            for (int j = 0; j <= defenders; ++j) {
                states[i][j] = new State();
            }
        }
        
        states[attackers][defenders].probability = new BigDecimal(1);
        
        // 0 attackers died, 0 defenders died
        BigDecimal a0d0 = new BigDecimal((1-defenderDieChance) * (1-attackerDieChance));
        // 1 attacker died, 1 defender died
        a1d1 = new BigDecimal(defenderDieChance * attackerDieChance);
        // 1 attacker died, 0 defenders died
        a1d0 = new BigDecimal((1-defenderDieChance) * attackerDieChance);
        // 0 attackers died, 1 defender died
        a0d1 = new BigDecimal(defenderDieChance * (1-attackerDieChance));
        
        BigDecimal sum = new BigDecimal(1).subtract(a0d0);
        
        // DIVIDIND a0d0 BETWEEN OTHER TRANSITIONS
        a1d1 = a1d1.add(a0d0.multiply(a1d1.divide(sum, 20, RoundingMode.HALF_UP)));
        a1d0 = a1d0.add(a0d0.multiply(a1d0.divide(sum, 20, RoundingMode.HALF_UP)));
        a0d1 = a0d1.add(a0d0.multiply(a0d1.divide(sum, 20, RoundingMode.HALF_UP)));
    }
    
    public int getAttackers() {
        return attackersMax;
    }
    
    public int getDefenders() {
        return defendersMax;
    }
    
    public double getAttackerDieChance() {
        return attackerDieChance;
    }
    
    public double getDefenderDieChance() {
        return defenderDieChance;
    }

    public FightSimulationExact compute() {
        int target = Math.max(attackersMax, defendersMax);
        
        System.out.println("COMPUTING FIGHT MATRIX A:" + attackersMax + " vs. D:" + defendersMax);
        System.out.println("Attacker die chance -> " + attackerDieChance);
        System.out.println("Defender die chance -> " + defenderDieChance);
        System.out.println("A1D1 -> " + FightUtils.formatterChance.format(a1d1));
        System.out.println("A1D0 -> " + FightUtils.formatterChance.format(a1d0));
        System.out.println("A0D1 -> " + FightUtils.formatterChance.format(a0d1));
        System.out.println("SUM  -> " + FightUtils.formatterChance.format((a1d1.add(a1d0).add(a0d1))));
        long time = System.currentTimeMillis();                
        
        System.out.println("FIRST HALF");
        for (int i = 0; i < target; ++i) {
            for (int j = 0; j < target; ++j) {
                int attackers = attackersMax - i + j;
                int defenders = defendersMax - j;
                
                if (attackers < 1 || attackers > attackersMax) break;
                if (defenders < 1 || defenders > defendersMax) break;
                
                updateState(attackers, defenders);            
            }
        }
        
        System.out.println("SECOND HALF");
        for (int i = 0; i < target; ++i) {
            for (int j = 0; j < target; ++j) {
                int attackers = 1 + j;
                int defenders = defendersMax - 1 - i - j;
                
                if (attackers < 1 || attackers > attackersMax) break;
                if (defenders < 1 || defenders > defendersMax) break;
                
                updateState(attackers, defenders);            
            }
        }
        
        System.out.println("FINISHED in " + (System.currentTimeMillis()-time) + " ms");
        
        return this;
    }
    
    public FightExactResult getResult() {
        
        System.out.println("READING PROBABILITIES");
        
        FightExactResult result = new FightExactResult();
        result.attackers = attackersMax;
        result.defenders = defendersMax;        
        result.attackersWinChances = new BigDecimal[result.attackers+1];
        result.defendersWinChances = new BigDecimal[result.defenders+1];
        
        BigDecimal sum = new BigDecimal(0);
        
        BigDecimal winSum = new BigDecimal(0);
        BigDecimal loseSum = new BigDecimal(0);
        
        BigDecimal expectedAttackersDeaths = new BigDecimal(0);
        BigDecimal expectedDefendersDeaths = new BigDecimal(0);
        
        for (int i = 1; i <= attackersMax; ++i) {
            System.out.println("WIN  ["+i+"][0] -> " + FightUtils.formatterChance.format(states[i][0].probability));
            sum = sum.add(states[i][0].probability);
            winSum = winSum.add(states[i][0].probability);
            
            BigDecimal attackersDied = new BigDecimal(attackersMax - i);
            BigDecimal defendersDied = new BigDecimal(defendersMax);
            
            expectedAttackersDeaths = expectedAttackersDeaths.add(states[i][0].probability.multiply(attackersDied));
            expectedDefendersDeaths = expectedDefendersDeaths.add(states[i][0].probability.multiply(defendersDied));
            
            result.attackersWinChances[i] = states[i][0].probability;
        }
        
        for (int i = 0; i <= defendersMax; ++i) {
            System.out.println("LOSE [0]["+i+"] -> " + FightUtils.formatterChance.format(states[0][i].probability));
            sum = sum.add(states[0][i].probability);
            loseSum = loseSum.add(states[0][i].probability);
            
            BigDecimal attackersDied = new BigDecimal(attackersMax);
            BigDecimal defendersDied = new BigDecimal(defendersMax - i);
            
            expectedAttackersDeaths = expectedAttackersDeaths.add(states[0][i].probability.multiply(attackersDied));
            expectedDefendersDeaths = expectedDefendersDeaths.add(states[0][i].probability.multiply(defendersDied));
            
            result.defendersWinChances[i] = states[0][i].probability;
        }
        
        result.attackersWinChance = winSum.doubleValue();
        result.defendersWinChance = loseSum.doubleValue();
        result.expectedAttackersDeaths = expectedAttackersDeaths.doubleValue();
        result.expectedDefendersDeaths = expectedDefendersDeaths.doubleValue();
        
        result.println();
        System.out.println("CONTROL SUM -> " + FightUtils.formatterChance.format(sum));
        
        this.result = result;
        
        return result;
    }
    
    private void updateState(int attackers, int defenders) {
        State state = states[attackers][defenders];
        
        int aMinus = attackers - 1;
        int dMinus = defenders - 1;
        
        //System.out.println("[" + attackers + "][" + defenders + "] -> " + FightUtils.formatterChance.format(state.probability) + " -> [" + aMinus + "][" + defenders + "] | [" + attackers + "][" + dMinus + "] | [" + aMinus + "][" + dMinus + "]");
        
        if (aMinus >= 0) {
            if (dMinus >= 0) {
                State sA1D1 = states[aMinus][dMinus];
                sA1D1.probability = sA1D1.probability.add(state.probability.multiply(a1d1));
            }
            
            State sA1D0 = states[aMinus][defenders];
            sA1D0.probability = sA1D0.probability.add(state.probability.multiply(a1d0));
        }
        if (dMinus >= 0) {
            State sA0D1 = states[attackers][dMinus];
            sA0D1.probability = sA0D1.probability.add(state.probability.multiply(a0d1));
        }    
    }
    
    public static void GenerateFightResults(int attackersMax, int defendersMax, double defenderDieChance, double attackerDieChance, File file) {
        FightExactResults results = new FightExactResults(attackersMax, defendersMax, attackerDieChance, defenderDieChance);
        for (int attackers = 1; attackers <= attackersMax; ++attackers) {
            for (int defenders = 1; defenders <= defendersMax; ++defenders) {
                FightSimulationExact simulator = new FightSimulationExact(attackers, defenders, defenderDieChance, attackerDieChance);
                simulator.compute();
                FightExactResult result = simulator.getResult();
                results.results[attackers][defenders] = result;
            }
        }
        System.out.println("SAVING FILE: " + file.getAbsolutePath());
        results.saveToFile(file);
        System.out.println("---// DONE //---");
    }
    
    public static void main(String[] args) {
        // EXAMPLE HOW TO GENERATE RESULTS FOR A SINGLE FIGHT
        new FightSimulationExact(10, 8, 0.6, 0.7).compute().getResult();
        
        // EXAMPLE HOW TO GENERATE AND SAVE THE WHOLE MATRIX...
        // => about 172MB file...
        //GenerateFightResults(50, 50, 0.6, 0.7, new File("Attackers50-Defenders50-Exact.obj"));
        
        // REQUIRES TOO MUCH MEMORY!
        //GenerateFightResults(100, 100, 0.6, 0.7, new File("Attackers100-Defenders100.obj"));
    }
    
}
