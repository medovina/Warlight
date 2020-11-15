package view;

import java.awt.*;

import game.*;

public class RegionInfo {
    private int diam;
    private String txt;
    private Region region;
    private Team team;
    private Color highlight;

    private int armies = 0;
    public int armiesPlus = 0;

    public static final Color
        Gray = new Color(180, 180, 180),
        Green = new Color(70, 189, 123);
    
    public RegionInfo() {
        init(30, Team.NEUTRAL);
    }
    
    private void init(int diam, Team team) {
        this.diam = diam;
        this.team = team;
    }
    
    public void setText(String s) {
        txt = s;
    }
    
    public void setHighlight(Color c) {
        this.highlight = c;
    }
        
    public void setHighlight(boolean state) {
        setHighlight(state ? Color.WHITE : null);
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public void setRegion(Region region) {
        this.region = region;
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
    
    protected void draw(Graphics g, int x, int y) {
        int width = 40; 
        
        if (highlight != null) {
            int thickness = 3;
            
            g.setColor(highlight);
            g.fillOval(x + width/2 - diam/2 - thickness, y + 4 - thickness,
                       this.diam + thickness * 2, this.diam + thickness * 2);
            g.setColor(TeamView.getHighlightColor(team));            
        } else
            g.setColor(TeamView.getColor(team));
        g.fillOval(x + width/2 - diam/2, y + 4, this.diam, this.diam);

        g.setColor(Color.BLACK);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(txt);
        g.drawString(txt, x + 20 - w / 2, y + 24);
    }
    
    public Team getTeam() {
        return team;
    }
}
