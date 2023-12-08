package danapp.cabotbook.bets;

import java.util.ArrayList;
import java.util.HashMap;

public class Bet {
    public Bet(String name) {
        this.name = name;
        betOdds = new HashMap<>();
    }

    private String name;

    private Double oddsWinAmerican(Double odds) {
        return OddsConverter.decimalToAmerican(odds);
    }

    private Double oddsWinDecimal(Double odds) {
        return OddsConverter.americanToDecimal(odds);
    }


    private HashMap<String, Double> betOdds;

    public void addBetOptionAmerican(String betSubject, Double oddsForSubjectAmerican) {

        this.betOdds.put(betSubject, oddsWinDecimal(oddsForSubjectAmerican));
    }

    public Double getBetOddsDecimal(String betSubject) {
        return this.betOdds.get(betSubject);
    }


    private void clearOdds() {
        this.betOdds.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
