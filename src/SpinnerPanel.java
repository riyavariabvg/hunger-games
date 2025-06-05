import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SpinnerPanel extends JPanel {
    private BufferedImage spinnerImage;
    private int displayWidth = 300;  
    private int displayHeight = 300;

  private double angle = 0; // Current rotation in radians

    public SpinnerPanel(String imagePath) {

        try {
            spinnerImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Error loading spinner image: " + e.getMessage());
            
        }

        
    }

  
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (spinnerImage == null) return;

    Graphics2D g2d = (Graphics2D) g.create();

    int centerX = displayWidth / 2;
    int centerY = displayHeight / 2;

    g2d.translate(centerX, centerY);
    g2d.rotate(angle);
    // Use displayWidth and displayHeight for both offset and size
    g2d.drawImage(spinnerImage, -displayWidth / 2, -displayHeight / 2,
                  displayWidth, displayHeight, this);
   
                  
    g2d.dispose();

     
}

    

    @Override
    public Dimension getPreferredSize() {
        
        return new Dimension(displayWidth, displayHeight);
    }

    // Rotate 30 degrees clockwise
    public void rotateClockwise() {
        angle += Math.toRadians(30);
        repaint();
    }

    // Rotate 30 degrees counterclockwise
    public void rotateCounterclockwise() {
        angle -= Math.toRadians(30);
        
        repaint();

        
    }
}
