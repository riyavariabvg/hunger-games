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
    private JLabel medicineLabel;
    private JLabel challengesLabel;
    private JLabel totalChallengesLabel;
    private JLabel sectionLabel;
    private JLabel restartLabel;
    private JScrollPane scrollPane;
    private SpinnerPanel spinnerPanel;
    private ImageIcon icon;
    
    // Inventory panel components
    private JPanel inventoryPanel;
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryListModel;
    
    // Store references to main layout components for restart functionality
    private JPanel centerPanel;
    private JPanel rightPanel;

    public AdventureGUI() {
        // Create spinner panel first
        spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
        // Pass spinner panel to game
        game = new Game(spinnerPanel, this);
        initializeGUI();
        displayMessage(game.getStartMessage());
        updateStatusLabels();
        updateInventoryDisplay();
    }

    private void initializeGUI() {
        setTitle("Welcome to the Hunger Games!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 700);
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
        
        medicineLabel = new JLabel("Medicine: 0");
        medicineLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        medicineLabel.setForeground(Color.BLUE);
        
        challengesLabel = new JLabel("Challenges: 0/3");
        challengesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        totalChallengesLabel = new JLabel("Total Challenges Completed: 0/39");
        totalChallengesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        totalChallengesLabel.setForeground(new Color(128, 0, 128));
        
        sectionLabel = new JLabel("Section: 1");
        sectionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        sectionLabel.setForeground(new Color(0, 128, 0));
        
        restartLabel = new JLabel("Type 'restart' to restart");
        restartLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        restartLabel.setForeground(Color.GRAY);

        statusPanel.add(healthLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(medicineLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(challengesLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(totalChallengesLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(sectionLabel);
        statusPanel.add(Box.createHorizontalStrut(30));
        statusPanel.add(restartLabel);

        // Create a panel to hold both the game output and right side panels
        centerPanel = new JPanel(new BorderLayout());
        setupGameContent();

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
    
    private void setupGameContent() {
        // Clear the center panel completely
        centerPanel.removeAll();
        
        // Force the panel to update its layout immediately
        centerPanel.revalidate();
        centerPanel.repaint();
        
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
        rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        // Add hunger games logo - make it smaller
        ImageIcon icon = new ImageIcon("src/images/HungerGamesLogo.png");
        Image scaledImage = icon.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create a panel for the spinner with flexible sizing
        JPanel spinnerContainer = new JPanel(new BorderLayout());
        spinnerContainer.setBorder(BorderFactory.createTitledBorder("Game Spinner"));
        
        // Add the spinner directly without size constraints
        spinnerPanel.setPreferredSize(new Dimension(270, 270));
        
        // Add the logo above spinner in spinnerContainer
        spinnerContainer.add(logoLabel, BorderLayout.NORTH);
        spinnerContainer.add(spinnerPanel, BorderLayout.CENTER);

        // Add instruction label - make it smaller
        JLabel instructionLabel = new JLabel(
                "<html><center>Type 'clockwise' or<br>'counterclockwise'<br>to rotate the spinner</center></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 9));
        spinnerContainer.add(instructionLabel, BorderLayout.SOUTH);

        // Create inventory panel
        createInventoryPanel();

        // Add both spinner and inventory to right panel with proper sizing
        rightPanel.add(spinnerContainer, BorderLayout.NORTH);
        rightPanel.add(inventoryPanel, BorderLayout.CENTER);

        // Add the right panel to the center panel
        centerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Force complete refresh of the display
        centerPanel.revalidate();
        centerPanel.repaint();
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
        inventoryScrollPane.setPreferredSize(new Dimension(250, 120));
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
            countLabel.setForeground(new Color(204, 102, 0));
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
        
        // Get the current room's challenge count
        int currentRoomChallenges = getCurrentRoomChallengesFromGame();
        challengesLabel.setText("Challenges: " + currentRoomChallenges + "/3");
        
        // Update total challenges label
        int totalCompleted = game.getTotalChallengesCompleted();
        int totalPossible = game.getTotalPossibleChallenges();
        totalChallengesLabel.setText("Total Challenges Completed: " + totalCompleted + "/" + totalPossible);
        
        // Color code total challenges label based on completion
        if (totalCompleted == 0) {
            totalChallengesLabel.setForeground(new Color(128, 0, 128));
        } else if (totalCompleted < totalPossible / 3) {
            totalChallengesLabel.setForeground(new Color(153, 51, 153));
        } else if (totalCompleted < 2 * (totalPossible / 3)) {
            totalChallengesLabel.setForeground(new Color(178, 102, 178));
        } else if (totalCompleted < totalPossible) {
            totalChallengesLabel.setForeground(new Color(102, 0, 153));
        } else {
            totalChallengesLabel.setForeground(new Color(102, 0, 204));
        }
        
        // Update section label
        updateSectionLabel();

        // change health label color based on health level
        if (player.getHealth() <= 0) {
            healthLabel.setForeground(Color.RED);
            healthLabel.setText("Health: DEAD");
        } else if (player.getHealth() <= 25) {
            healthLabel.setForeground(Color.RED);
        } else if (player.getHealth() <= 50) {
            healthLabel.setForeground(new Color(204, 102, 0));
        } else {
            healthLabel.setForeground(Color.BLACK);
        }
        
        // Color medicine label based on amount
        int medicineCount = game.getMedicineCount();
        if (medicineCount <= 0) {
            medicineLabel.setForeground(Color.RED);
        } else if (medicineCount <= 10) {
            medicineLabel.setForeground(new Color(204, 102, 0));
        } else {
            medicineLabel.setForeground(Color.BLUE);
        }
        
        // Update inventory display whenever status is updated
        updateInventoryDisplay();
    }
    
    private void updateSectionLabel() {
        Player player = game.getPlayer();
        String currentRoomId = player.getCurrentRoom();
        
        String sectionNumber = "0";
        int totalSections = 12;
        
        if (currentRoomId != null) {
            if (currentRoomId.equals("Middle")) {
                sectionNumber = "0";
            } else if (currentRoomId.startsWith("Section")) {
                try {
                    sectionNumber = currentRoomId.substring("Section".length());
                } catch (StringIndexOutOfBoundsException e) {
                    sectionNumber = "0";
                }
            }
        }
        
        sectionLabel.setText("Section: " + sectionNumber + "/" + totalSections);
        
        // Color coding based on progress
        try {
            int currentSection = Integer.parseInt(sectionNumber);
            if (currentSection == 0) {
                sectionLabel.setForeground(new Color(128, 128, 128));
            } else if (currentSection == 1) {
                sectionLabel.setForeground(new Color(0, 128, 0));
            } else if (currentSection >= totalSections) {
                sectionLabel.setForeground(new Color(255, 215, 0));
            } else {
                sectionLabel.setForeground(new Color(0, 100, 200));
            }
        } catch (NumberFormatException e) {
            sectionLabel.setForeground(new Color(128, 128, 128));
        }
    }

    private int getTotalSectionCount() {
        return 12;
    }
    
    private int getCurrentRoomChallengesFromGame() {
        return game.getCurrentRoomChallengesCompleted();
    }
    
    public void updateMedicineDisplay(int medicineCount) {
        medicineLabel.setText("Medicine: " + medicineCount);
        
        if (medicineCount <= 0) {
            medicineLabel.setForeground(Color.RED);
        } else if (medicineCount <= 10) {
            medicineLabel.setForeground(new Color(204, 102, 0));
        } else {
            medicineLabel.setForeground(Color.BLUE);
        }
    }
    
    // Fixed restart method
    public void resetToGameState() {
        try {
            // Completely recreate the Game object with the existing spinner panel
            game = new Game(spinnerPanel, this);
            
            // Clear the center panel
            centerPanel.removeAll();
            
            // Set up game content again
            setupGameContent();
            
            // Update the text area with the start message
            if (outputArea != null) {
                outputArea.setText("");
                displayMessage(game.getStartMessage());
            }
            
            // Update all status displays
            updateStatusLabels();
            
            // Restore focus to input field
            inputField.setText("");
            inputField.requestFocus();
            
            // Force layout updates
            centerPanel.revalidate();
            centerPanel.repaint();
            this.revalidate();
            this.repaint();
            
        } catch (Exception e) {
            // If there's an error during restart, show it and try a simpler approach
            System.err.println("Error during restart: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback restart approach
            game.restart();
            outputArea.setText("");
            displayMessage(game.getStartMessage());
            updateStatusLabels();
        }
    }
    
    private void processInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            displayMessage("> " + input);
            
            // Special handling for restart command
            if (input.equalsIgnoreCase("restart")) {
                inputField.setText("");
                resetToGameState();
                return;
            }
            
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

    public void gameOver() {
        // Remove the existing center content
        centerPanel.removeAll();
        
        // Force immediate layout update
        centerPanel.revalidate();
        centerPanel.repaint();
        
        // Load and scale the Game Over image to fit the center area
        ImageIcon gameOverIcon = new ImageIcon("src/images/GameOver.png");
        
        // Get the size of the center panel for proper scaling
        Dimension centerSize = centerPanel.getSize();
        if (centerSize.width == 0 || centerSize.height == 0) {
            centerSize = new Dimension(950, 500);
        }
        
        Image image = gameOverIcon.getImage().getScaledInstance(
            centerSize.width, centerSize.height, Image.SCALE_SMOOTH);
        gameOverIcon = new ImageIcon(image);

        // Create a label with the image
        JLabel gameOverLabel = new JLabel(gameOverIcon);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add the game over image to the center panel
        centerPanel.add(gameOverLabel, BorderLayout.CENTER);

        // Refresh the display
        centerPanel.revalidate();
        centerPanel.repaint();
        
        // Keep the input field active so player can type 'restart'
        inputField.requestFocus();
    }

    public void victory() {
        // Remove the existing center content
        centerPanel.removeAll();
        
        // Force immediate layout update
        centerPanel.revalidate();
        centerPanel.repaint();
        
        // Load and scale the Victory image to fit the center area
        ImageIcon victoryIcon = new ImageIcon("src/images/Victory.png");
        
        // Get the size of the center panel for proper scaling
        Dimension centerSize = centerPanel.getSize();
        if (centerSize.width == 0 || centerSize.height == 0) {
            centerSize = new Dimension(950, 500);
        }
        
        Image image = victoryIcon.getImage().getScaledInstance(
            centerSize.width, centerSize.height, Image.SCALE_SMOOTH);
        victoryIcon = new ImageIcon(image);

        // Create a label with the image
        JLabel victoryLabel = new JLabel(victoryIcon);
        victoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        victoryLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add the victory image to the center panel
        centerPanel.add(victoryLabel, BorderLayout.CENTER);

        // Refresh the display
        centerPanel.revalidate();
        centerPanel.repaint();
        
        // Keep the input field active
        inputField.requestFocus();
    }
}