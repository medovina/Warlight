package game;

public enum FightMode {
    
    /**
     * RISK-like attack
     * -- fight happens in round until one of side is fully wiped out
     * -- each round there is a 60% chance that 1 defending army is killed and 70% chance that 1 attacking army is killed (independent variables)
     * 
     * You may use: {@link Engine#doAttack_CONTINUAL_1_1_A60_D70(Random, int, int)} method for off-engine simulation.
     */
    CONTINUAL_1_1_A60_D70
    
}
