package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import engine.Robot;
import engine.robot.HumanRobot;
import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.WorldContinent;
import game.world.WorldRegion;

public class GUI extends JFrame implements MouseListener, KeyListener
{
    private static final long serialVersionUID = 0;
    private static final int WIDTH = 1240, HEIGHT = 622;
    
    public static int[][] positions = new int[][]{
        {95, 130},  //1.  Alaska
        {209, 140}, //2.  Northwest Territory
        {441, 76},  //3.  Greenland
        {190, 185}, //4.  Alberta
        {257, 189}, //5.  Ontario
        {355, 183}, //6.  Quebec
        {224, 243}, //7.  Western United States
        {295, 257},  //8.  Eastern United States
        {255, 313},  //9.  Central America
        {350, 353},  //10. Venezuela
        {344, 425},  //11. Peru
        {415, 414},  //12. Brazil
        {374, 491},  //13. Argentina
        {514, 138},  //14. Iceland
        {545, 180},  //15. Great Britain
        {627, 140},  //16. Scandinavia
        {699, 185},  //17. Ukraine
        {556, 246},  //18. Western Europe
        {618, 198}, //19. Northern Europe
        {650, 235}, //20. Southern Europe
        {576, 319},  //21. North Africa
        {647, 296},  //22. Egypt
        {698, 359},  //23. East Africa
        {654, 388},  //24. Congo
        {657, 458},  //25. South Africa
        {726, 445},  //26. Madagascar
        {800, 158},  //27. Ural
        {890, 126},  //28. Siberia
        {972, 130},  //29. Yakutsk
        {1080, 130}, //30. Kamchatka
        {942, 185},  //31. Irkutsk
        {798, 222},  //32. Kazakhstan
        {895, 259},  //33. China
        {965, 222},  //34. Mongolia
        {1030, 259}, //35. Japan
        {716, 275},  //36. Middle East
        {835, 296},  //37. India
        {908, 328},  //38. Siam
        {930, 392},  //39. Indonesia
        {1035, 402}, //40. New Guinea
        {983, 464},  //41. Western Australia
        {1055, 478}, //42. Eastern Australia
    };
    
    private GameState game;
    
    private GUINotif notification;
    
    private JLabel roundNumTxt;
    private JLabel actionTxt;
    
    private RegionInfo[] regions;
    private boolean clicked = false;
    private boolean rightClick = false;
    private boolean nextRound = false;
    private boolean continual = false;
    private int continualTime = 1000;
    
    private Robot[] bots;
    
    public Team[] continentOwner = new Team[WorldContinent.LAST_ID + 1];
    
    private Arrow mainArrow;
    
    private JLayeredPane mainLayer;
    
    private CountDownLatch chooseRegionAction;
    private Region chosenRegion;
    
    private CountDownLatch placeArmiesAction;
    private int armiesLeft;
    int armiesPlaced;
    private List<Region> armyRegions;
    private Button placeArmiesFinishedButton;

    private Team moving = null;
    private Map<Integer, Move> moves;  // maps encoded (fromId, toId) to Move
    private Region moveFrom;    
    private CountDownLatch moveArmiesAction;
    private Button moveArmiesFinishedButton;

