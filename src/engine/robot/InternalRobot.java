package engine.robot;

import java.util.*;

import bot.*;
import engine.Robot;
import engine.RobotConfig;
import game.*;
import game.move.*;
import game.world.MapRegion;

public class InternalRobot implements Robot {
    private Bot bot;

    public InternalRobot(String botFQCN) {
        bot = BotParser.constructBot(botFQCN);
    }
    
    @Override
    public void setup(RobotConfig config) {
        bot.init(config.timeoutMillis);
    }
    
    @Override
    public MapRegion getStartingRegion(GameState state)
    {
        return bot.chooseRegion(state);
    }
    
    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state)
    {
        return bot.placeArmies(state);
    }
    
    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state)
    {
        return bot.moveArmies(state);
    }
}
