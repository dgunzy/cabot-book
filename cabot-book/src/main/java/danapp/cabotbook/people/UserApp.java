package danapp.cabotbook.people;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danapp.cabotbook.bets.PlacedBet;
import danapp.cabotbook.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class UserApp {

    public UserApp(String name, int balance, String kindeId, ArrayList<String> transactionHistory) {
        this.name = name;
        this.balance = balance;
        this.kindeId = kindeId;
        this.transactionHistory = transactionHistory;
        this.pendingBets = new ArrayList<>();
        this.approvedBets = new ArrayList<>();

    }

    public UserApp() {

    }

    public static UserApp fromDatabaseModel(User user) {
        return new UserApp(user.getName(), user.getBalance(), user.getKindeId(), user.getTransactionHistory());
    }

    public User databaseUserFromUserApp() {
        return new User(this.name, this.kindeId, this.balance, this.transactionHistory);
    }

    public static boolean doesUserExist(ArrayList<UserApp> userList, String targetId) {
        for (UserApp obj : userList) {
            if (obj.getKindeId().equals(targetId)) {
                return true;
            }
        }
        return false;
    }

    public void updateFrom(UserApp newUser) {
        this.name = newUser.getName();
        this.balance = newUser.getBalance();
        this.kindeId = newUser.getKindeId();
        this.pendingBets = (newUser.getPendingBets() != null) ? new ArrayList<>(newUser.getPendingBets()) : this.pendingBets;
        this.approvedBets = (newUser.getApprovedBets() != null) ? new ArrayList<>(newUser.getApprovedBets()) : this.approvedBets;
        this.gradedBets = (newUser.getGradedBets() != null) ? new ArrayList<>(newUser.getGradedBets()) : this.gradedBets;
        this.transactionHistory = (newUser.getTransactionHistory() != null) ? new ArrayList<>(newUser.getTransactionHistory()) : this.transactionHistory;
    }


    private String name;

    private int balance;

    private String kindeId;

    private ArrayList<PlacedBet> pendingBets ;

    private ArrayList<PlacedBet> approvedBets ;


    private ArrayList<String> gradedBets;

    private ArrayList<String> transactionHistory;


    public void viewPendingBets() {
        for (PlacedBet pendingBet : pendingBets) {
            System.out.println(pendingBet.getKey() + pendingBet.getWager());
        }
    }
    public void viewApprovedBets() {
        for (PlacedBet approvedBet : approvedBets) {
            System.out.println(approvedBet.getKey() + approvedBet.getWager());
        }
    }


    public ArrayList<PlacedBet> getPendingBets() {
        return pendingBets;
    }

    public ArrayList<PlacedBet> getApprovedBets() {
        return approvedBets;
    }



    public void addBetToPending(String betKey, Double odds, int wager, String description) {
        PlacedBet newBet = new PlacedBet(betKey, odds, wager, description);
        this.balance -= wager;
        pendingBets.add(newBet);
    }

    public void approvePendingBet(String description, String horse) {
        Iterator<PlacedBet> iterator = pendingBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet placedBet = iterator.next();
            if(Objects.equals(placedBet.getUniqueDescription(), description) && Objects.equals(placedBet.getKey(), horse)) {
                approvedBets.add(placedBet);
                iterator.remove();
            }
        }
    }

    public void denyPendingBet(String description, String horse) {
        Iterator<PlacedBet> iterator = pendingBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet placedBet = iterator.next();
            if(Objects.equals(placedBet.getUniqueDescription(), description) && Objects.equals(placedBet.getKey(), horse)) {
                this.balance += placedBet.getWager();
                iterator.remove();
            }
        }
    }
    public void winApprovedBet(String description, String horse) throws JsonProcessingException {
        Iterator<PlacedBet> iterator = approvedBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet approvedBet = iterator.next();
            if(Objects.equals(approvedBet.getUniqueDescription(), description) && Objects.equals(approvedBet.getKey(), horse)) {
                this.balance += approvedBet.totalBookRisk();
                approvedBet.setDidWin(true);

                this.transactionHistory.add("Your bet on " + approvedBet.getKey() + " won! You have been credited with " + Math.round(approvedBet.totalBookRisk()) + ". Your balance is now " + this.balance);

                iterator.remove();
            }
        }
    }
    public void loseApprovedBet(String description, String horse) throws JsonProcessingException {
        Iterator<PlacedBet> iterator = approvedBets.iterator();
        while (iterator.hasNext()) {
            PlacedBet approvedBet = iterator.next();
            if(Objects.equals(approvedBet.getUniqueDescription(), description) && Objects.equals(approvedBet.getKey(), horse)) {
                this.balance -= approvedBet.getWager();
                approvedBet.setDidWin(false);
                this.transactionHistory.add("Your bet on " + approvedBet.getKey() + " lost. " + approvedBet.getWager() + " has been deducted from your account. Your balance is now " + this.balance);
                iterator.remove();
            }
        }
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(ArrayList<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
    public void setPendingBets(ArrayList<PlacedBet> pendingBets) {
        this.pendingBets = pendingBets;
    }

    public void setApprovedBets(ArrayList<PlacedBet> approvedBets) {
        this.approvedBets = approvedBets;
    }

    public ArrayList<String> getGradedBets() {
        return gradedBets;
    }

    public void setGradedBets(ArrayList<String> gradedBets) {
        this.gradedBets = gradedBets;
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