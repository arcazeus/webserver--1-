package ca.concordia.server;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {
    //represent a bank account with a balance and withdraw and deposit methods
    private AtomicInteger balance;
    private int id;

    public Account(AtomicInteger balance, int id){

        this.balance = balance;
        this.id = id;
    }

    public AtomicInteger getBalance(){
        return balance;
    }

    public void withdraw(int amount){
        balance.addAndGet(-amount);
    }

    public void deposit(int amount){
        balance.addAndGet(amount);
    }

    public int getID(){
        return id;
    }
    
}
