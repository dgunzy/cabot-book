package danapp.cabotbook.bets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



public class BetOdds {

    @JsonProperty("horse")
    private String horse;

    @JsonProperty("odds")
    private double odds;

    public BetOdds(String horse, double odds) {
        this.horse = horse;
        this.odds = odds;
    }

    public String getHorse() {
        return horse;
    }

    public void setHorse(String horse) {
        this.horse = horse;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }
}
