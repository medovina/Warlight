package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import engine.*;
import game.*;
import game.move.AttackTransfer;
import game.move.PlaceArmies;

public class GUI extends JFrame implements KeyListener
{
    private static final long serialVersionUID = 0;

    private int width, height;
    
    private Game game;
    
    private String message, message2;
    
    private RegionInfo[] regionInfo;
    private boolean clicked = false;
    private boolean rightClick = false;
    private boolean nextRound = false;
    private boolean continual = false;
    private int continualTime = 1000;
    
    private Config config;
    
    private Arrow mainArrow;
    
    private JLayeredPane layeredPane;
    MapView mapView;
    Overlay overlay;
    
    private CountDownLatch chooseRegionAction;
    private Region chosenRegion;
    
    public CountDownLatch placeArmiesAction;
    public int armiesLeft;
    int armiesPlaced;
    private List<Region> armyRegions;

    private int moving = -1;
    private Map<Integer, Move> moves;  // maps encoded (fromId, toId) to Move
    private Region moveFrom;    
    public CountDownLatch moveArmiesAction;

    public GUI(Game game, Config config)
    {
        this.game = game;
        this.config = config;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Warlight");
        addKeyListener(this);
        
        mapView = new MapView(game);
        Dimension size = mapView.getPreferredSize();
        width = size.width;
        height = size.height;
        add(mapView);

        layeredPane = getLayeredPane();

        overlay = new Overlay(this, game);
        overlay.setBounds(0, 0, width, height);
        layeredPane.add(overlay);

        regionInfo = new RegionInfo[game.numRegions()];
        
        for (int idx = 0; idx < game.numRegions(); idx++) {
            regionInfo[idx] = new RegionInfo();
            mapView.setOwner(idx, 0);
        }
        
        mainArrow = new Arrow(0, 0, width, height);
        layeredPane.add(mainArrow, JLayeredPane.PALETTE_LAYER);
                
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        if (game.getPhase() != Phase.STARTING_REGIONS) {
            regionsChosen(game.getRegions());
            newRound(game.getRoundNumber());
        }
    }

    RegionInfo regionInfo(int id) {
        return regionInfo[id];
    }

    RegionInfo regionInfo(Region region) {
        return regionInfo(region.getId());
    }

    public void setContinual(boolean state) {
        continual = state;
    }
    
