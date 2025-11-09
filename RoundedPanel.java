import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 25; 

    public RoundedPanel(Color bgColor) {
        this.backgroundColor = bgColor;
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        g2.dispose();
    }
}