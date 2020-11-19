package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;

import game.*;
import game.world.*;
import utils.Util;

class Overlay extends JPanel implements MouseListener {
    GameState game;
    MapView mapView;
    GUI gui;

    Rectangle doneBox;

    private static final long serialVersionUID = 1L;

    static final int TopMargin = 16;
    static Color Ocean = new Color(0x21, 0x4a, 0x8a);
    static Color TextColor = Color.LIGHT_GRAY;

    public Overlay(GUI gui, GameState game) {
        this.game = game;
        this.mapView = gui.mapView;
        this.gui = gui;

        setOpaque(false);
        addMouseListener(this);
    
        ToolTipManager m = ToolTipManager.sharedInstance();
        m.setDismissDelay(10000);
        m.setInitialDelay(10);
        m.setReshowDelay(10);

        setToolTipText("");
    }

    class CompareByName implements Comparator<WorldContinent> {
        @Override
        public int compare(WorldContinent o1, WorldContinent o2) {
            return o1.mapName.compareTo(o2.mapName);
        }
    }

    void drawLegend(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        GameMap map = game.getMap();
        for (int player = 1; player <= 2; ++player) {
            int offset = 40 * (player - 1);
            int x = getWidth() - 250;
            int y = TopMargin - 2 + offset;
            g.setColor(TeamView.getColor(player == 1 ? Team.PLAYER_1 : Team.PLAYER_2));
            g.fillOval(x + 1, y + 1, 18, 18);
            g.setColor(TextColor);
            g.drawOval(x, y, 20, 20);

            g.setColor(TextColor);
            g.drawString(gui.playerName(player), x + 29, y + 7);
            int armies = map.numberArmiesOwned(player);
            if (player == game.me())
                armies += gui.armiesPlaced;
            String s = String.format("[%d armies, +%d / turn]", armies, game.armiesEachTurn(player));
            g.drawString(s, x + 29, y + 23);
        }
    }

    void drawRegionInfo(Graphics2D g) {
        for (int id = 1 ; id <= WorldRegion.NUM_REGIONS ; ++id) {
            Point pos = mapView.regionPositions[id];
            RegionInfo ri = gui.regionInfo(id);
            int x = pos.x, y = pos.y;

            if (ri.highlight != null) {
                g.setColor(ri.highlight);
                Stroke save = g.getStroke();
                g.setStroke(new BasicStroke(2));
                g.drawOval(x - 17, y - 23, 38, 38);
                g.setStroke(save);
            }
    
            g.setColor(Color.BLACK);
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
            g.setFont(font);
    
            String text = "" + ri.armies;
            if (ri.armiesPlus > 0)
                text += "+" + ri.armiesPlus;
            Util.drawCentered(g, text, x + 2, y);
        }
    }
    
    void drawScroll(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        WorldContinent[] a = WorldContinent.values().clone();
        Arrays.sort(a, new CompareByName());

        GameMap map = game.getMap();
        for (int i = 0; i < a.length; ++i) {
            int x = 104;
            int y = 560 + 19 * i;

            Continent c = map.getContinent(a[i].id);
            int owner = c.getOwner();
            if (owner > 0) {
                g.setColor(TeamView.getHighlightColor(Team.getTeam(owner)));
                g.fillRect(x - 2, y - 13, 145, 17);
            }

            g.setColor(new Color(47, 79, 79));
            g.drawString(a[i].mapName, x + 2, y);
            g.drawString("" + a[i].reward, x + 130, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.renderNicely(g2);

        drawLegend(g);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        int width = getWidth();

        g.setColor(Color.WHITE);
        Util.drawCentered(g, gui.getMessage(), width / 2, TopMargin + 5);
        String s = gui.getMessage2();
        if (s != null)
            Util.drawCentered(g, s, width / 2, TopMargin + 22);

        if (gui.placeArmiesAction != null && gui.armiesLeft == 0 || gui.moveArmiesAction != null) {
            int y = TopMargin + 45;
            String text = "DONE";
            FontMetrics fm = g.getFontMetrics();
            int buttonWidth = fm.stringWidth(text) + 10, buttonHeight = fm.getHeight() + 10;
            doneBox = new Rectangle(
                width / 2 - buttonWidth / 2, y - buttonHeight / 2 - fm.getAscent() / 2 + 1,
                buttonWidth, buttonHeight);
            g.setColor(Ocean.brighter());
            g.fillRoundRect(doneBox.x, doneBox.y, doneBox.width, doneBox.height, 6, 6);
            g.setColor(TextColor);
            g.drawRoundRect(doneBox.x, doneBox.y, doneBox.width, doneBox.height, 6, 6);
            Util.drawCentered(g, text, width / 2, y);
        } else doneBox = null;

        Util.drawCentered(g, "Round " + game.getRoundNumber(), 100, TopMargin + 5);

        drawRegionInfo(g2);

        drawScroll(g);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Point p = e.getPoint();
        if (doneBox != null && doneBox.contains(p))
            gui.doneClicked();
        else {
            int id = mapView.regionFromPoint(p);
            if (id >= 0)
                gui.regionClicked(id, e.getButton() == MouseEvent.BUTTON1);
            else
                gui.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gui.mouseReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int id = mapView.regionFromPoint(event.getPoint());
        return id == -1 ? null : WorldRegion.forId(id).getName();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        int id = mapView.regionFromPoint(e.getPoint());
        if (id == -1)
            return new Point(0, 0);

        Point p = e.getPoint();
        return new Point(p.x + 20, p.y + 10);
    }
}
