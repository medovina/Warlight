package engine;

import java.util.List;

import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import view.GUI;

public class HumanBot implements Bot {
    private GUI gui;
    
    public HumanBot(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public Region chooseRegion(Game state) {
        return gui.chooseRegionHuman();
    }

    @Override
    public List<PlaceArmiesMove> placeArmies(Game state) {
        return gui.placeArmiesHuman();
    }

    @Override
    public List<AttackTransferMove> moveArmies(Game state) {
        return gui.moveArmiesHuman();
    }
}
