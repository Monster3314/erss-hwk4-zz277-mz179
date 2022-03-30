package com.EM_System.pojo;

import com.EM_System.app.RequestExecutor;

public class Account implements CreateRequestItem {
    private int account_id;
    private Double balance;
    private int version;

    public Account() {

    }

    public Account(int id, Double balance) {
        this.account_id = id;
        this.balance = balance;
    }

    public int getAccountId() {
        return account_id;
    }

    public Double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return "(account_id: " + account_id + ", balance: " + balance + ")";
    }

    @Override
    public Result exec(RequestExecutor executor) {
        return executor.executeCreate(this);
    }
}


