package agents;

import java.util.*;

import engine.Agent;
import game.*;
import game.move.*;

public class Julius implements Agent
{
    Random random = new Random(0);
    
    @Override
    public void init(long timeoutMillis) {
    }
    
    @Override
    public Region chooseRegion(Game game) {
        ArrayList<Region> choosable = game.getPickableRegions();
        return choosable.get(random.nextInt(choosable.size()));
    }

    boolean isBorder(Game game, Region r) {
        int me = game.currentPlayer();
        for (Region s : r.getNeighbors())
            if (game.getOwner(s) != me)
                return true;

        return false;
    }

    @Override
    public List<PlaceArmiesMove> placeArmies(Game game) {
        int me = game.currentPlayer();
        int available = game.armiesPerTurn(me);

        List<Region> mine = game.regionsOwnedBy(me);

        Continent c = mine.get(0).getContinent();
        for (Region r : mine) {
            Continent c1 = r.getContinent();
            if (game.getOwner(c1) != me &&
               (game.getOwner(c) == me || c1.getReward() < c.getReward()))
                c = c1;
        }

        ArrayList<Region> dest = new ArrayList<Region>();
        for (Region r : mine)
            if (r.getContinent() == c && isBorder(game, r))
                dest.add(r);
        if (dest.isEmpty())
            dest = new ArrayList<Region>(c.getRegions());
        
        int[] count = new int[dest.size() + 1];
        count[0] = 0;
        count[1] = available;
        for (int i = 2 ; i < count.length ; ++i)
            count[i] = random.nextInt(available + 1);
        Arrays.sort(count);
        
        List<PlaceArmiesMove> ret = new ArrayList<PlaceArmiesMove>();
        int i = 0;
        for (Region r : dest) {
            int n = count[i + 1] - count[i];
            if (n > 0)
                ret.add(new PlaceArmiesMove(r, n));
            i += 1;
        }
        
        return ret;
    }
    
    @Override
    public List<AttackTransferMove> moveArmies(Game game) {
        int me = game.currentPlayer();
        List<AttackTransferMove> ret = new ArrayList<AttackTransferMove>();
        
        for (Region from : game.regionsOwnedBy(me)) {
            ArrayList<Region> neighbors = new ArrayList<Region>(from.getNeighbors());
            Collections.shuffle(neighbors, random);
            Region to = neighbors.get(0);
            int i = 1;
            while (game.getOwner(to) == me && i < neighbors.size()) {
                to = neighbors.get(i);
                i += 1;
            }

            int min = game.getOwner(to) == me ? 1 : (int) (game.getArmies(to) * 1.5);
            int max = game.getArmies(from) - 1;

            if (min <= max)
                ret.add(new AttackTransferMove(from, to, min + random.nextInt(max - min + 1)));
        }
        return ret;        
    }
}
