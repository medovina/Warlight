package view;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import utils.Util;

class Arrow extends JPanel {
    int from_x, from_y, to_x, to_y;
    Color color = Color.BLACK;
    int number;
    
    private static final long serialVersionUID = 1L;
    
    public Arrow(int from_x, int from_y, int to_x, int to_y) {
        setOpaque(false);
        setBounds(from_x, from_y, to_x, to_y);
    }
    
    public void setFromTo(int from_x, int from_y, int to_x, int to_y) {
        double len = Math.sqrt(Math.pow(from_x - to_x, 2) + Math.pow(from_y - to_y, 2));
        double from_reduce = (len - 15) / len;
        double to_reduce = (len - 10) / len;
        
        this.from_x = (int) (from_x * from_reduce + to_x * (1 - from_reduce));
        this.from_y = (int) (from_y * from_reduce + to_y * (1 - from_reduce));
        this.to_x = (int) (to_x * to_reduce + from_x * (1 - to_reduce));
        this.to_y = (int) (to_y * to_reduce + from_y * (1 - to_reduce));
        
        repaint();
    }
    
    public void setColor(Color color) {
        this.color = color;
        repaint();
    }
    
    public void setNumber(int number) {
        this.number = number;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        Util.renderNicely(g);

        int len = (int) Math.sqrt(Math.pow(from_x - to_x, 2) + Math.pow(from_y - to_y, 2));
        Polygon arrow = new Polygon();
        arrow.addPoint(0, -5);
        arrow.addPoint(len - 12, -5);
        arrow.addPoint(len - 12, -10);
        arrow.addPoint(len, 0);
        arrow.addPoint(len - 12, 10);
        arrow.addPoint(len - 12, 5);
        arrow.addPoint(0, 5);
        
        AffineTransform save = g.getTransform();
        g.translate(from_x, from_y);
        double angle = Math.atan2(to_y - from_y, to_x - from_x);
        g.rotate(angle);

        g.setColor(color);
        g.fill(arrow);
        g.setColor(Color.BLACK);
        g.draw(arrow);

        g.setTransform(save);
        
        if (number > 0) {
            g.setColor(Color.BLACK);
            Font font = new Font("default", Font.BOLD, 18);
            g.setFont(font);
            FontMetrics m = g.getFontMetrics(font);
            String text = Integer.toString(number);
            int dx = m.stringWidth(text) / 2;
            int dy = m.getAscent() / 2;
            
            GlyphVector v = font.createGlyphVector(g.getFontRenderContext(), text);
            Shape shape = v.getOutline();
            
            g.translate((from_x + to_x) / 2 - dx, (from_y + to_y) / 2 + dy);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.draw(shape);
            g.setColor(Color.BLACK);
            g.fill(shape);
        }
    }
}
