package BattleshipCLI;

import java.util.ArrayList;

public record WarShip(String name, ArrayList<String> locationCells) {
    public String checkYourself(String guess) {
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
//    public WarShip(String name, ArrayList<String> locationCells){
//        this.name = name;
//        this.locationCells = locationCells;
//    }
//    public String checkYourself(String guess){
//        int hitIndex = locationCells.indexOf(guess);
//        if (hitIndex == -1) return "miss";
//
//        this.locationCells.remove(hitIndex);
//
//        if (this.locationCells.isEmpty()) return "kill";
//        else return "hit";
//    }
//
//    public String getName() { return this.name; }
//    public ArrayList<String> getLocationCells() { return this.locationCells; }
//}
