package game.move;

import java.util.List;

import game.Game;

public class PlaceArmiesMove implements Move {
    public List<PlaceArmies> commands;
    
    public PlaceArmiesMove(List<PlaceArmies> commands) { this.commands = commands; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PlaceArmiesMove))
            return false;
        
        PlaceArmiesMove m = (PlaceArmiesMove) o;
        return commands.equals(m.commands);
    }
    
    public void apply(Game state) {
        state.placeArmies(commands);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("PlaceArmiesMove(");
        for (int i = 0 ; i < commands.size() ; ++i) {
            if (i > 0)
                sb.append(", ");
            PlaceArmies p = commands.get(i);
            sb.append(p.getRegion().getName() + " = " + p.getArmies());
        }
        sb.append(")");
        return sb.toString();
    }
}
