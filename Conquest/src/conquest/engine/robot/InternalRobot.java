package conquest.engine.robot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.*;

import conquest.bot.*;
import conquest.engine.Robot;
import conquest.game.*;
import conquest.game.move.*;
import conquest.game.world.Region;

public class InternalRobot implements Robot {
    
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}
        
        @Override
        public void keyReleased(KeyEvent e) {}
        
        @Override
        public void keyPressed(KeyEvent e) {
            if (InternalRobot.this.config.team == Team.PLAYER_1) {
                if (Character.toLowerCase(e.getKeyChar()) == 'h') {
                    hijacked = !hijacked;
                    if (config.gui != null) {
                        config.gui.showNotification(
                            hijacked ? InternalRobot.this.config.player + " hijacked!" : InternalRobot.this.config.player + " resumed!"
                        );
                    }
                }
            }
            if (InternalRobot.this.config.team == Team.PLAYER_2) {
                if (Character.toLowerCase(e.getKeyChar()) == 'j') {
                    hijacked = !hijacked;
                    if (config.gui != null) {
                        config.gui.showNotification(
                            hijacked ? InternalRobot.this.config.player + " hijacked!" : InternalRobot.this.config.player + " resumed!"
                        );
                    }
                }
            }
        }
    }
    
    private Bot bot;

    private RobotConfig config;
    
    private boolean hijacked = false;
    
    private HumanRobot humanHijack;

    private MyKeyListener myKeyListener;

    private String botFQCN;
    
    public InternalRobot(int player, String botFQCN) throws IOException {
        this.botFQCN = botFQCN;
        
        bot = BotParser.constructBot(botFQCN);
        System.out.println(player + " -> " + botFQCN);
        
        humanHijack = new HumanRobot();
    }
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
        
        humanHijack.setup(config);
        
        if (config.gui != null) {
            myKeyListener = new MyKeyListener();
            config.gui.addKeyListener(myKeyListener);
        }
    }
    
    @Override
    public Region getStartingRegion(GameState state, long timeOut)
    {
        if (hijacked) {
            return humanHijack.getStartingRegion(state, timeOut);            
        }
        return bot.chooseRegion(state, timeOut);
    }
    
    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state, long timeOut)
    {
        if (hijacked) {
            return humanHijack.getPlaceArmiesMoves(state, timeOut);        
        }
        return bot.placeArmies(state, timeOut);
    }
    
    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state, long timeOut)
    {
        if (hijacked) {
            return humanHijack.getAttackTransferMoves(state, timeOut);    
        }
        return bot.moveArmies(state, timeOut);
    }
    
    @Override
    public void writeInfo(String info){
        humanHijack.writeInfo(info);
    }

    public boolean isRunning() {
        return bot != null;
    }
    
    public void finish() {
        if (config.gui != null) {
            config.gui.removeKeyListener(myKeyListener);
        }
        bot = null;
        if (humanHijack != null) {
            try {
                humanHijack.finish();
            } catch (Exception e) {                
            }
            humanHijack = null;
        }
    }

    @Override
    public int getRobotPlayer() {
        if (config == null) return 0;
        return config.player;
    }
    
    public String getRobotPlayerName() {
        if (config == null) return botFQCN.substring(1+botFQCN.lastIndexOf("."));
        return botFQCN.substring(1+botFQCN.lastIndexOf("."));
    }

}
