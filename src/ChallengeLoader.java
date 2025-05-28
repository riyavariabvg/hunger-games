import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChallengeLoader {
    private static final Gson gson = new Gson();
    
    public static List<Challenge> loadChallenges(String filename) {
        List<Challenge> challenges = new ArrayList<>();
        
        try (FileReader reader = new FileReader(filename)) {
            Type listType = new TypeToken<List<JsonObject>>(){}.getType();
            List<JsonObject> challengeDataList = gson.fromJson(reader, listType);
            
            for (JsonObject challengeObj : challengeDataList) {
                Challenge challenge = new Challenge();
                
                // set the id
                if (challengeObj.has("id")) {
                    challenge.setId(challengeObj.get("id").getAsString());
                }
                
                // Set prompt
                if (challengeObj.has("prompt")) {
                    challenge.setPrompt(challengeObj.get("prompt").getAsString());
                }
                
                // Set options
                if (challengeObj.has("options")) {
                    JsonArray optionsArray = challengeObj.getAsJsonArray("options");
                    List<Challenge.Option> options = new ArrayList<>();
                    
                    for (JsonElement optionElement : optionsArray) {
                        JsonObject optionObj = optionElement.getAsJsonObject();
                        
                        Challenge.Option option = new Challenge.Option();
                        
                        if (optionObj.has("command")) {
                            option.setCommand(optionObj.get("command").getAsString());
                        }
                        
                        if (optionObj.has("result")) {
                            option.setResult(optionObj.get("result").getAsString());
                        }
                        
                        if (optionObj.has("healthChange")) {
                            option.setHealthChange(optionObj.get("healthChange").getAsInt());
                        }
                        
                        if (optionObj.has("success")) {
                            option.setSuccess(optionObj.get("success").getAsBoolean());
                        }
                        
                        options.add(option);
                    }
                    
                    challenge.setOptions(options);
                }
                
                challenges.add(challenge);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading challenges: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing challenges JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return challenges;
    }
}