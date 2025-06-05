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

    private double angle = 0; 

    private Timer timer;
    private double targetAngle = 0;       // how far we want to rotate in total this animation
    private double rotationStep = 0;      // how much to rotate per timer tick
    private double rotatedSoFar = 0;      // how much we have rotated in this animation so far

    public SpinnerPanel(String imagePath) {
        try {
            spinnerImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Error loading spinner image: " + e.getMessage());
        }

        timer = new Timer(15, e -> {
            // rotate by a small step
            angle += rotationStep;
            rotatedSoFar += Math.abs(rotationStep);

            repaint();

            // check if we've rotated enough
            if (rotatedSoFar >= Math.abs(targetAngle)) {
                // correct any small overshoot
                angle = angle - (rotatedSoFar - Math.abs(targetAngle)) * Math.signum(rotationStep);

                // stop animation
                timer.stop();
                rotatedSoFar = 0;
                targetAngle = 0;
                rotationStep = 0;
            }
        });
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
        g2d.drawImage(spinnerImage, -displayWidth / 2, -displayHeight / 2,
                displayWidth, displayHeight, this);

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(displayWidth, displayHeight);
    }

    public void rotateClockwise() {
        if (timer.isRunning()) return; // ignore if already animating
        targetAngle = Math.toRadians(30);  
        rotationStep = Math.toRadians(2);  
        rotatedSoFar = 0;
        timer.start();
    }

    public void rotateCounterclockwise() {
        if (timer.isRunning()) return; 
        targetAngle = Math.toRadians(-30);  
        rotationStep = Math.toRadians(-2);  
        rotatedSoFar = 0;
        timer.start();
    }
}
