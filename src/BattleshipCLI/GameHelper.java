package BattleshipCLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameHelper {
    private static  final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private final int gridLength, gridSize;
    private final int[] grid;
    private int warshipsPlaced;

    public GameHelper(int gridLength) {
        // maximum allowed grid length = 10
        this.gridLength = Math.min(gridLength, 10);
        this.gridSize = this.gridLength * this.gridLength;
        this.grid = new int[this.gridSize]; // Linearized form of 2D grid

        this.warshipsPlaced = 0;
    }
    public ArrayList<String> placeShip(int shipSize) {
        ArrayList<String> shipCells = new ArrayList<>();

        int[] coordinates  = new int[shipSize];
        int attempts  = 0;
        boolean success = false;
        int location;

        this.warshipsPlaced++;

        int increment = 1; // horizontal placing --> move rightwards
        if (this.warshipsPlaced % 2 == 1) increment = this.gridLength; // vertical placing --> move leftwards

        // limiting the number of attempts to ensure termination of loop in case no location is available
        while ( !success && attempts++ < 200 ){
            location = randomInt(this.gridSize);
            int x = 0; // iterator over ship blocks
            success = true;

            // Iterating over the ship, and attempting to place  each block
            while (success && x < shipSize){
                if (grid[location] != 0){
                    // grid location is not free
                    success = false;
                    break;
                }
                coordinates[x++] = location;
                location += increment;

                if (location >= gridSize) // ship goes outside grid
                    success = false;
                else if (location % gridLength == 0)
                    success = false; // horizontal ship wraps over to next row
            } // end ship iterator while

        }// end attempts while
        if (success){
            // converting grid numbers to alphanumeric co-ordinates of the ship
            for (int co_ord : coordinates){
                // for each coordinate, get alphanumeric form
                this.grid[co_ord] = 1; // mark cell as used;
                int row = (co_ord / this.gridLength);
                int col = co_ord % this.gridLength;

                String shipCell = alphabet.charAt(col) + Integer.toString(row);
                shipCells.add(shipCell);
            }
        }
        return shipCells; // will be empty  if (attempts >= 200) is true
    }

    @org.jetbrains.annotations.Nullable
    public static String getUserInput(String prompt) {
        String inputLine = null;
        System.out.print(prompt + "\t");
        try{
            BufferedReader isr = new BufferedReader(new InputStreamReader(System.in));
            inputLine = isr.readLine();
            if (inputLine.isEmpty()) return null;
        }
        catch (IOException e){
            System.out.println("IOException: " + e);
        }
        return inputLine;
    }

    public static int randomInt(int limit){
        // return integer between 0 to limit-1 inclusive
        return (int) (Math.random() * limit);
    }

}
