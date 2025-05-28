import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomLoader {
    private static final Gson gson = new Gson();
    
    public static Map<String, Room> loadRooms(String filename) {
        Map<String, Room> rooms = new HashMap<>();
        
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();
            Map<String, JsonObject> roomData = gson.fromJson(reader, type);
            
            for (Map.Entry<String, JsonObject> entry : roomData.entrySet()) {
                String roomId = entry.getKey();
                JsonObject roomObj = entry.getValue();
                
                Room room = new Room();
                
                // Set description
                if (roomObj.has("description")) {
                    room.setDescription(roomObj.get("description").getAsString());
                }
                
                // Set exits
                if (roomObj.has("exits")) {
                    JsonObject exitsObj = roomObj.getAsJsonObject("exits");
                    Map<String, String> exits = new HashMap<>();
                    for (Map.Entry<String, JsonElement> exitEntry : exitsObj.entrySet()) {
                        exits.put(exitEntry.getKey(), exitEntry.getValue().getAsString());
                    }
                    room.setExits(exits);
                }
                
                // Set items
                if (roomObj.has("items")) {
                    JsonArray itemsArray = roomObj.getAsJsonArray("items");
                    for (JsonElement itemElement : itemsArray) {
                        JsonObject itemObj = itemElement.getAsJsonObject();
                        String id = itemObj.get("id").getAsString();
                        String name = itemObj.get("name").getAsString();
                        String description = itemObj.get("description").getAsString();
                        
                        Item item = new Item(id, name, description);
                        room.addItem(item);
                    }
                }
                
                rooms.put(roomId, room);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing rooms JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rooms;
    }
}
    
    // // sets up a new challenge for each room and says what the solution should be
    // private Challenge getChallengeForRoom(String roomId) {
    //     switch (roomId) {
    //         case "Section1":
    //             return new Challenge("Avoid the tracker jackers!", "repellent");
    //         case "Section2":
    //             return new Challenge("Fight the Career Tributes!", "sword");
    //         case "Section3":
    //             return new Challenge("Escape the paralyzing fog!", "flashlight");
    //         case "Section4":
    //             return new Challenge("Survive the fire!", "water bottle");
    //         case "Section5":
    //             return new Challenge("Resist the jabberjay screams!", "earplugs");
    //         case "Section6":
    //             return new Challenge("Fight the beast muttations!", "bee smoke");
    //         case "Section7":
    //             return new Challenge("Navigate the lightning zone!", "rubber boots");
    //         case "Section8":
    //             return new Challenge("Survive the blood rain!", "camouflage cloth");
    //         case "Section9":
    //             return new Challenge("Outrun the tsunami!", "raft");
    //         case "Section10":
    //             return new Challenge("Don't eat the poisonous fruit!", "sleep flower");
    //         case "Section11":
    //             return new Challenge("Escape the monkey mutts!", "repellent");
    //         case "Section12":
    //             return new Challenge("Survive the unknown insects!", "broken map");
    //         default:
    //             return new Challenge("Unknown challenge", "none");
    //     }
    // }


