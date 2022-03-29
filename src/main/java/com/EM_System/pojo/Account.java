package com.EM_System.pojo;

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

    @Override
    public String toString() {
        return "(account_id: " + account_id + ", balance: " + balance + ")";
    }
}


