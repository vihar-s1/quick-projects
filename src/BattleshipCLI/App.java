package BattleshipCLI;

public class App {
    public static void main(String[] args) {
        BattleshipGame game = new BattleshipGame();
        game.setUpGame();
        game.startPlaying();
    }
}
