package game.move;

import game.*;

public class ChooseAction implements Action {
    // The bot API uses Regions, but we store WorldRegions inside this and other action classes
    // so that actions generated in a cloned game state are still valid in the original state.

    public MapRegion region;
    
    public ChooseAction(Region region) {
        this.region = region.getMapRegion();
    }
    
    public void apply(Game state) {
        state.chooseRegion(state.region(region));
    }

    @Override
    public String toString() {
        return String.format("[ChooseAction %s]", region.getName());
    }
}
