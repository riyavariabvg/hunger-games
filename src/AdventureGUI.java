import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AdventureGUI extends JFrame implements ActionListener {
    private Game game;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel healthLabel;
    private JLabel challengesLabel;
    private JScrollPane scrollPane;
    
    public AdventureGUI() {
        game = new Game();
        initializeGUI();
        displayMessage(game.getStartMessage());
        updateStatusLabels();
    }
    
    private void initializeGUI() {
        setTitle("Welcome to the Hunger Games!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // create status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.LIGHT_GRAY);
        statusPanel.setBorder(BorderFactory.createTitledBorder("Player Status"));
        
        healthLabel = new JLabel("Health: 100/100");
        healthLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        challengesLabel = new JLabel("Challenges: 0/3");
        challengesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        statusPanel.add(healthLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(challengesLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(new JLabel("| Inventory: Type 'inventory' to view items"));
        
        // create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setCaretColor(Color.GREEN);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Output"));
        
        // create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Command Input"));
        
        inputField = new JTextField();
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // chat gpt helped with this review what it does
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processInput();
                }
            }
        });
        
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        
        inputPanel.add(new JLabel("Enter command: "), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        
        // add components to main panel
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // add main panel to frame
        add(mainPanel);
        
        // set focus to input field
        inputField.requestFocus();
        
        setVisible(true);
    }
    
    private void displayMessage(String message) {
        outputArea.append(message + "\n\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    private void updateStatusLabels() {
        Player player = game.getPlayer();
        healthLabel.setText("Health: " + player.getHealth() + "/100");
        challengesLabel.setText("Challenges: " + player.getChallengesCompleted() + "/3");
        
        // change health label color based on health level
        if (player.getHealth() <= 0) {
            healthLabel.setForeground(Color.RED);
            healthLabel.setText("Health: DEAD");
        } else if (player.getHealth() <= 25) {
            healthLabel.setForeground(Color.RED);
        } else if (player.getHealth() <= 50) {
            healthLabel.setForeground(Color.ORANGE);
        } else {
            healthLabel.setForeground(Color.BLACK);
        }
    }
    
    private void processInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            displayMessage("> " + input);
            String response = game.processCommand(input);
            displayMessage(response);
            updateStatusLabels();
            inputField.setText("");
        }
        inputField.requestFocus();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            processInput();
        }
    }
}