import java.util.List;

public class Challenge {
    private String id;
    private String prompt;
    private List<Option> options;
    
    public Challenge() {}
    
    public Challenge(String id, String prompt, List<Option> options) {
        this.id = id;
        this.prompt = prompt;
        this.options = options;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public List<Option> getOptions() {
        return options;
    }
    
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
    public Option getOption(String command) {
        if (options == null) return null;
        
        return options.stream()
            .filter(option -> option.getCommand().equalsIgnoreCase(command.trim()))
            .findFirst()
            .orElse(null);
    }
    
    public String getOptionsString() {
        if (options == null || options.isEmpty()) {
            return "No options available.";
        }
        
        StringBuilder sb = new StringBuilder("Available commands:\n");
        for (Option option : options) {
            sb.append("- ").append(option.getCommand()).append("\n");
        }
        return sb.toString();
    }
    
    public static class Option {
        private String command;
        private String result;
        private int healthChange;
        private boolean success;
        
        public Option() {}
        
        public Option(String command, String result, int healthChange, boolean success) {
            this.command = command;
            this.result = result;
            this.healthChange = healthChange;
            this.success = success;
        }
        
        // Getters and setters
        public String getCommand() {
            return command;
        }
        
        public void setCommand(String command) {
            this.command = command;
        }
        
        public String getResult() {
            return result;
        }
        
        public void setResult(String result) {
            this.result = result;
        }
        
        public int getHealthChange() {
            return healthChange;
        }
        
        public void setHealthChange(int healthChange) {
            this.healthChange = healthChange;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}