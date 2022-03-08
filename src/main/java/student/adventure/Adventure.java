package student.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import static java.util.Arrays.asList;

/**
 * This is the class that controls everything needed to play the game.
 */
public class Adventure {
    private Layout layout;
    private Layout.Room[] rooms;
    private String currentRoom;
    private Layout.Room currentRoomObject;
    private List<String> inventory = new LinkedList<>();
    private List<String> history = new LinkedList<>();
    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
    }
    private int instanceID;
    private List<String> directions = new ArrayList<>();

    public List<String> getInventory() {
        return inventory;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public Layout.Room getCurrentRoomObject() {
        return currentRoomObject;
    }

    public List<String> getDirections() {
        return directions;
    }

    public static Adventure findInstanceWithID(ArrayList<Adventure> adventures, int id) {
        return adventures.stream()
                .filter(adventure -> id == adventure.getInstanceID())
                .findFirst()
                .orElse(null);
    }

    public int getInstanceID() {
        return instanceID;
    }

    public List<String> getHistory() {
        return history;
    }

    public Layout getLayout() {
        return layout;
    }

    /**
     * Load data needed for game. This is a new version.
     * @param userInput A path to json file
     * @param id An id for each Adventure instance
     * @throws FileNotFoundException
     */
    public void loadData(InputStream userInput, int id) throws FileNotFoundException {
        // make a file out of user's input which is file path
        Scanner scanner = new Scanner(userInput);
        String pathName = scanner.nextLine();
        File file;

        // check if file is valid
        try {
            file = new File(pathName);
            layout = new ObjectMapper().readValue(file, Layout.class);
        }catch(Exception e) {
            System.out.println("You path is invalid!");
            throw new FileNotFoundException();
        }

        // check if scheme is valid
        if(!isSchemaValid(layout)) {
            System.out.println("Your Scheme is not valid!");
            throw new IllegalArgumentException();
        }

        // initialize some useful variables
        rooms = layout.getRooms();
        currentRoom = layout.getStartingRoom();
        setCurrentRoomObject(currentRoom);
        history.add(currentRoom);
        this.instanceID = id;
        addDirection();
    }

    /**
     * Load data needed for game. This version is deprecated.
     * @param userInput A path to json file
     * @throws FileNotFoundException
     */
    public void loadData(InputStream userInput) throws FileNotFoundException {
        // make a file out of user's input which is file path
        Scanner scanner = new Scanner(userInput);
        String pathName = scanner.nextLine();
        File file;

        // check if file is valid
        try {
            file = new File(pathName);
            layout = new ObjectMapper().readValue(file, Layout.class);
        }catch(Exception e) {
            System.out.println("You path is invalid!");
            throw new FileNotFoundException();
        }

        // check if scheme is valid
        if(!isSchemaValid(layout)) {
            System.out.println("Your Scheme is not valid!");
            throw new IllegalArgumentException();
        }

        // initialize some useful variables
        rooms = layout.getRooms();
        currentRoom = layout.getStartingRoom();
        setCurrentRoomObject(currentRoom);
        history.add(currentRoom);
        addDirection();
    }

    /**
     * Check if a layout is valid.
     * @param layout is objects in Json file
     * @return true if a scheme is valid, otherwise false
     */
    public boolean isSchemaValid(Layout layout) {
        // check if layout has valid object
        if (layout.getStartingRoom() == null || layout.getEndingRoom() == null
        || layout.getRooms() == null || layout.getRooms().length < 2) {
            return false;
        }

        // check if each room has valid object
        for (Layout.Room room : layout.getRooms()) {
            if (room.getName() == null || room.getName().equals("") || room.getDescription() == null ||
                   room.getDescription().equals("") || room.getDirections() == null || room.getDirections().length == 0) {
                return false;
            }

            // check each direction of a room
            for (Layout.Room.Direction direction : room.getDirections()) {
                if (direction.getDirectionName() == null || direction.getDirectionName().equals("")
                || direction.getRoom() == null | direction.getRoom().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This function will be used repeatedly throughout a game. This is a new version.
     * @param input Input is composed of commandName and commandValue.
     * @param adventure An instance of Adventure class.
     * @return String that is corresponding to input.
     */
    public String startGameV2(String[] input, Adventure adventure) {
        // the first run of the game
        if (input[0].equals("Start") && input[1].equals("Game")) {
            printRoomInformation();//check
            System.out.print("> ");
            return " ";
        }

        // when Array input has more than two strings, reconstruct them so that
        // input always consist of two arrays. Ex: "take frontdoor key" has three strings.
        // Make it "take" and "frontdoor key"
        if (input.length > 1) {
            String[] commandValue = Arrays.copyOfRange(input, 1, input.length);
            String commandValueString = String.join(" ", commandValue);
            input = new String[]{input[0], commandValueString};
        }

        // take care of invalid inputs by user
        if ((input.length == 1 || input.length >= 3)
                && !(input[0].equals("quit") || input[0].equals("exit"))
                && !input[0].equals("examine")
                && !input[0].equals("history")) {
            return ("I don't understand \"" + String.join(" ", input) + "\"!");
        }

        // from here, input[0] is command and input[1] is commandvalue
        // ex. go East:  go is command and East is value
        if (input[0].contains("quit") || input[0].contains("exit")) {
            return "Exit Game";
        }

        if (input[0].contains("examine")) {
            return "keep going";
        }

        if (input[0].contains("go")) {
            return goNextRoom(input[1]);
        }

        if (input[0].contains("take")) {
            return pickItem(input[1]);
        }

        if (input[0].contains("drop")) {
            return dropItem(input[1]);
        }

        if (input[0].contains("history")) {
            return showHistory();
        }

        return "";
    }

    /**
     * This function will be used repeatedly throughout a game.
     * @param inputStream will be used for user command
     * @param resultOfDecision is a result of a decision that user made at each prompt
     * @return the result of decision
     */
    public String startGame(InputStream inputStream, String resultOfDecision, Adventure adventure) {
        Scanner scanner = new Scanner(inputStream);

        // when user takes or drops item, show follow-up prompt for next user's command w/o showing description again.
        // likewise, if user made invalid decision or if there's no item in room or inventory,
        // re-ask user for next command w/o showing description again
        if (resultOfDecision.equals("Took an item") || resultOfDecision.equals("Dropped an item") ||
                resultOfDecision.contains("There is no item ") || resultOfDecision.contains("You don't have ") ||
                resultOfDecision.contains("I don't understand ") || resultOfDecision.contains("I can't go") ||
                resultOfDecision.contains("Your history rooms: ")) {
            System.out.print("> ");
        }

        // otherwise, print room information again and prompt
        // this case include when resultOfDecision = "Start the Game"
        else {
            printRoomInformation();//check
            System.out.print("> ");
        }

        // get user's command
        String userInput = scanner.nextLine(); // ex. go <direction> , take <item>
        String loweredUserInput = userInput.toLowerCase();

        // when user wants to quit a game
        if (loweredUserInput.equals("quit") || loweredUserInput.equals("exit")) {
            return "Exit Game";
        }

        // when user wants room information again
        if (loweredUserInput.equals("examine")) {
            return "keep going";
        }

        // when user chooses to move
        if (loweredUserInput.contains("go ")) {
            return goNextRoom(userInput);
        }

        // when user picks up an item. when picking or dropping an item, should wait for next command
        if (userInput.contains("take ")) {
           return pickItem(userInput);
        }

        // when user drops an item
        if (userInput.contains("drop ")) {
            return dropItem(userInput);
        }

        if (userInput.equals("show history")) {
            return showHistory();
        }

        // if none is selected, user's command is undefined one
        return "I don't understand \"" + userInput + "\"!";
    } //startGame

    private String showHistory() {
        return "Your history rooms: " + Arrays.toString(history.toArray());
    }

    /**
     * Pick an item at a room
     * @param userInput user's command
     * @return the result of user's command
     */
    public String pickItem(String userInput) {
        // check if userItem is in item list of room, not user's inventory
        if(isValidItemRoom(userInput)) {
            deleteItemFromRoom(userInput); // delete from items at room
            inventory.add(userInput); // add item to user's inventory
            return "Took an item";
        } else {
            return "There is no item \"" + userInput + "\" in the room.";
        }
    }

    /**
     * Drop Item back at room and remove the item from user's inventory
     * @param userInput user's command
     * @return the result of user's command
     */
    public String dropItem(String userInput) {
        // check if userDropItem is in Inventory, not item list of room
        if(inventory.contains(userInput)) {
            addItemToRoom(userInput);// add to item list of room
            inventory.remove(userInput); // remove a item from inventory since a user is putting it back at room
            return "Dropped an item";
        } else {
            return "You don't have \"" + userInput + "\"!";
        }
    }

    /**
     * Add item to the item list of room when user drops an item
     * @param userDropItem the item user wants to drop at room
     */
    private void addItemToRoom(String userDropItem) {
        List<String> itemList = new java.util.ArrayList<>(Arrays.asList(currentRoomObject.getItems()));
        itemList.add(userDropItem);

        // make array to temporarily save itemList.toArray
        // and use it to set updated item list of the room
        String[] array = new String[itemList.size()];
        currentRoomObject.setItems(itemList.toArray(array));
    }

    /**
     * move to a room that user choose
     * @param userInput user's decision on which direction a user will go. ex) "go West"
     * @return a result of user's decision
     */
    public String goNextRoom(String userInput) {
        // check is chosen direction is valid
        if (isValidDirection(userInput)) {
            // if valid direction, update currentRoom as the next room that the direction is pointing at
            currentRoom = findNextRoom(userInput);

            history.add(currentRoom);

            directions.clear();
            addDirection();

            // if next Room is ending room, print out win sign and exit game
            if (currentRoom.equals(layout.getEndingRoom())) {
                return "You've made it on time to code review; you win!";
            }

            setCurrentRoomObject(currentRoom);

            return "keep going";

        } else {
            return "I can't go \"" + userInput + "\"!";
        }
    }

    /**
     * Add possible direction at currentRoomObject to directions.
     */
    private void addDirection() {
        for (Layout.Room.Direction direction : currentRoomObject.getDirections()) {
            directions.add(direction.getDirectionName());
        }
    }

    /**
     * Delete userItem from an item list of room
     * @param userItem an item that user wants to take
     */
    private void deleteItemFromRoom(String userItem) {
        // make itemList to use remove function
        List<String> itemList = new java.util.ArrayList<>(Arrays.asList(currentRoomObject.getItems()));
        itemList.remove(userItem);

        // make array to temporarily save itemList.toArray
        // and use it to set updated item list of the room
        String[] array = new String[itemList.size()];
        currentRoomObject.setItems(itemList.toArray(array));
    }

    /**
     * check if userItem is valid one at room, i.e., is in an item list of room
     * @param userItem an item name
     * @return true if valid, otherwise false
     */
    private boolean isValidItemRoom(String userItem) {
        for (String item : currentRoomObject.getItems()) {
            if (item.toLowerCase().equals(userItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set variable currentRoomObject corresponding to String currentRoom.
     * @param currentRoom where a user currently is at
     */
    public void setCurrentRoomObject(String currentRoom) {
        for (Layout.Room room : rooms) {
            // find a room with the name of String currentRoom
            if (room.getName().equals(currentRoom)) {
                currentRoomObject = room;
            }
        }
    }

    /**
     * find a room corresponding to a direction user choose at currentRoom
     * @param userDirection a direction use chose
     * @return name of a next room
     */
    public String findNextRoom(String userDirection) { //userDirection here is lowercased.
        String nextRoom = null;
        for (Layout.Room.Direction direction : currentRoomObject.getDirections()) {
            if (direction.getDirectionName().toLowerCase().equals(userDirection)) {
                nextRoom =  direction.getRoom();
            }
        }
        return nextRoom;
    }

    /**
     * Check if a direction user chose is valid one.
     * @param userDirection a direction that use chose
     * @return true if valid, otherwise false
     */
    private boolean isValidDirection(String userDirection) {
        // check each room
        for (Layout.Room room : rooms) {
            // find a room with the name of String currentRoom
            if (room.getName().equals(currentRoom)) {
                // check each direction that the room has
                for (Layout.Room.Direction direct : room.getDirections()) {
                    // if the room has given direction as one of its directions, return true
                    if (direct.getDirectionName().toLowerCase().equals(userDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Print currentRoom's information
     */
    public void printRoomInformation() {
        for (Layout.Room room : rooms) {
            if (room.getName().equals(currentRoom)) {
                System.out.println(currentRoomObject.getDescription());
                printRoomDirections();
                printRoomItems();
                break;
            }
        }
    }

    /**
     * Print currentRoom's directions
     */
    public void printRoomDirections() {
        System.out.print("From here you can go: ");

        //print out the directions I can go from the room
        int numOfDirections = currentRoomObject.getDirections().length;
        for (Layout.Room.Direction direction : currentRoomObject.getDirections()) {
            // the last element of an array
            if (asList(currentRoomObject.getDirections()).indexOf(direction) == numOfDirections - 1) { //the last element
                System.out.println(direction.getDirectionName());
            }
            // the second from the last
            else if (asList(currentRoomObject.getDirections()).indexOf(direction) == numOfDirections - 2) { //second from the last
                System.out.print(direction.getDirectionName() + " or " );
            }
            else {
                System.out.print(direction.getDirectionName() + ", ");
            }
        }
    }

    /**
     * Print items of currentRoom
     */
    public void printRoomItems() {
        //if no item available, don't print "Items visible:"
        int numOfItems = currentRoomObject.getItems().length;
        if (numOfItems < 1) {
            return;
        }

        System.out.print("Items visible: ");
        for (int i = 0; i < numOfItems; i++) {
            // the last element of an array
            if (i == numOfItems - 1) {
                System.out.println(currentRoomObject.getItems()[i]);
            }
            // the second element from the last
            else if (i == numOfItems - 2) {
                System.out.print(currentRoomObject.getItems()[i] + " or ");
            }
            else {
                System.out.print(currentRoomObject.getItems()[i] + ", ");
            }
        }
    }


}
