package tournament;

public class TotalResults {
    public int numPlayers;
    public int games;
    public int[] totalVictories;
    public int[] totalScore;

    public TotalResults(int numPlayers, int games) {
        this.numPlayers = numPlayers;
        this.games = games;
        totalVictories = new int[numPlayers + 1];
        totalScore = new int[numPlayers + 1];
    }

    public String getCSVHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("games");
        for (int p = 1 ; p <= numPlayers ; ++p) {
            sb.append(String.format(";wonBy%d;winRate%d", p, p));
            if (numPlayers > 2)
                sb.append(";avgScore" + p);
        }
        return sb.toString();
    }

    public String getCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(games);
        for (int p = 1 ; p <= numPlayers ; ++p) {
            sb.append(";" + totalVictories[p]);
            sb.append(String.format(";%.2f", 1.0 * totalVictories[p] / games));
            if (numPlayers > 2)
                sb.append(String.format(";%.2f", 1.0 * totalScore[p] / games));
        }
        return sb.toString();
    }
}
