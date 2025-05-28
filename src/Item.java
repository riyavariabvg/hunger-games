public class Item {
    private String id;
    private String name;
    private String description;
    
    public Item(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return id.equals(item.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}