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
    private SpinnerPanel spinnerPanel; // Add this field
    private ImageIcon icon;

    // = new ImageIcon("src/images/HungerGamesLogo.png");
    // private JLabel imageLabel = new JLabel(icon);

    public AdventureGUI() {
        // Create spinner panel first
        spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
        // Pass spinner panel to game
        game = new Game(spinnerPanel);
        initializeGUI();
        displayMessage(game.getStartMessage());
        updateStatusLabels();
    }

    private void initializeGUI() {
        setTitle("Welcome to the Hunger Games!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Made wider to accommodate spinner
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

        // Create a panel to hold both the game output and spinner
        JPanel centerPanel = new JPanel(new BorderLayout());

        // create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setBackground(new Color(55, 79, 47));
        outputArea.setForeground(new Color(230, 230, 230));
        outputArea.setCaretColor(Color.GREEN);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Output"));

        // Add the game output to the center
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Add hunger games logo
        // Load and resize the logo
        ImageIcon icon = new ImageIcon("src/images/HungerGamesLogo.png");
        Image scaledImage = icon.getImage().getScaledInstance(180, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(resizedIcon);

        // Create a panel for the spinner
        JPanel spinnerContainer = new JPanel(new BorderLayout());
        spinnerContainer.setBorder(BorderFactory.createTitledBorder("Game Spinner"));
        spinnerContainer.add(spinnerPanel, BorderLayout.CENTER);

        // Add the logo below spinner in spinnerContainer
        spinnerContainer.add(logoLabel, BorderLayout.NORTH);

        // Add instruction label
        JLabel instructionLabel = new JLabel(
                "<html><center>Type 'clockwise' or<br>'counterclockwise'<br>to rotate the spinner</center></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        spinnerContainer.add(instructionLabel, BorderLayout.SOUTH);

        // Add the spinner panel to the right side
        centerPanel.add(spinnerContainer, BorderLayout.EAST);

        // create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Command Input"));

        inputField = new JTextField();
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Add key listener for Enter key
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
        mainPanel.add(centerPanel, BorderLayout.CENTER);
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