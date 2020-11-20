package engine.robot;

import java.util.List;

import engine.Robot;
import engine.RobotConfig;
import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.MapRegion;

public class HumanRobot implements Robot {
    private RobotConfig config;
    
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
}
