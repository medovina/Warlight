package game.move;

import game.Game;

public interface Action {
    void apply(Game state);
}
