package BattleshipGame;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameHelper {
    private static  final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private final int gridSize, cellCount;
    private final int[] grid;
    private int warshipsPlaced;

    public GameHelper(int gridSize) {
        // maximum allowed grid length = 10
        this.gridSize = Math.min(gridSize, 10);
        this.cellCount = this.gridSize * this.gridSize;
        this.grid = new int[this.cellCount]; // Linearized form of 2D grid

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
        if (this.warshipsPlaced % 2 == 1) increment = this.gridSize; // vertical placing --> move leftwards

        // limiting the number of attempts to ensure termination of loop in case no location is available
        while ( !success && attempts++ < 200 ){
            location = randomInt(this.cellCount);
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

                if (location >= cellCount) // ship goes outside grid
                    success = false;
                else if (location % gridSize == 0)
                    success = false; // horizontal ship wraps over to next row
            } // end ship iterator while

        }// end attempts while
        if (success){
            // converting grid numbers to alphanumeric co-ordinates of the ship
            for (int co_ord : coordinates){
                // for each coordinate, get alphanumeric form
                this.grid[co_ord] = 1; // mark cell as used;
                int row = (co_ord / this.gridSize);
                int col = co_ord % this.gridSize;

                String shipCell = alphabet.charAt(col) + Integer.toString(row);
                shipCells.add(shipCell);
            }
        }
        return shipCells; // will be empty  if (attempts >= 200) is true
    }

    public void reset(){
        this.warshipsPlaced = 0;
        for (int i = 0; i<this.cellCount; i++){
            this.grid[i] = 0;
        }
    }

    public static @NotNull String getUserInput(@NotNull String prompt) {
        String inputLine = null;
        System.out.print(prompt + "\t");
        try{
            BufferedReader isr = new BufferedReader(new InputStreamReader(System.in));
            inputLine = isr.readLine();
            if (inputLine.isEmpty()) return "";
        }
        catch (IOException e){
            System.out.println("IOException: " + e);
        }
        return inputLine == null ? "" : inputLine;
    }

    public static int randomInt(int limit){
        // return integer between 0 to limit-1 inclusive
        return (int) (Math.random() * limit);
    }
}
