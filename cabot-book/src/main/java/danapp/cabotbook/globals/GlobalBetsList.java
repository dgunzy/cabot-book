package danapp.cabotbook.globals;

import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.bets.PlacedBet;
import danapp.cabotbook.people.UserApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class GlobalBetsList {

    private static GlobalBetsList instance;

    private ArrayList<Bet> currentBets;

    private GlobalBetsList() {
        currentBets = new ArrayList<>();
    }

    public static GlobalBetsList getInstance() {
        if (instance == null) {
            instance = new GlobalBetsList();
        }
        return instance;
    }

    public ArrayList<Bet> getCurrentBets() {
        return currentBets;
    }

    public void addBetToGlobalList(Bet bet) {currentBets.add(bet);
    }
    public void clearAllBets() {
        currentBets.clear();
    }

    public void setCurrentBets(ArrayList<Bet> currentBets) {
        this.currentBets = currentBets;
    }

    public void removeBetFromGlobalList(String name) {
        Iterator<Bet> iterator = currentBets.iterator();

        while (iterator.hasNext()) {
            Bet bet = iterator.next();
            if(Objects.equals(bet.getName(), name)) {
                iterator.remove();
            }
        }
    }


}
