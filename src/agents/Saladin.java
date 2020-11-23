package agents;

import java.util.*;

import engine.AgentBase;
import game.*;
import game.move.*;

public class Saladin extends AgentBase
{
    Random random = new Random(0);
    int[] distFromTarget;
    
    @Override
    public void init(long timeoutMillis) {
    }
    
    @Override
    public Region chooseRegion(Game game) {
        ArrayList<Region> choosable = game.getPickableRegions();
        return choosable.get(random.nextInt(choosable.size()));
    }

    boolean isEnemyBorder(Game game, Region r) {
        int me = game.currentPlayer();
        for (Region s : r.getNeighbors())
            if (game.getOwner(s) != me && game.getOwner(s) != 0)
                return true;

        return false;
    }

    int[] distFrom(Game game, List<Region> from) {
        int[] dist = new int[game.numRegions()];
        for (int i = 0 ; i < dist.length ; ++i)
            dist[i] = -1;

        ArrayDeque<Region> queue = new ArrayDeque<Region>();
            for (Region r : from) {
                dist[r.id] = 0;
                queue.add(r);
            }

        while (!queue.isEmpty()) {
            Region r = queue.remove();
            for (Region s : r.getNeighbors())
                if (dist[s.id] == -1) {
                    dist[s.id] = dist[r.id] + 1;
                    queue.add(s);
                }
        }

        return dist;
    }

    boolean isTarget(Game game, Region region, Continent goal) {
        int me = game.currentPlayer();
        int owner = game.getOwner(region);

        if (region.getContinent() == goal && owner != me)
            return true;
        
        if (owner != me && owner != 0) {
            for (Region s : region.getNeighbors())
                if (game.getOwner(s) == me) {
                    Continent c = s.getContinent();
                    if (c == goal || game.getOwner(c) == me)
                        return true;
                }
        }

        return false;
    }

    @Override
    public List<PlaceArmies> placeArmies(Game game) {
        int me = game.currentPlayer();
        int available = game.armiesPerTurn(me);

        ArrayList<Region> mine = game.regionsOwnedBy(me);
        int[] distFromMe = distFrom(game, mine);

        Continent goal = null;
        int bestScore = Integer.MAX_VALUE;
        for (Continent c : game.getContinents()) {
            if (game.getOwner(c) == me)
                continue;

            int minDist = Integer.MAX_VALUE;
            int missing = 0;
            for (Region r : c.getRegions()) {
                minDist = Math.min(minDist, distFromMe[r.id]);
                int owner = game.getOwner(r);
                if (owner == 0)
                    missing += 1;
                else if (owner != me)
                    missing += 2;
            }
            int score = missing + minDist;
            if (score < bestScore) {
                goal = c;
                bestScore = score;
            }
        }

        ArrayList<Region> targets = new ArrayList<Region>();
        for (Region r : game.getRegions())
            if (isTarget(game, r, goal))
                targets.add(r);
        distFromTarget = distFrom(game, targets);

        int minDistFromTarget = Integer.MAX_VALUE;
        for (Region r : mine)
            if (distFromTarget[r.id] < minDistFromTarget)
                minDistFromTarget = distFromTarget[r.id];

        ArrayList<Region> dest = new ArrayList<Region>();
        for (Region r : mine) {
            if (isEnemyBorder(game, r) && distFromTarget[r.id] == minDistFromTarget)
                dest.add(r);
        }

        if (dest.isEmpty())
            for (Region r : mine) {
                if (distFromTarget[r.id] == minDistFromTarget)
                    dest.add(r);
            }
        if (dest.isEmpty())
            dest = mine;
        
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
    
    @Override
    public List<AttackTransfer> attackTransfer(Game game) {
        int me = game.currentPlayer();
        List<AttackTransfer> ret = new ArrayList<AttackTransfer>();
        
        for (Region from : game.regionsOwnedBy(me)) {
            ArrayList<Region> neighbors = new ArrayList<Region>(from.getNeighbors());
            Collections.shuffle(neighbors, random);
            Region to = null;
            for (Region n : neighbors) {
                if (to == null || distFromTarget[n.id] < distFromTarget[to.id])
                    to = n;
            }

            int min = game.getOwner(to) == me ? 1 : (int) Math.ceil(game.getArmies(to) * 1.6);
            int max = game.getArmies(from) - 1;

            if (min <= max)
                ret.add(new AttackTransfer(from, to, min + random.nextInt(max - min + 1)));
        }
        return ret;        
    }
}
