package game.move;

import java.util.List;

import game.GameState;

public class AttackTransferAction implements Action {
    public List<AttackTransferMove> commands;
    
    public AttackTransferAction(List<AttackTransferMove> commands) { this.commands = commands; }
    
    public void apply(GameState state) {
        state.attackTransfer(commands);
    }
}
