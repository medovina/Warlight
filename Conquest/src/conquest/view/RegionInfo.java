package conquest.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import conquest.game.Team;
import conquest.game.world.WorldRegion;

public class RegionInfo extends JPanel implements MouseListener {
    private GUI gui;
    private int diam;
    private JLabel txt;
    private JLabel name;
    private WorldRegion region;
    private int armies = 0;
    private Team team;
    private Color highlight;

    public int armiesPlus = 0;

    private static final long serialVersionUID = 1L;
    
    public static final Color
        Gray = new Color(180, 180, 180),
        Green = new Color(70, 189, 123);
    
    public RegionInfo(GUI gui) {
        this.gui = gui;
        init(30, Team.NEUTRAL);
    }
    
    private void init(int diam, Team team) {
        this.setTeam(team);
        
        this.setOpaque(false);
        this.setBounds(0,0, 100, diam < 30 ? 34 : diam+8);
        
        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(layout);
        
        //Region name
        this.name = new JLabel("PLR", JLabel.CENTER);
        this.name.setSize(100, 15);
        this.name.setPreferredSize(this.name.getSize());
        this.name.setOpaque(false);
        this.name.setFont(new Font("default", 0, 12));
        this.name.setAlignmentX(0.5f);
        this.name.setForeground(Color.BLACK);
        this.add(this.name);
        
        //Text
        this.txt = new JLabel("2", JLabel.CENTER);
        this.txt.setSize(100, 15);
        this.txt.setPreferredSize(this.txt.getSize());
        this.txt.setOpaque(false);
        this.txt.setFont(new Font("default", 0, 12));
        this.txt.setAlignmentX(0.5f);
        this.txt.setForeground(Color.BLACK);
        this.add(this.txt);        
        
        //Circle
        this.diam = diam;
        
        addMouseListener(this);
    }
    
    public void setNameLabel(String s) {
        this.name.setText(s);
    }
    
    public void setText(String s) {
        this.txt.setText(s);            
        this.revalidate();
        this.repaint();
    }
    
    public void setHighlight(Color c) {
        if (this.highlight != c) {
            this.highlight = c;
            this.revalidate();
            this.repaint();
        }
    }
        
    public void setHighlight(boolean state) {
        setHighlight(state ? Color.WHITE : null);
    }
    
    public void setTeam(Team team) {
        this.team = team;
        if (this.txt != null) {
            this.txt.setForeground(Color.BLACK);
        }
        if (this.name != null) {
            this.name.setForeground(Color.BLACK);
        }
        this.revalidate();
        this.repaint();
    }
    
    public void drawName() {
        this.name.setText(gui.showIds ? region.id + ":" + region.mapName : region.mapName);
    }
    
    public void setRegion(WorldRegion region) {
        this.region = region;
        drawName();
        this.revalidate();
        this.repaint();
    }
    
    public WorldRegion getRegion() {
        return region;
    }
    
    public int getArmies() {
        return armies;
    }
    
    public void setArmies(int armies) {
        this.armies = armies;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = this.getBounds().width; 
        
        if (highlight != null) {
            int thickness = 3;
            
            g.setColor(highlight);
            g.fillOval(width/2 - diam/2 - thickness, 4 - thickness, this.diam + thickness * 2, this.diam + thickness * 2);
            g.setColor(TeamView.getHighlightColor(team));            
        } else
            g.setColor(TeamView.getColor(team));
        g.fillOval(width/2 - diam/2, 4, this.diam, this.diam);
    }
    
    public Team getTeam() {
        return team;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gui.regionClicked(this, e.getButton() == MouseEvent.BUTTON1);
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {    }
    
}
