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
    int[] score;
    public ArrayList<Region> pickableRegions;
    public Random random;
    GUI gui;
    
    Game() { }
    
    public Game(GameConfig config) {
        this.config = config != null ? config : new GameConfig();
        world = new World(config.mapName);

        armies = new int[world.numRegions()];
        for (int i = 0 ; i < world.numRegions() ; ++i)
            armies[i] = 2;

        owner = new int[world.numRegions()];

        score = new int[config.numPlayers + 1];
        for (int p = 1 ; p <= config.numPlayers ; ++p)
            score[p] = -1;

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
        s.score = score.clone();
        s.pickableRegions = new ArrayList<Region>(pickableRegions);

        // If you make several clones, each will have a distinct random number sequence.
        s.random = new Random(random.nextInt());

        return s;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Game ");
        if (isDone())
            sb.append("p" + winningPlayer() + " victory in " + round + " rounds");
        else
            for (int player = 1 ; player <= numPlayers() ; ++player) {
                sb.append("p" + player + ": ");
                for (Region r : regionsOwnedBy(player))
                    sb.append(r.getName() + "=" + getArmies(r) + " ");
            }
        sb.append("]");
        return sb.toString();
    }

    public int numPlayers() { return config.numPlayers; }

    // world information

    public World getWorld() { return world; }

    public int numContinents() { return world.numContinents(); }
    
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

    int maxScore() {
        int max = -1;
        for (int p = 1 ; p <= config.numPlayers ; ++p)
            max = Math.max(max, score[p]);

        return max;
    }

    void setOwner(Region region, int player) {
        int oldOwner = owner[region.getId()];
        owner[region.getId()] = player;

        if (oldOwner > 0 && numberRegionsOwned(oldOwner) == 0) {    // player is eliminated
            int max = maxScore();
            if (score[oldOwner] != -1)
                throw new Error("score should be -1");
            score[oldOwner] = max + 1;

            if (score[oldOwner] == config.numPlayers - 2) { // only one player remains
                for (int p = 1 ; p <= config.numPlayers ; ++p)
                    if (score[p] == -1) {
                        score[p] = config.numPlayers - 1;   // mark as winner
                        break;
                    }
            }
        }
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

    public Phase getPhase() {
        return phase;
    }

    public int winningPlayer() {
        for (int p = 1 ; p <= config.numPlayers ; ++p)
            if (score[p] == config.numPlayers - 1)
                return p;
        
        throw new Error("game is not done");
    }
    
    public boolean isDone() {
        for (int p = 1 ; p <= config.numPlayers ; ++p)
            if (score[p] == -1)
                return false;

        return true;
    }

    public int getScore(int player) {
        return score[player];
    }

    public int[] getScoreArray() {
        return score;
    }
       
    public ArrayList<Region> getPickableRegions() {
        return pickableRegions;
    }

    public int armiesPerTurn(int player, boolean first)
    {
        int armies = 5;
        if (first && numPlayers() == 2)
            armies = 3;
        
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
        return config.warlords ? world.numContinents() / numPlayers() : 4;
    }

    boolean bordersEnemy(Region r, int forPlayer) {
        for (Region n : r.getNeighbors())
            if (getOwner(n) != 0 && getOwner(n) != forPlayer)
                return true;

        return false;
    }

    int regionsOnContinent(Continent c, int player) {
        int count = 0;
        for (Region s : c.getRegions())
            if (getOwner(s) == player)
                count += 1;

        return count;
    }

    public Region getRandomStartingRegion(int forPlayer) {
        for (int pass = 1 ; pass <= 2 ; ++pass) {
            ArrayList<Region> possible = new ArrayList<Region>();
            
            for (Region r : pickableRegions)
                if (regionsOnContinent(r.getContinent(), forPlayer) < 2 &&
                    (!bordersEnemy(r, forPlayer) || pass == 2))
                    possible.add(r);

            if (!possible.isEmpty())
                return possible.get(random.nextInt(possible.size()));
        }

        throw new Error("no possible starting region");
    }

    void setAsStarting(Region r, int player) {
        setOwner(r, player);
        setArmies(r, 2 + config.extraArmies.get(player));
        pickableRegions.remove(r);
    }

    void regionsChosen() {
        if (gui != null)
            gui.regionsChosen(getRegions());

        round = 1;
        phase = Phase.PLACE_ARMIES;
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

        if (config.manualDistribution) {
            phase = Phase.STARTING_REGIONS;
            if (gui != null) {
                gui.showPickableRegions();
            }
        }
        else {  // automatic distribution
            for (int i = 0 ; i < numStartingRegions() ; ++i)
                for (int player = 1; player <= numPlayers(); ++player) {
                    Region r = getRandomStartingRegion(player);
                    setAsStarting(r, player);
                }
            round = 1;
            phase = Phase.PLACE_ARMIES;
        }
    }

    void nextTurn() {
        do {
            turn += 1;
            if (turn > numPlayers()) {
                turn = 1;
                round += 1;

                if (round > config.maxGameRounds) {     // game ends
                    int max = maxScore();
                            
                    while (true) {
                        int minPlayer = -1, minRegions = 0, minArmies = 0;
                        for (int p = 1 ; p <= config.numPlayers ; ++p)
                            if (score[p] == -1) {
                                int r = numberRegionsOwned(p);
                                int a = numberArmiesOwned(p);
                                if (minPlayer == -1 ||
                                    r < minRegions || r == minRegions && a < minArmies) {
                                        minPlayer = p;
                                        minRegions = r;
                                        minArmies = a;
                                    }
                            }
                        if (minPlayer == -1)    // all players have scores
                            return;

                        score[minPlayer] = ++max;
                    }
                }

                if (gui != null) {
                    gui.newRound(getRoundNumber());
                    gui.updateRegions(getRegions());
                    gui.updateMap();
                }
            }
        } while (score[turn] >= 0);
    }
    
    public void chooseRegion(Region region) {
        if (phase != Phase.STARTING_REGIONS)
            throw new Error("cannot choose regions after game has begun");
        
        if (!pickableRegions.contains(region))
            throw new Error("starting region is not pickable");
        
        setAsStarting(region, turn);
        turn += 1;
        if (turn > numPlayers())
            turn = 1;
        
        if (numberRegionsOwned(turn) == numStartingRegions())
            regionsChosen();
    }
    
    void illegalMove(String s) {
        System.out.printf("ignoring illegal move by player %d: %s\n", turn, s);
    }

    public void placeArmies(List<PlaceArmies> moves)
    {
        ArrayList<PlaceArmies> valid = new ArrayList<PlaceArmies>();

        if (phase != Phase.PLACE_ARMIES) {
            illegalMove("wrong time to place armies");
            return;
        }

        int left = armiesPerTurn(turn); 

        for(PlaceArmies move : moves)
        {
            Region region = move.getRegion();
            int armies = move.getArmies();
            
            if (!isOwnedBy(region, turn))
                illegalMove("can't place armies on unowned region " + region.getName());
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

        if (gui != null)
            gui.placeArmies(turn, valid);
    
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

    int round(double d, boolean mostLikely) {
        if (mostLikely)
            return (int) Math.round(d);

        double p = d - Math.floor(d);
        return (int) (random.nextDouble() < p ? Math.ceil(d) : Math.floor(d));
    }

    FightResult doAttack(int attackingArmies, int defendingArmies, boolean mostLikely) {
        FightResult result = new FightResult();

        result.defendersDestroyed = Math.min(round(attackingArmies * 0.6, mostLikely), defendingArmies);
        result.attackersDestroyed = Math.min(round(defendingArmies * 0.7, mostLikely), attackingArmies);
        
        result.postProcess(attackingArmies, defendingArmies);
        return result;
    }

    private void doAttack(AttackTransfer move, boolean mostLikely)
    {
        Region fromRegion = move.getFromRegion();
        Region toRegion = move.getToRegion();
        int attackingArmies = move.getArmies();
        int defendingArmies = getArmies(toRegion);
        
        FightResult result = doAttack(attackingArmies, defendingArmies, mostLikely);
        
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

    List<AttackTransfer> validateAttackTransfers(List<AttackTransfer> moves) {
        ArrayList<AttackTransfer> valid = new ArrayList<AttackTransfer>();
        int[] totalFrom = new int[numRegions()];
        
        for (int i = 0 ; i < moves.size() ; ++i) {
            AttackTransfer move = moves.get(i);
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
                    AttackTransfer n = moves.get(j);
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
    
    public void attackTransfer(List<AttackTransfer> moves, boolean mostLikely) {
        if (phase != Phase.ATTACK_TRANSFER) {
            illegalMove("wrong time to attack/transfer");
            return;
        }

        List<AttackTransfer> valid = validateAttackTransfers(moves);
        
        for (AttackTransfer move : valid) {
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
                doAttack(move, mostLikely);
                if (isDone())
                    return;
            }
        }
        
        nextTurn();
        phase = Phase.PLACE_ARMIES;
    }

    public void move(Move move, boolean mostLikely) {
        move.apply(this, mostLikely);
    }

    public void move(Move move) {
        move(move, false);
    }

    public void pass() {
        switch (phase) {
            case STARTING_REGIONS:
                Region r = pickableRegions.get(random.nextInt(pickableRegions.size()));
                chooseRegion(r);
                break;
            case PLACE_ARMIES:
                List<Region> owned = regionsOwnedBy(turn);
                r = owned.get(random.nextInt(owned.size()));
                PlaceArmies place = new PlaceArmies(r, armiesPerTurn(turn));
                placeArmies(List.of(place));
                break;
            case ATTACK_TRANSFER:
                attackTransfer(List.of(), false);
                break;
        }
    }
}
