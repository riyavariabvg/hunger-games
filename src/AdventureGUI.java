import javax.swing.*;
import java.awt.*;

public class AdventureGUI {
    private JFrame frame;
    private static JTextArea outputArea;
    private JTextField inputField;
    private JLabel imageLabel;
    private Game game;

    public AdventureGUI(Game game) {
        this.game = game;
        buildGUI();
    }

    private void buildGUI() {
        frame = new JFrame("Text Adventure Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());


        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(imageLabel, BorderLayout.NORTH);

        SpinnerPanel spinner = new SpinnerPanel("src/images/spinner.png");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(spinner, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        frame.add(centerPanel, BorderLayout.CENTER);

        
        outputArea.setBackground(new Color(100, 80, 100));

        

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> handleInput());
        inputField.addActionListener(e -> handleInput());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true); 
        printText("Welcome to the 75th Annual Hunger Games.");
        int x = (int)(Math.random()*8)+5;
        int y = (int)(Math.random()*2)+1;
        String gender = "";
            if (y==1) {
                gender = "female";
            } else {
                gender = "male";
            }
        printText ("You have been reaped as the " + gender + " tribute from District " + x);
        printText ("Your goal is to complete the challenges and stay alive. You have "  + game.getPlayer().getLives() + " lives");
        printText ("May the odds be ever in your favour!");
        printText(game.getCurrentRoom().getLongDescription());
        printChallengePrompt(game.getCurrentRoom().getChallenge());

        updateRoomDisplay();
    }

    private void handleInput() {
        String input = inputField.getText().trim();
        inputField.setText("");
        if (!input.isEmpty()) {
            printText("> " + input);
            printText(game.processCommand(input));
            printChallengePrompt(game.getCurrentRoom().getChallenge());
            updateRoomDisplay();

        }
    }

    public static void printText(String text) {
        outputArea.append(text + "\n");
    }

    private void updateRoomDisplay() {
        String roomId = game.getPlayer().getCurrentRoomId();
        ImageIcon icon = new ImageIcon("images/" + roomId + ".png");
        Image img = icon.getImage().getScaledInstance(800, 200, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img)); 
    }
    public void printChallengePrompt(Challenge challenge) {
        printText(challenge.getPrompt());
    }
}
