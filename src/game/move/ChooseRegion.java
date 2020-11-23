package game.move;

import game.*;

public class ChooseRegion implements Move {
    public Region region;
    
    public ChooseRegion(Region region) {
        this.region = region;
    }
    
    public void apply(Game game) {
        game.chooseRegion(region);
    }

    @Override
    public String toString() {
        return String.format("[ChooseAction %s]", region.getName());
    }
}
