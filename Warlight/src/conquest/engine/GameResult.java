package conquest.engine;

import conquest.game.Team;

public class GameResult {
    
    public Config config;
    
    public int player1Regions;
    public int player1Armies;
    
    public int player2Regions;
    public int player2Armies;
    
    public Team winner = null;
    
    /**
     * Number of the round the game ended.
     */
    public int round;

    public int getWinner() {
        if (winner == null) return 0;
        switch (winner) {
        case NEUTRAL: return 0;
        case PLAYER_1: return 1;
        case PLAYER_2: return 2;
        }
        return 0;
    }
    
    public String getWinnerName() {
        if (winner == null) return "NONE";
        switch (winner) {
        case NEUTRAL: return "NONE";
        case PLAYER_1: return config == null ? "Bot1" : config.player1Name;
        case PLAYER_2: return config == null ? "Bot2" : config.player2Name;
        }
        return null;
    }
    
    public String getLoserName() {
        if (winner == null) return "NONE";
        switch (winner) {
        case NEUTRAL: return "NONE";
        case PLAYER_1: return config == null ? "Bot2" : config.player2Name;
        case PLAYER_2: return config == null ? "Bot1" : config.player1Name;
        }
        return null;
    }
    
    public int getWinnerRegions() {
        return winner == Team.PLAYER_1 ? player1Regions : player2Regions;
    }
    
    public int getWinnerArmies() {
        return winner == Team.PLAYER_1 ? player1Armies : player2Armies;
    }

    public String asString() {
        return getWinner() + ";" + player1Regions + ";" + player1Armies + ";" +
               player2Regions + ";" + player2Armies + ";" + round;
    }
    
    public String getHumanString() {
        return "Winner: " + getWinner() + "[" + getWinnerName() + "] in round " + round +
               "\nPlayer1: " + player1Regions + " regions / " + player1Armies +
               " armies\nPlayer2: " +player2Regions + " regions / " + player2Armies + " armies";
    }
    
    public String getCSVHeader() {
        return "winnerName;loserName;winner;winnerId;player1Regions;player1Armies;player2Regions;player2Armies;round;" + config.getCSVHeader();
    }
    
    public String getCSV() {
        return getWinnerName() + ";" + getLoserName() + ";" +
        (winner == null || winner == Team.NEUTRAL ? "NONE" : winner) + ";" +
        getWinner() + ";" + player1Regions + ";" + player1Armies + ";" +
        player2Regions + ";" + player2Armies + ";" + round + ";" + config.getCSV();
    }
    
}