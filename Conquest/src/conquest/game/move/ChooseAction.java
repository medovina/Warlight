package conquest.game.move;

import conquest.game.GameState;
import conquest.game.world.WorldRegion;

public class ChooseAction implements Action {
    public WorldRegion region;
    
    public ChooseAction(WorldRegion region) {
        this.region = region;
    }
    
    public void apply(GameState state) {
        state.chooseRegion(region);
    }
}
