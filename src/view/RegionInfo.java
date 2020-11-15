package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import game.*;

public class RegionInfo extends JPanel implements MouseListener {
    private GUI gui;
    private int diam;
    private String txt;
    private Region region;
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
        this.setBounds(0,0, 40, diam < 30 ? 34 : diam+8);
                
        //Circle
        this.diam = diam;
        
        addMouseListener(this);
    }
    
    public void setText(String s) {
        txt = s;
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
        this.revalidate();
        this.repaint();
    }
    
    public void setRegion(Region region) {
        this.region = region;
        this.revalidate();
        this.repaint();
    }
    
    public Region getRegion() {
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

        g.setColor(Color.BLACK);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(txt);
        g.drawString(txt, 20 - w / 2, 24);
    }
    
    public Team getTeam() {
        return team;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gui.regionClicked(this, e.getButton() == MouseEvent.BUTTON1);
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {    }
    
}
