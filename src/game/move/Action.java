package game.move;

import game.GameState;

public interface Action {
    void apply(GameState state);
}
