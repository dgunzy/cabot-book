package danapp.cabotbook.bets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;



public class Bet implements  Cloneable{

    //Bet, which can be cloned
    public Bet(String name, ArrayList<BetOdds> betOdds) {
        this.name = name;
        this.betOdds = betOdds;
    }

    private String name;


    @JsonProperty("betOdds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArrayList<BetOdds> betOdds;

    @Override
    public Bet clone() {
        try {
            Bet clonedBet = (Bet) super.clone();

            if (betOdds != null) {
                clonedBet.betOdds = new ArrayList<>();
                for (BetOdds betOdd : betOdds) {
                    clonedBet.betOdds.add(betOdd.clone());
                }
            }

            return clonedBet;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    public String getName() {
        return name;
    }

    public void clearOdds() {
        this.betOdds.clear();
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


