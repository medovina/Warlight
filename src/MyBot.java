import java.io.File;
import java.util.*;

import warlight.bot.BotParser;
import warlight.bot.Bot;
import warlight.bot.fight.FightSimulation.FightAttackersResults;
import warlight.engine.Config;
import warlight.engine.RunGame;
import warlight.game.*;
import warlight.game.move.*;
import warlight.game.world.WorldRegion;
import warlight.utils.Util;

public class MyBot implements Bot
{
    Random rand = new Random();
    
    FightAttackersResults attackResults;
    
    public MyBot() {
        attackResults = FightAttackersResults.loadFromFile(Util.findFile(
                "Warlight-Bots/FightSimulation-Attackers-A200-D200.obj"));
    }
    
    // Code your bot here.
    
    //
    // This is a dummy implemementation that moves randomly.
    //

    @Override
    public void init(long timeoutMillis) {
    }
    
    // Choose a starting region.
    
    @Override
    public WorldRegion chooseRegion(GameState state) {
        ArrayList<Region> choosable = state.getPickableRegions();
        return choosable.get(rand.nextInt(choosable.size())).getWorldRegion();
    }

    // Decide where to place armies this turn.
    // state.armiesPerTurn(state.me()) is the number of armies available to place.
    
    @Override
    public List<PlaceArmiesMove> placeArmies(GameState state) {
        int me = state.me();
        List<Region> mine = state.regionsOwnedBy(me);
        int numRegions = mine.size();
        
        int[] count = new int[numRegions];
        for (int i = 0 ; i < state.armiesPerTurn(me) ; ++i) {
            int r = rand.nextInt(numRegions);
            count[r]++;
        }
        
        List<PlaceArmiesMove> ret = new ArrayList<PlaceArmiesMove>();
        for (int i = 0 ; i < numRegions ; ++i)
            if (count[i] > 0)
                ret.add(new PlaceArmiesMove(mine.get(i), count[i]));
        return ret;
    }
    
    // Decide where to move armies this turn.
    
    @Override
    public List<AttackTransferMove> moveArmies(GameState state) {
        List<AttackTransferMove> ret = new ArrayList<AttackTransferMove>();
        
        for (Region rd : state.regionsOwnedBy(state.me())) {
            int count = rand.nextInt(rd.getArmies());
            if (count > 0) {
                List<Region> neighbors = rd.getNeighbors();
                Region to = neighbors.get(rand.nextInt(neighbors.size()));
                ret.add(new AttackTransferMove(rd, to, count));
            }
        }
        return ret;        
    }
    
    public static void runInternal() {
        Config config = new Config();
        
        config.bot1Init = "internal:MyBot";
        
        config.bot2Init = "internal:warlight.bot.custom.AggressiveBot";
        //config.bot2Init = "human";
        
        config.botCommandTimeoutMillis = 20 * 1000;
        
        config.visualize = true;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
    
    public static void runExternal() {
        BotParser parser = new BotParser(new MyBot());
        parser.setLogFile(new File("./MyBot.log"));
        parser.run();
    }
}
