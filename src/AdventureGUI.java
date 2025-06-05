import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AdventureGUI extends JFrame implements ActionListener {
    private Game game;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel healthLabel;
    private JLabel medicineLabel; // Add medicine label
    private JLabel challengesLabel;
    private JScrollPane scrollPane;
    private SpinnerPanel spinnerPanel; // Add this field
    private ImageIcon icon;
    
    // Inventory panel components
    private JPanel inventoryPanel;
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryListModel;

    // = new ImageIcon("src/images/HungerGamesLogo.png");
    // private JLabel imageLabel = new JLabel(icon);

    public AdventureGUI() {
        // Create spinner panel first
        spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
        // Pass spinner panel to game
        game = new Game(spinnerPanel, this);
        initializeGUI();
        displayMessage(game.getStartMessage());
        updateStatusLabels();
        updateInventoryDisplay(); // Initialize inventory display
    }

    private void initializeGUI() {
        setTitle("Welcome to the Hunger Games!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 700); // Made even wider to accommodate larger right panel
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
        
        medicineLabel = new JLabel("Medicine: 0"); // Add medicine label
        medicineLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        medicineLabel.setForeground(Color.BLUE); // Blue color for medicine
        
        challengesLabel = new JLabel("Challenges: 0/3");
        challengesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        statusPanel.add(healthLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(medicineLabel); // Add medicine to status panel
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(challengesLabel);

        // Create a panel to hold both the game output and right side panels
        JPanel centerPanel = new JPanel(new BorderLayout());

        // create output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setBackground(new Color(34, 49, 29));
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

        // Create right side panel to hold both spinner and inventory
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(300, 0)); // Increased from 280 to 300

        // Add hunger games logo - make it smaller
        ImageIcon icon = new ImageIcon("src/images/HungerGamesLogo.png");
        Image scaledImage = icon.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH); // Reduced from 180x100
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create a panel for the spinner with flexible sizing
        JPanel spinnerContainer = new JPanel(new BorderLayout());
        spinnerContainer.setBorder(BorderFactory.createTitledBorder("Game Spinner"));
        
        // Add the spinner directly without size constraints
        spinnerPanel.setPreferredSize(new Dimension(270, 270)); // Increased from 250 to 270
        
        // Add the logo above spinner in spinnerContainer
        spinnerContainer.add(logoLabel, BorderLayout.NORTH);
        spinnerContainer.add(spinnerPanel, BorderLayout.CENTER);

        // Add instruction label - make it smaller
        JLabel instructionLabel = new JLabel(
                "<html><center>Type 'clockwise' or<br>'counterclockwise'<br>to rotate the spinner</center></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 9)); // Smaller font
        spinnerContainer.add(instructionLabel, BorderLayout.SOUTH);

        // Create inventory panel
        createInventoryPanel();

        // Add both spinner and inventory to right panel with proper sizing
        rightPanel.add(spinnerContainer, BorderLayout.NORTH);
        rightPanel.add(inventoryPanel, BorderLayout.CENTER);

        // Add the right panel to the center panel
        centerPanel.add(rightPanel, BorderLayout.EAST);

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

    private void createInventoryPanel() {
        inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
        
        // Create list model and JList for inventory
        inventoryListModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryListModel);
        inventoryList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        inventoryList.setBackground(new Color(245, 245, 245));
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add scroll pane for inventory list
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryList);
        inventoryScrollPane.setPreferredSize(new Dimension(250, 120)); // Reduced height slightly
        inventoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);
        
        // Add inventory count label
        JLabel inventoryCountLabel = new JLabel("Items: 0/5", SwingConstants.CENTER);
        inventoryCountLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        inventoryPanel.add(inventoryCountLabel, BorderLayout.SOUTH);
    }

    private void updateInventoryDisplay() {
        Player player = game.getPlayer();
        List<Item> items = player.getInventory();
        
        // Clear the current list
        inventoryListModel.clear();
        
        // Add all items to the list
        if (items.isEmpty()) {
            inventoryListModel.addElement("(No items)");
        } else {
            for (Item item : items) {
                inventoryListModel.addElement("• " + item.getName());
            }
        }
        
        // Update inventory count label
        JLabel countLabel = (JLabel) inventoryPanel.getComponent(1);
        countLabel.setText("Items: " + items.size() + "/5");
        
        // Change color based on inventory fullness
        if (items.size() >= 5) {
            countLabel.setForeground(Color.RED);
        } else if (items.size() >= 4) {
            countLabel.setForeground(new Color(204, 102, 0)); // Darker orange
        } else {
            countLabel.setForeground(Color.BLACK);
        }
    }

    private void displayMessage(String message) {
        outputArea.append(message + "\n\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void updateStatusLabels() {
        Player player = game.getPlayer();
        healthLabel.setText("Health: " + player.getHealth() + "/100");
        
        // Update medicine label
        medicineLabel.setText("Medicine: " + game.getMedicineCount());
        
        // FIX: Get the current room's challenge count from the Game class instead of Player
        int currentRoomChallenges = getCurrentRoomChallengesFromGame();
        challengesLabel.setText("Challenges: " + currentRoomChallenges + "/3");

        // change health label color based on health level
        if (player.getHealth() <= 0) {
            healthLabel.setForeground(Color.RED);
            healthLabel.setText("Health: DEAD");
        } else if (player.getHealth() <= 25) {
            healthLabel.setForeground(Color.RED);
        } else if (player.getHealth() <= 50) {
            healthLabel.setForeground(new Color(204, 102, 0)); // Darker orange
        } else {
            healthLabel.setForeground(Color.BLACK);
        }
        
        // Color medicine label based on amount
        int medicineCount = game.getMedicineCount();
        if (medicineCount <= 0) {
            medicineLabel.setForeground(Color.RED);
        } else if (medicineCount <= 10) {
            medicineLabel.setForeground(new Color(204, 102, 0)); // Darker orange
        } else {
            medicineLabel.setForeground(Color.BLUE);
        }
        
        // Update inventory display whenever status is updated
        updateInventoryDisplay();
    }
    
    private int getCurrentRoomChallengesFromGame() {
        // Access the Game's method to get current room challenge count
        return game.getCurrentRoomChallengesCompleted();
    }
    
    // Method to update medicine display (called from Game class)
    public void updateMedicineDisplay(int medicineCount) {
        medicineLabel.setText("Medicine: " + medicineCount);
        
        // Update color based on medicine count
        if (medicineCount <= 0) {
            medicineLabel.setForeground(Color.RED);
        } else if (medicineCount <= 10) {
            medicineLabel.setForeground(new Color(204, 102, 0)); // Darker orange
        } else {
            medicineLabel.setForeground(Color.BLUE);
        }
    }

    private void processInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            displayMessage("> " + input);
            String response = game.processCommand(input);
            displayMessage(response);
            updateStatusLabels(); // This will also update inventory display
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

    public void gameOver() {
        // Remove all components from the frame
        getContentPane().removeAll();
        repaint();

        // Load and scale the Game Over image
        ImageIcon gameOverIcon = new ImageIcon("src/images/GameOver.png");
        Image image = gameOverIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        gameOverIcon = new ImageIcon(image);

        // Create a label with the image
        JLabel gameOverLabel = new JLabel(gameOverIcon);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add it to the frame
        add(gameOverLabel);

        // Refresh the frame
        revalidate();
        repaint();
    }

    public void victory() {
        // Remove all components from the frame
        getContentPane().removeAll();
        repaint();

        // Load and scale the Victory image
        ImageIcon victoryIcon = new ImageIcon("src/images/Victory.png");
        Image image = victoryIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        victoryIcon = new ImageIcon(image);

        // Create a label with the image
        JLabel victoryLabel = new JLabel(victoryIcon);
        victoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        victoryLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add it to the frame
        add(victoryLabel);

        // Refresh the frame
        revalidate();
        repaint();
    }
}