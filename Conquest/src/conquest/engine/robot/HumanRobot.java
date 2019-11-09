package conquest.engine.robot;

import java.util.List;

import conquest.engine.Robot;
import conquest.game.*;
import conquest.game.move.AttackTransferMove;
import conquest.game.move.PlaceArmiesMove;

public class HumanRobot implements Robot {
    private RobotConfig config;
    private boolean running = true;;
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
    }

    @Override
    public Region getStartingRegion(GameState state) {
        return config.gui.chooseRegionHuman();
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
        return "Human";
    }

}
