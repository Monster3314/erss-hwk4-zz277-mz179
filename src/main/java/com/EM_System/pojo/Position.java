package com.EM_System.pojo;

public class Position implements CreateRequestItem {
    private int account_id;
    private int amount;
    private String symbol_name;

    public Position() {

    }

    public Position(int id, int amount, String symbol_name) {
        this.account_id = id;
        this.amount = amount;
        this.symbol_name = symbol_name;
    }

    public int getAccountId() {
        return account_id;
    }

    public String getSymbol() {
        return symbol_name;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    @Override
    public String toString() {
        return "(account_id: " + account_id + ", amount: " + amount + ", symbol: " + symbol_name + ")";
    }
}


