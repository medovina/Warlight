package conquest.view;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

class Arrow extends JPanel {
    int from_x, from_y, to_x, to_y;
    Color color = Color.BLACK;
    int number;
    
    Polygon arrowHead;

    private static final long serialVersionUID = 1L;
    
    public Arrow(int from_x, int from_y, int to_x, int to_y) {
        setOpaque(false);
        setBounds(from_x, from_y, to_x, to_y);
        
        arrowHead = new Polygon();  
        arrowHead.addPoint( 0, 6);
        arrowHead.addPoint( -8, -6);
        arrowHead.addPoint( 8, -6);        
    }
    
    public void setFromTo(int from_x, int from_y, int to_x, int to_y) {
        double len = Math.sqrt(Math.pow(from_x - to_x, 2) + Math.pow(from_y - to_y, 2));
        double from_reduce = (len - 23) / len;
        double to_reduce = (len - 25) / len;
        
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

        g.setColor(color);
        g.setStroke(new BasicStroke(6));
        g.drawLine(from_x, from_y, to_x, to_y);
        
        double angle = Math.atan2(to_y - from_y, to_x - from_x);
        
        AffineTransform save = g.getTransform();
        g.translate(to_x, to_y);
        g.rotate(angle - Math.PI / 2);
        g.fill(arrowHead);
        g.setTransform(save);
        
        if (number > 0) {
            g.setColor(Color.BLACK);
            Font font = new Font("default", Font.BOLD, 20);
            g.setFont(font);
            FontMetrics m = g.getFontMetrics(font);
            String text = Integer.toString(number);
            int dx = m.stringWidth(text) / 2;
            int dy = m.getAscent() / 2;
            
            GlyphVector v = font.createGlyphVector(g.getFontRenderContext(), text);
            Shape shape = v.getOutline();
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            g.translate((from_x + to_x) / 2 - dx, (from_y + to_y) / 2 + dy);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.draw(shape);
            g.setColor(Color.BLACK);
            g.fill(shape);
        }
    }
}
