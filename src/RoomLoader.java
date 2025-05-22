import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class RoomLoader {
    public Map<String, Room> loadRooms(String filePath) {
        Map<String, Room> rooms = new HashMap<>();

        try {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(new FileReader(filePath), JsonObject.class);

            for (String key : data.keySet()) {
                JsonObject obj = data.getAsJsonObject(key);

                String name = key;  
                String description = obj.get("description").getAsString();

                
                Map<String, String> exits = new HashMap<>();
                if (obj.has("exits")) {
                    JsonObject exitsJson = obj.getAsJsonObject("exits");
                    for (String dir : exitsJson.keySet()) {
                        exits.put(dir, exitsJson.get(dir).getAsString());
                    }
                }

                
                List<Item> items = new ArrayList<>();
                if (obj.has("items")) {
                    JsonArray itemArray = obj.getAsJsonArray("items");
                    for (JsonElement e : itemArray) {
                        JsonObject itemObj = e.getAsJsonObject();
                        String id = itemObj.get("id").getAsString();
                        String itemName = itemObj.get("name").getAsString();
                        String itemDesc = itemObj.get("description").getAsString();
                        items.add(new Item(id, itemName, itemDesc));
                    }
                }

                
                Room room = new Room(key, name, description, exits, items);
                rooms.put(key, room);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }
}
