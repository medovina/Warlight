package warlight.view;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import warlight.game.*;
import warlight.game.move.AttackTransferMove;
import warlight.game.move.PlaceArmiesMove;
import warlight.game.world.WorldContinent;
import warlight.game.world.WorldRegion;
import warlight.view.TriButton.ClickListener;

public class GUI extends JFrame implements MouseListener, KeyListener
{
    private static final long serialVersionUID = 2116436198852146401L;
    private static final String RESOURCE_IMAGE_FILE = "resources/images/warlight-map.png";
    private static final int WIDTH = 1239;
    private static final int HEIGHT = 664;
    
    public static int[][] positions = new int[][]{
        {95, 150},  //1.  Alaska
        {209, 160}, //2.  Northwest Territory
        {441, 96},  //3.  Greenland
        {190, 205}, //4.  Alberta
        {257, 209}, //5.  Ontario
        {355, 203}, //6.  Quebec
        {224, 263}, //7.  Western United States
        {295,277},  //8.  Eastern United States
        {255,333},  //9.  Central America
        {350,373},  //10. Venezuela
        {344,445},  //11. Peru
        {415,434},  //12. Brazil
        {374,511},  //13. Argentina
        {514,158},  //14. Iceland
        {545,200},  //15. Great Britain
        {627,160},  //16. Scandinavia
        {699,205},  //17. Ukraine
        {556,266},  //18. Western Europe
        {618, 218}, //19. Northern Europe
        {650, 255}, //20. Southern Europe
        {576,339},  //21. North Africa
        {647,316},  //22. Egypt
        {698,379},  //23. East Africa
        {654,408},  //24. Congo
        {657,478},  //25. South Africa
        {726,465},  //26. Madagascar
        {800,178},  //27. Ural
        {890,146},  //28. Siberia
        {972,150},  //29. Yakutsk
        {1080,150}, //30. Kamchatka
        {942,205},  //31. Irkutsk
        {798,242},  //32. Kazakhstan
        {895,279},  //33. China
        {965,242},  //34. Mongolia
        {1030,279}, //35. Japan
        {716,295},  //36. Middle East
        {835,316},  //37. India
        {908,348},  //38. Siam
        {930,412},  //39. Indonesia
        {1035,422}, //40. New Guinea
        {983,484},  //41. Western Australia
        {1055,500}, //42. Eastern Australia
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
    
    private String botName1;
    private String botName2;
    
    private RegionInfo p1;
    private RegionInfo p2;
    
    public Team[] continentOwner = new Team[WorldContinent.LAST_ID + 1];
    
    private Arrow mainArrow;
    
    private JLayeredPane mainLayer;
    
    public boolean showIds = false;
    
    public GUI(GameState game)
    {
        System.out.println("GUI: Click to advance to next round.");
        System.out.println("GUI: Hold right mouse button to QUICKLY advance through many rounds.");
        
        this.game = game;
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Warlight");
        this.addMouseListener(this);
        this.addKeyListener(this);
        
        this.setLayout(null);
        
        mainLayer = new JLayeredPane();
        mainLayer.setBounds(0, 0, WIDTH, HEIGHT);
        mainLayer.setSize(WIDTH, HEIGHT);
        mainLayer.setPreferredSize(mainLayer.getSize());
        mainLayer.setLocation(0, -19);
        this.add(mainLayer);

        //Map image
        JLabel labelForImage = new MapView(this);
        labelForImage.setBounds(0, 0, WIDTH, HEIGHT);
        URL iconURL = this.getClass().getResource(RESOURCE_IMAGE_FILE);
        ImageIcon icon = new ImageIcon(iconURL);
        labelForImage.setIcon(icon);
        mainLayer.add(labelForImage, JLayeredPane.DEFAULT_LAYER);

        int boxWidth = 300;
        
        //Current round number
        roundNumTxt = new JLabel("Round: --", JLabel.CENTER);
        roundNumTxt.setBounds(WIDTH / 2 - boxWidth / 2, 20, boxWidth, 15);
        roundNumTxt.setBackground(Color.gray);
        roundNumTxt.setOpaque(true);
        roundNumTxt.setForeground(Color.WHITE);
        mainLayer.add(roundNumTxt, JLayeredPane.DRAG_LAYER);
        
        actionTxt = new JLabel("ACTION", JLabel.CENTER);
        actionTxt.setBounds(WIDTH / 2 - boxWidth / 2, 35, boxWidth, 15);
        actionTxt.setBackground(Color.gray);
        actionTxt.setOpaque(true);
        actionTxt.setForeground(Color.WHITE);
            actionTxt.setPreferredSize(actionTxt.getSize());
        mainLayer.add(actionTxt, JLayeredPane.DRAG_LAYER);
                
        this.regions = new RegionInfo[42];
        
        for (int idx = 0; idx < 42; idx++) {
            this.regions[idx] = new RegionInfo(this);
            this.regions[idx].setLocation(positions[idx][0] - 50, positions[idx][1]);
            this.regions[idx].setRegion(game.getRegion(idx+1));            
            mainLayer.add(this.regions[idx], JLayeredPane.PALETTE_LAYER);
        }
        
        //Legend
        p1 = new RegionInfo(this);
        p1.setLocation(45,50);
        p1.setTeam(Team.PLAYER_1);
        p1.setNameLabel("PLR1");
        p1.setText("PLR1");
        mainLayer.add(p1, JLayeredPane.PALETTE_LAYER);
        
        p2 = new RegionInfo(this);
        p2.setLocation(45,85);
        p2.setTeam(Team.PLAYER_2);
        p2.setNameLabel("PLR2");
        p2.setText("PLR2");
        mainLayer.add(p2, JLayeredPane.PALETTE_LAYER);
        
        notification = new GUINotif(mainLayer, 1015, 45, 200, 50);        
        
        mainArrow = new Arrow(0, 0, WIDTH, HEIGHT);
        mainLayer.add(mainArrow, JLayeredPane.PALETTE_LAYER);
                
        //Finish
        this.pack();
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
                
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
    
    // ==============
    // MOUSE LISTENER
    // ==============
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (moving != null) {
                moveFrom = null;
                highlight();
            } else clicked = true;
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
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
        case 'i':
            showIds = !showIds;
            for (RegionInfo i : regions)
                i.drawName();
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
    
    // =====
    // NOTIF
    // =====
    
    public void showNotification(String txt) {
        notification.show(txt, 1500);
    }
    
    // =============
    // ENGINE EVENTS
    // =============
    
    private void updateStats() {
        
        int plr1Regions = 0;
        int plr2Regions = 0;
        
        int plr1Armies = 0;
        int plr2Armies = 0;
        
        int plr1Income = 5;
        int plr2Income = 5;
        
        for (RegionInfo region : this.regions) {
            switch(region.getTeam()) {
            case PLAYER_1:
                ++plr1Regions;
                plr1Armies += region.getArmies();
                break;
            case PLAYER_2:
                ++plr2Regions;
                plr2Armies += region.getArmies();
                break;
            case NEUTRAL:
                break;
            }
        }
        
        for (WorldContinent continent : WorldContinent.values()) {
            boolean plr1 = true;
            boolean plr2 = true;
            for (WorldRegion region : continent.getRegions()) {
                RegionInfo info = regions[region.id-1];
                if (info.getTeam() == Team.PLAYER_1) plr2 = false;
                if (info.getTeam() == Team.PLAYER_2) plr1 = false;
                if (info.getTeam() == Team.NEUTRAL) {
                    plr1 = false;
                    plr2 = false;
                }
                if (!plr1 && !plr2) break;
            }
            if (plr1) {
                plr1Income += continent.reward;
                continentOwner[continent.id] = Team.PLAYER_1;
            } else if (plr2) {
                plr2Income += continent.reward;
                continentOwner[continent.id] = Team.PLAYER_2;
            } else
                continentOwner[continent.id] = null;
        }
        
        p1.setText("[" + plr1Regions + " / " + plr1Armies + " / +" + plr1Income + "]");
        p2.setText("[" + plr2Regions + " / " + plr2Armies + " / +" + plr2Income + "]");
        repaint();
    }
    
    public void newRound(int roundNum) {
        roundNumTxt.setText("Round: " + Integer.toString(roundNum));
        actionTxt.setText("NEW ROUND");
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

    public void pickableRegions() {
        this.requestFocusInWindow();
        
        actionTxt.setText("PICKABLE REGIONS");
        
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
        
        actionTxt.setText("CHOSEN REGIONS");
        
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
        
        actionTxt.setText(botName(player) + " places " + total + " armies");
        
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

    public void transfer(AttackTransferMove move) {
        this.requestFocusInWindow();
        
        String toName = move.getToRegion().mapName;
        actionTxt.setText(botName(game.me()) + " transfers to " + toName);
        Team player = getTeam(game.me());
        
        RegionInfo fromRegion = this.regions[move.getFromRegion().id - 1];
        RegionInfo toRegion = this.regions[move.getToRegion().id - 1];
        int armies = move.getArmies();
        
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
    
    String botName(int player) {
        switch (player) {
        case 1: return botName1;
        case 2: return botName2;
        default: throw new Error("unknown name");
        }
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
        
        String toName = move.getToRegion().mapName;
        actionTxt.setText(botName(game.me()) + " attacks " + toName);
        
        Team attacker = getTeam(game.me());
        RegionInfo fromRegion = this.regions[move.getFromRegion().id - 1];
        RegionInfo toRegion = this.regions[move.getToRegion().id - 1];
        int armies = move.getArmies();
        
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
        
        boolean success;
        
        if (fromRegion.getOwner() == toRegion.getOwner()) {
            success = true;
            actionTxt.setText("SUCCESS [A:" + (attackersDestroyed > 0 ? "-" : "") + attackersDestroyed + " | D:" + (defendersDestroyed > 0 ? "-" : "") + defendersDestroyed + "]");
            fromRegionInfo.setArmies(fromRegionInfo.getArmies() + fromRegionInfo.armiesPlus);
            toRegionInfo.setTeam(getTeam(toRegion.getOwner()));
            toRegionInfo.setArmies((-fromRegionInfo.armiesPlus) - attackersDestroyed);
        } else {
            success = false;
            actionTxt.setText("FAILURE [A:" + (attackersDestroyed > 0 ? "-" : "") + attackersDestroyed + " | D:" + (defendersDestroyed > 0 ? "-" : "") + defendersDestroyed + "]");
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
    
    private CountDownLatch chooseRegionAction;
    
    private Region chosenRegion;
    
    Button doneButton() {
        Button b = new Button("DONE");
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setSize(60, 30);
        b.setLocation(WIDTH / 2 - 30, HEIGHT - 100);
        return b;
    }
    
    public Region chooseRegionHuman() {
        requestFocusInWindow();
        
        chooseRegionAction = new CountDownLatch(1);
        
        actionTxt.setText(botName(game.me()) + ": choose a starting region");
        
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
    
    private CountDownLatch placeArmiesAction;
    
    private int armiesLeft;
    
    private List<Region> armyRegions;
    
    private List<TriButton> armyRegionButtons;
    
    private Button placeArmiesFinishedButton;
        
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
    
    public List<PlaceArmiesMove> placeArmiesHuman(List<Region> availableRegions) {
        this.armyRegions = availableRegions;
        armiesLeft = game.armiesPerTurn(game.me());
                
        placeArmiesAction = new CountDownLatch(1);
        
        actionTxt.setText(botName(game.me()) + ": place " + armiesLeft +
                          (armiesLeft == 1 ? "army" : " armies"));
        
        armyRegionButtons = new ArrayList<TriButton>();
        
        int ch1 = 1;
        int ch2 = 4;
        int ch3 = 10;
        
        for (Region region : armyRegions) {
            int[] regionPos = positions[region.getId()-1];
            
            TriButton button = new TriButton(mainLayer, regionPos[0], regionPos[1] + 30, ch1, ch2, ch3);
            
            armyRegionButtons.add(button);
                        
            final Region targetRegion = region;
            
            button.clickListener = new ClickListener() {
                @Override
                public void clicked(int change) {
                    placeArmyRegionClicked(targetRegion, change);
                    GUI.this.requestFocusInWindow();
                }
            };
        }
        
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
        
        for (TriButton button : armyRegionButtons) {
            button.dispose();
        }
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
        armiesLeft -= change;
        
        if (info.armiesPlus > 0) {
            info.setText(info.getArmies() + "+" + info.armiesPlus);
            info.setHighlight(true);
        } else {
            info.setText(String.valueOf(info.getArmies()));
            info.setHighlight(false);
        }
        
        actionTxt.setText(botName(game.me()) + ": place " + armiesLeft +
                (armiesLeft == 1 ? " army" : " armies"));
        
        placeArmiesFinishedButton.setVisible(armiesLeft == 0);
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
    
    private Team moving = null;
    private Map<Integer, Move> moves;  // maps encoded (fromId, toId) to Move
    private Region moveFrom;    
    
    private CountDownLatch moveArmiesAction;
    private Button moveArmiesFinishedButton;
        
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
        
        actionTxt.setText(botName(game.me()) + ": move armies");
            
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
    
    public void setPlayerNames(String player1Name, String player2Name) {
        botName1 = player1Name;
        p1.setNameLabel(player1Name);
        
        botName2 = player2Name;
        p2.setNameLabel(player2Name);
    }
} 
