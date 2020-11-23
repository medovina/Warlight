package game.move;

import java.util.List;

import game.Game;

public class AttackTransferMove implements Move {
    public List<AttackTransfer> commands;
    
    public AttackTransferMove(List<AttackTransfer> commands) { this.commands = commands; }
    
    public void apply(Game game) {
        game.attackTransfer(commands);
    }
}
