import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
    private Player player;
    private Map<String, Room> rooms;
    private List<Challenge> challenges;
    private Map<String, List<Item>> sectionInventories;
    private Challenge currentChallenge;
    private Random random;
    private SpinnerPanel spinnerPanel;

    public Game() {
        this.player = new Player();
        this.random = new Random();
        loadGameData();
        spinnerPanel = new SpinnerPanel("src/images/spinnerImage.png");
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
                   "Challenges completed in this room: " + player.getChallengesCompleted() + "/3\n" +
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
        player.addItem(item);
        
        String result = "You take the " + item.getName() + ".";
        
        // Check if we should start challenges after taking an item
        if (player.getChallengesCompleted() == 0 && !challenges.isEmpty()) {
            Challenge nextChallenge = getNextChallenge();
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
                   "Challenges completed: " + player.getChallengesCompleted() + "/3";
        }
        
        Room currentRoom = rooms.get(player.getCurrentRoom());
        if (currentRoom == null) {
            return "You are not in a valid room.";
        }
        
        String nextRoomId = currentRoom.getExit(direction);
        if (nextRoomId == null) {
            return "You can't go " + direction + " from here. " + currentRoom.getExitsString();
        }
        
        Room nextRoom = rooms.get(nextRoomId);
        if (nextRoom == null) {
            return "The " + direction + " exit leads nowhere.";
        }
        
        player.setCurrentRoom(nextRoomId);
        spinnerPanel.rotateClockwise();

        currentChallenge = null; // Clear current challenge when moving
        
        return "You move " + direction + ".\n\n" + getCurrentRoomDescription();
    }
    
    private String tryProcessChallengeCommand(String command) {
        // Only allow challenge commands if we haven't completed all challenges
        if (player.getChallengesCompleted() >= 3) {
            return null;
        }
        
        // Get the next challenge
        Challenge nextChallenge = getNextChallenge();
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
        // Apply health change
        player.changeHealth(option.getHealthChange());
        
        // Check if player died
        if (player.getHealth() <= 0) {
            return option.getResult() + "\n\nYour health has dropped to 0! GAME OVER!\nType 'restart' to begin again.";
        }
        
        // Increment challenges completed
        player.incrementChallengesCompleted();
        
        String result = option.getResult();
        
        if (option.getHealthChange() != 0) {
            String healthChange = option.getHealthChange() > 0 ? 
                "+" + option.getHealthChange() : String.valueOf(option.getHealthChange());
            result += " (Health " + healthChange + ")";
        }
        
        result += "\nHealth: " + player.getHealth() + "/100";
        result += "\nChallenges completed: " + player.getChallengesCompleted() + "/3";
        
        // Clear current challenge
        currentChallenge = null;
        
        // Check if there are more challenges
        if (player.getChallengesCompleted() < 3) {
            Challenge nextChallenge = getNextChallenge();
            if (nextChallenge != null) {
                currentChallenge = nextChallenge;
                result += "\n\n" + nextChallenge.getPrompt() + "\n" + nextChallenge.getOptionsString();
            }
        } else {
            result += "\n\nYou have completed all challenges in this room! You can now move to another room.";
        }
        
        return result;
    }
    
    private Challenge getNextChallenge() {
        if (challenges.isEmpty() || player.getChallengesCompleted() >= challenges.size()) {
            return null;
        }
        
        // For now, return challenges in order. You could randomize this if desired.
        return challenges.get(player.getChallengesCompleted());
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
        return "Welcome to the Adventure Game!\n" +
               "You find yourself in the middle of your journey...\n" +
               "Health: " + player.getHealth() + "/100\n" +
               "Type 'help' for available commands.\n\n" +
               getCurrentRoomDescription() + "\n\n" +
               "You notice some items around. Pick one up to begin facing challenges!";
    }
    
    public void restart() {
        this.player = new Player();
        this.currentChallenge = null;
    }
    
    private String getHelpText() {
        return "Available commands:\n" +
               "- help: Show this help message\n" +
               "- look: Look around the current room\n" +
               "- take <item>: Take an item from the room\n" +
               "- drop <item>: Drop an item from your inventory\n" +
               "- inventory (or inv): Show your inventory and items you're carrying\n" +
               "- health: Show your health and status\n" +
               "- clockwise: Move clockwise (when available)\n" +
               "- counterclockwise: Move counterclockwise (when available)\n" +
               "- restart: Restart the game if you die\n" +
               "\nChallenge commands will be shown when challenges appear.\n" +
               "You must complete 3 challenges in each room before you can move to the next room.\n" +
               "WARNING: If your health reaches 0, you will die and need to restart!";
    }
    
    //     return CommandParser.parse(input, player, rooms, spinnerPanel);
    // }

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


