package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;

import game.*;
import game.world.WorldContinent;
import utils.Util;

class Overlay extends JPanel {
    GUI gui;
    GameState game;

    private static final long serialVersionUID = 1L;
    
    public Overlay(GUI gui, GameState game) {
        this.gui = gui;
        this.game = game;
        setOpaque(false);
    }
    
    class CompareByName implements Comparator<WorldContinent> {
        @Override
        public int compare(WorldContinent o1, WorldContinent o2) {
            return o1.mapName.compareTo(o2.mapName);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.renderNicely(g2);

        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);

        int width = getWidth();

        final int Margin = 18;
        g.setColor(Color.WHITE);
        Util.drawCentered(g, "Round " + game.getRoundNumber(), width / 2, Margin);
        Util.drawCentered(g, gui.getMessage(), width / 2, Margin + 17);

        gui.drawRegionInfo(g2);

        // legend in upper left

        GameMap map = game.getMap();

        for (int player = 1 ; player <= 2 ; ++player) {
            int y = Margin - 4 + 35 * (player - 1);
            g.setColor(Color.LIGHT_GRAY);
            g.drawOval(26, y, 20, 20);
            g.setColor(TeamView.getColor(player == 1 ? Team.PLAYER_1 : Team.PLAYER_2));
            g.fillOval(27, y + 1, 18, 18);

            g.setColor(Color.LIGHT_GRAY);
            g.drawString(gui.playerName(player), 52, Margin + 3 + 35 * (player - 1));
            int armies = map.numberArmiesOwned(player);
            if (player == game.me())
                armies += gui.armiesPlaced;
            String s = String.format("[%d armies, +%d / turn]",
                armies, game.armiesEachTurn(player));
            g.drawString(s, 52, Margin + 17 + 35 * (player - 1));
        }

        // scroll in lower left

        font = new Font("default", Font.BOLD, 14);
        g.setFont(font);

        WorldContinent[] a = WorldContinent.values().clone();
        Arrays.sort(a, new CompareByName());
        
        for (int i = 0 ; i < a.length ; ++i) {
            int x = 104;
            int y = 560 + 19 * i;

            Continent c = map.getContinent(a[i].id);
            int owner = c.getOwner();
            if (owner > 0) {
                g.setColor(TeamView.getHighlightColor(Team.getTeam(owner)));
                g.fillRect(x, y - 13, 135, 17);
            }
            
            g.setColor(new Color(47, 79, 79));
            g.drawString(a[i].mapName, x + 2, y);
            g.drawString("" + a[i].reward, x + 130, y);
        }
    }
}
