package game.move;

import game.*;

public class ChooseAction implements Action {
    public Region region;
    
    public ChooseAction(Region region) {
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
