package conquest.view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLayeredPane;

public class TriButton {
    
    private int change1 = 1;
    private int change2 = 5;
    private int change3 = 20;
    
    public static interface ClickListener {
        
        public void clicked(int change);
        
    }
    
    private class MyMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
        
    }
    
    private class Button1Listener extends MyMouseListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (clickListener != null) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickListener.clicked(change1);
                } else {
                    clickListener.clicked(-change1);
                }
            }
        }
        
    }
    
    private class Button2Listener extends MyMouseListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (clickListener != null) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickListener.clicked(change2);
                } else {
                    clickListener.clicked(-change2);
                }
            }
        }
        
    }

    private class Button3Listener extends MyMouseListener {
    
        @Override
        public void mouseClicked(MouseEvent e) {
            if (clickListener != null) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickListener.clicked(change3);
                } else {
                    clickListener.clicked(-change3);
                }
            }
        }
        
    }
    
    
    private Button button1;
    private Button button2;
    private Button button3;
    
    private Button1Listener listener1 = new Button1Listener();
    private Button2Listener listener2 = new Button2Listener();
    private Button3Listener listener3 = new Button3Listener();
    
    public ClickListener clickListener;
    
    private JLayeredPane host;
    
    public TriButton(JLayeredPane host, int x, int y, int change1, int change2, int change3) {
        this(host, x, y, change1, change2, change3, true);
    }
    
    public TriButton(JLayeredPane host, int x, int y, int change1, int change2, int change3, boolean visible) {
        this.change1 = change1;
        this.change2 = change2;
        this.change3 = change3;
        
        this.host = host;
        
        Font font = new Font("default", 0, 11);
        
        Button button;
        
        button = new Button("" + change1);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFont(font);
        button.setSize(20, 20);
        button.setLocation(x - 30, y + 10);
        
        button1 = button;
        
        button = new Button("" + change2);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFont(font);
        button.setSize(20, 20);
        button.setLocation(x - 10, y + 10);
        
        button2 = button;
        
        button = new Button("" + change3);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFont(font);
        button.setSize(20, 20);
        button.setLocation(x + 10, y + 10);
        
        button3 = button;
        
        button1.setVisible(visible);
        button2.setVisible(visible);
        button3.setVisible(visible);
                
        host.add(button1, JLayeredPane.MODAL_LAYER);
        host.add(button2, JLayeredPane.MODAL_LAYER);
        host.add(button3, JLayeredPane.MODAL_LAYER);
        
        button1.addMouseListener(listener1);
        button2.addMouseListener(listener2);
        button3.addMouseListener(listener3);
    }

    public void setVisible(boolean state) {
        button1.setVisible(state);
        button2.setVisible(state);
        button3.setVisible(state);
    }
    
    public void dispose() {
        clickListener = null;
        
        button1.removeMouseListener(listener1);
        button2.removeMouseListener(listener2);
        button3.removeMouseListener(listener3);
        
        host.remove(button1);
        host.remove(button2);
        host.remove(button3);
    }

}
