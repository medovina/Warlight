package game;

public enum Team {
    
    PLAYER_1,
    PLAYER_2,
    NEUTRAL;
    
    public static Team getTeam(int player) {
        switch (player) {
            case 0: return NEUTRAL;
            case 1: return PLAYER_1;
            case 2: return PLAYER_2;
            default: throw new RuntimeException("unknown player");
        }
    }
}
