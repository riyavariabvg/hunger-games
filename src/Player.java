import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    private static final int MAX_INVENTORY_SIZE = 5;

    private int health;
    private List<Item> inventory;
    private String currentRoom;
    private int challengesCompleted;

    public Player() {
        this.health = 100;
        this.inventory = new ArrayList<>();
        this.currentRoom = "Middle";
        this.challengesCompleted = 0;
    }

    // Health methods
    public int getHealth() {
        return health;
    }

    public void changeHealth(int amount) {
        health += amount;
        if (health < 0)
            health = 0;
        if (health > 100)
            health = 100;
    }

    // Inventory methods
    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    // public void addItem(Item item) {
    // inventory.add(item);
    // }

    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println("You're carrying too much! You can only carry 5 items.");
            return false;
        }
        inventory.add(item);
        return true;
    }

    public boolean removeItem(String itemName) {
        return inventory.removeIf(item -> item.getName().equalsIgnoreCase(itemName) ||
                item.getId().equalsIgnoreCase(itemName));
    }

    public boolean hasItem(String itemName) {
        return inventory.stream().anyMatch(item -> item.getName().equalsIgnoreCase(itemName) ||
                item.getId().equalsIgnoreCase(itemName));
    }

    public Item getItem(String itemName) {
        return inventory.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName) ||
                        item.getId().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);
    }

    // Room methods
    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String room) {
        this.currentRoom = room;
        this.challengesCompleted = 0; // Reset challenges when entering new room
    }

    // Challenge methods
    public int getChallengesCompleted() {
        return challengesCompleted;
    }

    public void incrementChallengesCompleted() {
        challengesCompleted++;
    }

    public boolean canMoveToNextRoom() {
        return challengesCompleted >= 3; // Need to complete 3 challenges
    }

    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.\nYou are not carrying any items.";
        }

        StringBuilder sb = new StringBuilder("=== INVENTORY ===\n");
        sb.append("Items you are carrying:\n");
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            sb.append((i + 1)).append(". ").append(item.getName())
                    .append(" - ").append(item.getDescription()).append("\n");
        }
        // sb.append("Total items: ").append(inventory.size());

        sb.append("Total items: ").append(inventory.size())
                .append(" / ").append(MAX_INVENTORY_SIZE);

        return sb.toString();
    }

    public void move(String direction, Map<String, Room> rooms) {
        Room current = rooms.get(currentRoom);
        if (current == null) {
            System.out.println("Current room not found: " + currentRoom);
            return;
        }

        String nextRoomId = current.getExit(direction);
        //
        System.out.println("Trying to move " + direction + " from " + currentRoom + " to " + nextRoomId);

        if (nextRoomId != null && rooms.containsKey(nextRoomId)) {
            setCurrentRoom(nextRoomId); // This also resets challenges
        }else {
        System.out.println("Can't move " + direction + " from " + currentRoom);
    }

    }

}