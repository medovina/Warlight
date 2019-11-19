package conquest.game.move;

import java.util.List;

import conquest.game.GameState;

public class PlaceArmiesAction implements Action {
    public List<PlaceArmiesMove> commands;
    
    public PlaceArmiesAction(List<PlaceArmiesMove> commands) { this.commands = commands; }
    
    public void apply(GameState state) {
        state.placeArmies(commands);
    }
}
