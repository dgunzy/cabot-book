package danapp.cabotbook.bets;

import java.util.ArrayList;
import java.util.HashMap;

public class Bet {

    private String name;

    private Double oddsWinAmerican(Double odds) {
        return OddsConverter.decimalToAmerican(odds);
    }

    private Double oddsWinDecimal(Double odds) {
        return OddsConverter.americanToDecimal(odds);
    }


    private HashMap<String, Double> betOdds;

    public void addBetOptionAmerican(String betSubject, Double oddsForSubjectAmerican) {
        betOdds.put(betSubject, oddsWinDecimal(oddsForSubjectAmerican));
    }


    private void clearOdds() {
        betOdds.clear();
    }
}
