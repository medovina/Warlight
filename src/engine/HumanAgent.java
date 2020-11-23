package engine;

import java.util.List;

import game.*;
import game.move.AttackTransfer;
import game.move.PlaceArmies;
import view.GUI;

public class HumanAgent extends AgentBase {
    private GUI gui;
    
    public HumanAgent(GUI gui) {
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
    public List<PlaceArmies> placeArmies(Game state) {
        return gui.placeArmiesHuman();
    }

    @Override
    public List<AttackTransfer> attackTransfer(Game state) {
        return gui.moveArmiesHuman();
    }
}
