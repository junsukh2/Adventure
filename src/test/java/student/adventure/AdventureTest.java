package student.adventure;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.List;

public class AdventureTest {
    private Adventure adventure;
    private PrintStream old = System.out;
    private ByteArrayOutputStream outputHere;

    @Before
    public void setUp() throws FileNotFoundException {
        // This is run before every test.
        adventure = new Adventure();
        String data = "src/main/java/student/myAdventureMap.json";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        adventure.loadData(input, adventure.getInstanceID());
    }

    @Test (expected = FileNotFoundException.class)
    public void invalidPath() throws FileNotFoundException {
        String data = "src/main/resources/invalid.json";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        adventure.loadData(input, adventure.getInstanceID());
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidScheme() throws FileNotFoundException {
        String data = "src/main/java/student/myInvalid.json";
        ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        adventure.loadData(input, adventure.getInstanceID());
    }

    @Test
    public void invalidDirection() {
        String[] input = {"go", "Heaven"};

        String evaluated = adventure.startGameV2(input, adventure);

        String expected = "I can't go \"Heaven\"!";

        assertEquals(expected, evaluated);
    }

    @Test
    public void userInputWhiteSpace() {
        outputHere = voidOutput();

        String userInput = "go     west";

        // this line is used in main() to take care of such a case
        String[] inputToArray = userInput.toLowerCase().split("\\W+");

        adventure.startGameV2(inputToArray,adventure);

        adventure.printRoomInformation();

        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are at the student dining and residential program building . " +
                "Get Information you need!\r\n" +
                "From here you can go: East, South or Up\r\n" +
                "Items visible: information desk, snack, dorm or vending machine\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void undefinedCommand() {
        String[] input = {"fly to the sky"};

        String evaluated = adventure.startGameV2(input,  adventure);

        String expected = "I don't understand \"fly to the sky\"!";

        assertEquals(expected, evaluated);
    }

    @Test
    public void caseSensitivity() {
        outputHere = voidOutput();

        String userInput = "take HamBURGer";
        String[] inputToArray = userInput.toLowerCase().split("\\W+");

        adventure.startGameV2(inputToArray, adventure);

        adventure.printRoomInformation();

        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are on Matthews, outside the Siebel Center\r\n" +
                "From here you can go: East, West or South\r\n" +
                "Items visible: frontdoor key or backdoor key\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void dropItemAlreadyInItemList() {
        // add an item which is already in a room to inventory
        List<String> inventory = adventure.getInventory();
        inventory.add("hamburger");

        // set user's inventory with the one just made above
        adventure.setInventory(inventory);

        // drop a hamburger in the inventory at a room
        adventure.dropItem("hamburger");

        outputHere = voidOutput();
        adventure.printRoomInformation();
        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are on Matthews, outside the Siebel Center\r\n" +
                "From here you can go: East, West or South\r\n" +
                "Items visible: frontdoor key, backdoor key, hamburger or hamburger\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void dropItemNotInInventory() {
        // drop brain which is not in user's inventory
        String evaluated = adventure.dropItem("brain");

        String expected = "You don't have \"brain\"!";

        assertEquals(expected ,evaluated);
    }

    @Test
    public void dropItem() {
        // pick two items and drop one of them back at room
        adventure.pickItem("hamburger");
        adventure.pickItem("frontdoor key");
        adventure.dropItem("hamburger");

        // use alternative stream to save System.out.println value
        outputHere = voidOutput();
        adventure.printRoomInformation();
        // convert stream to string
        String evaluated = outputHere.toString();

        // put stream back to original
        putBackStreamer();

        String expected = "You are on Matthews, outside the Siebel Center\r\n" +
                "From here you can go: East, West or South\r\n" +
                "Items visible: backdoor key or hamburger\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void checkAllItemsTaken() {
        // take all items in a room
        adventure.pickItem("hamburger");
        adventure.pickItem("frontdoor key");
        adventure.pickItem("backdoor key");

        outputHere = voidOutput();
        adventure.printRoomInformation();
        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are on Matthews, outside the Siebel Center\r\n" +
                "From here you can go: East, West or South\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void takeTooManySameItem() {
        // pick hamburger twice while there's only one hamburger
        adventure.pickItem("hamburger");

        String evaluated = adventure.pickItem("hamburger");

        String expected = "There is no item \"hamburger\" in the room.";

        assertEquals(expected, evaluated);
    }

    @Test
    public void takeInvalidItem() {
        // pick unavailable item zebra
        String evaluated = adventure.pickItem("zebra");

        String expected = "There is no item \"zebra\" in the room.";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testFindNextRoom() {
        String evaluated = adventure.findNextRoom("west");
        String expected = "Ikenberry";

        assertEquals(expected, evaluated);
    }

    @Test
    public void takeItem() {
        adventure.pickItem("hamburger");

        outputHere = voidOutput();
        adventure.printRoomInformation();
        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are on Matthews, outside the Siebel Center\r\n" +
                "From here you can go: East, West or South\r\n" +
                "Items visible: frontdoor key or backdoor key\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testPrintRoomDirections() {
        outputHere = voidOutput();

        adventure.printRoomDirections();

        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "From here you can go: East, West or South\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testPrintRoomInformation() {
        outputHere = voidOutput();

        // set currentRoomObjects as an object of room "ARC"
        adventure.setCurrentRoomObject("ARC");

        // save System.output.print in outputHere and convert it to String
        adventure.printRoomInformation();
        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are at the recreation center of UIUC. Enjoy weight training!\r\n" +
                "From here you can go: North, NorthWest or Down\r\n" +
                "Items visible: front desk or covid test center\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testPrintRoomItems() {
        // set currentRoomObjects as an object of room "ARC"
        adventure.setCurrentRoomObject("ARC");

        outputHere = voidOutput();
        adventure.printRoomItems();
        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "Items visible: front desk or covid test center\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testSetCurrentRoomObject() {
        outputHere = voidOutput();

        adventure.setCurrentRoomObject("ARC");
        adventure.printRoomInformation();

        String evaluated = outputHere.toString();

        putBackStreamer();

        String expected = "You are at the recreation center of UIUC. Enjoy weight training!\r\n" +
                "From here you can go: North, NorthWest or Down\r\n" +
                "Items visible: front desk or covid test center\r\n";

        assertEquals(expected, evaluated);
    }

    @Test
    public void correctOutputAfterMove() {
        outputHere = voidOutput();

        adventure.startGameV2(new String[]{"go", "east"}, adventure);

        adventure.printRoomInformation();
        String evaluated = outputHere.toString();
        putBackStreamer();

        String expected = "You are in the west entry of Siebel Center. You can see the elevator, " +
                "the ACM office, and hallways to the north and east.\r\n" +
                "From here you can go: West, Northeast, North or East\r\n" +
                "Items visible: information, motivated or scared\r\n";

        assertEquals(expected, evaluated);
    }

    /**
     * Test when user inputs "quit"
     */
    @Test
    public void quit() {
        String[] input = {"quit"};

        String evaluated = adventure.startGameV2(input, adventure);

        String expected = "Exit Game";

        assertEquals(expected, evaluated);
    }

    /**
     * Test when user inputs "quit"
     */
    @Test
    public void exit() {
        String[] input = {"exit"};

        String evaluated = adventure.startGameV2(input,adventure);

        String expected = "Exit Game";

        assertEquals(expected, evaluated);
    }

    @Test
    public void winGame() {
        // save output of goNextRoom in outputHere and then make a string out of the outputHere
        String evaluated = adventure.startGameV2(new String[]{"go", "south"}, adventure);

        String expected = "You've made it on time to code review; you win!";

        assertEquals(expected, evaluated);
    }

    @Test
    public void testExamine() {
        String[] input = {"examine"};

        String evaluated = adventure.startGameV2(input, adventure);

        String expected = "keep going";

        assertEquals(expected, evaluated);
    }

    /**
     * Helper functions
     */

    /**
     * Uses the value from System.out.print of void function
     * @return ByteArrayOutputStream
     */
    private ByteArrayOutputStream voidOutput() {
        outputHere = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputHere);
        System.setOut(ps);
        return outputHere;
    }

    private void putBackStreamer() {
        System.out.flush();
        System.setOut(old);
    }
}