package agents;

import java.util.*;

import engine.AgentBase;
import game.*;
import game.move.*;

public class Napoleon extends AgentBase
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

    boolean isEnemyBorder(Game game, Region r) {
        int me = game.currentPlayer();
        for (Region s : r.getNeighbors())
            if (game.getOwner(s) != me && game.getOwner(s) != 0)
                return true;

        return false;
    }

    @Override
    public List<PlaceArmies> placeArmies(Game game) {
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
        for (Region r : mine) {
            Continent c1 = r.getContinent();
            if (isEnemyBorder(game, r) && (c1 == c || game.getOwner(c1) == me))
                dest.add(r);
        }
        if (dest.isEmpty())
            for (Region r : mine)
                if (r.getContinent() == c && isBorder(game, r))
                    dest.add(r);
        if (dest.isEmpty())
            dest = new ArrayList<Region>(mine);
        
        int[] count = new int[dest.size() + 1];
        count[0] = 0;
        count[1] = available;
        for (int i = 2 ; i < count.length ; ++i)
            count[i] = random.nextInt(available + 1);
        Arrays.sort(count);
        
        List<PlaceArmies> ret = new ArrayList<PlaceArmies>();
        int i = 0;
        for (Region r : dest) {
            int n = count[i + 1] - count[i];
            if (n > 0)
                ret.add(new PlaceArmies(r, n));
            i += 1;
        }
        
        return ret;
    }
    
    int priority(Game game, Region from, Region to) {
        int me = game.currentPlayer();
        int who = game.getOwner(to);
        if (who == me)
            return 1;
        else if (who == 0)
            return to.getContinent() == from.getContinent() ? 3 : 2;
        else return 4;
    }

    @Override
    public List<AttackTransfer> attackTransfer(Game game) {
        int me = game.currentPlayer();
        List<AttackTransfer> ret = new ArrayList<AttackTransfer>();
        
        for (Region from : game.regionsOwnedBy(me)) {
            ArrayList<Region> neighbors = new ArrayList<Region>(from.getNeighbors());
            Collections.shuffle(neighbors, random);
            Region to = null;
            for (Region n : neighbors) {
                if (to == null || priority(game, from, n) > priority(game, from, to))
                    to = n;
            }

            int min = game.getOwner(to) == me ? 1 : (int) Math.ceil(game.getArmies(to) * 1.5);
            int max = game.getArmies(from) - 1;

            if (min <= max)
                ret.add(new AttackTransfer(from, to, max));
        }
        return ret;        
    }
}
