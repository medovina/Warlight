package bots;

import java.util.*;

import bot.Bot;
import bots.map.RegionBFS;
import bots.map.RegionBFS.*;
import game.*;
import game.move.*;
import game.world.MapRegion;

public class AggressiveBot implements Bot 
{
    GameState state;

    @Override
    public void init(long timeoutMillis) {
    }
    
    // ================
    // CHOOSING REGIONS
    // ================
    
    @Override
    public MapRegion chooseRegion(GameState state) {
        ArrayList<Region> choosable = state.getPickableRegions();
        
        int min = Integer.MAX_VALUE;
        Region best = null;
        
        for (Region r : choosable) {
            int p = getPreferredContinentPriority(r.getContinent());
            if (p < min) {
                min = p;
                best = r;
            }
        }
        
        return best.getWorldRegion();
    }
    
    public int getPreferredContinentPriority(Continent continent) {
        switch (continent.getWorldContinent()) {
        case Australia:     return 1;
        case South_America: return 2;
        case North_America: return 3;
        case Europe:        return 4;        
        case Africa:        return 5;
        case Asia:          return 6;
        default:            return 7;
        }
    }

    // ==============
    // PLACING ARMIES
    // ==============
    
    @Override
    public List<PlaceArmiesMove> placeArmies(GameState state) {
        this.state = state;

        int me = state.me();
        List<PlaceArmiesMove> result = new ArrayList<PlaceArmiesMove>();
        
        // CLONE REGIONS OWNED BY ME
        List<Region> mine = state.regionsOwnedBy(me);
        
        // SORT THEM IN DECREASING ORDER BY SCORE
        Collections.sort(mine, new Comparator<Region>() {

            @Override
            public int compare(Region o1, Region o2) {
                int regionScore1 = getRegionScore(o1);
                int regionScore2 = getRegionScore(o2);
                return regionScore2 - regionScore1;
            }

        });
        
        // DO NOT ADD SOLDIER TO REGIONS THAT HAVE SCORE 0 (not perspective)
        int i = 0;
        while (i < mine.size() && getRegionScore(mine.get(i)) > 0) ++i;
        while (i < mine.size()) mine.remove(i);

        // DISTRIBUTE ARMIES
        int armiesLeft = state.armiesPerTurn(me);
        
        int index = 0;
        
        while (armiesLeft > 0) {
            int count = Math.min(3, armiesLeft);
            result.add(new PlaceArmiesMove(mine.get(index), count));
            armiesLeft -= count;
            ++index;
            if (index >= mine.size()) index = 0;
        }
        
        return result;
    }
    
    private int getRegionScore(Region o1) {
        int result = 0;
        
        for (Region reg : o1.getNeighbors()) {
            result += (reg.isOwnedBy(0) ? 1 : 0) * 5;
            result += (reg.isOwnedBy(state.opp()) ? 1 : 0) * 2;
        }
        
        return result;
    }

    // =============
    // MOVING ARMIES
    // =============

    @Override
    public List<AttackTransferMove> moveArmies(GameState state) {
        this.state = state;
        
        int me = state.me();
        List<AttackTransferMove> result = new ArrayList<AttackTransferMove>();
        Collection<Region> regions = state.regionsOwnedBy(me);
        
        // CAPTURE ALL REGIONS WE CAN
        for (Region from : regions) {
            int available = from.getArmies() - 1;  // 1 army must stay behind
            
            for (Region to : from.getNeighbors()) {
                // DO NOT ATTACK OWN REGIONS
                if (to.isOwnedBy(me)) continue;
                
                // IF YOU HAVE ENOUGH ARMY TO WIN WITH 70%
                int need = getRequiredSoldiersToConquerRegion(from, to, 0.7);
                
                if (available >= need) {
                    // => ATTACK
                    result.add(new AttackTransferMove(from, to, need));
                    available -= need;
                }
            }
        }
        
        // MOVE LEFT OVERS CLOSER TO THE FRONT
        for (Region from : regions) {
            if (hasOnlyMyNeighbours(from) && from.getArmies() > 1) {
                result.add(moveToFront(from));
            }
        }
        
        return result;
    }
    
    private boolean hasOnlyMyNeighbours(Region from) {
        for (Region region : from.getNeighbors()) {            
            if (!region.isOwnedBy(state.me())) return false;
        }
        return true;
    }

    private int getRequiredSoldiersToConquerRegion(Region from, Region to, double winProbability) {
        int req = (int) Math.round(to.getArmies() * 1.25 + 1);
        return req <= from.getArmies() - 1 ? req : Integer.MAX_VALUE;
    }
        
    private AttackTransferMove transfer(Region from, Region to) {
        AttackTransferMove result = new AttackTransferMove(from, to, from.getArmies()-1);
        return result;
    }
    
    private MapRegion moveToFrontRegion;
    
    private AttackTransferMove moveToFront(Region from) {
        RegionBFS<BFSNode> bfs = new RegionBFS<BFSNode>();
        moveToFrontRegion = null;
        bfs.run(from.getWorldRegion(), new BFSVisitor<BFSNode>() {

            @Override
            public BFSVisitResult<BFSNode> visit(MapRegion region, int level, BFSNode parent, BFSNode thisNode) {
                if (!hasOnlyMyNeighbours(state.region(region))) {
                    moveToFrontRegion = region;
                    return new BFSVisitResult<BFSNode>(BFSVisitResultType.TERMINATE, thisNode == null ? new BFSNode() : thisNode);
                }
                return new BFSVisitResult<BFSNode>(thisNode == null ? new BFSNode() : thisNode);
            }
            
        });
        
        if (moveToFrontRegion != null) {
            //List<Region> path = fw.getPath(from.getRegion(), moveToFrontRegion);
            List<MapRegion> path = bfs.getAllPaths(moveToFrontRegion).get(0);
            MapRegion moveTo = path.get(1);
            
            return transfer(from, state.region(moveTo));
        }
        
        return null;
    }
        
}
