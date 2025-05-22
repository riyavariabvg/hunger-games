import javax.swing.*;
import java.awt.*;

public class AdventureGUI {
    private JFrame frame;
    private JTextArea outputArea;
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

        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> handleInput());
        inputField.addActionListener(e -> handleInput());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        printText(game.getCurrentRoom().getLongDescription());
        updateRoomDisplay();
    }

    private void handleInput() {
        String input = inputField.getText().trim();
        inputField.setText("");
        if (!input.isEmpty()) {
            printText("> " + input);
            printText(game.processCommand(input));
            updateRoomDisplay();
        }
    }

    private void printText(String text) {
        outputArea.append(text + "\n");
    }

    private void updateRoomDisplay() {
        String roomId = game.getPlayer().getCurrentRoomId();
        ImageIcon icon = new ImageIcon("images/" + roomId + ".png");
        Image img = icon.getImage().getScaledInstance(800, 200, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
    }
}