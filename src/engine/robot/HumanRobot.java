package engine.robot;

import java.util.List;

import engine.Robot;
import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.MapRegion;
import view.GUI;

public class HumanRobot implements Robot {
    private GUI gui;
    
    public HumanRobot(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public MapRegion getStartingRegion(GameState state) {
        return gui.chooseRegionHuman().getWorldRegion();
    }

    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state) {
        return gui.placeArmiesHuman();
    }

    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state) {
        return gui.moveArmiesHuman();
    }
}
