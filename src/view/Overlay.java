package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.*;

import game.*;
import utils.Util;

class Overlay extends JPanel implements MouseListener {
    Game game;
    MapView mapView;
    GUI gui;

    Rectangle doneBox;
    boolean showConnections;

    private static final long serialVersionUID = 1L;

    static final int TopMargin = 16;
    static Color Ocean = new Color(0x21, 0x4a, 0x8a);
    static Color TextColor = Color.LIGHT_GRAY;

    public Overlay(GUI gui, Game game) {
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

    public void toggleConnections() {
        showConnections = !showConnections;
        repaint();
    }

    class CompareByName implements Comparator<Continent> {
        @Override
        public int compare(Continent o1, Continent o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    void drawLegend(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        for (int player = 1; player <= game.numPlayers(); ++player) {
            int offset, x;
            if (game.numPlayers() < 4) {
                x = getWidth() - 250;
                offset = player - 1;
            } else {
                x = getWidth() - 430 + ((player - 1) / 2) * 220;
                offset = (player - 1) % 2;
            }
            int y = TopMargin - 2 + offset * 40;

            g.setColor(PlayerColors.getColor(player));
            g.fillOval(x + 1, y + 1, 18, 18);
            g.setColor(TextColor);
            g.drawOval(x, y, 20, 20);

            g.setColor(TextColor);
            g.drawString(gui.playerName(player), x + 29, y + 7);
            int armies = game.numberArmiesOwned(player);
            if (player == game.currentPlayer())
                armies += gui.armiesPlaced;
            String s = String.format("[%d armies, +%d / turn]", armies, game.armiesEachTurn(player));
            g.drawString(s, x + 29, y + 23);
        }
    }

    void drawConnections(Graphics2D g) {
        Stroke save = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.YELLOW.darker());

        for (Region r : game.getRegions())
            for (Region s : r.getNeighbors()) {
                if (r.id < s.id) {
                    Point p = mapView.regionPositions[r.id],
                          q = mapView.regionPositions[s.id];
                    g.drawLine(p.x, p.y, q.x, q.y);
                }
            }
        g.setStroke(save);
    }

    void drawRegionInfo(Graphics2D g) {
        for (int id = 0 ; id < game.numRegions() ; ++id) {
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
        if (!mapView.hasScroll)
            return;

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        ArrayList<Continent> a = new ArrayList<Continent>(game.getContinents());
        Collections.sort(a, new CompareByName());

        for (int i = 0; i < a.size(); ++i) {
            Continent mc = a.get(i);
            int x = 110;
            int y = 582 + 19 * i;

            Continent c = game.getContinent(mc.id);
            int owner = game.getOwner(c);
            if (owner > 0) {
                g.setColor(PlayerColors.getHighlightColor(owner));
                g.fillRect(x - 2, y - 13, 145, 17);
            }

            g.setColor(new Color(47, 79, 79));
            g.drawString(mc.name, x + 2, y);
            g.drawString("" + mc.reward, x + 130, y);
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

        Util.drawCentered(g, "Round " + game.getRoundNumber(), 80, TopMargin + 5);

        if (showConnections)
            drawConnections(g2);

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
            Region r = mapView.regionFromPoint(p);
            if (r != null)
                gui.regionClicked(r.id, e.getButton() == MouseEvent.BUTTON1);
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
        Point p = event.getPoint();
        Continent c = mapView.rewardFromPoint(p);
        if (c != null)
            return c.getName();

        Region r = mapView.regionFromPoint(event.getPoint());
        return r == null ? null : r.getName();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        if (getToolTipText(e) == null)
            return new Point(0, 0);

        Point p = e.getPoint();
        return new Point(p.x + 20, p.y + 10);
    }
}
