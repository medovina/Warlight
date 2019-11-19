package warlight.game.move;

import warlight.game.GameState;

public interface Action {
    void apply(GameState state);
}
