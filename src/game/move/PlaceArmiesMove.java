package game.move;

import java.util.List;

import game.Game;

public class PlaceArmiesMove implements Move {
    public List<PlaceArmies> commands;
    
    public PlaceArmiesMove(List<PlaceArmies> commands) { this.commands = commands; }
    
    public void apply(Game state) {
        state.placeArmies(commands);
    }
}
