package danapp.cabotbook.people;

import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.bets.PlacedBet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class User {

    public User(String name, int balance, String kindeId) {
        this.name = name;
        this.balance = balance;
        this.kindeId = kindeId;
    }


    private String name;

    private int balance;

    private String kindeId;

    private ArrayList<PlacedBet> pendingBets = null;

    private ArrayList<PlacedBet> approvedBets = null;

    private ArrayList<PlacedBet> gradedBets = null;


    public void addBetToPending(String betKey, Double odds, int wager) {
        PlacedBet newBet = new PlacedBet(betKey, odds, wager);
        this.balance -= wager;
    }

    public void approvePendingBet(String betname) {
        Iterator<PlacedBet> iterator = pendingBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet placedBet = iterator.next();
            if(placedBet.getKey().equals(betname)) {
                approvedBets.add(placedBet);
                iterator.remove();
            }
        }
    }

    public void denyPendingBet(String betname) {
        Iterator<PlacedBet> iterator = pendingBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet placedBet = iterator.next();
            if(placedBet.getKey().equals(betname)) {
                this.balance += placedBet.getWager();
                iterator.remove();
            }
        }
    }
    public void winPendingBet(String betname) {
        Iterator<PlacedBet> iterator = approvedBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet approvedBet = iterator.next();
            if(approvedBet.getKey().equals(betname)) {
                this.balance += approvedBet.totalBookRisk();
                approvedBet.setDidWin(true);
                gradedBets.add(approvedBet);
                iterator.remove();
            }
        }
    }
    public void losePendingBet(String betname) {
        Iterator<PlacedBet> iterator = approvedBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet approvedBet = iterator.next();
            if(approvedBet.getKey().equals(betname)) {
                this.balance -= approvedBet.totalBookRisk();
                approvedBet.setDidWin(false);
                gradedBets.add(approvedBet);
                iterator.remove();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getKindeId() {
        return kindeId;
    }

    public void setKindeId(String kindeId) {
        this.kindeId = kindeId;
    }

}