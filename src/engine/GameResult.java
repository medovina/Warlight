package engine;

import game.Game;

public class GameResult {
    public Config config;
    
    public int[] regions;
    public int[] armies;
    
    public int winner;

    public int[] totalMoves;
    public long[] totalTime;
    
    /**
     * Number of the round the game ended.
     */
    public int round;

    public GameResult(Config config, Game game, int[] totalMoves, long[] totalTime) {
        this.config = config;
        this.totalMoves = totalMoves;
        this.totalTime = totalTime;

        regions = new int[config.numPlayers() + 1];
        armies = new int[config.numPlayers() + 1];

        for (int p = 1 ; p <= config.numPlayers() ; ++p) {
            regions[p] = game.numberRegionsOwned(p);
            armies[p] = game.numberArmiesOwned(p);
        }

        winner = game.winningPlayer();
        round = game.getRoundNumber();
    }

    public int getWinner() {
        return winner;
    }
    
    public String getWinnerName() {
        return winner > 0 ? config.playerName(winner) : "NONE";
    }
    
    public String getLoserName() {
        if (config.numPlayers() == 2 && winner > 0)
            return config.playerName(3 - winner);
        else
            return "NONE";
    }
    
    public int getWinnerRegions() {
        return regions[winner];
    }
    
    public int getWinnerArmies() {
        return armies[winner];
    }

    public String asString() {
        return getWinner() + ";" + regions[1] + ";" + armies[1] + ";" +
               regions[2] + ";" + armies[2] + ";" + round;
    }
    
    public static String getCSVHeader() {
        return "winnerName;loserName;winner;rounds;player1Regions;player1Armies;" +
               "player2Regions;player2Armies";
    }
    
    public String getCSV() {
        return getWinnerName() + ";" + getLoserName() + ";" +
            winner + ";" + round + ";" + regions[1] + ";" + armies[1] + ";" +
            regions[2] + ";" + armies[2];
    }
    
}
