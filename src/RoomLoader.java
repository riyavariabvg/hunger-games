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

                
                Challenge challenge = getChallengeForRoom(key);
                Room room = new Room(key, name, description, exits, items, challenge);
                rooms.put(key, room);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }
    private Challenge getChallengeForRoom(String roomId) {
        switch (roomId) {
            case "Section1":
                return new Challenge("Avoid the tracker jackers!", "repellent");
            case "Section2":
                return new Challenge("Fight the Career Tributes!", "sword");
            case "Section3":
                return new Challenge("Escape the paralyzing fog!", "flashlight");
            case "Section4":
                return new Challenge("Survive the fire!", "water bottle");
            case "Section5":
                return new Challenge("Resist the jabberjay screams!", "earplugs");
            case "Section6":
                return new Challenge("Fight the beast muttations!", "bee smoke");
            case "Section7":
                return new Challenge("Navigate the lightning zone!", "rubber boots");
            case "Section8":
                return new Challenge("Survive the blood rain!", "camouflage cloth");
            case "Section9":
                return new Challenge("Outrun the tsunami!", "raft");
            case "Section10":
                return new Challenge("Don't eat the poisonous fruit!", "sleep flower");
            case "Section11":
                return new Challenge("Escape the monkey mutts!", "repellent");
            case "Section12":
                return new Challenge("Survive the unknown insects!", "broken map");
            default:
                return new Challenge("Unknown challenge", "none");
        }
    }

}
