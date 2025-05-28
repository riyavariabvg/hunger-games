import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryLoader {
    private static final Gson gson = new Gson();
    
    public static Map<String, List<Item>> loadInventories(String filename) {
        Map<String, List<Item>> inventories = new HashMap<>();
        
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();
            Map<String, JsonObject> inventoryData = gson.fromJson(reader, type);
            
            for (Map.Entry<String, JsonObject> entry : inventoryData.entrySet()) {
                String sectionId = entry.getKey();
                JsonObject sectionObj = entry.getValue();
                
                List<Item> items = new ArrayList<>();
                
                if (sectionObj.has("items")) {
                    JsonArray itemsArray = sectionObj.getAsJsonArray("items");
                    
                    for (JsonElement itemElement : itemsArray) {
                        JsonObject itemObj = itemElement.getAsJsonObject();
                        
                        String id = itemObj.get("id").getAsString();
                        String name = itemObj.get("name").getAsString();
                        String description = itemObj.get("description").getAsString();
                        
                        Item item = new Item(id, name, description);
                        items.add(item);
                    }
                }
                
                inventories.put(sectionId, items);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading inventories: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing inventories JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return inventories;
    }
}