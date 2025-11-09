import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Kelas UI kustom untuk tombol oval.
 * Diambil dari inner class.
 */
public class RoundedButton extends JButton {
    private Color buttonColor;

    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.buttonColor = bgColor;
        setForeground(Color.WHITE); 
      
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false); 
        setPreferredSize(new Dimension(150, 45));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     
        g2.setColor(buttonColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 45, 45)); 

      
        super.paintComponent(g2);
        g2.dispose();
    }
}