package conquest.game;

public enum FightMode {
    
    /**
     * Original Warlight fight without luck:
     * -- each attacking army has 60% chance to kill one defending army
     * -- each defending army has 70% chance to kill one attacking army
     * 
     * You may use: {@link Engine#doAttack_ORIGINAL_A60_D70(Random, int, int)} method for off-engine simulation.
     */
    ORIGINAL_A60_D70,

    /**
     * RISK-like attack
     * -- fight happens in round until one of side is fully wiped out
     * -- each round there is a 60% chance that 1 defending army is killed and 70% chance that 1 attacking army is killed (independent variables)
     * 
     * You may use: {@link Engine#doAttack_CONTINUAL_1_1_A60_D70(Random, int, int)} method for off-engine simulation.
     */
    CONTINUAL_1_1_A60_D70
    
}