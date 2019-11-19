package conquest.game.move;

import conquest.game.GameState;

public interface Action {
    void apply(GameState state);
}
