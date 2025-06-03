import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Game {
    private Player player;
    private Map<String, Room> rooms;
    private List<Challenge> challenges;
    private Map<String, List<Item>> sectionInventories;
    private Challenge currentChallenge;
    private Random random;
    private SpinnerPanel spinnerPanel;
    private boolean gameWon = false;

    // Constructor that accepts a SpinnerPanel from the GUI
    public Game(SpinnerPanel spinnerPanel) {
        this.player = new Player();
        this.random = new Random();
        this.spinnerPanel = spinnerPanel;
        loadGameData();
    }

    // Default constructor for backward compatibility
    public Game() {
        this.player = new Player();
        this.random = new Random();
        loadGameData();
        // Only create spinner panel if one wasn't provided
        if (this.spinnerPanel == null) {
            this.spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
        }
    }

    private void loadGameData() {
        this.rooms = RoomLoader.loadRooms("rooms.json");
        this.challenges = ChallengeLoader.loadChallenges("challenges.json");
        this.sectionInventories = InventoryLoader.loadInventories("inventory.json");

        System.out.println("Loaded " + rooms.size() + " rooms");
        System.out.println("Loaded " + challenges.size() + " challenges");
        System.out.println("Loaded " + sectionInventories.size() + " section inventories");
    }

    public String processCommand(String input) {
        // Check if player has won
        if (gameWon) {
            if (input.trim().equalsIgnoreCase("restart")) {
                restart();
                return "Game restarted!\n\n" + getStartMessage();
            }
            return getWinMessage() + "\n\nType 'restart' to play again!";
        }

        // Check if player is dead
        if (player.getHealth() <= 0) {
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

        // Handle health/status command
        if (CommandParser.isHealthCommand(action)) {
            return "Health: " + player.getHealth() + "/100\n" +
                    "Challenges completed in this room: " + player.getRoomChallengesCompleted() + "/3\n" +
                    "Total challenges completed: " + player.getTotalChallengesCompleted() + "/" + getTotalChallengesInGame() + "\n" +
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

        boolean added = player.addItem(item);
        if (!added) {
            JOptionPane.showMessageDialog(null, "You're carrying too much! You can't carry more than 5 items.");
            // Put the item back in the room since player couldn't take it
            currentRoom.addItem(item);
            return "You can't carry any more items.";
        }

        String result = "You take the " + item.getName() + ".";

        // Check if we should start challenges after taking an item
        if (player.getRoomChallengesCompleted() == 0 && hasRoomChallenges()) {
            Challenge nextChallenge = getNextChallengeForRoom();
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
        if (!player.canMoveToNextRoom()) {
            return "You need to complete all challenges in this room before you can move. " +
                    "Challenges completed: " + player.getRoomChallengesCompleted() + "/3";
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
        // Only allow challenge commands if we haven't completed all challenges in this room
        if (player.getRoomChallengesCompleted() >= 3) {
            return null;
        }

        // Get the next challenge for this specific room
        Challenge nextChallenge = getNextChallengeForRoom();
        if (nextChallenge == null) {
            return null;
        }

        Challenge.Option option = nextChallenge.getOption(command);
        if (option != null) {
            currentChallenge = nextChallenge;
            return handleChallengeOption(option);
        }

        return null;
    }

    private String handleChallengeOption(Challenge.Option option) {
        String result = option.getResult();
        int healthChange = option.getHealthChange();
        
        // Check if this challenge requires an inventory item
        String requiredItem = getRequiredItemForChallenge(currentChallenge);
        if (requiredItem != null && !player.hasItem(requiredItem)) {
            // Player doesn't have required item, lose extra health
            healthChange -= 15;
            result += "\n\nYou don't have the required item (" + requiredItem + ") and suffer additional damage!";
        } else if (requiredItem != null && player.hasItem(requiredItem)) {
            // Player has required item, bonus health
            healthChange += 5;
            result += "\n\nYou use your " + requiredItem + " effectively and gain a bonus!";
        }

        // Apply health change
        player.changeHealth(healthChange);

        // Check if player died
        if (player.getHealth() <= 0) {
            return result + "\n\nYour health has dropped to 0! GAME OVER!\nType 'restart' to begin again.";
        }

        // Increment challenges completed for this room and total
        player.incrementRoomChallengesCompleted();
        player.incrementTotalChallengesCompleted();

        if (healthChange != 0) {
            String healthChangeStr = healthChange > 0 ? "+" + healthChange : String.valueOf(healthChange);
            result += " (Health " + healthChangeStr + ")";
        }

        result += "\nHealth: " + player.getHealth() + "/100";
        result += "\nChallenges completed: " + player.getRoomChallengesCompleted() + "/3";
        result += "\nTotal challenges completed: " + player.getTotalChallengesCompleted() + "/" + getTotalChallengesInGame();

        // Clear current challenge
        currentChallenge = null;

        // Check for win condition FIRST
        if (checkWinCondition()) {
            gameWon = true;
            return result + "\n\n" + getWinMessage();
        }

        // Check if there are more challenges in this room
        if (player.getRoomChallengesCompleted() < 3) {
            Challenge nextChallenge = getNextChallengeForRoom();
            if (nextChallenge != null) {
                currentChallenge = nextChallenge;
                result += "\n\n" + nextChallenge.getPrompt() + "\n" + nextChallenge.getOptionsString();
            }
        } else {
            result += "\n\nYou have completed all challenges in this room! You can now move to another room.";
        }

        return result;
    }

    private Challenge getNextChallengeForRoom() {
        String currentRoom = player.getCurrentRoom();
        int challengesCompleted = player.getRoomChallengesCompleted();
        
        // Get challenges specific to this room
        List<Challenge> roomChallenges = getRoomChallenges(currentRoom);
        
        if (roomChallenges.isEmpty() || challengesCompleted >= roomChallenges.size()) {
            return null;
        }

        return roomChallenges.get(challengesCompleted);
    }

    private List<Challenge> getRoomChallenges(String roomId) {
        List<Challenge> roomChallenges = new ArrayList<>();
        
        // Filter challenges by room ID
        for (Challenge challenge : challenges) {
            if (challenge.getId().startsWith(roomId + "_")) {
                roomChallenges.add(challenge);
            }
        }
        
        return roomChallenges;
    }

    private boolean hasRoomChallenges() {
        return !getRoomChallenges(player.getCurrentRoom()).isEmpty();
    }

    private String getRequiredItemForChallenge(Challenge challenge) {
        // Define which challenges require specific items
        // This could be moved to JSON configuration later
        String challengeId = challenge.getId();
        
        if (challengeId.contains("Section1_A") || challengeId.contains("Section9_A")) {
            return "sword"; // Wild boar challenges
        } else if (challengeId.contains("Section2_A") || challengeId.contains("Section10_A")) {
            return "raft"; // River challenges
        } else if (challengeId.contains("Section7_A")) {
            return "repellent"; // Snake challenge
        } else if (challengeId.contains("Section6_A")) {
            return "matches"; // Earthquake challenge (for signaling)
        } else if (challengeId.contains("Section5_A")) {
            return "bee_smoke"; // Bee swarm challenge
        }
        
        return null; // No required item
    }

    // WIN CONDITION METHODS
    private boolean checkWinCondition() {
        int totalChallenges = getTotalChallengesInGame();
        return player.getTotalChallengesCompleted() >= totalChallenges;
    }

    private int getTotalChallengesInGame() {
        // Calculate total number of challenges across all rooms
        // Assuming each room has 3 challenges
        return rooms.size() * 3;
    }

    private String getWinMessage() {
        return "🎉 CONGRATULATIONS! YOU HAVE WON THE HUNGER GAMES! 🎉\n\n" +
               "You have successfully completed all challenges in all sections of the arena!\n" +
               "You have proven yourself as the ultimate tribute and survived the deadly competition.\n\n" +
               "Final Stats:\n" +
               "- Health: " + player.getHealth() + "/100\n" +
               "- Total Challenges Completed: " + player.getTotalChallengesCompleted() + "/" + getTotalChallengesInGame() + "\n" +
               "- Items Collected: " + player.getInventory().size() + "\n\n" +
               "You are the victor of the Hunger Games!\n" +
               "May the odds have been ever in your favor!";
    }

    public boolean isGameWon() {
        return gameWon;
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

        return description.toString();
    }

    public String getStartMessage() {
        int x = (int) (Math.random() * 8) + 5; // chooses a random number between 5-12 --> reps. district for the player to be from
        int y = (int) (Math.random() * 2) + 1; // choose a random number between 1-2 --> reps. district for the gender

        // chooses the gender
        String gender = "";
        if (y == 1) {
            gender = "female";
        } else {
            gender = "male";
        }

        return "You have been reaped as the " + gender + " tribute from district " + x + "\n" +
                "May the odds be ever in your favor. \n" +
                "You find yourself in the middle of your journey...\n" +
                "Health: " + player.getHealth() + "/100\n" +
                "Total Challenges to Complete: " + getTotalChallengesInGame() + "\n" +
                "Type 'help' for available commands.\n\n" +
                getCurrentRoomDescription() + "\n\n" +
                "You notice some items around. Pick one up to begin facing challenges!";
    }

    public void restart() {
        this.player = new Player();
        this.currentChallenge = null;
        this.gameWon = false;
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "- help: Show this help message\n" +
                "- look: Look around the current room\n" +
                "- take <item>: Take an item from the room\n" +
                "- drop <item>: Drop an item from your inventory\n" +
                "- inventory (or inv): Show your inventory and items you're carrying\n" +
                "- health: Show your health, progress, and status\n" +
                "- clockwise: Move clockwise (when available)\n" +
                "- counterclockwise: Move counterclockwise (when available)\n" +
                "- restart: Restart the game\n" +
                "\nChallenge commands will be shown when challenges appear.\n" +
                "You must complete 3 challenges in each room before you can move to the next room.\n" +
                "Complete ALL challenges in ALL rooms to win the Hunger Games!\n" +
                "Some challenges require specific inventory items - collect items to succeed!\n" +
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
}