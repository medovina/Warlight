package engine.robot;

import java.util.List;

import engine.Robot;
import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.MapRegion;

public class HumanRobot implements Robot {
    private RobotConfig config;
    private boolean running = true;;
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
    }

    @Override
    public MapRegion getStartingRegion(GameState state) {
        return config.gui.chooseRegionHuman().getWorldRegion();
    }

    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state) {
        return config.gui.placeArmiesHuman();
    }

    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state) {
        return config.gui.moveArmiesHuman();
    }

    @Override
    public void writeInfo(String info) {
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void finish() {
        running = false;
    }

    @Override
    public int getRobotPlayer() {
        if (config == null) return 0;
        return config.player;
    }
    
    public String getRobotPlayerName() {
        return "You";
    }

}
