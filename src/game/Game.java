package game;

import java.util.*;

import game.move.*;
import view.GUI;

public class Game implements Cloneable {
    public GameConfig config;
    World world;
    int[] armies;
    int[] owner;
    int round;
    int turn;
    Phase phase;
    public ArrayList<Region> pickableRegions;
    public Random random;
    GUI gui;
    
    Game() { }
    
    public Game(GameConfig config) {
        this.config = config != null ? config : new GameConfig();
        world = new World();

        armies = new int[world.numRegions()];
        for (int i = 0 ; i < world.numRegions() ; ++i)
            armies[i] = 2;

        owner = new int[world.numRegions()];

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
        s.world = world;
        s.armies = armies.clone();
        s.owner = owner.clone();
        s.round = round;
        s.turn = turn;
        s.phase = phase;
        s.pickableRegions = new ArrayList<Region>(pickableRegions);

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
                    sb.append(r.getName() + "=" + getArmies(r) + " ");
            }
        sb.append("]");
        return sb.toString();
    }

    // world information

    public World getWorld() { return world; }
    
    public List<Continent> getContinents() { return world.getContinents(); }

    public Continent getContinent(int id) { return world.getContinent(id); }

    public int numRegions() { return world.numRegions(); }

    public List<Region> getRegions() { return world.getRegions(); }

    public Region getRegion(int id) { return world.getRegion(id); }

    public boolean isNeighbor(Region r, Region s) {
        return r.getNeighbors().contains(s);
    }
    
    // information about armies/owners

    public int getArmies(Region region) {
        return armies[region.id];
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

    public int numberArmiesOwned(int player) {
        int n = 0;
        
        for (Region r: getRegions())
            if (getOwner(r) == player)
                n += getArmies(r);
        
        return n;
    }

    public int numberRegionsOwned(int player) {
        int n = 0;
        
        for (Region r: getRegions())
            if (getOwner(r) == player)
                n += 1;
        
        return n;
    }

    public ArrayList<Region> regionsOwnedBy(int player)
    {
        ArrayList<Region> ownedRegions = new ArrayList<Region>();
        
        for(Region region : getRegions())
            if(getOwner(region) == player)
                ownedRegions.add(region);

        return ownedRegions;
    }

    // round/turn/phase information

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

       
    public ArrayList<Region> getPickableRegions() {
        return pickableRegions;
    }

    public int armiesPerTurn(int player, boolean first)
    {
        int armies = 5;
        if (first)
            armies /= 2;
        
        for(Continent cd : getContinents())
            if (getOwner(cd) == player)
                armies += cd.getReward();
        
        return armies;
    }
    
    public int armiesPerTurn(int player) {
        return armiesPerTurn(player, player == 1 && round <= 1);
    }

    public int armiesEachTurn(int player) {
        return armiesPerTurn(player, false);
    }

    public int numStartingRegions() {
        return config.warlords ? 3 : 4;
    }

    public Region getRandomStartingRegion(int forPlayer) {
        while (true) {
            Region r = pickableRegions.get(random.nextInt(pickableRegions.size()));

            // Don't allow starting regions to border enemies
            boolean ok = true;
            for (Region n : r.getNeighbors())
                if (getOwner(n) != 0 && getOwner(n) != forPlayer) {
                    ok = false;
                    break;
                }
            if (!ok)
                continue;

            // Each player can have at most two starting regions on any continent.
            int count = 0;
            for (Region s : r.getContinent().getRegions())
                if (getOwner(s) == forPlayer)
                    count += 1;
            if (count < 2)
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
            for(Continent continent : world.getContinents()) {
                int numRegions = continent.getRegions().size();
                while (true) {
                    int randomRegionId = random.nextInt(numRegions);
                    Region region = continent.getRegions().get(randomRegionId);
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
            pickableRegions = new ArrayList<Region>(getRegions());

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
    
    void illegalMove(String s) {
        System.out.printf("warning: ignoring illegal move: %s\n", s);
    }

    public List<PlaceArmiesMove> placeArmies(List<PlaceArmiesMove> moves)
    {
        ArrayList<PlaceArmiesMove> valid = new ArrayList<PlaceArmiesMove>();

        if (phase != Phase.PLACE_ARMIES) {
            illegalMove("wrong time to place armies");
            return valid;
        }

        int left = armiesPerTurn(turn); 
                
        for(PlaceArmiesMove move : moves)
        {
            Region region = move.getRegion();
            int armies = move.getArmies();
            
            if (!isOwnedBy(region, turn))
                illegalMove(region.getName() + " not owned");
            else if (armies < 1)
                illegalMove("cannot place less than 1 army");
            else if (left <= 0)
                illegalMove("no armies left to place");
            else {
                if(armies > left) { //player wants to place more armies than he has left
                    System.out.printf(
                        "warning: move wants to place %d armies, but only %d are available\n",
                        armies, left);
                    move.setArmies(left); //place all armies he has left
                    armies = left;
                }
                
                left -= armies;
                setArmies(region, getArmies(region) + armies);
                valid.add(move);
            }
        }
        
        phase = Phase.ATTACK_TRANSFER;
        return valid;
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
        Region fromRegion = move.getFromRegion();
        Region toRegion = move.getToRegion();
        int attackingArmies = move.getArmies();
        int defendingArmies = getArmies(toRegion);
        
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
            throw new Error("Unhandled FightResult.winner: " + result.winner);
        }
        
        if (gui != null) {
            gui.attackResult(fromRegion, toRegion, result.attackersDestroyed, result.defendersDestroyed);
        }
    }

    List<AttackTransferMove> validateAttackTransfers(List<AttackTransferMove> moves) {
        ArrayList<AttackTransferMove> valid = new ArrayList<AttackTransferMove>();
        int[] totalFrom = new int[numRegions()];
        
        for (int i = 0 ; i < moves.size() ; ++i) {
            AttackTransferMove move = moves.get(i);
            Region fromRegion = move.getFromRegion();
            Region toRegion = move.getToRegion();

            if (!isOwnedBy(fromRegion, turn))
                illegalMove("attack/transfer from unowned region");
            else if (!isNeighbor(fromRegion, toRegion))
                illegalMove("attack/transfer to region that is not a neighbor");
            else if (move.getArmies() < 1)
                illegalMove("attack/transfer cannot use less than 1 army");
            else if (totalFrom[fromRegion.getId()] + move.getArmies() >= getArmies(fromRegion))
                illegalMove("attack/transfer requests more armies than are available");
            else {
                boolean ok = true;
                for (int j = 0 ; j < i ; ++j) {
                    AttackTransferMove n = moves.get(j);
                    if (n.getFromRegion() == move.getFromRegion() &&
                        n.getToRegion() == move.getToRegion()) {
                        illegalMove("player has already moved between same regions in this turn");
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    totalFrom[fromRegion.getId()] += move.getArmies();
                    valid.add(move);
                }
            }
        }

        return valid;
    }
    
    public void attackTransfer(List<AttackTransferMove> moves) {
        if (phase != Phase.ATTACK_TRANSFER) {
            illegalMove("wrong time to attack/transfer");
            return;
        }

        List<AttackTransferMove> valid = validateAttackTransfers(moves);
        
        for (AttackTransferMove move : valid) {
            Region fromRegion = move.getFromRegion();
            Region toRegion = move.getToRegion();
            
            move.setArmies(Math.min(move.getArmies(), getArmies(fromRegion) - 1));

            if(isOwnedBy(toRegion, turn)) { //transfer
                setArmies(fromRegion, getArmies(fromRegion) - move.getArmies());
                setArmies(toRegion, getArmies(toRegion) + move.getArmies());
                if (gui != null) {
                    gui.transfer(move);
                }
            } else { //attack
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
