import java.util.*;

import engine.Bot;
import game.*;
import game.move.*;

public class MyBot implements Bot
{
    Random random = new Random(0);
    
    // Code your bot here.
    
    //
    // This is a dummy implemementation that moves randomly.
    //

    @Override
    public void init(long timeoutMillis) {
    }
    
    // Choose a starting region.
    
    @Override
    public Region chooseRegion(Game game) {
        ArrayList<Region> choosable = game.getPickableRegions();
        return choosable.get(random.nextInt(choosable.size()));
    }

    // Decide where to place armies this turn.
    // game.armiesPerTurn(game.currentPlayer()) is the number of armies available to place.
    
    @Override
    public List<PlaceArmiesMove> placeArmies(Game game) {
        int me = game.currentPlayer();
        List<Region> mine = game.regionsOwnedBy(me);
        int numRegions = mine.size();
        
        int[] count = new int[numRegions];
        for (int i = 0 ; i < game.armiesPerTurn(me) ; ++i) {
            int r = random.nextInt(numRegions);
            count[r]++;
        }
        
        List<PlaceArmiesMove> ret = new ArrayList<PlaceArmiesMove>();
        for (int i = 0 ; i < numRegions ; ++i)
            if (count[i] > 0)
                ret.add(new PlaceArmiesMove(mine.get(i), count[i]));
        return ret;
    }
    
    // Decide where to move armies this turn.
    
    @Override
    public List<AttackTransferMove> moveArmies(Game game) {
        List<AttackTransferMove> ret = new ArrayList<AttackTransferMove>();
        
        for (Region rd : game.regionsOwnedBy(game.currentPlayer())) {
            int count = random.nextInt(game.getArmies(rd));
            if (count > 0) {
                List<Region> neighbors = rd.getNeighbors();
                Region to = neighbors.get(random.nextInt(neighbors.size()));
                ret.add(new AttackTransferMove(rd, to, count));
            }
        }
        return ret;        
    }
}
