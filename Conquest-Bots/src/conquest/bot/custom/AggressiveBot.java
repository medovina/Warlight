package conquest.bot.custom;

import java.io.File;
import java.util.*;

import conquest.bot.BotParser;
import conquest.bot.GameBot;
import conquest.bot.fight.FightSimulation.FightAttackersResults;
import conquest.bot.fight.FightSimulation.FightDefendersResults;
import conquest.bot.map.RegionBFS;
import conquest.bot.map.RegionBFS.*;
import conquest.engine.Config;
import conquest.engine.RunGame;
import conquest.game.*;
import conquest.game.move.*;
import conquest.game.world.Continent;
import conquest.game.world.Region;
import conquest.utils.Util;

public class AggressiveBot extends GameBot 
{
    FightAttackersResults aRes;
    FightDefendersResults dRes;
    
    public AggressiveBot() {
        aRes = FightAttackersResults.loadFromFile(Util.findFile("Conquest-Bots/FightSimulation-Attackers-A200-D200.obj"));
        dRes = FightDefendersResults.loadFromFile(Util.findFile("Conquest-Bots/FightSimulation-Defenders-A200-D200.obj"));
        System.err.println("---==[ AGGRESSIVE BOT INITIALIZED ]==---");
    }
    
    // ================
    // CHOOSING REGIONS
    // ================
    
    @Override
    public Region chooseRegion(long timeout) {
        ArrayList<Region> choosable = state.getPickableRegions();
        
        int min = Integer.MAX_VALUE;
        Region best = null;
        
        for (Region r : choosable) {
            int p = getPreferredContinentPriority(r.continent);
            if (p < min) {
                min = p;
                best = r;
            }
        }
        
        return best;
    }
    
    public int getPreferredContinentPriority(Continent continent) {
        switch (continent) {
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
    public List<PlaceArmiesMove> placeArmies(long timeout) {
      int me = state.me();
        List<PlaceArmiesMove> result = new ArrayList<PlaceArmiesMove>();
        
        // CLONE REGIONS OWNED BY ME
        List<RegionData> mine = state.regionsOwnedBy(me);
        
        // SORT THEM IN DECREASING ORDER BY SCORE
        Collections.sort(mine, new Comparator<RegionData>() {

            @Override
            public int compare(RegionData o1, RegionData o2) {
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
            result.add(new PlaceArmiesMove(mine.get(index).getRegion(), count));
            armiesLeft -= count;
            ++index;
            if (index >= mine.size()) index = 0;
        }
        
        return result;
    }
    
    private int getRegionScore(RegionData o1) {
        int result = 0;
        
        for (RegionData reg : o1.getNeighbors()) {
            result += (reg.isOwnedBy(0) ? 1 : 0) * 5;
            result += (reg.isOwnedBy(state.opp()) ? 1 : 0) * 2;
        }
        
        return result;
    }

    // =============
    // MOVING ARMIES
    // =============

    @Override
    public List<AttackTransferMove> moveArmies(long timeout) {
        int me = state.me();
        List<AttackTransferMove> result = new ArrayList<AttackTransferMove>();
        Collection<RegionData> regions = state.regionsOwnedBy(me);
        
        // CAPTURE ALL REGIONS WE CAN
        for (RegionData from : regions) {
            int available = from.getArmies() - 1;  // 1 army must stay behind
            
            for (RegionData to : from.getNeighbors()) {
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
        for (RegionData from : regions) {
            if (hasOnlyMyNeighbours(from) && from.getArmies() > 1) {
                result.add(moveToFront(from));
            }
        }
        
        return result;
    }
    
    private boolean hasOnlyMyNeighbours(RegionData from) {
        for (RegionData region : from.getNeighbors()) {            
            if (!region.isOwnedBy(state.me())) return false;
        }
        return true;
    }

    private int getRequiredSoldiersToConquerRegion(RegionData from, RegionData to, double winProbability) {
        int attackers = from.getArmies() - 1;
        int defenders = to.getArmies();
        
        for (int a = defenders; a <= attackers; ++a) {
            double chance = aRes.getAttackersWinChance(a, defenders);
            if (chance >= winProbability) {
                return a;
            }
        }
        
        return Integer.MAX_VALUE;
    }
        
    private AttackTransferMove transfer(RegionData from, RegionData to) {
        AttackTransferMove result = new AttackTransferMove(from.getRegion(), to.getRegion(), from.getArmies()-1);
        return result;
    }
    
    private Region moveToFrontRegion;
    
    private AttackTransferMove moveToFront(RegionData from) {
        RegionBFS<BFSNode> bfs = new RegionBFS<BFSNode>();
        moveToFrontRegion = null;
        bfs.run(from.getRegion(), new BFSVisitor<BFSNode>() {

            @Override
            public BFSVisitResult<BFSNode> visit(Region region, int level, BFSNode parent, BFSNode thisNode) {
                //System.err.println((parent == null ? "START" : parent.level + ":" + parent.region) + " --> " + level + ":" + region);
                if (!hasOnlyMyNeighbours(state.region(region))) {
                    moveToFrontRegion = region;
                    return new BFSVisitResult<BFSNode>(BFSVisitResultType.TERMINATE, thisNode == null ? new BFSNode() : thisNode);
                }
                return new BFSVisitResult<BFSNode>(thisNode == null ? new BFSNode() : thisNode);
            }
            
        });
        
        if (moveToFrontRegion != null) {
            //List<Region> path = fw.getPath(from.getRegion(), moveToFrontRegion);
            List<Region> path = bfs.getAllPaths(moveToFrontRegion).get(0);
            Region moveTo = path.get(1);
            
            boolean first = true;
            for (Region region : path) {
                if (first) first = false;
                else System.err.print(" --> ");
                System.err.print(region);
            }
            System.err.println();
            
            return transfer(from, state.region(moveTo));
        }
        
        return null;
    }
    
    
    public static void runInternal() {
        Config config = new Config();
        
        config.bot1Init = "internal:conquest.bot.custom.AggressiveBot";
        //config.bot1Init = "dir;process:../Conquest-Bots;java -cp ./bin;../Conquest/bin conquest.bot.external.JavaBot conquest.bot.custom.AggressiveBot ./AggressiveBot.log";
        //config.bot2Init = "internal:conquest.bot.BotStarter";
        config.bot2Init = "human";
        
        config.botCommandTimeoutMillis = 24*60*60*1000;
        //config.botCommandTimeoutMillis = 20 * 1000;
        
        config.game.maxGameRounds = 200;
        
        config.game.fight = FightMode.CONTINUAL_1_1_A60_D70;
        
        config.visualize = true;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
    
    public static void runExternal() {
        BotParser parser = new BotParser(new AggressiveBot());
        parser.setLogFile(new File("./AggressiveBot.log"));
        parser.run();
    }

    public static void main(String[] args)
    {
        //JavaBot.exec(new String[]{"conquest.bot.custom.AggressiveBot", "./AggressiveBot.log"});        
        runInternal();
    }

}
