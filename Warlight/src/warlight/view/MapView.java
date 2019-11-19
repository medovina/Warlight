package warlight.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JLabel;

import warlight.game.Team;
import warlight.game.world.WorldContinent;

class MapView extends JLabel {
    GUI gui;

    private static final long serialVersionUID = 1L;
    
    public MapView(GUI gui) { this.gui = gui; }
    
    class CompareByName implements Comparator<WorldContinent> {
        @Override
        public int compare(WorldContinent o1, WorldContinent o2) {
            return o1.mapName.compareTo(o2.mapName);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Font font = new Font("default", Font.BOLD, 14);
        g.setFont(font);
        
        WorldContinent[] a = WorldContinent.values().clone();
        Arrays.sort(a, new CompareByName());
        
        for (int i = 0 ; i < a.length ; ++i) {
            int y = 487 + 19 * i;

            WorldContinent c = a[i];
            Team owner = gui.continentOwner[c.id];
            if (owner != null) {
                g.setColor(TeamView.getHighlightColor(owner));
                g.fillRect(86, y - 13, 135, 17);
            }
            
            g.setColor(new Color(47, 79, 79));
            g.drawString(c.mapName, 88, y);
            g.drawString("" + c.reward, 208, y);
        }
    }
}
