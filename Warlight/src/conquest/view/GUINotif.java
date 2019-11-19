package conquest.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GUINotif extends JPanel {

    private static final long serialVersionUID = 1L;
    
    public static final int[] NOTIF_COLOR_RGB = new int[] { 240, 240, 240 };
    
    public static final float[] NOTIF_COLOR_HSB = Color.RGBtoHSB(NOTIF_COLOR_RGB[0], NOTIF_COLOR_RGB[1], NOTIF_COLOR_RGB[2], null);
    
    private JLabel txt;
    
    private Timer timer;
        
    public GUINotif(JLayeredPane host, int x, int y, int width, int height) {
        
        this.setOpaque(false);
        this.setBounds(x, y, width, height);
        
        this.txt = new JLabel("HI!", JLabel.CENTER);
        this.txt.setSize(width, height-11);
        this.txt.setPreferredSize(this.txt.getSize());
        this.txt.setOpaque(false);
        this.txt.setAlignmentX(0.5f);
        this.txt.setAlignmentY(0.5f);
        this.txt.setForeground(Color.BLACK);
        this.add(this.txt);
        
        host.add(this, JLayeredPane.PALETTE_LAYER);
        
        this.setVisible(false);
        
        timer = new Timer("GUINotif");
    }
    
    public void show(String msg, long timeoutMillis) {
        
        System.out.println("GUI --> " + msg);
        
        txt.setText(msg);
        this.revalidate();
        this.repaint();
        
        this.setVisible(true);
                
        timer.cancel();
        timer.purge();
        timer = new Timer("GUINotif");
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    GUINotif.this.setVisible(false);
                } catch (Exception e) {                    
                }
            }
            
        }, timeoutMillis);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);        
        g.setColor(Color.getHSBColor(NOTIF_COLOR_HSB[0], NOTIF_COLOR_HSB[1], NOTIF_COLOR_HSB[2]));
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    }
    
}
