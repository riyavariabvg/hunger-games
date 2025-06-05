import java.util.ArrayList;
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
    

    // getters and setters
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
        private int medicineChange; // add medicine change field
        private boolean success;
        private List<String> requiredInventory;

        public Option() {
            this.requiredInventory = new ArrayList<>();
            this.medicineChange = 0; // default to no medicine change
        }

        public Option(String command, String result, int healthChange, boolean success) {
            this.command = command;
            this.result = result;
            this.healthChange = healthChange;
            this.success = success;
            this.requiredInventory = new ArrayList<>();
            this.medicineChange = 0; // default to no medicine change
        }

        public Option(String command, String result, int healthChange, int medicineChange, boolean success) {
            this.command = command;
            this.result = result;
            this.healthChange = healthChange;
            this.medicineChange = medicineChange;
            this.success = success;
            this.requiredInventory = new ArrayList<>();
        }

        // getters and setters
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

        public int getMedicineChange() {
            return medicineChange;
        }

        public void setMedicineChange(int medicineChange) {
            this.medicineChange = medicineChange;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<String> getRequiredInventory() {
            return requiredInventory;
        }

        public void setRequiredInventory(List<String> requiredInventory) {
            this.requiredInventory = requiredInventory;
        }
    }
}