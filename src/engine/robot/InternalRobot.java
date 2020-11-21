package engine.robot;

import java.util.*;

import engine.Bot;
import engine.Robot;
import game.*;
import game.move.*;
import game.world.MapRegion;

public class InternalRobot implements Robot {
    private Bot bot;

    static Bot constructBot(Class<?> botClass) {        
        Object botObj;
        try {
            botObj = botClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
        if (!(Bot.class.isAssignableFrom(botObj.getClass()))) {
            throw new RuntimeException("Constructed bot does not implement " + Bot.class.getName() + " interface, bot class instantiated: " + botClass.getName());
        }
        Bot bot = (Bot) botObj;
        return bot;
    }
    
    static Bot constructBot(String botFQCN) {
        Class<?> botClass;
        try {
            try {
                botClass = Class.forName(botFQCN);
            } catch (ClassNotFoundException e) {
                botClass = Class.forName("bots." + botFQCN);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to locate bot class: " + botFQCN, e);
        }
        return constructBot(botClass);
    }
    
    public InternalRobot(String botFQCN) {
        bot = constructBot(botFQCN);
    }
    
    @Override
    public void init(long timeoutMillis) {
        bot.init(timeoutMillis);
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
