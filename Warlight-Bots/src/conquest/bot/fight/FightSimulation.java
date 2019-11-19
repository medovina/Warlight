package conquest.bot.fight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serves for the computation of victory probabilities of #ATTACKERS vs #DEFENDERS fights.
 * @author Jimmy
 */
public class FightSimulation {
    
    public static class FightResults implements Serializable {
        
        /**
         * AUTO-GENERATED 
         */
        private static final long serialVersionUID = -2574692112490137019L;
        
        public final FightResult[][] results;
        
        /**
         * Chance that an attacker will be killed during one fight round.
         */
        public final double attackerDieChance;
        /**
         * Chance that a defender will be killed during one fight round.
         */
        public final double defenderDieChance;
        /**
         * If fight-state probability dropped below this number, it was rounded to zero and was not used during computation.
         */
        public final double computationAttackersWinChanceTrim;        
        
        public FightResults(int attackersMax, int defendersMax, double attackerDieChance, double defenderDieChance, double computationAttackersWinChanceTrim) {
            results = new FightResult[attackersMax+1][defendersMax+1];
            for (int i = 0; i <= attackersMax; ++i) {
                for (int j = 0; j <= defendersMax; ++j) {
                    results[i][j] = null;
                }
            }
            this.attackerDieChance = attackerDieChance;
            this.defenderDieChance = defenderDieChance;
            this.computationAttackersWinChanceTrim = computationAttackersWinChanceTrim;
        }
        
        public int getAttackersMax() {
            return results.length-1;
        }
        
        public int getDefendersMax() {
            return results[0].length-1;
        }
        
        public FightResult getResult(int attackers, int defenders) {
            return results[attackers][defenders];
        }
        
    }
    
    public static class FightAttackersResults extends FightResults implements Serializable {
        
        /**
         * AUTO-GENERATED 
         */
        private static final long serialVersionUID = -2574698112490127019L;
        
        /**
         * Whenever overall WIN CHANCE of the attackers drops below this number, we will not generate results for it, we will store NULL for that instead.
         */
        public final double attackersWinChanceTrim;
        
        public FightAttackersResults(int attackersMax, int defendersMax, double attackerDieChance, double defenderDieChance, double computationAttackersWinChanceTrim, double attackersWinChanceTrim) {
            super(attackersMax, defendersMax, attackerDieChance, defenderDieChance, computationAttackersWinChanceTrim);        
            this.attackersWinChanceTrim = attackersWinChanceTrim;
        }

        public double getAttackersWinChance(int attackers, int defenders) {
            try {
                if (results[attackers][defenders] == null) return 0.0;
                return results[attackers][defenders].attackersWinChance;
            } catch (Exception e) {
                return 0.0;
            }
        }

        public double getDefendersWinChance(int attackers, int defenders) {
            try {
                if (results[attackers][defenders] == null) return 1.0;
                return results[attackers][defenders].defendersWinChance;
            } catch (Exception e) {
                return 1.0;
            }
        }

        public double getExpectedAttackersDeaths(int attackers, int defenders) {
            try {
                if (results[attackers][defenders] == null) return attackers;
                return results[attackers][defenders].expectedAttackersDeaths;
            } catch (Exception e) {
                return attackers;
            }
        }

        public Double getExpectedDefendersDeaths(int attackers, int defenders) {
            try {
                if (results[attackers][defenders] == null) return null;
                return results[attackers][defenders].expectedDefendersDeaths;
            } catch (Exception e) {
                return null;
            }
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
                throw new RuntimeException("Failed to save FightAttackersResults into: " + file.getAbsolutePath(), e);
            }            
        }
        
