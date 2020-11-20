package game;

import java.util.*;

import game.move.*;
import game.world.*;
import view.GUI;

public class GameState implements Cloneable {
    public GameConfig config;
    GameMap map;
    int round;
    int turn;
    Phase phase;
    public ArrayList<Region> pickableRegions;
    public Random random;
    GUI gui;
    
    public GameState(GameConfig config, GameMap map,
                     int round, int turn, Phase phase, ArrayList<Region> pickableRegions,
                     Random random) {
        this.config = config;
        this.map = map;
        this.round = round;
        this.turn = turn;
        this.phase = phase;
        this.pickableRegions = pickableRegions;
        this.random = random;
    }
    
    public GameState(GameConfig config, ArrayList<Region> pickableRegions) {
        this(
            config != null ? config : new GameConfig(),
            makeInitMap(),
            0, 1, Phase.STARTING_REGIONS, pickableRegions,
            (config == null || config.seed < 0) ? new Random() : new Random(config.seed));

        if (pickableRegions == null)
            initStartingRegions();
    }
    
    public GameState(GameConfig config) {
        this(config, null);
    }
    
    public void setGUI(GUI gui) {
        this.gui = gui;
    }
    
    @Override
    public GameState clone() {
        GameMap newMap = map.clone();
        ArrayList<Region> newPickable = new ArrayList<Region>();
        for (Region r : pickableRegions)
            newPickable.add(newMap.getRegion(r.getId()));
        
        // If you make several clones, each will have a distinct random number sequence.
        return new GameState(config, newMap, round, turn, phase, newPickable,
                             new Random(random.nextInt()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[GameState ");
        if (isDone())
            sb.append("p" + winningPlayer() + " victory in " + round + " rounds");
        else
            for (int player = 1 ; player <= 2 ; ++player) {
                sb.append("p" + player + ": ");
                for (Region r : regionsOwnedBy(player))
                    sb.append(r.getWorldRegion().getName() + "=" + r.getArmies() + " ");
            }
        sb.append("]");
        return sb.toString();
    }
    
    public GameMap getMap() { return map; }

    public int getRoundNumber() {
        return round;
    }

    public void setRoundNumber(int round) {
        this.round = round;
    }
    
    public int me() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int opp() {
        return 3 - turn;
    }
    
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    
    public int winningPlayer() {
        if (round == 0) return 0;
        
        int regions1 = map.numberRegionsOwned(1), regions2 = map.numberRegionsOwned(2);
        if (regions1 == 0) return 2;
        if (regions2 == 0) return 1;
        
        if (round > config.maxGameRounds) {
            if (regions1 > regions2) return 1;
            if (regions2 > regions1) return 2;
            
            int armies1 = map.numberArmiesOwned(1), armies2 = map.numberArmiesOwned(2);
            if (armies1 > armies2) return 1;
            if (armies2 > armies1) return 2;
        }
        
        return 0;
    }
    
    public boolean isDone() {
        return round > 0 && (round > config.maxGameRounds || winningPlayer() > 0);
    }

    public Region getRegion(int id) {
        return map.getRegion(id);
    }

    public Region region(MapRegion region) {
        return map.getRegion(region.id);
    }

    public ArrayList<Region> regionsOwnedBy(int player) {
        return map.ownedRegionsByPlayer(player);
    }
        
    public ArrayList<Region> getPickableRegions() {
        return pickableRegions;
    }

    public void setPickableRegions(ArrayList<Region> regions) {
        this.pickableRegions = regions;
    }
    
    //calculate how many armies a player is able to place on the map each round
    public int armiesPerTurn(int player, boolean first)
    {
        int armies = config.startingArmies;
        if (first)
            armies /= 2;
        
        for(Continent cd : map.getContinents())
            if (cd.getOwner() == player)
                armies += cd.getArmiesReward();
        
        return armies;
    }
    
    public int armiesPerTurn(int player) {
        return armiesPerTurn(player, player == 1 && round <= 1);
    }

    public int armiesEachTurn(int player) {
        return armiesPerTurn(player, false);
    }

    public static GameMap makeInitMap()
    {
        GameMap map = new GameMap();
        
        Map<MapContinent, Continent> continents = new TreeMap<MapContinent, Continent>(new Comparator<MapContinent>() {
            @Override
            public int compare(MapContinent o1, MapContinent o2) {
                return o1.id - o2.id;
            }           
        });
        
        for (MapContinent worldContinent : MapContinent.values()) {
            Continent continent = new Continent(worldContinent, 0);
            continents.put(worldContinent, continent);
        }
        
        Map<MapRegion, Region> regions = new TreeMap<MapRegion, Region>(new Comparator<MapRegion>() {
            @Override
            public int compare(MapRegion o1, MapRegion o2) {
                return o1.id - o2.id;
            }
        });
        
        for (MapRegion worldRegion : MapRegion.values()) {
            Region region = new Region(worldRegion, continents.get(worldRegion.worldContinent));
            regions.put(worldRegion, region);
        }
        
        for (MapRegion regionName : MapRegion.values()) {
            Region region = regions.get(regionName);
            for (MapRegion neighbour : regionName.getForwardNeighbours()) {
                region.addNeighbor(regions.get(neighbour));
            }
        }
        
        for (Region region : regions.values()) {
            map.add(region);
        }
        
        for (Continent continent : continents.values()) {
            map.add(continent);
        }

        // Make every region neutral with 2 armies to start with
        for(Region region : map.regions)
        {
            region.setOwner(0);
            region.setArmies(2);
        }

        return map;
    }

    public int numStartingRegions() {
        return config.warlords ? 3 : 4;
    }
    
    void initStartingRegions() {
        pickableRegions = new ArrayList<Region>();
        
        if (config.warlords)
            for(MapContinent continent : MapContinent.values())
            {
                int nrOfRegions = continent.getRegions().size();
                //get one random subregion from continent
                int randomRegionId = random.nextInt(nrOfRegions);
                
                Region randomRegion = region(continent.getRegions().get(randomRegionId));
                pickableRegions.add(randomRegion);
            }
        else
            pickableRegions = new ArrayList<Region>(map.regions);
    }
    
    public void chooseRegion(Region region) {
        if (phase != Phase.STARTING_REGIONS)
            throw new Error("cannot choose regions after game has begun");
        
        if (!pickableRegions.contains(region))
            throw new Error("starting region is not pickable");
        
        region.setOwner(turn);
        pickableRegions.remove(region);
        turn = 3 - turn;
        
        if (map.numberRegionsOwned(turn) == numStartingRegions()) {
            round = 1;
            phase = Phase.PLACE_ARMIES;
        }
    }
    
    public void placeArmies(List<PlaceArmiesMove> moves)
    {
        if (phase != Phase.PLACE_ARMIES)
            throw new Error("wrong time to place armies");

        int left = armiesPerTurn(turn); 
                
        for(PlaceArmiesMove move : moves)
        {
            Region region = region(move.getRegion());
            int armies = move.getArmies();
            
            if (!region.isOwnedBy(turn))
                move.setIllegalMove(region.getId() + " not owned");
            else if (armies < 1)
                move.setIllegalMove("cannot place less than 1 army");
            else if (left <= 0)
                move.setIllegalMove("no armies left to place");
            else {
                if(armies > left) { //player wants to place more armies than he has left
                    move.setArmies(left); //place all armies he has left
                    armies = left;
                }
                
                left -= armies;
                region.setArmies(region.getArmies() + armies);
            }
        }
        
        phase = Phase.ATTACK_TRANSFER;
    }
    
    public static enum FightSide {
        ATTACKER,
        DEFENDER
    }
    
    public static class FightResult {
        public FightSide winner;
        public int attackersDestroyed;
        public int defendersDestroyed;
        
        protected void postProcess(int attackingArmies, int defendingArmies) {
            if (attackersDestroyed == attackingArmies && defendersDestroyed == defendingArmies)
                defendersDestroyed -= 1;
            
            winner = defendersDestroyed >= defendingArmies ? FightSide.ATTACKER :
                                                             FightSide.DEFENDER;
        }
    }

    int prob_round(double d) {
        double p = d - Math.floor(d);
        return (int) (random.nextDouble() < p ? Math.ceil(d) : Math.floor(d));
    }

    FightResult doAttack(int attackingArmies, int defendingArmies) {
        FightResult result = new FightResult();

        result.defendersDestroyed = Math.min(prob_round(attackingArmies * 0.6), defendingArmies);
        result.attackersDestroyed = Math.min(prob_round(defendingArmies * 0.7), attackingArmies);
        
        result.postProcess(attackingArmies, defendingArmies);
        return result;
    }

    private void doAttack(AttackTransferMove move)
    {
        Region fromRegion = region(move.getFromRegion());
        Region toRegion = region(move.getToRegion());
        int attackingArmies;
        int defendingArmies = toRegion.getArmies();
        
        if (fromRegion.getArmies() <= 1) {
            move.setIllegalMove(fromRegion.getId() + " attack " + "only has 1 army");
            return;
        }
        
        if(fromRegion.getArmies()-1 >= move.getArmies()) //are there enough armies on fromRegion?
            attackingArmies = move.getArmies();
        else
            attackingArmies = fromRegion.getArmies()-1;
        
        FightResult result = doAttack(attackingArmies, defendingArmies);
        
        switch (result.winner) {
        case ATTACKER: //attack success
            fromRegion.setArmies(fromRegion.getArmies() - attackingArmies);
            toRegion.setOwner(turn);
            toRegion.setArmies(attackingArmies - result.attackersDestroyed);
            break; 
        case DEFENDER: //attack fail
            fromRegion.setArmies(fromRegion.getArmies() - result.attackersDestroyed);
            toRegion.setArmies(toRegion.getArmies() - result.defendersDestroyed);
            break;
        default:
            throw new RuntimeException("Unhandled FightResult.winner: " + result.winner);
        }
        
        if (gui != null) {
            gui.attackResult(fromRegion, toRegion, result.attackersDestroyed, result.defendersDestroyed);
        }
    }

    void validateAttackTransfers(List<AttackTransferMove> moves)
    {
        int[] totalFrom = new int[MapRegion.NUM_REGIONS + 1];
        
        for (int i = 0 ; i < moves.size() ; ++i) {
            AttackTransferMove move = moves.get(i);
            Region fromRegion = region(move.getFromRegion());
            Region toRegion = region(move.getToRegion());

            if (!fromRegion.isOwnedBy(turn))
                move.setIllegalMove(fromRegion.getId() + " attack/transfer not owned");
            else if (!fromRegion.isNeighbor(toRegion))
                move.setIllegalMove(toRegion.getId() + " attack/transfer not a neighbor");
            else if (move.getArmies() < 1)
                move.setIllegalMove("attack/transfer cannot use less than 1 army");
            else if (totalFrom[fromRegion.getId()] + move.getArmies() >= fromRegion.getArmies())
                move.setIllegalMove(fromRegion.getId() +
                        " attack/transfer has used all available armies");
            else {
                for (int j = 0 ; j < i ; ++j) {
                    AttackTransferMove n = moves.get(j);
                    if (n.getFromRegion() == move.getFromRegion() && n.getToRegion() == move.getToRegion()) {
                        move.setIllegalMove(
                            "player has already attacked/transfered from region " +
                                fromRegion.getId() + " to region " + toRegion.getId() + " in this turn");
                        break;
                    }
                }
                totalFrom[fromRegion.getId()] += move.getArmies();
            }
        }
    }
    
    public void attackTransfer(List<AttackTransferMove> moves) {
        if (phase != Phase.ATTACK_TRANSFER)
            throw new Error("wrong time to attack/transfer");

        validateAttackTransfers(moves);
        
        for (AttackTransferMove move : moves) {
            if(!move.getIllegalMove().equals("")) //the move is illegal
                continue;
            
            Region fromRegion = region(move.getFromRegion());
            Region toRegion = region(move.getToRegion());
            
            move.setArmies(Math.min(move.getArmies(), fromRegion.getArmies() - 1));

            if(toRegion.isOwnedBy(turn)) //transfer
            {
                if (gui != null) {
                    gui.transfer(move);
                }
                fromRegion.setArmies(fromRegion.getArmies() - move.getArmies());
                toRegion.setArmies(toRegion.getArmies() + move.getArmies());
            }
            else //attack
            {
                if (gui != null) {
                    gui.attack(move);
                }
                doAttack(move);
            }
        }
        
        turn = 3 - turn;
        phase = Phase.PLACE_ARMIES;
        if (turn == 1)
            round++;
    }
}
