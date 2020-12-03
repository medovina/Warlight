package engine;

import java.util.List;

import game.*;
import game.move.*;

public abstract class AgentBase implements Agent {
    public abstract Region chooseRegion(Game game);
    public abstract List<PlaceArmies> placeArmies(Game game);
    public abstract List<AttackTransfer> attackTransfer(Game game);
    
    @Override public Move getMove(Game game) {
        switch (game.getPhase()) {
            case STARTING_REGIONS:
                return new ChooseRegion(chooseRegion(game));
            case PLACE_ARMIES:
                return new PlaceArmiesMove(placeArmies(game));
            case ATTACK_TRANSFER:
                return new AttackTransferMove(attackTransfer(game));
            default:
                throw new Error("unknown phase");
        }
    }

    @Override public void terminate() { }
}
