package game.move;

import game.*;

public class ChooseRegion extends Move {
    public Region region;
    
    public ChooseRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChooseRegion))
            return false;
        
        return region == ((ChooseRegion) o).region;
    }
    
    @Override
    public void apply(Game game, boolean mostLikely) {
        game.chooseRegion(region);
    }

    @Override
    public String toString() {
        return String.format("ChooseRegion(%s)", region.getName());
    }
}
