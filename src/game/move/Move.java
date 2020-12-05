package game.move;

import game.Game;

public abstract class Move {
    public abstract void apply(Game game, boolean mostLikely);
}
