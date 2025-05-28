import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    private String description;
    private Map<String, String> exits;
    private List<Item> items;
    
    public Room() {
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
    }
    
    public Room(String description) {
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
    }
    
    // Getters and setters
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, String> getExits() {
        return new HashMap<>(exits);
    }
    
    public void setExits(Map<String, String> exits) {
        this.exits = new HashMap<>(exits);
    }
    
    public void addExit(String direction, String room) {
        exits.put(direction.toLowerCase(), room);
    }
    
    public String getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }
    
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    public void setItems(List<Item> items) {
        this.items = new ArrayList<>(items);
    }
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public boolean removeItem(String itemName) {
        return items.removeIf(item -> 
            item.getName().equalsIgnoreCase(itemName) || 
            item.getId().equalsIgnoreCase(itemName));
    }
    
    public Item getItem(String itemName) {
        return items.stream()
            .filter(item -> item.getName().equalsIgnoreCase(itemName) || 
                           item.getId().equalsIgnoreCase(itemName))
            .findFirst()
            .orElse(null);
    }
    
    public boolean hasItem(String itemName) {
        return items.stream().anyMatch(item -> 
            item.getName().equalsIgnoreCase(itemName) || 
            item.getId().equalsIgnoreCase(itemName));
    }
    
    public String getItemsString() {
        if (items.isEmpty()) {
            return "There are no items here.";
        }
        
        StringBuilder sb = new StringBuilder("Items in this room:\n");
        for (Item item : items) {
            sb.append("- ").append(item.getName())
              .append(": ").append(item.getDescription()).append("\n");
        }
        return sb.toString();
    }
    
    public String getExitsString() {
        if (exits.isEmpty()) {
            return "There are no exits from this room.";
        }
        
        StringBuilder sb = new StringBuilder("Exits: ");
        boolean first = true;
        for (String direction : exits.keySet()) {
            if (!first) sb.append(", ");
            sb.append(direction);
            first = false;
        }
        return sb.toString();
    }
}