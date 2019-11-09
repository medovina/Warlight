package conquest.game.move;

import conquest.game.GameState;
import conquest.game.world.Region;

public class ChooseAction implements Action {
    public Region region;
    
    public ChooseAction(Region region) {
        this.region = region;
    }
    
    public void apply(GameState state) {
        state.chooseRegion(region);
    }
}
