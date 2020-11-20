package engine.robot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.*;

import bot.*;
import engine.Robot;
import engine.RobotConfig;
import game.*;
import game.move.*;
import game.world.MapRegion;
import utils.Util;

public class InternalRobot implements Robot {
    
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}
        
        @Override
        public void keyReleased(KeyEvent e) {}
        
        @Override
        public void keyPressed(KeyEvent e) {
        }
    }
    
    private Bot bot;

    private RobotConfig config;
    
    private MyKeyListener myKeyListener;

    private String botFQCN;
    
    public InternalRobot(int player, BotLoader botLoader, String botFQCN) throws IOException {
        this.botFQCN = botFQCN;
        
        bot = BotParser.constructBot(botLoader, botFQCN);
    }
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
        
        if (config.gui != null) {
            myKeyListener = new MyKeyListener();
            config.gui.addKeyListener(myKeyListener);
        }

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
    
    @Override
    public void writeInfo(String info){
    }

    public boolean isRunning() {
        return bot != null;
    }
    
    public void finish() {
        if (config.gui != null) {
            config.gui.removeKeyListener(myKeyListener);
        }
        bot = null;
    }

    @Override
    public int getRobotPlayer() {
        if (config == null) return 0;
        return config.player;
    }
    
    public String getRobotPlayerName() {
        return Util.className(botFQCN);
    }

}
