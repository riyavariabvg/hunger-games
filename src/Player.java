import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    private static final int MAX_HEALTH = 100;
    private static final int MAX_INVENTORY_SIZE = 5;
    
    private int health;
    private String currentRoom;
    private List<Item> inventory;
    private int roomChallengesCompleted;
    private int totalChallengesCompleted;
    
    public Player() {
        this.health = MAX_HEALTH;
        this.currentRoom = "Section1"; // Starting room
        this.inventory = new ArrayList<>();
        this.roomChallengesCompleted = 0;
        this.totalChallengesCompleted = 0;
    }
    
    // Health methods
    public int getHealth() {
        return health;
    }
    
    public void changeHealth(int amount) {
        health += amount;
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        } else if (health < 0) {
            health = 0;
        }
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    // Room methods
    public String getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(String room) {
        this.currentRoom = room;
        // Reset room challenges when entering a new room
        this.roomChallengesCompleted = 0;
    }
    
    public void move(String direction, Map<String, Room> rooms) {
        Room currentRoomObj = rooms.get(currentRoom);
        if (currentRoomObj != null) {
            String nextRoom = currentRoomObj.getExit(direction);
            if (nextRoom != null && rooms.containsKey(nextRoom)) {
                setCurrentRoom(nextRoom);
            }
        }
    }
    
    // Challenge methods
    public int getRoomChallengesCompleted() {
        return roomChallengesCompleted;
    }
    
    public void incrementRoomChallengesCompleted() {
        roomChallengesCompleted++;
    }
    
    public int getTotalChallengesCompleted() {
        return totalChallengesCompleted;
    }
    
    public void incrementTotalChallengesCompleted() {
        totalChallengesCompleted++;
    }
    
    public boolean canMoveToNextRoom() {
        return roomChallengesCompleted >= 3;
    }
    
    // Inventory methods
    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }
    
    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            return false;
        }
        inventory.add(item);
        return true;
    }
    
    public boolean removeItem(String itemName) {
        return inventory.removeIf(item -> 
            item.getName().equalsIgnoreCase(itemName) || 
            item.getId().equalsIgnoreCase(itemName));
    }
    
    public Item getItem(String itemName) {
        return inventory.stream()
            .filter(item -> item.getName().equalsIgnoreCase(itemName) || 
                           item.getId().equalsIgnoreCase(itemName))
            .findFirst()
            .orElse(null);
    }
    
    public boolean hasItem(String itemName) {
        return inventory.stream().anyMatch(item -> 
            item.getName().equalsIgnoreCase(itemName) || 
            item.getId().equalsIgnoreCase(itemName));
    }
    
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }
        
        StringBuilder sb = new StringBuilder("Your inventory (" + inventory.size() + "/" + MAX_INVENTORY_SIZE + "):\n");
        for (Item item : inventory) {
            sb.append("- ").append(item.getName())
              .append(": ").append(item.getDescription()).append("\n");
        }
        return sb.toString();
    }
    
    // Status methods
    public String getStatusString() {
        return "Health: " + health + "/" + MAX_HEALTH + "\n" +
               "Current Room: " + currentRoom + "\n" +
               "Room Challenges Completed: " + roomChallengesCompleted + "/3\n" +
               "Total Challenges Completed: " + totalChallengesCompleted + "\n" +
               "Inventory: " + inventory.size() + "/" + MAX_INVENTORY_SIZE + " items";
    }
}