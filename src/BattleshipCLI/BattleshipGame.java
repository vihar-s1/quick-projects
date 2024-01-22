package BattleshipCLI;

import java.util.ArrayList;

class BattleshipGame {

    private int guessCount = 0;
    private final int gridSize = 7;
    private final int shipCount = 3;
    private final GameHelper helper = new GameHelper(gridSize);
    private final ArrayList<WarShip> warShipList = new ArrayList<>();

    private void leakGame() {
        // prints all the ship names and their locations
        // for development purposes
        for (WarShip ship : this.warShipList) {
            System.out.println(ship.name());
            for (String cell : ship.locationCells())
                System.out.print(cell + "  ");
            System.out.println();
        }
    }

    public int getGuessCount() { return guessCount; }
    public int getGridSize() { return gridSize; }
    public int getShipCount() { return shipCount; }

    public void setUpGame() {
        // Create Ships and place them at random locations
        String[] shipName = {"Carrier5", "Battleship4", "Cruiser3", "Submarine3", "destroyer2"};

        for (int i = 0; i < this.shipCount; i++) {
            String name = shipName[GameHelper.randomInt(shipName.length)];
            WarShip warShip = new WarShip(
                    name,
                    helper.placeShip(name.charAt(name.length() - 1) - '0')  // the last character of the name is the size of ship
            );
            this.warShipList.add(warShip);
        }
    }

    public void startPlaying() {
        System.out.println("********** WELCOME TO BATTLESHIP **********");

        System.out.println("Following WarShips are hidden over a size "+ this.gridSize + " grid...");
        for (int i = 0; i < this.shipCount; i++) {
            System.out.println((i + 1) + ". " + this.warShipList.get(i).name());
        }
        System.out.println("\nTry to sink them all in fewest number of guesses\n");

        // game play
        while (!this.warShipList.isEmpty()) {
            String userGuess = GameHelper.getUserInput("Guess a Position: ");
            String result = checkUserGuess(userGuess);
            System.out.println(result);
        }
        finishGame();
    }

    private String checkUserGuess(String guess) {
        this.guessCount++;

        for (WarShip warShip : this.warShipList) {
            String result = warShip.checkYourself(guess);
            if (result.equals("kill")) {
                this.warShipList.remove(warShip);
                return "killed " + warShip.name();
            } else if (result.equals("hit"))
                return "hit " + warShip.name();
        }

        return "miss";
    }

    private void finishGame() {
        System.out.println("All warships have been sunk!!");
        System.out.println("It took you " + this.guessCount + " guesses.");
    }
}