        public static FightAttackersResults loadFromFile(File file) {
            ObjectInputStream stream = null;
            FightAttackersResults result = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                result = (FightAttackersResults)stream.readObject();
                stream.close();
            } catch (Exception e) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e1) {                        
                    }
                }
                String error = "Failed to load FightAttackersResults from: " + file.getAbsolutePath();
                System.err.println(error);
                throw new RuntimeException(error, e);
            }        
            return result;
        }
        
    }
    
    public static class FightDefendersResults extends FightResults implements Serializable {
        
        /**
         * AUTO-GENERATED 
         */
        private static final long serialVersionUID = -2571698112490127018L;
        
        /**
         * Whenever overall WIN CHANCE of the defenders drops below this number, we will not generate results for it, we will store NULL for that instead.
         */
        public final double defendersWinChanceTrim;
        
        public FightDefendersResults(int attackersMax, int defendersMax, double attackerDieChance, double defenderDieChance, double computationAttackersWinChanceTrim, double defendersWinChanceTrim) {
            super(attackersMax, defendersMax, attackerDieChance, defenderDieChance, computationAttackersWinChanceTrim);
            this.defendersWinChanceTrim = defendersWinChanceTrim;
        }
                
        public double getAttackersWinChance(int attackers, int defenders) {
            if (results[attackers][defenders] == null) return 1;
            return results[attackers][defenders].attackersWinChance;
        }
        
        public double getDefendersWinChance(int attackers, int defenders) {
            if (results[attackers][defenders] == null) return 0;
            return results[attackers][defenders].defendersWinChance;
        }
        
        public Double getExpectedAttackersDeaths(int attackers, int defenders) {
            if (results[attackers][defenders] == null) return null;
            return results[attackers][defenders].expectedAttackersDeaths;
        }
        
        public double getExpectedDefendersDeaths(int attackers, int defenders) {
            if (results[attackers][defenders] == null) return defenders;
            return results[attackers][defenders].expectedDefendersDeaths;
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
                throw new RuntimeException("Failed to save FightDefendersResults into: " + file.getAbsolutePath(), e);
            }            
        }
        
        public static FightDefendersResults loadFromFile(File file) {
            ObjectInputStream stream = null;
            FightDefendersResults result = null;
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                result = (FightDefendersResults)stream.readObject();
                stream.close();
            } catch (Exception e) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e1) {                        
                    }
                }
                throw new RuntimeException("Failed to load FightDefendersResults from: " + file.getAbsolutePath(), e);
            }        
            return result;
        }
        
    }
        
    public double[][] states;
    
    public FightResult result;
    
    private int attackersMax;
    private int defendersMax;
    private double attackerDieChance;
    private double defenderDieChance;
    
    private double a1d1;
    private double a1d0;
    private double a0d1;
    
    public FightSimulation(int attackers, int defenders, double defenderDieChance, double attackerDieChance) {
        this.attackersMax = attackers;
        this.defendersMax = defenders;
        this.attackerDieChance = defenderDieChance;
        this.defenderDieChance = attackerDieChance;
        
        states = new double[attackers+1][defenders+1];
        for (int i = 0; i <= attackers; ++i) {
            for (int j = 0; j <= defenders; ++j) {
                states[i][j] = 0;
            }
        }
        
        states[attackers][defenders] = 1;
        
        // 0 attackers died, 0 defenders died
        double a0d0 = (1-defenderDieChance) * (1-attackerDieChance);
        // 1 attacker died, 1 defender died
        a1d1 = defenderDieChance * attackerDieChance;
        // 1 attacker died, 0 defenders died
        a1d0 = (1-defenderDieChance) * attackerDieChance;
        // 0 attackers died, 1 defender died
        a0d1 = defenderDieChance * (1-attackerDieChance);
        
        double sum = 1 - a0d0;
        
        // DIVIDIND a0d0 BETWEEN OTHER TRANSITIONS
        a1d1 = a1d1 + a0d0 * (a1d1 / sum);
        a1d0 = a1d0 + a0d0 * (a1d0 / sum);
        a0d1 = a0d1 + a0d0 * (a0d1 / sum);
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

    /**
     * @param chanceTrim once state probability drops below 'chanceTrim' it will  be rounded to 0 and won't be used to compute further results.
     * @return
     */
    public FightSimulation compute(double attackersVictoryChanceTrim) {
        int target = Math.max(attackersMax, defendersMax);
        
        System.out.println("COMPUTING FIGHT MATRIX A:" + attackersMax + " vs. D:" + defendersMax);
        System.out.println("Attacker die chance -> " + attackerDieChance);
        System.out.println("Defender die chance -> " + defenderDieChance);
        System.out.println("A1D1 -> " + FightUtils.formatterChance.format(a1d1));
        System.out.println("A1D0 -> " + FightUtils.formatterChance.format(a1d0));
        System.out.println("A0D1 -> " + FightUtils.formatterChance.format(a0d1));
        System.out.println("SUM  -> " + FightUtils.formatterChance.format(a1d1 + a1d0 + a0d1));
        long time = System.currentTimeMillis();                
        
        System.out.println("FIRST HALF");
        for (int i = 0; i < target; ++i) {
            for (int j = 0; j < target; ++j) {
                int attackers = attackersMax - i + j;
                int defenders = defendersMax - j;
                
                if (attackers < 1 || attackers > attackersMax) break;
                if (defenders < 1 || defenders > defendersMax) break;
                
                if (states[attackers][defenders] > attackersVictoryChanceTrim) {                
                    updateState(attackers, defenders);
                }
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
    
    public FightResult getResult() {
        
        System.out.println("READING PROBABILITIES");
        
        FightResult result = new FightResult();
        
        double sum = 0;
        
        double winSum = 0;
        double loseSum = 0;
        
        double expectedAttackersDeaths = 0;
        double expectedDefendersDeaths = 0;
        
        for (int i = 1; i <= attackersMax; ++i) {
            System.out.println("WIN  ["+i+"][0] -> " + FightUtils.formatterChance.format(states[i][0]));
            sum = sum + states[i][0];
            winSum = winSum + states[i][0];
            
            double attackersDied = attackersMax - i;
            double defendersDied = defendersMax;
            
            expectedAttackersDeaths = expectedAttackersDeaths + states[i][0] * attackersDied;
            expectedDefendersDeaths = expectedDefendersDeaths + states[i][0] * defendersDied;
        }
        
        for (int i = 0; i <= defendersMax; ++i) {
            System.out.println("LOSE [0]["+i+"] -> " + FightUtils.formatterChance.format(states[0][i]));
            sum = sum + states[0][i];
            loseSum = loseSum + states[0][i];
            
            double attackersDied = attackersMax;
            
            // defenders get extra army if both armies are wiped out
            double defendersDied = defendersMax - (i == 0 ? 1 : i);    
            
            expectedAttackersDeaths = expectedAttackersDeaths + states[0][i] * attackersDied;
            expectedDefendersDeaths = expectedDefendersDeaths + states[0][i] * defendersDied;
        }
        
        result.attackersWinChance = winSum;
        result.defendersWinChance = loseSum;
        result.expectedAttackersDeaths = expectedAttackersDeaths;
        result.expectedDefendersDeaths = expectedDefendersDeaths;
        
        result.println();
        System.out.println("CONTROL SUM -> " + FightUtils.formatterChance.format(sum));
        
        this.result = result;
        
        return result;
    }
    
    private void updateState(int attackers, int defenders) {
        double state = states[attackers][defenders];
        
        int aMinus = attackers - 1;
        int dMinus = defenders - 1;
        
        //System.out.println("[" + attackers + "][" + defenders + "] -> " + FightUtils.formatterChance.format(state.probability) + " -> [" + aMinus + "][" + defenders + "] | [" + attackers + "][" + dMinus + "] | [" + aMinus + "][" + dMinus + "]");
        
        if (aMinus >= 0) {
            if (dMinus >= 0) {
                double sA1D1 = states[aMinus][dMinus];
                states[aMinus][dMinus] = sA1D1 + state * a1d1;
            }
            
            double sA1D0 = states[aMinus][defenders];
            states[aMinus][defenders] = sA1D0 + state * a1d0;
        }
        if (dMinus >= 0) {
            double sA0D1 = states[attackers][dMinus];
            states[attackers][dMinus] = sA0D1 + state * a0d1;
        }    
    }
    
    public static void GenerateFightResults(int attackersMax, int defendersMax, double defenderDieChance, double attackerDieChance, double computationAttackersWinChanceTrim, double attackersWinChanceTrim, double defendersWinChanceTrim, File attackersFile, File defendersFile) {
        // ATTACKERS
        FightAttackersResults attackersResults = new FightAttackersResults(attackersMax, defendersMax, attackerDieChance, defenderDieChance, computationAttackersWinChanceTrim, attackersWinChanceTrim);
        for (int defenders = 1; defenders <= defendersMax; ++defenders) {
            for (int attackers = attackersMax; attackers > 0; --attackers) {            
                FightSimulation simulator = new FightSimulation(attackers, defenders, defenderDieChance, attackerDieChance);
                simulator.compute(computationAttackersWinChanceTrim);
                FightResult result = simulator.getResult();
                if (result.attackersWinChance < attackersWinChanceTrim) {
                    // TOO SMALL CHANCE FOR VICTORY!
                    attackersResults.results[attackers][defenders] = null;
                    // having fewer attackers won't help us
                    // => continue with another number of defenders
                    break;
                } else {
                    attackersResults.results[attackers][defenders] = result;
                }
            }
        }
        System.out.println("SAVING FILE: " + attackersFile.getAbsolutePath());
        attackersResults.saveToFile(attackersFile);
        
        // DEFENDERS
        FightDefendersResults defendersResults = new FightDefendersResults(attackersMax, defendersMax, attackerDieChance, defenderDieChance, computationAttackersWinChanceTrim, defendersWinChanceTrim);
        for (int attackers = 1; attackers <= attackersMax; ++attackers) {
            for (int defenders = defendersMax; defenders > 0; --defenders) {                        
                FightSimulation simulator = new FightSimulation(attackers, defenders, defenderDieChance, attackerDieChance);
                simulator.compute(computationAttackersWinChanceTrim);
                FightResult result = simulator.getResult();
                if (result.defendersWinChance < defendersWinChanceTrim) {
                    // TOO SMALL CHANCE FOR DEFENDERS VICTORY!
                    defendersResults.results[attackers][defenders] = null;
                    // having fewer defenders won't help us
                    // => continue with another number of attackers
                    break;
                } else {
                    defendersResults.results[attackers][defenders] = result;
                }
            }
        }
        System.out.println("SAVING FILE: " + defendersFile.getAbsolutePath());
        defendersResults.saveToFile(defendersFile);
        
        System.out.println("---// DONE //---");
    }
    
    public static void GenerateFightDefendersResults(int attackersMax, int defendersMax, double defenderDieChance, double attackerDieChance, double computationAttackersWinChanceTrim, double winChanceTrim, File file) {
        
        System.out.println("---// DONE //---");
    }
    
    public static void main(String[] args) {
        // EXAMPLE HOW TO GENERATE RESULTS FOR A SINGLE FIGHT
        //new FightSimulation(80, 90, 0.6, 0.7).compute(0.00000001).getResult();
        
        // EXAMPLE HOW TO GENERATE AND SAVE THE WHOLE MATRIX...
        //GenerateFightResults(  50,   50, 0.6, 0.7, 0.00000001, 0.001, 0.001, new File("FightSimulation-Attackers-A50-D50.obj"),     new File("FightSimulation-Defenders-A50-D50.obj"));        
        //GenerateFightResults( 100,  100, 0.6, 0.7, 0.00000001, 0.001, 0.001, new File("FightSimulation-Attackers-A100-D100.obj"),   new File("FightSimulation-Defenders-A100-D100.obj"));
        GenerateFightResults( 200,  200, 0.6, 0.7, 0.00000001, 0.001, 0.001, new File("FightSimulation-Attackers-A200-D200.obj"),   new File("FightSimulation-Defenders-A200-D200.obj"));
        //GenerateFightResults( 500,  500, 0.6, 0.7, 0.00000001, 0.001, 0.001, new File("FightSimulation-Attackers-A500-D500.obj"),   new File("FightSimulation-Defenders-A500-D500.obj"));
        //GenerateFightResults(1000, 1000, 0.6, 0.7, 0.00000001, 0.001, 0.001, new File("FightSimulation-Attackers-A1000-D1000.obj"), new File("FightSimulation-Defenders-A1000-D1000.obj"));        
    }    
}
