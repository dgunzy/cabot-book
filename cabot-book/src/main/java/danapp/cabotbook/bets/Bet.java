package danapp.cabotbook.bets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;



public class Bet {
    public Bet(String name, ArrayList<BetOdds> betOdds) {
        this.name = name;
        this.betOdds = betOdds;
    }

    private String name;


    @JsonProperty("betOdds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArrayList<BetOdds> betOdds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<BetOdds> getBetOdds() {
        return betOdds;
    }

    public void setBetOdds(ArrayList<BetOdds> betOdds) {
        this.betOdds = betOdds;
    }
}


