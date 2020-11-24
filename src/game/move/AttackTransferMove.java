package game.move;

import java.util.List;

import game.Game;

public class AttackTransferMove implements Move {
    public List<AttackTransfer> commands;
    
    public AttackTransferMove(List<AttackTransfer> commands) { this.commands = commands; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof AttackTransferMove))
            return false;
        
        AttackTransferMove m = (AttackTransferMove) o;
        return commands.equals(m.commands);
    }
    
    public void apply(Game game) {
        game.attackTransfer(commands);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("AttackTransferMove(");
        for (int i = 0 ; i < commands.size() ; ++i) {
            if (i > 0)
                sb.append(", ");
            AttackTransfer a = commands.get(i);
            sb.append(a.getFromRegion().getName() + " -> " + a.getArmies() + " -> " +
                      a.getToRegion().getName());
        }
        sb.append(")");
        return sb.toString();
    }
}
