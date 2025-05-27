import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SpinnerPanel extends JPanel {
    private BufferedImage spinnerImage;
    private int displayWidth = 150;  
    private int displayHeight = 150;

    public SpinnerPanel(String imagePath) {
        try {
            spinnerImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Error loading spinner image: " + e.getMessage());
            
        }
    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (spinnerImage == null) return;

        
        g.drawImage(spinnerImage, 0, 0, displayWidth, displayHeight, this);
    }

    
    public Dimension getPreferredSize() {
        
        return new Dimension(displayWidth, displayHeight);
    }
}
