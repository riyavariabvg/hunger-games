public class CommandParser {
    
    public static class ParsedCommand {
        private String action;
        private String target;
        private String fullCommand;
        
        public ParsedCommand(String action, String target, String fullCommand) {
            this.action = action;
            this.target = target;
            this.fullCommand = fullCommand;
        }
        
        public String getAction() {
            return action;
        }
        
        public String getTarget() {
            return target;
        }
        
        public String getFullCommand() {
            return fullCommand;
        }
    }
    
    public static ParsedCommand parseCommand(String input) {
        if (input == null) {
            return new ParsedCommand("", "", "");
        }
        
        String trimmed = input.trim().toLowerCase();
        String original = input.trim();
        
        if (trimmed.isEmpty()) {
            return new ParsedCommand("", "", "");
        }
        
        // Handle single word commands
        if (!trimmed.contains(" ")) {
            return new ParsedCommand(trimmed, "", original);
        }
        
        // Split into action and target
        String[] parts = trimmed.split("\\s+", 2);
        String action = parts[0];
        String target = parts.length > 1 ? parts[1] : "";
        
        // Handle special cases for take/drop commands
        if ((action.equals("take") || action.equals("get") || action.equals("pick")) && target.startsWith("up ")) {
            target = target.substring(3); // Remove "up " from "pick up item"
        }
        
        return new ParsedCommand(action, target, original);
    }
    
    public static boolean isMovementCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("clockwise") || cmd.equals("counterclockwise") || 
               cmd.equals("north") || cmd.equals("south") || 
               cmd.equals("east") || cmd.equals("west") ||
               cmd.equals("up") || cmd.equals("down");
    }
    
    public static boolean isInventoryCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("inventory") || cmd.equals("inv") || cmd.equals("i");
    }
    
    public static boolean isTakeCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("take") || cmd.equals("get") || cmd.equals("pick") || cmd.startsWith("take ") || cmd.startsWith("get ") || cmd.startsWith("pick ");
    }
    
    public static boolean isDropCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("drop") || cmd.startsWith("drop ");
    }
    
    public static boolean isLookCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("look") || cmd.equals("l") || cmd.equals("examine") || cmd.equals("ex");
    }
    
    public static boolean isHelpCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("help") || cmd.equals("h") || cmd.equals("?");
    }
    
    public static boolean isHealthCommand(String command) {
        String cmd = command.toLowerCase().trim();
        return cmd.equals("health") || cmd.equals("hp") || cmd.equals("status");
    }
}