package student;

import org.glassfish.grizzly.http.server.HttpServer;
import student.adventure.Adventure;
import student.server.AdventureResource;
import student.server.AdventureServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Adventure adventure = new Adventure();

        HttpServer server = AdventureServer.createServer(AdventureResource.class);
        server.start();

        //temporarily for test
        String data = "src/main/java/student/myAdventureMap.json";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        adventure.loadData(input, adventure.getInstanceID());

        String[] inputArray = {"Start", "Game"};
        String resultOfDecision2 = adventure.startGameV2(inputArray, adventure);

        while (!resultOfDecision2.equals("Exit Game") && !resultOfDecision2.equals("User Wins")) {
            // process System.in or user input here and refine it into
            // String[] input which will be used in adventure.startGameV2(input, adventure)
            Scanner userInputReader = new Scanner(System.in);
            String userInput = userInputReader.nextLine();
            String[] inputToArray = userInput.toLowerCase().split("\\W+");

            // remove spaces before command. Have to convert array to list and back to array.
            ArrayList inputToList = removeSpacesAtFront(inputToArray);
            inputToArray = new String[inputToList.size()];
            inputToList.toArray(inputToArray);

            resultOfDecision2 = adventure.startGameV2(inputToArray, adventure);

            if (!(resultOfDecision2.equals("keep going")
                    || resultOfDecision2.equals("Took an item")
                    || resultOfDecision2.equals("Dropped an item"))) {
                System.out.println(resultOfDecision2); // for ex, "I can't go XXX" or "I don't understand ~"
            }

            if (resultOfDecision2.equals("You've made it on time to code review; you win!")
                    || resultOfDecision2.equals("Exit Game")) {
                break;
            }

            // when user takes or drops item, show follow-up prompt for next user's command w/o showing description again.
            // likewise, if user made invalid decision or if there's no item in room or inventory,
            // re-ask user for next command w/o showing description again
            if (resultOfDecision2.equals("Took an item") || resultOfDecision2.equals("Dropped an item") ||
                    resultOfDecision2.contains("There is no item ") || resultOfDecision2.contains("You don't have ") ||
                    resultOfDecision2.contains("I don't understand ") || resultOfDecision2.contains("I can't go") ||
                    resultOfDecision2.contains("Your history rooms: ")) {
                System.out.print("> ");
            }
            else {
                adventure.printRoomInformation();//check
                System.out.print("> ");
            }
        }
    }

    private static ArrayList removeSpacesAtFront(String[] inputToArray) {
        ArrayList inputToList = new ArrayList(Arrays.asList(inputToArray));
        for (int i = 0; i < inputToList.size(); i++) {
            if (inputToList.get(i).equals("")) {
                inputToList.remove(i);
            }
        }
        return inputToList;
    }
}