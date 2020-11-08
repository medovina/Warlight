package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JLabel;

import game.*;
import game.world.WorldContinent;

class MapView extends JLabel {
    GUI gui;
    GameState game;

    private static final long serialVersionUID = 1L;
    
    public MapView(GUI gui, GameState game) {
        this.gui = gui;
        this.game = game;
    }
    
    class CompareByName implements Comparator<WorldContinent> {
        @Override
        public int compare(WorldContinent o1, WorldContinent o2) {
            return o1.mapName.compareTo(o2.mapName);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);
        GameMap map = game.getMap();

        for (int player = 1 ; player <= 2 ; ++player) {
            g.setColor(TeamView.getColor(player == 1 ? Team.PLAYER_1 : Team.PLAYER_2));
            g.fillOval(26, 53 + 35 * (player - 1), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString(gui.playerName(player), 52, 60 + 35 * (player - 1));
            int armies = map.numberArmiesOwned(player);
            if (player == game.me())
                armies += gui.armiesPlaced;
            String s = String.format("[%d armies, +%d / turn]",
                armies, game.armiesEachTurn(player));
            g.drawString(s, 52, 74 + 35 * (player - 1));
        }

        font = new Font("default", Font.BOLD, 14);
        g.setFont(font);

        WorldContinent[] a = WorldContinent.values().clone();
        Arrays.sort(a, new CompareByName());
        
        for (int i = 0 ; i < a.length ; ++i) {
            int y = 487 + 19 * i;

            Continent c = map.getContinent(a[i].id);
            int owner = c.getOwner();
            if (owner > 0) {
                g.setColor(TeamView.getHighlightColor(Team.getTeam(owner)));
                g.fillRect(86, y - 13, 135, 17);
            }
            
            g.setColor(new Color(47, 79, 79));
            g.drawString(a[i].mapName, 88, y);
            g.drawString("" + a[i].reward, 208, y);
        }
    }
}
