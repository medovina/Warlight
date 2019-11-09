package conquest.bot;

import java.util.List;

import conquest.bot.Bot;
import conquest.game.GameState;
import conquest.game.move.AttackTransferMove;
import conquest.game.move.PlaceArmiesMove;
import conquest.game.world.Region;

public abstract class GameBot implements Bot {
    protected GameState state;
    
    void updateState(GameState state) {
        this.state = state;
    }
    
    @Override
    public final Region getStartingRegion(GameState state, Long timeOut) {
        updateState(state);
        
        return chooseRegion(timeOut == null ? Long.MAX_VALUE : timeOut);
    }
    
    public abstract Region chooseRegion(long timeout);    
    
    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state, Long timeOut) {
        updateState(state);
        
        return placeArmies(timeOut == null ? Long.MAX_VALUE : timeOut);
    }
    
    public abstract List<PlaceArmiesMove> placeArmies(long timeout);
    
    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state, Long timeOut) {
        updateState(state);
        
        return moveArmies(timeOut == null ? Long.MAX_VALUE : timeOut);
    }
    
    public abstract List<AttackTransferMove> moveArmies(long timeout);
}