    public void setContinualFrameTime(int millis) {
        continualTime = millis;
    }

    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (moving != -1) {
                moveFrom = null;
                highlight();
            } else clicked = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = false;
        }
    }
    
    private void waitForClick() {
        long time = System.currentTimeMillis() + continualTime;
        clicked = false;
        
        while(!clicked && !rightClick && !nextRound) { //wait for click, or skip if right button down
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (continual && time < System.currentTimeMillis()) break; // skip if continual action and time out
        }
    }
    
    // ============
    // KEY LISTENER
    // ============
    
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        switch(c) {
        case 'n':
            nextRound = true;
            break;
        case 'c':
            continual = !continual;
            break;
        case 'C':
            overlay.toggleConnections();
            break;
        case ' ':
            clicked = true;
            break;
        case '+':
            continualTime += 100;
            continualTime = Math.min(continualTime, 3000);
            break;
        case '-':
            continualTime -= 100;
            continualTime = Math.max(continualTime, 200);
            break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    private void updateOverlay() {
        overlay.repaint();
    }

    void message(String s, String t) {
        message = s;
        message2 = t;
        updateOverlay();
    }

    void message(String s) {
        message(s, null);
    }

    public String getMessage() {
        return message;
    }
    
    public String getMessage2() {
        return message2;
    }
    
    public void newRound(int roundNum) {
        message("New round begins");
        nextRound = false;

        waitForClick();        
    }
    
    public void updateMap() {
        requestFocusInWindow();
        
        for (Region region : game.getRegions()) {
            int id = region.getId();
            regionInfo[id].setArmies(game.getArmies(region));
            mapView.setOwner(id, game.getOwner(region));
        }

        updateOverlay();
    }

    public void showPickableRegions() {
        requestFocusInWindow();
        
        message("Available territories");
        
        for (Region region : game.pickableRegions) {
            int id = region.getId();
            RegionInfo ri = this.regionInfo[id];
            ri.setHighlight(RegionInfo.Green);
        }
        
        waitForClick();
        
        for (Region region : game.pickableRegions) {
            int id = region.getId();
            RegionInfo ri = this.regionInfo[id];
            ri.setHighlight(false);
        }
    }
    
    public void updateRegions(List<Region> regions) {
        this.requestFocusInWindow();
        
        for (Region region : regions) {
            int id = region.getId();
            RegionInfo ri = this.regionInfo[id];
            mapView.setOwner(id, game.getOwner(region));
            ri.setArmies(game.getArmies(region));
        }
    }
    
    public void regionsChosen(List<Region> regions) {
        this.requestFocusInWindow();
        
        message("Starting territories");
        
        updateRegions(regions);
        updateOverlay();
        
        waitForClick();
    }
    
    public void placeArmies(int player, List<PlaceArmies> placeArmiesMoves) {
        if (config.isHuman(player))
            return;

        this.requestFocusInWindow();
        
        updateRegions(game.getRegions());
        
        int total = 0;
        
        for (PlaceArmies move : placeArmiesMoves) {
            int id = move.getRegion().id;
            RegionInfo region = this.regionInfo[id];    
            region.setArmies(region.getArmies() - move.getArmies());
            region.armiesPlus += move.getArmies();
            region.setHighlight(true);
            total += move.getArmies();
        }
        
        message(playerName(player) + " places " + total + " armies");
        
        updateOverlay();
        waitForClick();
        
        for (PlaceArmies move : placeArmiesMoves) {
            int id = move.getRegion().id;
            RegionInfo region = this.regionInfo[id];
            region.setArmies(region.getArmies() + region.armiesPlus);
            region.armiesPlus = 0;
            region.setHighlight(false);
        }
        
        message("---");
        
        updateOverlay();
    }    

    String armies(int n) {
        return n > 1 ? n + " armies " : "1 army ";
    }

    public void transfer(AttackTransfer move) {
        int fromId = move.getFromRegion().id, toId = move.getToRegion().id;
        this.requestFocusInWindow();
        
        int armies = move.getArmies();
        String toName = move.getToRegion().getName();

        String text;
        if (config.isHuman(game.currentPlayer()))
            text = "You transfer ";
        else
            text = playerName(game.currentPlayer()) + " transfers ";

        message(text + armies(armies) + " to " + toName);
        int player = game.currentPlayer();
        
        RegionInfo fromRegionInfo = regionInfo[fromId];
        RegionInfo toRegionInfo = regionInfo[toId];
        
        fromRegionInfo.setHighlight(true);
        toRegionInfo.setHighlight(true);
        
        Point fromPos = mapView.regionPositions[fromId];
        Point toPos = mapView.regionPositions[toId];
        mainArrow.setFromTo(fromPos.x + 3, fromPos.y - 3, toPos.x + 3, toPos.y - 3);
        mainArrow.setColor(PlayerColors.getColor(player));
        mainArrow.setNumber(armies);
        mainArrow.setVisible(true);
        
        waitForClick();
        
        fromRegionInfo.setHighlight(false);
        fromRegionInfo.setArmies(game.getArmies(game.getRegion(fromId)));
        
        toRegionInfo.setHighlight(false);
        toRegionInfo.setArmies(game.getArmies(game.getRegion(toId)));
        
        mainArrow.setVisible(false);
        updateOverlay();
        
        message("---");
    }
    
    String playerName(int player) {
        return config.playerName(player);
    }
    
    void showArrow(Arrow arrow, int fromRegionId, int toRegionId, int player, int armies) {
        Point fromPos = mapView.regionPositions[fromRegionId];
        Point toPos = mapView.regionPositions[toRegionId];
        arrow.setFromTo(fromPos.x + 3, fromPos.y - 3, toPos.x + 3, toPos.y - 3);
        arrow.setColor(PlayerColors.getColor(player));
        arrow.setNumber(armies);
        arrow.setVisible(true);
    }
    
    public void attack(AttackTransfer move) {
        this.requestFocusInWindow();
        
        String toName = move.getToRegion().getName();
        int armies = move.getArmies();

        String text;
        if (config.isHuman(game.currentPlayer()))
            text = "You attack ";
        else
            text = playerName(game.currentPlayer()) + " attacks ";
        message(text + toName + " with " + armies(armies));
        
        int attacker = game.currentPlayer();
        RegionInfo fromRegion = this.regionInfo[move.getFromRegion().id];
        RegionInfo toRegion = this.regionInfo[move.getToRegion().id];
        
        fromRegion.setHighlight(true);
        toRegion.setHighlight(true);
        
        showArrow(mainArrow, move.getFromRegion().id, move.getToRegion().id, attacker, armies);
        
        waitForClick();        
    }

    static Color withSaturation(Color c, float sat) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return new Color(Color.HSBtoRGB(hsb[0], sat, hsb[2]));
    }
    
    public void attackResult(Region fromRegion, Region toRegion, int attackersDestroyed, int defendersDestroyed) {
        this.requestFocusInWindow();
        
        RegionInfo fromRegionInfo = this.regionInfo[fromRegion.getId()];
        RegionInfo toRegionInfo = this.regionInfo[toRegion.getId()];
        int attacker = game.getOwner(fromRegion);
        
        boolean success = game.getOwner(fromRegion) == game.getOwner(toRegion);
        
        message("Attack " + (success ? "succeeded!" : "failed!"),
                String.format("(attackers lost %d, defenders lost %d)", 
                               attackersDestroyed, defendersDestroyed));

        fromRegionInfo.setArmies(game.getArmies(fromRegion));
        toRegionInfo.setArmies(game.getArmies(toRegion));

        if (success)
            mapView.setOwner(toRegion.getId(), game.getOwner(toRegion));
        
        fromRegionInfo.setHighlight(true);
        toRegionInfo.setHighlight(true);
        Color c = PlayerColors.getColor(attacker);
        mainArrow.setColor(withSaturation(c, success ? 0.5f : 0.2f));
        mainArrow.setNumber(0);

        updateOverlay();
        
        waitForClick();        
        
        fromRegionInfo.setHighlight(false);
        toRegionInfo.setHighlight(false);
        mainArrow.setVisible(false);
        
        message("---");    
    }
    
    // ======================
    // CHOOSE INITIAL REGIONS
    // ======================
    
    Button doneButton() {
        Button b = new Button("Done");
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setSize(60, 30);
        b.setLocation(width / 2 - 30, 60);
        return b;
    }
    
    public Region chooseRegionHuman() {
        requestFocusInWindow();
        
        chooseRegionAction = new CountDownLatch(1);
        
        message("Choose a starting territory");

        updateMap();
        
        if (game.config.warlords)
            for (Region region : game.pickableRegions) {
                RegionInfo ri = this.regionInfo[region.getId()];
                ri.setHighlight(RegionInfo.Green);
            }
        
        try {
            chooseRegionAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }

        if (game.config.warlords)
            for (Region region : game.pickableRegions) {
                RegionInfo ri = this.regionInfo[region.getId()];
                ri.setHighlight(false);
            }
        
        chooseRegionAction = null;
        return chosenRegion;
    }
    
    // ============
    // PLACE ARMIES
    // ============
    
    public List<PlaceArmies> placeArmiesHuman() {
        this.requestFocusInWindow();
        
        List<Region> availableRegions = game.regionsOwnedBy(game.currentPlayer());
        return placeArmiesHuman(availableRegions);
    }
    
    void setPlaceArmiesText(int armiesLeft) {
        if (armiesLeft > 0)
            message(
                "Place " + armiesLeft + (armiesLeft == 1 ? " army" : " armies") +
                " on your territories");
        else
            message("");
    }

    public List<PlaceArmies> placeArmiesHuman(List<Region> availableRegions) {
        this.armyRegions = availableRegions;
        armiesLeft = game.armiesPerTurn(game.currentPlayer());
        armiesPlaced = 0;
                
        placeArmiesAction = new CountDownLatch(1);
        
        setPlaceArmiesText(armiesLeft);
                
        try {
            placeArmiesAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }
        
        placeArmiesAction = null;
        
        List<PlaceArmies> result = new ArrayList<PlaceArmies>();
        
        for (Region region : availableRegions) {
            RegionInfo info = regionInfo[region.getId()];
            if (info.armiesPlus > 0) {
                info.setArmies(info.getArmies() + info.armiesPlus);
                info.setHighlight(false);

                PlaceArmies command = new PlaceArmies(region, info.armiesPlus);
                info.armiesPlus = 0;
                
                result.add(command);
            }
        }
        
        armiesPlaced = 0;
        return result;
    }
    
    void doneClicked() {
        if (placeArmiesAction != null) {
            if (armiesLeft == 0) {
                placeArmiesAction.countDown();
            }
            GUI.this.requestFocusInWindow();
        } else if (moveArmiesAction != null) {
            moveArmiesAction.countDown();
            GUI.this.requestFocusInWindow();
        }
    }

    private void placeArmyRegionClicked(Region region, int change) {        
        change = Math.min(armiesLeft, change);
        if (change == 0) return;
        
        RegionInfo info = regionInfo[region.getId()];
        
        if (change < 0) {
            change = -Math.min(Math.abs(change), info.armiesPlus);
        }
        if (change == 0) return;
        
        info.armiesPlus += change;
        armiesPlaced += change;
        armiesLeft -= change;
        
        if (info.armiesPlus > 0) {
            info.setHighlight(true);
        } else {
            info.setHighlight(false);
        }
        
        setPlaceArmiesText(armiesLeft);
        
        updateOverlay();
    }

    // ===========
    // MOVE ARMIES
    // ===========
    
    class Move {
        Region from;
        Region to;
        int armies;
        Arrow arrow;
        
        Move(Region from, Region to, int armies, Arrow arrow) {
            this.from = from; this.to = to; this.armies = armies; this.arrow = arrow;
        }
    }
    
    int encode(int fromId, int toId) {
        return fromId * (game.numRegions() + 1) + toId;
    }
    
    int totalFrom(Region r) {
        int sum = 0;
        
        for (Move m : moves.values())
            if (m.from == r)
                sum += m.armies;
        
        return sum;
    }
    
    void move(Region from, Region to, int delta) {
        int e = encode(to.getId(), from.getId());
        Move m = moves.get(e);
        if (m != null) { // move already exists in the opposite direction
             move(to, from, - delta);
             return;
        }
        
        if (totalFrom(from) + delta >= regionInfo(from).getArmies())
            return;        // no available armies
        
        e = encode(from.getId(), to.getId());
        m = moves.get(e);
        if (m == null && delta > 0) {
            Arrow arrow = new Arrow(0, 0, width, height);
            showArrow(arrow, from.getId(), to.getId(), moving, delta);
            layeredPane.add(arrow, JLayeredPane.PALETTE_LAYER);
            moves.put(e, new Move(from, to, delta, arrow));
        } else if (m != null) {
            m.armies += delta;
            if (m.armies > 0)
                m.arrow.setNumber(m.armies);
            else {
                layeredPane.remove(m.arrow);
                layeredPane.repaint();
                moves.remove(e);
            }
        }
        
    }
    
    void highlight() {
        int from = moveFrom == null ? -1 : moveFrom.getId();

        for (int id = 0 ; id < regionInfo.length ; ++id)
            regionInfo[id].setHighlight(id == from ? RegionInfo.Green : null);
        
        if (moveFrom != null)
            for (Region n : moveFrom.getNeighbors())
                regionInfo(n).setHighlight(RegionInfo.Gray);

        updateOverlay();
    }
    
    void regionClicked(int id, boolean left) {
        Region region = game.getRegion(id);
        
        if (chooseRegionAction != null) {
            if (game.pickableRegions.contains(region)) {
                chosenRegion = region;
                chooseRegionAction.countDown();
            }
            return;
        }

        if (placeArmiesAction != null) {
            if (armyRegions.contains(region)) {
                placeArmyRegionClicked(region, left ? 1 : -1);
                GUI.this.requestFocusInWindow();
            }
            return;
        }
        
        if (moving == -1) {
            clicked = true;
            return;
        }
        
        if (moveFrom != null && game.isNeighbor(moveFrom, region)) {
            move(moveFrom, region, left ? 1 : -1);
            return;
        }
        if (!left)
            return;
        
        moveFrom = game.getOwner(region) == game.currentPlayer() ? region : null;
        highlight();
    }

    public List<AttackTransfer> moveArmiesHuman() {
        this.requestFocusInWindow();
        moving = game.currentPlayer();
        moveFrom = null;
        
        message("Move and/or attack");
            
        moveArmiesAction = new CountDownLatch(1);
        
        moves = new HashMap<Integer, Move>();
        
        highlight();
        
        try {
            moveArmiesAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }
        moveArmiesAction = null;
        
        for (RegionInfo info : regionInfo)
            info.setHighlight(false);
        
        List<AttackTransfer> moveArmies = new ArrayList<AttackTransfer>();
        
        for (Move m : moves.values()) {
            moveArmies.add(new AttackTransfer(m.from, m.to, m.armies));
            layeredPane.remove(m.arrow);
        }
        
        moving = -1;
        
        return moveArmies;
    }
}
