import java.util.*;

import bot.Bot;
import game.*;
import game.move.*;
import game.world.MapRegion;

public class MyBot implements Bot
{
    Random rand = new Random();
    
    // Code your bot here.
    
    //
    // This is a dummy implemementation that moves randomly.
    //

    @Override
    public void init(long timeoutMillis) {
    }
    
    // Choose a starting region.
    
    @Override
    public MapRegion chooseRegion(GameState state) {
        ArrayList<Region> choosable = state.getPickableRegions();
        return choosable.get(rand.nextInt(choosable.size())).getWorldRegion();
    }

    // Decide where to place armies this turn.
    // state.armiesPerTurn(state.me()) is the number of armies available to place.
    
    @Override
    public List<PlaceArmiesMove> placeArmies(GameState state) {
        int me = state.me();
        List<Region> mine = state.regionsOwnedBy(me);
        int numRegions = mine.size();
        
        int[] count = new int[numRegions];
        for (int i = 0 ; i < state.armiesPerTurn(me) ; ++i) {
            int r = rand.nextInt(numRegions);
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
    public List<AttackTransferMove> moveArmies(GameState state) {
        List<AttackTransferMove> ret = new ArrayList<AttackTransferMove>();
        
        for (Region rd : state.regionsOwnedBy(state.me())) {
            int count = rand.nextInt(rd.getArmies());
            if (count > 0) {
                List<Region> neighbors = rd.getNeighbors();
                Region to = neighbors.get(rand.nextInt(neighbors.size()));
                ret.add(new AttackTransferMove(rd, to, count));
            }
        }
        return ret;        
    }
}
