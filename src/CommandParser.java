import java.util.Map;

public class CommandParser {
    public static String parse(String input, Player player, Map<String, Room> rooms) {
        String[] tokens = input.trim().split(" ");
        if (tokens.length == 0) return "Enter a command.";

        String cmd = tokens[0].toLowerCase();
        Room room = rooms.get(player.getCurrentRoomId());

        switch (cmd) {
            case "go":
                if (tokens.length < 2) return "Go where?";
                String dir = tokens[1].toLowerCase();
                if (room.getExits().containsKey(dir)) {
                    player.setCurrentRoomId(room.getExits().get(dir));
                    return rooms.get(player.getCurrentRoomId()).getLongDescription();
                } else {
                    return "You can't go that way.";
                }

            case "look":
                return room.getLongDescription();

            case "inventory":
                if (player.getInventory().isEmpty()) {
                    return "You are not carrying anything.";
                }
                StringBuilder inv = new StringBuilder("You are carrying:\n");
                for (Item item : player.getInventory()) {
                    inv.append("- ").append(item.getName()).append(": ").append(item.getDescription()).append("\n");
                }
                return inv.toString();

            case "take":
                if (tokens.length < 2) return "Take what?";
                String takeItem = input.substring(5).trim().toLowerCase(); 
                for (Item item : room.getItems()) {
                    if (item.getName().equalsIgnoreCase(takeItem)) {
                        player.addItem(item);
                        room.getItems().remove(item);
                        return "You took the " + item.getName() + ".";
                    }
                }
                return "There is no such item here.";

            case "drop":
                if (tokens.length < 2) return "Drop what?";
                String dropItem = input.substring(5).trim().toLowerCase();
                for (Item item : player.getInventory()) {
                    if (item.getName().equalsIgnoreCase(dropItem)) {
                        player.removeItem(item);
                        room.getItems().add(item);
                        return "You dropped the " + item.getName() + ".";
                    }
                }
                return "You don't have that item.";

            case "help":
                return """
                        Commands:
                        - go <direction> (e.g. go clockwise)
                        - look
                        - inventory
                        - take <item>
                        - drop <item>
                        - help
                        """;

            default:
                return "Unknown command.";
        }
    }
}
