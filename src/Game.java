import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Game {
    private AdventureGUI gui;
    private Player player;
    private Map<String, Room> rooms;
    private List<Challenge> challenges;
    private Map<String, List<Item>> sectionInventories;
    private Challenge currentChallenge;
    private Random random;
    private SpinnerPanel spinnerPanel;
    private int medicineCount; // Add medicine count
    
    // Track challenges completed per room
    private Map<String, Integer> roomChallengeCompletions;
    
    // Track available challenges per room (room-specific challenges)
    private Map<String, List<Challenge>> roomChallenges;

    // Constructor that accepts a SpinnerPanel from the GUI
    public Game(SpinnerPanel spinnerPanel, AdventureGUI gui) {
        this.player = new Player();
        this.random = new Random();
        this.gui = gui;
        this.spinnerPanel = spinnerPanel;
        this.roomChallengeCompletions = new HashMap<>();
        this.roomChallenges = new HashMap<>();
        this.medicineCount = 0; // Start with 0 medicine
        loadGameData();
        initializeRoomChallenges();
        addMedicineVialToPlayer(); // Add medicine vial to starting inventory
    }
    

    // Default constructor for backward compatibility
    public Game() {
        this.player = new Player();
        this.random = new Random();
        this.roomChallengeCompletions = new HashMap<>();
        this.roomChallenges = new HashMap<>();
        this.medicineCount = 0; // Start with 0 medicine
        loadGameData();
        initializeRoomChallenges();
        addMedicineVialToPlayer(); // Add medicine vial to starting inventory
        // Only create spinner panel if one wasn't provided
        if (this.spinnerPanel == null) {
            this.spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
        }
    }
    
    public int getTotalChallengesCompleted() {
    int total = 0;
    for (Integer completed : roomChallengeCompletions.values()) {
        total += completed;
    }
    return total;
}

// Get total possible challenges across all rooms
public int getTotalPossibleChallenges() {
    int total = 0;
    for (List<Challenge> roomChallenge : roomChallenges.values()) {
        total += roomChallenge.size();
    }
    return total;
}

    private void addMedicineVialToPlayer() {
        // Create a medicine vial item and add it to player's inventory
        Item medicineVial = new Item("medicine_vial", "Medicine Vial", "A plastic container filled with medicine");
        player.addItem(medicineVial);
    }

    private void loadGameData() {
        this.rooms = RoomLoader.loadRooms("rooms.json");
        this.challenges = ChallengeLoader.loadChallenges("challenges.json");
        this.sectionInventories = InventoryLoader.loadInventories("inventory.json");
    }
    
    private void initializeRoomChallenges() {
        // Initialize challenge completion tracking for each room
        for (String roomId : rooms.keySet()) {
            roomChallengeCompletions.put(roomId, 0);
        }
        
        // Group challenges by room based on challenge ID prefixes
        // Assuming challenge IDs follow format like "Section1_A", "Section1_B", etc.
        for (Challenge challenge : challenges) {
            String challengeId = challenge.getId();
            String roomId = extractRoomIdFromChallenge(challengeId);
            
            roomChallenges.computeIfAbsent(roomId, k -> new ArrayList<>()).add(challenge);
        }
    }
    
    private String extractRoomIdFromChallenge(String challengeId) {
        // Extract room ID from challenge ID (e.g., "Section12_A" -> "Section12")
        if (challengeId.contains("_")) {
            return challengeId.substring(0, challengeId.lastIndexOf("_"));
        }
        return challengeId; // fallback
    }

    public String processCommand(String input) {
        // Check if player is dead
        if (player.getHealth() <= 0) {
            gui.gameOver();
            return "GAME OVER! Your health has reached 0. You have died in the adventure.\nType 'restart' to begin again.";
        }

        CommandParser.ParsedCommand command = CommandParser.parseCommand(input);
        String action = command.getAction();
        String target = command.getTarget();
        String fullCommand = command.getFullCommand();

        // Handle restart command
        if (action.equals("restart")) {
            restart();
            return "Game restarted!\n\n" + getStartMessage();
        }

        // Handle help command
        if (CommandParser.isHelpCommand(action)) {
            return getHelpText();
        }

        // Handle medicine use command
        if (action.equals("use") && target.contains("medicine")) {
            return handleMedicineUse(target);
        }

        // Handle health/status command
        if (CommandParser.isHealthCommand(action)) {
            int currentRoomChallenges = getCurrentRoomChallengesCompleted();
            return "Health: " + player.getHealth() + "/100\n" +
                    "Medicine: " + medicineCount + "\n" +
                    "Challenges completed in this room: " + currentRoomChallenges + "/3\n" +
                    player.getInventoryString();
        }

        // Handle inventory command
        if (CommandParser.isInventoryCommand(action)) {
            return player.getInventoryString();
        }

        // Handle look command
        if (CommandParser.isLookCommand(action)) {
            return getCurrentRoomDescription();
        }

        // Handle take command
        if (CommandParser.isTakeCommand(action)) {
            return handleTakeCommand(target);
        }

        if (CommandParser.isIgnoreCommand(action)) {
            return handleIgnoreCommand(target);
        }

        // Handle drop command
        if (CommandParser.isDropCommand(action)) {
            return handleDropCommand(target);
        }

        // Handle movement commands
        if (CommandParser.isMovementCommand(action)) {
            return handleMovementCommand(action);
        }

        // Check if it's a challenge command
        if (currentChallenge != null) {
            Challenge.Option option = currentChallenge.getOption(fullCommand);
            if (option != null) {
                // Check if player has required inventory items
                if (!hasRequiredInventory(option)) {
                    return "You don't have the required items to choose this option. Required: " + 
                           String.join(", ", option.getRequiredInventory());
                }
                
                return handleChallengeOption(option);
            }
        }

        // Check if command matches any challenge option from available challenges
        String challengeResult = tryProcessChallengeCommand(fullCommand);
        if (challengeResult != null) {
            return challengeResult;
        }

        return "I don't understand that command. Type 'help' for available commands.";
    }

    private String handleMedicineUse(String target) {
        // Parse the amount from commands like "use 5 medicine" or "5 medicine"
        String[] parts = target.split("\\s+");
        int amount = 0;
        
        for (String part : parts) {
            try {
                amount = Integer.parseInt(part);
                break;
            } catch (NumberFormatException e) {
                // Continue looking for a number
            }
        }
        
        if (amount <= 0) {
            return "Please specify how much medicine to use (e.g., 'use 5 medicine')";
        }
        
        if (medicineCount < amount) {
            return "You don't have enough medicine! You have " + medicineCount + " medicine available.";
        }
        
        if (player.getHealth() >= 100) {
            return "Your health is already at maximum (100/100). No need to use medicine.";
        }
        
        // Use the medicine
        medicineCount -= amount;
        int healthGained = Math.min(amount, 100 - player.getHealth()); // Don't exceed max health
        player.changeHealth(healthGained);
        
        return "You use " + amount + " medicine and restore " + healthGained + " health.\n" +
               "Health: " + player.getHealth() + "/100\n" +
               "Medicine remaining: " + medicineCount;
    }

    private String handleTakeCommand(String itemName) {
    if (itemName.isEmpty()) {
        return "Take what? Please specify an item.";
    }

    Room currentRoom = rooms.get(player.getCurrentRoom());
    if (currentRoom == null) {
        return "You are not in a valid room.";
    }

    Item item = currentRoom.getItem(itemName);
    if (item == null) {
        return "There is no '" + itemName + "' here to take.";
    }

    currentRoom.removeItem(itemName);

    // Check if item gives medicine
    String result = "";
    if ((item.getId().contains("medicine") || item.getId().contains("first"))&& !item.getId().equals("medicine_vial")) {
        // Handle medicine items
        int medicineGained = 0;
        if (item.getId().equals("medicine_pack")) {
            medicineGained = 3;
        } else if (item.getId().equals("first_aid")) {
            medicineGained = 5;
        }
        
        medicineCount += medicineGained;
        result = "You take the " + item.getName() + " and gain " + medicineGained + " medicine!\n";
        result += "Medicine: " + medicineCount;
    } else {
        // Regular item handling
        boolean added = player.addItem(item);
        if (!added) {
            JOptionPane.showMessageDialog(null, "You're carrying too much! You can't carry more than 5 items.");
            currentRoom.addItem(item); // Put back
            return "You can't carry any more items.";
        }
        result = "You take the " + item.getName() + ".";
    }

    // Rest of your existing challenge logic...
    if (getCurrentRoomChallengesCompleted() == 0 && hasRoomChallenges()) {
        Challenge nextChallenge = getNextChallengeForCurrentRoom();
        if (nextChallenge != null) {
            currentChallenge = nextChallenge;
            result += "\n\n" + nextChallenge.getPrompt() + "\n" + nextChallenge.getOptionsString();
        }
    }

    return result;
}
    private String handleIgnoreCommand(String itemName) {
        Room currentRoom = rooms.get(player.getCurrentRoom());
        if (currentRoom == null) {
            return "You are not in a valid room.";
        }
        String result = "You ignore the inventory.";
        if (getCurrentRoomChallengesCompleted() == 0 && hasRoomChallenges()) {
            Challenge nextChallenge = getNextChallengeForCurrentRoom();
            if (nextChallenge != null) {
                currentChallenge = nextChallenge;
                result += "\n\n" + nextChallenge.getPrompt() + "\n" + nextChallenge.getOptionsString();
            }
        }
        return result;
    }

    private String handleDropCommand(String itemName) {
        if (itemName.isEmpty()) {
            return "Drop what? Please specify an item.";
        }

        // Prevent dropping the medicine vial
        if (itemName.toLowerCase().contains("medicine") || itemName.toLowerCase().contains("vial")) {
            return "You cannot drop your medicine vial! It's essential for your survival.";
        }

        Item item = player.getItem(itemName);
        if (item == null) {
            return "You don't have '" + itemName + "' in your inventory.";
        }

        Room currentRoom = rooms.get(player.getCurrentRoom());
        if (currentRoom == null) {
            return "You are not in a valid room.";
        }

        player.removeItem(itemName);
        currentRoom.addItem(item);

        return "You drop the " + item.getName() + ".";
    }

    private String handleMovementCommand(String direction) {
        if (!canMoveFromCurrentRoom()) {
            int completed = getCurrentRoomChallengesCompleted();
            return "You need to complete all challenges in this room before you can move. " +
                    "Challenges completed: " + completed + "/3";
        }

        Room currentRoom = rooms.get(player.getCurrentRoom());
        if (currentRoom == null) {
            return "You are not in a valid room.";
        }

        String nextRoomId = currentRoom.getExit(direction);
        if (nextRoomId == null || !rooms.containsKey(nextRoomId)) {
            return "You can't go " + direction + " from here. " + currentRoom.getExitsString();
        }

        // Move the player
        player.move(direction, rooms);

        // Rotate spinner in correct direction (only if spinner panel exists)
        if (spinnerPanel != null) {
            if (direction.equalsIgnoreCase("clockwise")) {
                spinnerPanel.rotateClockwise();
            } else if (direction.equalsIgnoreCase("counterclockwise")) {
                spinnerPanel.rotateCounterclockwise();
            }
        }

        currentChallenge = null; // Clear current challenge when moving

        return "You move " + direction + ".\n\n" + getCurrentRoomDescription();
    }

    private String tryProcessChallengeCommand(String command) {
        // Only allow challenge commands if we haven't completed all challenges in current room
        if (getCurrentRoomChallengesCompleted() >= 3) {
            return null;
        }

        // Get the next challenge for current room
        Challenge nextChallenge = getNextChallengeForCurrentRoom();
        if (nextChallenge == null) {
            return null;
        }

        Challenge.Option option = nextChallenge.getOption(command);
        if (option != null) {
            // Check if player has required inventory items
            if (!hasRequiredInventory(option)) {
                return "You don't have the required items to choose this option. Required: " + 
                       String.join(", ", option.getRequiredInventory());
            }
            
            // Set current challenge and handle the option
            currentChallenge = nextChallenge;
            return handleChallengeOption(option);
        }

        return null;
    }
    
    private boolean hasRequiredInventory(Challenge.Option option) {
        if (option.getRequiredInventory() == null || option.getRequiredInventory().isEmpty()) {
            return true; // No requirements
        }
        
        for (String requiredItem : option.getRequiredInventory()) {
            if (!player.hasItem(requiredItem)) {
                return false;
            }
        }
        return true;
    }

    public void checkVictoryCondition() {
        // Iterate over all rooms and check if their challenges are fully completed
        for (String roomId : rooms.keySet()) {
            List<Challenge> roomSpecificChallenges = roomChallenges.get(roomId);
            int completed = roomChallengeCompletions.getOrDefault(roomId, 0);

            // If any room has incomplete challenges, return early
            if (roomSpecificChallenges == null || completed < roomSpecificChallenges.size()) {
                return; // Not all challenges completed yet
            }
        }
        
        // If we get here, all rooms are fully completed
        gui.victory();
    }

    private String handleChallengeOption(Challenge.Option option) {
    // Remove required inventory items from player's inventory
    if (option.getRequiredInventory() != null && !option.getRequiredInventory().isEmpty()) {
        for (String requiredItem : option.getRequiredInventory()) {
            Item item = player.getItem(requiredItem);
            if (item != null) {
                player.removeItem(requiredItem);
            }
        }
    }

    // Apply health change
    player.changeHealth(option.getHealthChange());

    // Apply medicine change if specified
    if (option.getMedicineChange() != 0) {
        medicineCount += option.getMedicineChange();
        if (medicineCount < 0) {
            medicineCount = 0; // Prevent negative medicine
        }
    }

    // Check if player died
    if (player.getHealth() <= 0) {
        gui.gameOver();
        return option.getResult() + "\n\nYour health has dropped to 0! GAME OVER!\nType 'restart' to begin again.";
    }

    // Increment challenges completed for current room
    String currentRoomId = player.getCurrentRoom();
    int currentCompleted = roomChallengeCompletions.get(currentRoomId);
    roomChallengeCompletions.put(currentRoomId, currentCompleted + 1);

    checkVictoryCondition();

    String result = option.getResult();

    if (option.getHealthChange() != 0) {
        String healthChange = option.getHealthChange() > 0 ? "+" + option.getHealthChange()
                : String.valueOf(option.getHealthChange());
        result += " (Health " + healthChange + ")";
    }

    if (option.getMedicineChange() != 0) {
        String medicineChange = option.getMedicineChange() > 0 ? "+" + option.getMedicineChange()
                : String.valueOf(option.getMedicineChange());
        result += " (Medicine " + medicineChange + ")";
    }

    result += "\nHealth: " + player.getHealth() + "/100";
    result += "\nMedicine: " + medicineCount;
    result += "\nChallenges completed: " + getCurrentRoomChallengesCompleted() + "/3";

    // Clear current challenge
    currentChallenge = null;

    // Check if there are more challenges in current room
    if (getCurrentRoomChallengesCompleted() < 3) {
        Challenge nextChallenge = getNextChallengeForCurrentRoom();
        if (nextChallenge != null) {
            currentChallenge = nextChallenge;
            result += "\n\n" + nextChallenge.getPrompt() + "\n" + nextChallenge.getOptionsString();
        }
    } else {
        result += "\n\nYou have completed all challenges in this room! You can now move to another room.";
    }

    return result;
}

    private Challenge getNextChallengeForCurrentRoom() {
        String currentRoomId = player.getCurrentRoom();
        List<Challenge> roomSpecificChallenges = roomChallenges.get(currentRoomId);
        
        if (roomSpecificChallenges == null || roomSpecificChallenges.isEmpty()) {
            return null;
        }
        
        int challengesCompleted = getCurrentRoomChallengesCompleted();
        if (challengesCompleted >= roomSpecificChallenges.size()) {
            return null;
        }

        return roomSpecificChallenges.get(challengesCompleted);
    }
    
    private boolean hasRoomChallenges() {
        String currentRoomId = player.getCurrentRoom();
        List<Challenge> roomSpecificChallenges = roomChallenges.get(currentRoomId);
        return roomSpecificChallenges != null && !roomSpecificChallenges.isEmpty();
    }
    
    public int getCurrentRoomChallengesCompleted() {
        String currentRoomId = player.getCurrentRoom();
        return roomChallengeCompletions.getOrDefault(currentRoomId, 0);
    }
    
    private boolean canMoveFromCurrentRoom() {
        return getCurrentRoomChallengesCompleted() >= 3;
    }

    public String getCurrentRoomDescription() {
        Room currentRoom = rooms.get(player.getCurrentRoom());
        if (currentRoom == null) {
            return "You are in an unknown location.";
        }

        StringBuilder description = new StringBuilder();
        description.append(currentRoom.getDescription()).append("\n\n");
        description.append(currentRoom.getItemsString()).append("\n");
        description.append(currentRoom.getExitsString());

        // Add current challenge information if player is facing one
        if (currentChallenge != null) {
            description.append("\n\n--- Current Challenge ---\n");
            description.append(currentChallenge.getPrompt()).append("\n");
            description.append(currentChallenge.getOptionsString());
        }

        return description.toString();
    }

    public String getStartMessage() {
        int x = (int) (Math.random() * 8) + 5; // chooses a random number between 5-12 --> reps. district for the player
                                               // to be from
        int y = (int) (Math.random() * 2) + 1; // choose a random number between 1-2 --> reps. district for the gender

        // chooses the gender
        String gender = "";
        if (y == 1) {
            gender = "female";
        } else {
            gender = "male";
        }

        return "WELCOME TO THE 75th ANNUAL HUNGER GAMES!\n" +
        "You have been reaped as the " + gender + " tribute from district " + x + "\n" +
                
                "To be crowned victor, your goal is to get through all sections of the arena without losing your health. \n" +
                "You start with a Medicine Vial that can be used to restore health. Collect medicine throughout your journey!\n" +
                "May the odds be ever in your favor. \n" + "\n" +
                "Health: " + player.getHealth() + "/100\n" +
                "Medicine: " + medicineCount + "\n" +
                "Type 'help' for available commands.\n\n" +
                getCurrentRoomDescription() + "\n\n" +
                "You notice some items around. Pick one up to begin facing challenges!";
    }

    public void restart() {
    // Create a completely new player instance
    this.player = new Player();
    
    // Clear current challenge
    this.currentChallenge = null;
    
    // Reset medicine count to 0
    this.medicineCount = 0;
    
    // Reset all room challenge completions
    for (String roomId : rooms.keySet()) {
        roomChallengeCompletions.put(roomId, 0);
    }
    
    // Reload game data to reset room inventories
    loadGameData();
    
    // Re-initialize room challenges
    initializeRoomChallenges();
    
    // Add the starting medicine vial to the new player's inventory
    addMedicineVialToPlayer();
    
    // Update the GUI if it exists
    if (gui != null) {
        gui.updateMedicineDisplay(medicineCount);
    }
}

    private String getHelpText() {
        return "Available commands:\n" +
                "- help: Show this help message\n" +
                "- look: Look around the current room\n" +
                "- take <item>: Take an item from the room\n" +
                "- drop <item>: Drop an item from your inventory\n" +
                "- inventory (or inv): Show your inventory and items you're carrying\n" +
                "- ignore: ignores inventory to move on to challenges\n" +
                "- health: Show your health, medicine, and status\n" +
                "- use <amount> medicine: Use medicine to restore health (e.g., 'use 5 medicine')\n" +
                "- clockwise: Move clockwise (when available)\n" +
                "- counterclockwise: Move counterclockwise (when available)\n" +
                "- restart: Restart the game if you die\n" +
                "\nChallenge commands will be shown when challenges appear.\n" +
                "You must complete 3 challenges in each room before you can move to the next room.\n" +
                "Some challenge options require specific inventory items!\n" +
                "Medicine can be gained through certain challenge options and used to restore health.\n" +
                "WARNING: If your health reaches 0, you will die and need to restart!";
    }

    public SpinnerPanel getSpinnerPanel() {
        return spinnerPanel;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public int getMedicineCount() {
        return medicineCount;
    }
}