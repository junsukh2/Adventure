package student.server;

import student.adventure.Adventure;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class myAdventureService implements AdventureService {
    private int id = -1;
    private ArrayList<Adventure> adventures = new ArrayList<>();
    private String[] commands = new String[2];

    /**
     * Clear out any instances of your adventure game,
     * and reset the instance ID back to zero.
     */
    @Override
    public void reset() {
        id = -1;
        adventures.clear();
    }

    @Override
    public int newGame() throws AdventureException, FileNotFoundException {
        id += 1;
        Adventure adventure = new Adventure();
        String data = "src/main/java/student/myAdventureMap.json";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        adventure.loadData(input, id);
        adventures.add(adventure);

        if (id < 0) {
            throw new AdventureException("Invalid ID");
        }

        return id;
    }

    /**
     * Returns the state of the game instance associated with the given ID.
     * @param id the instance id
     * @return the current state of the game
     */
    @Override
    public GameStatus getGame(int id) {
        boolean error = false;
        String message;

        if (id < 0) {
            throw new IllegalArgumentException("Invalid ID");
        }

        Adventure adventure = Adventure.findInstanceWithID(adventures, id);
        HashMap<String, List<String>> commandOptions = new HashMap<>();
        List<String> history = new ArrayList<>();
        String videoUrl = adventure.getLayout().getVideoUrl();

        try {
            if (commands[0] != null && commands[0].equals("history")) {
                message = "you've visited: " + adventure.getHistory();
            } else {
                message = adventure.getCurrentRoomObject().getDescription();

            }
        } catch (Exception e) {
            error = true;
            message = "";
        }

        history.add("Visited Locations");

        commandOptions.put("go", adventure.getDirections());
        commandOptions.put("history", history);
        commandOptions.put("take", Arrays.asList(adventure.getCurrentRoomObject().getItems()));
        commandOptions.put("drop", adventure.getInventory());

        String imageUrl = adventure.getCurrentRoomObject().getImage();
        AdventureState state = new AdventureState();

        String currentRoom = adventure.getCurrentRoom();
        String endingRoom = adventure.getLayout().getEndingRoom();
        if (currentRoom.equals(endingRoom)) {
            videoUrl = null;
            commandOptions = new HashMap<>();
        }
        GameStatus newGameStatus = new GameStatus(
                error,
                id,
                message,
                imageUrl,
                videoUrl,
                state,
                commandOptions);

        return newGameStatus;
    }

    @Override
    public boolean destroyGame(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid ID");
        }

        Adventure adventure = Adventure.findInstanceWithID(adventures, id);
        if (adventure == null) {
            return false;
        } else {
            adventures.remove(adventure);
            return true;
        }
    }

    @Override
    public void executeCommand(int id, Command command) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid ID");
        }

        Adventure adventure = Adventure.findInstanceWithID(adventures, id);

        commands[0] = command.getCommandName().toLowerCase();
        commands[1] = command.getCommandValue().toLowerCase();

        adventure.startGameV2(commands, adventure);
    }

    @Override
    public SortedMap<String, Integer> fetchLeaderboard() {
        return null;
    }
}
