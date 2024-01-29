package BattleshipGame;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record WarShip(@NotNull String name, @NotNull ArrayList<String> locationCells) {
    public @NotNull String checkYourself(String guess) {
        int hitIndex = locationCells.indexOf(guess);
        if (hitIndex == -1) return "miss";

        this.locationCells.remove(hitIndex);

        if (this.locationCells.isEmpty()) return "kill";
        else return "hit";
    }
}
//public class WarShip {
//    private final String name;
//    private final ArrayList<String> locationCells;
//
//    public WarShip(@NotNull String name, @NotNull ArrayList<String> locationCells){
//        this.name = name;
//        this.locationCells = locationCells;
//    }
//    public @NotNull String checkYourself(String guess){
//        int hitIndex = locationCells.indexOf(guess);
//        if (hitIndex == -1) return "miss";
//
//        this.locationCells.remove(hitIndex);
//
//        if (this.locationCells.isEmpty()) return "kill";
//        else return "hit";
//    }
//
//    public @NotNull String getName() { return this.name; }
//    public ArrayList<String> getLocationCells() { return this.locationCells; }
//}
