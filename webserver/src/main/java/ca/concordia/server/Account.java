package ca.concordia.server;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {
    //represent a bank account with a balance and withdraw and deposit methods
    private AtomicInteger balance;
    private String id;

    public Account(AtomicInteger balance, String id){

        this.balance = balance;
        this.id = id;
    }

    public AtomicInteger getBalance(){
        return balance;
    }

    public int withdraw(int amount){
        balance.addAndGet(-amount);
        return amount;
    }

    public int deposit(int amount){
        balance.addAndGet(amount);
        return amount;
    }

    public String getID(){
        return id;
    }
    
    public void setID(String ID){
        id = ID;
    }
}
