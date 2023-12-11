package danapp.cabotbook.bets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



public class BetOdds implements Cloneable {

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

    @Override
    public BetOdds clone() {
        try {
            return (BetOdds) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
