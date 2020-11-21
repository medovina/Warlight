package game;

import java.util.*;

import game.move.*;
import view.GUI;

public class Game implements Cloneable {
    public GameConfig config;
    World world;
    GameMap map;
    int[] armies;
    int[] owner;
    int round;
    int turn;
    Phase phase;
    public ArrayList<Region> pickableRegions;
    public Random random;
    GUI gui;
    
    Game() {
    }
    
    public Game(GameConfig config) {
        this.config = config != null ? config : new GameConfig();
        world = new World();
        map = makeInitMap();
        turn = 1;
        random = (config == null || config.seed < 0) ? new Random() : new Random(config.seed);

        initStartingRegions();
    }
    
    public void setGUI(GUI gui) {
        this.gui = gui;
    }
    
    @Override
    public Game clone() {
        Game s = new Game();
        s.config = config;
        s.map = map.clone();
        s.round = round;
        s.turn = turn;
        s.phase = phase;

        ArrayList<Region> newPickable = new ArrayList<Region>();
        for (Region r : pickableRegions)
            newPickable.add(s.map.getRegion(r.getId()));
        s.pickableRegions = newPickable;

        // If you make several clones, each will have a distinct random number sequence.
        s.random = new Random(random.nextInt());

        return s;
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
                    sb.append(r.getMapRegion().getName() + "=" + getArmies(r) + " ");
            }
        sb.append("]");
        return sb.toString();
    }

    public World getWorld() { return world; }
    
    public List<MapContinent> allMapContinents() { return world.getContinents(); }

    public int numRegions() { return world.numRegions(); }

    public List<MapRegion> allMapRegions() { return world.getRegions(); }

    public MapRegion getMapRegion(int id) { return world.getMapRegion(id); }

    public GameMap getMap() { return map; }

    public int getArmies(MapRegion region) {
        return armies[region.id];
    }

    public int getArmies(Region region) {
        return armies[region.getId()];
    }

    void setArmies(Region region, int n) {
        armies[region.getId()] = n;
    }

    public int getOwner(Region region) {
        return owner[region.getId()];
    }

    public boolean isOwnedBy(Region region, int player) {
        return owner[region.getId()] == player;
    }

    void setOwner(Region region, int player) {
        owner[region.getId()] = player;
    }

    public int getOwner(Continent continent) {
        int player = getOwner(continent.getRegions().get(0));
        for(Region region : continent.getRegions())
            if (player != getOwner(region))
                return 0;
                
        return player;
    }

    public int getRoundNumber() {
        return round;
    }

    public int currentPlayer() {
        return turn;
    }

    public int opponent() {
        return 3 - turn;
    }
    
    public Phase getPhase() {
        return phase;
    }

    public int winningPlayer() {
        if (round == 0) return 0;
        
        int regions1 = numberRegionsOwned(1), regions2 = numberRegionsOwned(2);
        if (regions1 == 0) return 2;
        if (regions2 == 0) return 1;
        
        if (round > config.maxGameRounds) {
            if (regions1 > regions2) return 1;
            if (regions2 > regions1) return 2;
            
            int armies1 = numberArmiesOwned(1), armies2 = numberArmiesOwned(2);
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
        return ownedRegionsByPlayer(player);
    }

    public int numberArmiesOwned(int player) {
        int n = 0;
        
        for (Region r: map.getRegions())
            if (getOwner(r) == player)
                n += getArmies(r);
        
        return n;
    }

    public int numberRegionsOwned(int player) {
        int n = 0;
        
        for (Region r: map.getRegions())
            if (getOwner(r) == player)
                n += 1;
        
        return n;
    }

    public ArrayList<Region> ownedRegionsByPlayer(int player)
    {
        ArrayList<Region> ownedRegions = new ArrayList<Region>();
        
        for(Region region : map.getRegions())
            if(getOwner(region) == player)
                ownedRegions.add(region);

        return ownedRegions;
    }
        
    public ArrayList<Region> getPickableRegions() {
        return pickableRegions;
    }

    public int armiesPerTurn(int player, boolean first)
    {
        int armies = 5;
        if (first)
            armies /= 2;
        
        for(Continent cd : map.getContinents())
            if (getOwner(cd) == player)
                armies += cd.getArmiesReward();
        
        return armies;
    }
    
    public int armiesPerTurn(int player) {
        return armiesPerTurn(player, player == 1 && round <= 1);
    }

    public int armiesEachTurn(int player) {
        return armiesPerTurn(player, false);
    }

    public GameMap makeInitMap()
    {
        GameMap map = new GameMap();
        
        for (MapContinent mapContinent : allMapContinents())
            map.add(new Continent(mapContinent));
        
        for (MapRegion mapRegion : world.getRegions())
            map.add(new Region(mapRegion, map.getContinent(mapRegion.mapContinent)));
        
        for (MapRegion mapRegion : world.getRegions()) {
            Region region = map.getRegion(mapRegion);
            for (MapRegion neighbour : mapRegion.getNeighbours()) {
                region.addNeighbor(map.getRegion(neighbour));
            }
        }

        armies = new int[world.numRegions()];
        owner = new int[world.numRegions()];

        for (int i = 0 ; i < world.numRegions() ; ++i)
            armies[i] = 2;
        
        return map;
    }

    public int numStartingRegions() {
        return config.warlords ? 3 : 4;
    }

    public Region getRandomStartingRegion(int forPlayer) {
        while (true) {
            Region r = pickableRegions.get(random.nextInt(pickableRegions.size()));
            boolean ok = true;
            for (Region n : r.getNeighbors())
                if (getOwner(n) != 0 && getOwner(n) != forPlayer) {
                    ok = false;
                    break;
                }
            if (ok)
                return r;
        }
    }

    void setAsStarting(Region r, int player) {
        setOwner(r, player);
        pickableRegions.remove(r);
    }
    
    void initStartingRegions() {
        pickableRegions = new ArrayList<Region>();
        
        if (config.warlords)
            for(MapContinent continent : world.getContinents()) {
                int numRegions = continent.getRegions().size();
                while (true) {
                    int randomRegionId = random.nextInt(numRegions);
                    Region region = region(continent.getRegions().get(randomRegionId));
                    boolean ok = true;
                    for (Region n : region.getNeighbors())
                        if (pickableRegions.contains(n)) {
                            ok = false;
                            break;
                        }
                    if (ok) {
                        pickableRegions.add(region);
                        break;
                    }
                }
            }
        else
            pickableRegions = new ArrayList<Region>(map.regions);

        if (config.manualDistribution)
            phase = Phase.STARTING_REGIONS;
        else {  // automatic distribution
            for (int i = 0 ; i < numStartingRegions() ; ++i)
                for (int player = 1; player <= 2; ++player) {
                    Region r = getRandomStartingRegion(player);
                    setAsStarting(r, player);
                }
            phase = Phase.PLACE_ARMIES;
            round = 1;
        }
    }
    
    public void chooseRegion(Region region) {
        if (phase != Phase.STARTING_REGIONS)
            throw new Error("cannot choose regions after game has begun");
        
        if (!pickableRegions.contains(region))
            throw new Error("starting region is not pickable");
        
        setAsStarting(region, turn);
        turn = 3 - turn;
        
        if (numberRegionsOwned(turn) == numStartingRegions()) {
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
            
            if (!isOwnedBy(region, turn))
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
                setArmies(region, getArmies(region) + armies);
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
        int defendingArmies = getArmies(toRegion);
        
        if (getArmies(fromRegion) <= 1) {
            move.setIllegalMove(fromRegion.getId() + " attack " + "only has 1 army");
            return;
        }
        
        if(getArmies(fromRegion)-1 >= move.getArmies()) //are there enough armies on fromRegion?
            attackingArmies = move.getArmies();
        else
            attackingArmies = getArmies(fromRegion)-1;
        
        FightResult result = doAttack(attackingArmies, defendingArmies);
        
        switch (result.winner) {
        case ATTACKER: //attack success
            setArmies(fromRegion, getArmies(fromRegion) - attackingArmies);
            setOwner(toRegion, turn);
            setArmies(toRegion, attackingArmies - result.attackersDestroyed);
            break; 
        case DEFENDER: //attack fail
            setArmies(fromRegion, getArmies(fromRegion) - result.attackersDestroyed);
            setArmies(toRegion, getArmies(toRegion) - result.defendersDestroyed);
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
        int[] totalFrom = new int[numRegions() + 1];
        
        for (int i = 0 ; i < moves.size() ; ++i) {
            AttackTransferMove move = moves.get(i);
            Region fromRegion = region(move.getFromRegion());
            Region toRegion = region(move.getToRegion());

            if (!isOwnedBy(fromRegion, turn))
                move.setIllegalMove(fromRegion.getId() + " attack/transfer not owned");
            else if (!fromRegion.isNeighbor(toRegion))
                move.setIllegalMove(toRegion.getId() + " attack/transfer not a neighbor");
            else if (move.getArmies() < 1)
                move.setIllegalMove("attack/transfer cannot use less than 1 army");
            else if (totalFrom[fromRegion.getId()] + move.getArmies() >= getArmies(fromRegion))
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
            
            move.setArmies(Math.min(move.getArmies(), getArmies(fromRegion) - 1));

            if(isOwnedBy(toRegion, turn)) //transfer
            {
                if (gui != null) {
                    gui.transfer(move);
                }
                setArmies(fromRegion, getArmies(fromRegion) - move.getArmies());
                setArmies(toRegion, getArmies(toRegion) + move.getArmies());
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
