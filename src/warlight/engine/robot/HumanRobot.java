package warlight.engine.robot;

import java.util.List;

import warlight.engine.Robot;
import warlight.game.*;
import warlight.game.move.AttackTransferMove;
import warlight.game.move.PlaceArmiesMove;
import warlight.game.world.WorldRegion;

public class HumanRobot implements Robot {
    private RobotConfig config;
    private boolean running = true;;
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
    }

    @Override
    public WorldRegion getStartingRegion(GameState state) {
        return config.gui.chooseRegionHuman().getWorldRegion();
    }

    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state) {
        return config.gui.placeArmiesHuman(config.team);
    }

    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state) {
        return config.gui.moveArmiesHuman(config.team);
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
