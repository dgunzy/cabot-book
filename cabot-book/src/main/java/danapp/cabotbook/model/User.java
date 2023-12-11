package danapp.cabotbook.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("user")
public class User {

    //Represents a user in the database for saving
    @Id
    private String id;

    private String name;

    private String kindeId;

    private int balance;


    private ArrayList<String> transactionHistory;


    public User(String name, String kindeId, int balance, ArrayList<String> transactionHistory) {
        this.name = name;
        this.kindeId = kindeId;
        this.balance = balance;
        this.transactionHistory = transactionHistory;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKindeId() {
        return kindeId;
    }

    public void setKindeId(String kindeId) {
        this.kindeId = kindeId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }



    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(ArrayList<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
