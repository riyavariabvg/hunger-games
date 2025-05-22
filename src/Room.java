import java.util.List;
import java.util.Map;

public class Room {
    private String id;
    private String name;
    private String description;
    private Map<String, String> exits;
    private List<Item> items;
    private Challenge challenge;

    public Room(String id, String name, String description, Map<String, String> exits, List<Item> items, Challenge challenge) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exits = exits;
        this.items = items;
        this.challenge = challenge;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getExits() {
        return exits;
    }

    public List<Item> getItems() {
        return items;
    }

    public Challenge getChallenge() {
        return challenge;
    }
    public void addItem(Item item) {
        items.add(item);
    }

    
    public void removeItem(Item item) {
        items.remove(item);
    }

    
    public String getLongDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(description).append("\n");

        if (!items.isEmpty()) {
            sb.append("You see: ");
            for (Item item : items) {
                sb.append(item.getName()).append(", ");
            }
            sb.setLength(sb.length() - 2); 
            sb.append(".\n");
        }

        if (!exits.isEmpty()) {
            sb.append("Exits: ");
            for (String direction : exits.keySet()) {
                sb.append(direction).append(" -> ").append(exits.get(direction)).append(", ");
            }
            sb.setLength(sb.length() - 2); 
            sb.append(".\n");
        }

        return sb.toString();
    }
}