    public GUI(GameState game, Robot[] bots)
    {
        this.game = game;
        this.bots = bots;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Warlight");
        addMouseListener(this);
        addKeyListener(this);
        
        mainLayer = new JLayeredPane();
        mainLayer.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(mainLayer);

        //Map image
        MapImage mapImage = new MapImage(WIDTH, HEIGHT);
        mapImage.setBounds(0, 0, WIDTH, HEIGHT);
        mapImage.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainLayer.add(mapImage, JLayeredPane.DEFAULT_LAYER);

        final int BoxWidth = 450, BoxHeight = 18;
        
        //Current round number
        roundNumTxt = new JLabel("Territory Distribution", JLabel.CENTER);
        roundNumTxt.setBounds(WIDTH / 2 - BoxWidth / 2, 20, BoxWidth, BoxHeight);
        roundNumTxt.setBackground(Color.gray);
        roundNumTxt.setOpaque(true);
        roundNumTxt.setForeground(Color.WHITE);
        mainLayer.add(roundNumTxt, JLayeredPane.DRAG_LAYER);
        
        actionTxt = new JLabel("ACTION", JLabel.CENTER);
        actionTxt.setBounds(WIDTH / 2 - BoxWidth / 2, 20 + BoxHeight, BoxWidth, BoxHeight);
        actionTxt.setBackground(Color.gray);
        actionTxt.setOpaque(true);
        actionTxt.setForeground(Color.WHITE);
         actionTxt.setPreferredSize(actionTxt.getSize());
        mainLayer.add(actionTxt, JLayeredPane.DRAG_LAYER);
                
        regions = new RegionInfo[42];
        
        for (int idx = 0; idx < 42; idx++) {
            regions[idx] = new RegionInfo(this);
            regions[idx].setLocation(positions[idx][0] - 50, positions[idx][1]);
            regions[idx].setRegion(game.getRegion(idx+1));            
            mainLayer.add(this.regions[idx], JLayeredPane.PALETTE_LAYER);
        }
        
        notification = new GUINotif(mainLayer, 1015, 45, 200, 50);        
        
        mainArrow = new Arrow(0, 0, WIDTH, HEIGHT);
        mainLayer.add(mainArrow, JLayeredPane.PALETTE_LAYER);
                
        //Finish
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    RegionInfo regionInfo(Region region) {
        return regions[region.getId() - 1];
    }
    
    public Team getTeam(int player) {
        switch (player) {
        case 0: return Team.NEUTRAL;
        case 1: return Team.PLAYER_1;
        case 2: return Team.PLAYER_2;
        default: return null;
        }
    }
    
    public void setContinual(boolean state) {
        continual = state;
    }
    
    public void setContinualFrameTime(int millis) {
        continualTime = millis;
    }

    boolean humanGame() {
        return bots[0] instanceof HumanRobot || bots[1] instanceof HumanRobot;
    }
    
    // ==============
    // MOUSE LISTENER
    // ==============
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (moving != null) {
                moveFrom = null;
                highlight();
            } else clicked = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = true;
        }
    }

    @Override
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
        c = Character.toLowerCase(c);
        switch(c) {
        case 'n':
            nextRound = true;
            showNotification("SKIP TO NEXT ROUND");
            break;
        case 'c':
            continual = !continual;
            showNotification( continual ? "Continual run enabled" : "Continual run disabled");
            break;
        case ' ':
            clicked = true;
            break;
        case '+':
            continualTime += 100;
            continualTime = Math.min(continualTime, 3000);
            showNotification("Action visualized for: " + continualTime + " ms");
            break;
        case '-':
            continualTime -= 100;
            continualTime = Math.max(continualTime, 200);
            showNotification("Action visualized for: " + continualTime + " ms");
            break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    public void showNotification(String txt) {
        notification.show(txt, 1500);
    }
    
    private void updateStats() {
        repaint();
    }
    
    public void newRound(int roundNum) {
        roundNumTxt.setText("Round " + Integer.toString(roundNum));
        actionTxt.setText("New round begins");
        nextRound = false;

        //Wait for user to request next round
        waitForClick();        
    }
    
    public void updateMap() {
        this.requestFocusInWindow();
        
        //Update regions info
        for(Region region : game.getMap().regions) {
            int id = region.getId();
            this.regions[id-1].setArmies(region.getArmies());
            this.regions[id-1].setText(Integer.toString(region.getArmies()));            
            this.regions[id-1].setTeam(getTeam(region.getOwner()));
        }

        updateStats();
    }

    public void showPickableRegions() {
        if (humanGame())
            return;

        this.requestFocusInWindow();
        
        actionTxt.setText("Available territories");
        
        for (Region region : game.pickableRegions) {
            int id = region.getId();
            RegionInfo ri = this.regions[id-1];
            ri.setHighlight(RegionInfo.Green);
        }
        
        waitForClick();
        
        for (Region region : game.pickableRegions) {
            int id = region.getId();
            RegionInfo ri = this.regions[id-1];
            ri.setHighlight(false);
        }
    }
    
    public void updateRegions(List<Region> regions) {
        this.requestFocusInWindow();
        
        for (Region data : regions) {
            int id = data.getId();
            RegionInfo region = this.regions[id-1];
            region.setTeam(getTeam(data.getOwner()));
            region.setArmies(data.getArmies());
            region.setText("" + region.getArmies());
        }
    }
    
    public void regionsChosen(List<Region> regions) {
        this.requestFocusInWindow();
        
        if (!humanGame()) {
            actionTxt.setText("Chosen territories");
            
            updateRegions(regions);
            
            for (Region data : regions) {
                int id = data.getId();
                RegionInfo region = this.regions[id-1];
                region.setHighlight(region.getTeam() != Team.NEUTRAL);
            }

            waitForClick();
            
            for (Region region : regions) {
                int id = region.getId();
                RegionInfo regionInfo = this.regions[id-1];
                regionInfo.setHighlight(false);
            }
        }
        
        updateStats();
    }
    
    public void placeArmies(int player, ArrayList<Region> regions, List<PlaceArmiesMove> placeArmiesMoves) {
        this.requestFocusInWindow();
        
        updateRegions(regions);
        
        int total = 0;
        
        for (PlaceArmiesMove move : placeArmiesMoves) {
            int id = move.getRegion().id;
            RegionInfo region = this.regions[id-1];    
            region.setArmies(region.getArmies() - move.getArmies());
            region.armiesPlus += move.getArmies();
            region.setText(region.getArmies() + "+" + region.armiesPlus);
            region.setHighlight(true);
            total += move.getArmies();
        }
        
        actionTxt.setText(playerName(player) + " places " + total + " armies");
        
        repaint();
        waitForClick();
        
        for (PlaceArmiesMove move : placeArmiesMoves) {
            int id = move.getRegion().id;
            RegionInfo region = this.regions[id-1];
            region.setArmies(region.getArmies() + region.armiesPlus);
            region.armiesPlus = 0;
            region.setText("" + region.getArmies());
            region.setHighlight(false);
        }
        
        actionTxt.setText("---");
        
        updateStats();
    }    

    String armies(int n) {
        return n > 1 ? n + " armies " : "1 army ";
    }

    public void transfer(AttackTransferMove move) {
        this.requestFocusInWindow();
        
        int armies = move.getArmies();
        String toName = move.getToRegion().getFullName();

        String text;
        if (bot(game.me()) instanceof HumanRobot)
            text = "You transfer ";
        else
            text = playerName(game.me()) + " transfers ";

        actionTxt.setText(text + armies(armies) + " to " + toName);
        Team player = getTeam(game.me());
        
        RegionInfo fromRegion = this.regions[move.getFromRegion().id - 1];
        RegionInfo toRegion = this.regions[move.getToRegion().id - 1];
        
        fromRegion.armiesPlus = -armies;
        fromRegion.setHighlight(true);
        
        toRegion.armiesPlus = armies;
        toRegion.setHighlight(true);
        
        int[] fromPos = positions[move.getFromRegion().id - 1];
        int[] toPos = positions[move.getToRegion().id - 1];
        mainArrow.setFromTo(fromPos[0], fromPos[1] + 20, toPos[0], toPos[1] + 20);
        mainArrow.setColor(TeamView.getColor(player));
        mainArrow.setNumber(armies);
        mainArrow.setVisible(true);
        
        waitForClick();
        
        fromRegion.setHighlight(false);
        fromRegion.setArmies(fromRegion.getArmies() + fromRegion.armiesPlus);
        fromRegion.setText(String.valueOf(fromRegion.getArmies()));
        fromRegion.armiesPlus = 0;
        
        toRegion.setHighlight(false);
        toRegion.setArmies(toRegion.getArmies() + toRegion.armiesPlus);
        toRegion.setText(String.valueOf(toRegion.getArmies()));
        toRegion.armiesPlus = 0;
        
        mainArrow.setVisible(false);
        
        actionTxt.setText("---");
    }
    
    Robot bot(int player) {
        return bots[player - 1];
    }

    String playerName(int player) {
        return bot(player).getRobotPlayerName();
    }
    
    void showArrow(Arrow arrow, int fromRegionId, int toRegionId, Team team, int armies) {
        int[] fromPos = positions[fromRegionId - 1];
        int[] toPos = positions[toRegionId - 1];
        arrow.setFromTo(fromPos[0], fromPos[1] + 20, toPos[0], toPos[1] + 20);
        arrow.setColor(TeamView.getColor(team));
        arrow.setNumber(armies);
        arrow.setVisible(true);
    }
    
    public void attack(AttackTransferMove move) {
        this.requestFocusInWindow();
        
        String toName = move.getToRegion().getFullName();
        int armies = move.getArmies();

        String text;
        if (bot(game.me()) instanceof HumanRobot)
            text = "You attack ";
        else
            text = playerName(game.me()) + " attacks ";
        actionTxt.setText(text + toName + " with " + armies(armies));
        
        Team attacker = getTeam(game.me());
        RegionInfo fromRegion = this.regions[move.getFromRegion().id - 1];
        RegionInfo toRegion = this.regions[move.getToRegion().id - 1];
        
        fromRegion.armiesPlus = -armies;
        fromRegion.setHighlight(true);
        
        toRegion.armiesPlus = armies;
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
        
        RegionInfo fromRegionInfo = this.regions[fromRegion.getId() - 1];
        RegionInfo toRegionInfo = this.regions[toRegion.getId() - 1];
        Team attacker = getTeam(fromRegion.getOwner());
        
        boolean success = fromRegion.getOwner() == toRegion.getOwner();
        
        String outcome = String.format("Attack %s! (attackers lost %d, defenders lost %d)",
            success ? "succeeded" : "failed", attackersDestroyed, defendersDestroyed);
        actionTxt.setText(outcome);

        if (success) {
            fromRegionInfo.setArmies(fromRegionInfo.getArmies() + fromRegionInfo.armiesPlus);
            toRegionInfo.setTeam(getTeam(toRegion.getOwner()));
            toRegionInfo.setArmies((-fromRegionInfo.armiesPlus) - attackersDestroyed);
        } else {
            fromRegionInfo.setArmies(fromRegionInfo.getArmies() - attackersDestroyed);
            toRegionInfo.setArmies(toRegionInfo.getArmies() - defendersDestroyed);
        }
        
        fromRegionInfo.armiesPlus = 0;
        fromRegionInfo.setText("" + fromRegionInfo.getArmies());
        
        toRegionInfo.armiesPlus = 0;
        toRegionInfo.setText("" + toRegionInfo.getArmies());
        
        fromRegionInfo.setHighlight(true);
        toRegionInfo.setHighlight(true);
        Color c = TeamView.getColor(attacker);
        mainArrow.setColor(withSaturation(c, success ? 0.5f : 0.2f));
        mainArrow.setNumber(0);

        updateStats();
        
        waitForClick();        
        
        fromRegionInfo.setHighlight(false);
        toRegionInfo.setHighlight(false);
        mainArrow.setVisible(false);
        
        actionTxt.setText("---");    
    }
    
    // --------------
    // ==============
    // HUMAN CONTROLS
    // ==============
    // --------------
    
    // ======================
    // CHOOSE INITIAL REGIONS
    // ======================
    
    Button doneButton() {
        Button b = new Button("Done");
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setSize(60, 30);
        b.setLocation(WIDTH / 2 - 30, 60);
        return b;
    }
    
    public Region chooseRegionHuman() {
        requestFocusInWindow();
        
        chooseRegionAction = new CountDownLatch(1);
        
        actionTxt.setText("Choose a starting territory");
        
        for (Region region : game.pickableRegions) {
            RegionInfo ri = this.regions[region.getId()-1];
            ri.setHighlight(RegionInfo.Green);
        }
        
        try {
            chooseRegionAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }
        
        for (Region region : game.pickableRegions) {
            RegionInfo ri = this.regions[region.getId()-1];
            ri.setHighlight(false);
        }
        
        chooseRegionAction = null;
        return chosenRegion;
    }
    
    // ============
    // PLACE ARMIES
    // ============
    
    public List<PlaceArmiesMove> placeArmiesHuman(Team team) {
        this.requestFocusInWindow();
        
        List<Region> availableRegions = new ArrayList<Region>();
        for (int i = 0; i < regions.length; ++i) {
            RegionInfo info = regions[i];
            if (info.getTeam() == team) {
                availableRegions.add(game.getRegion(i + 1));
            }            
        }
        return placeArmiesHuman(availableRegions);
    }
    
    void setPlaceArmiesText(int armiesLeft) {
        actionTxt.setText(
            "Place " + armiesLeft + (armiesLeft == 1 ? " army" : " armies") +
            " on your territories");
    }

    public List<PlaceArmiesMove> placeArmiesHuman(List<Region> availableRegions) {
        this.armyRegions = availableRegions;
        armiesLeft = game.armiesPerTurn(game.me());
        armiesPlaced = 0;
                
        placeArmiesAction = new CountDownLatch(1);
        
        setPlaceArmiesText(armiesLeft);
        
        if (placeArmiesFinishedButton == null) {
            placeArmiesFinishedButton = doneButton();
            placeArmiesFinishedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (armiesLeft == 0) {
                        placeArmiesAction.countDown();
                    }
                    GUI.this.requestFocusInWindow();
                }
            });
        }
        mainLayer.add(placeArmiesFinishedButton, JLayeredPane.MODAL_LAYER);
        placeArmiesFinishedButton.setVisible(false);
        repaint();
        
        try {
            placeArmiesAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }
        
        placeArmiesAction = null;
        mainLayer.remove(placeArmiesFinishedButton);
        
        List<PlaceArmiesMove> result = new ArrayList<PlaceArmiesMove>();
        
        for (Region region : availableRegions) {
            RegionInfo info = regions[region.getId()-1];
            if (info.armiesPlus > 0) {
                info.setArmies(info.getArmies() + info.armiesPlus);
                info.setText("" + info.getArmies());
                info.setHighlight(false);

                PlaceArmiesMove command = new PlaceArmiesMove(region, info.armiesPlus);
                info.armiesPlus = 0;
                
                result.add(command);
            }
        }
        
        armiesPlaced = 0;
        return result;
    }
    
    private void placeArmyRegionClicked(Region region, int change) {        
        change = Math.min(armiesLeft, change);
        if (change == 0) return;
        
        RegionInfo info = regions[region.getId()-1];
        
        if (change < 0) {
            change = -Math.min(Math.abs(change), info.armiesPlus);
        }
        if (change == 0) return;
        
        info.armiesPlus += change;
        armiesPlaced += change;
        armiesLeft -= change;
        
        if (info.armiesPlus > 0) {
            info.setText(info.getArmies() + "+" + info.armiesPlus);
            info.setHighlight(true);
        } else {
            info.setText(String.valueOf(info.getArmies()));
            info.setHighlight(false);
        }
        
        setPlaceArmiesText(armiesLeft);
        
        placeArmiesFinishedButton.setVisible(armiesLeft == 0);
        repaint();
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
    
    static int encode(int fromId, int toId) {
        return fromId * (WorldRegion.LAST_ID + 1) + toId;
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
            Arrow arrow = new Arrow(0, 0, WIDTH, HEIGHT);
            showArrow(arrow, from.getId(), to.getId(), moving, delta);
            mainLayer.add(arrow, JLayeredPane.PALETTE_LAYER);
            moves.put(e, new Move(from, to, delta, arrow));
        } else if (m != null) {
            m.armies += delta;
            if (m.armies > 0)
                m.arrow.setNumber(m.armies);
            else {
                mainLayer.remove(m.arrow);
                moves.remove(e);
                repaint();
            }
        }
        
    }
    
    void highlight() {
        if (moveFrom == null)
            for (RegionInfo ri : regions)
                ri.setHighlight(ri.getTeam() == moving);
        else {
            for (RegionInfo ri : regions)
                ri.setHighlight(ri.getRegion() == moveFrom ? RegionInfo.Green : null);
            
            for (Region n : moveFrom.getNeighbors())
                regionInfo(n).setHighlight(RegionInfo.Gray);
        }
    }
    
    boolean isNeighbor(Region r, Region s) {
        return r.getNeighbors().contains(s);
    }
    
    void regionClicked(RegionInfo ri, boolean left) {
        Region region = ri.getRegion();
        
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
        
        if (moving == null) {
            clicked = true;
            return;
        }
        
        if (moveFrom != null && isNeighbor(moveFrom, region)) {
            move(moveFrom, region, left ? 1 : -1);
            return;
        }
        if (!left)
            return;
        
        moveFrom = (ri.getTeam() == moving) ? region : null;
        highlight();
    }

    public List<AttackTransferMove> moveArmiesHuman(Team team) {
        this.requestFocusInWindow();
        moving = team;
        moveFrom = null;
        
        actionTxt.setText("Move and/or attack");
            
        moveArmiesAction = new CountDownLatch(1);
        
        moves = new HashMap<Integer, Move>();
        
        if (moveArmiesFinishedButton == null) {
            moveArmiesFinishedButton = doneButton();
            moveArmiesFinishedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveArmiesAction.countDown();
                    GUI.this.requestFocusInWindow();
                }
            });
        }
        mainLayer.add(moveArmiesFinishedButton, JLayeredPane.MODAL_LAYER);
        highlight();
        
        try {
            moveArmiesAction.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while awaiting user action.");
        }
        
        mainLayer.remove(moveArmiesFinishedButton);
        
        for (RegionInfo info : regions)
            info.setHighlight(false);
        
        List<AttackTransferMove> moveArmies = new ArrayList<AttackTransferMove>();
        
        for (Move m : moves.values()) {
            moveArmies.add(new AttackTransferMove(m.from, m.to,    m.armies));
            mainLayer.remove(m.arrow);
        }
        repaint();
        
        moving = null;
        
        return moveArmies;
    }
} 
