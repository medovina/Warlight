package agents;

import java.util.*;

import engine.AgentBase;
import game.*;
import game.move.*;

public class Attila extends AgentBase
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

        int num = 0;
        for (Region r : mine)
            if (r.getContinent() == c)
                num += 1;
        
        int[] count = new int[num + 1];
        count[0] = 0;
        count[1] = available;
        for (int i = 2 ; i < count.length ; ++i)
            count[i] = random.nextInt(available + 1);
        Arrays.sort(count);
        
        List<PlaceArmies> ret = new ArrayList<PlaceArmies>();
        int i = 0;
        for (Region r : mine)
            if (r.getContinent() == c) {
                int n = count[i + 1] - count[i];
                if (n > 0)
                    ret.add(new PlaceArmies(r, n));
                i += 1;
            }
        return ret;
    }
    
    @Override
    public List<AttackTransfer> attackTransfer(Game game) {
        int me = game.currentPlayer();
        List<AttackTransfer> ret = new ArrayList<AttackTransfer>();
        
        for (Region from : game.regionsOwnedBy(me)) {
            ArrayList<Region> neighbors = new ArrayList<Region>(from.getNeighbors());
            Collections.shuffle(neighbors, random);
            Region to = neighbors.get(0);
            int i = 1;
            while (game.getOwner(to) == me && i < neighbors.size()) {
                to = neighbors.get(i);
                i += 1;
            }

            int min = game.getOwner(to) == me ? 1 : game.getArmies(to);
            int max = game.getArmies(from) - 1;

            if (min <= max)
                ret.add(new AttackTransfer(from, to, min + random.nextInt(max - min + 1)));
        }
        return ret;        
    }
}
