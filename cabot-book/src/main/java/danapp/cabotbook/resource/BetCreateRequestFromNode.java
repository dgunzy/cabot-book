package danapp.cabotbook.resource;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class BetCreateRequestFromNode {

    private String name;

    private ArrayList<String> horse;

    private ArrayList<Integer> odds;

    public BetCreateRequestFromNode(String name, ArrayList<String> horse, ArrayList<Integer> odds) {
        this.name = name;
        this.horse = horse;
        this.odds = odds;
    }
    public  BetCreateRequestFromNode() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getHorse() {
        return horse;
    }

    public void setHorse(ArrayList<String> horse) {
        this.horse = horse;
    }

    public ArrayList<Integer> getOdds() {
        return odds;
    }

    public void setOdds(ArrayList<Integer> odds) {
        this.odds = odds;
    }
}

