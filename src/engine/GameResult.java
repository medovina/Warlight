package engine;

import game.Game;

public class GameResult {
    public Config config;
    
    public int[] regions;
    public int[] armies;
    
    public int winner;
    public int[] score;

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
        score = game.getScoreArray();
        round = game.getRoundNumber();
    }

    public static String getCSVHeader(int numPlayers) {
        StringBuilder sb = new StringBuilder();
        for (int p = 1 ; p <= numPlayers ; ++p)
            sb.append(String.format("p%dScore;", p));

        sb.append("rounds");
        for (int p = 1 ; p <= numPlayers ; ++p)
            sb.append(String.format(";p%dRegions;p%dArmies", p, p));

        return sb.toString();
    }
    
    public String getCSV() {
        StringBuilder sb = new StringBuilder();
        for (int p = 1 ; p <= config.numPlayers() ; ++p)
            sb.append(score[p] + ";");
        sb.append(round);
        for (int p = 1 ; p <= config.numPlayers() ; ++p)
            sb.append(";" + regions[p] + ";" + armies[p]);
        return sb.toString();
    }
    
}
