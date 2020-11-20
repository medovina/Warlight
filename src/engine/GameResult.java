package engine;

public class GameResult {
    
    public Config config;
    
    public int player1Regions;
    public int player1Armies;
    
    public int player2Regions;
    public int player2Armies;
    
    public int winner = -1;
    
    /**
     * Number of the round the game ended.
     */
    public int round;

    public int getWinner() {
        return winner;
    }
    
    public String getWinnerName() {
        switch (winner) {
            case -1:
            case 0:
                return "NONE";
            case 1:
                return config == null ? "Bot1" : config.player1Name;
            case 2:
                return config == null ? "Bot2" : config.player2Name;
        }
        return null;
    }
    
    public String getLoserName() {
        switch (winner) {
            case -1:
            case 0:
                return "NONE";
            case 1:
                return config == null ? "Bot2" : config.player2Name;
            case 2:
                return config == null ? "Bot1" : config.player1Name;
        }
        return null;
    }
    
    public int getWinnerRegions() {
        return winner == 1 ? player1Regions : player2Regions;
    }
    
    public int getWinnerArmies() {
        return winner == 1 ? player1Armies : player2Armies;
    }

    public String asString() {
        return getWinner() + ";" + player1Regions + ";" + player1Armies + ";" +
               player2Regions + ";" + player2Armies + ";" + round;
    }
    
    public String getHumanString() {
        return "Winner: " + getWinner() + " [" + getWinnerName() + "] in round " + round +
               "\nPlayer1: " + player1Regions + " regions / " + player1Armies +
               " armies; Player2: " +player2Regions + " regions / " + player2Armies + " armies";
    }
    
    public String getCSVHeader() {
        return "winnerName;loserName;winner;winnerId;player1Regions;player1Armies;player2Regions;player2Armies;round;" + config.getCSVHeader();
    }
    
    public String getCSV() {
        return getWinnerName() + ";" + getLoserName() + ";" +
        (winner == -1 || winner == 0 ? "NONE" : winner) + ";" +
        getWinner() + ";" + player1Regions + ";" + player1Armies + ";" +
        player2Regions + ";" + player2Armies + ";" + round + ";" + config.getCSV();
    }
    
}
