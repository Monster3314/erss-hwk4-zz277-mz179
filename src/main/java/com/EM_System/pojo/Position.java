package com.EM_System.pojo;

public class Position {
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

    @Override
    public String toString() {
        return "(account_id: " + account_id + ", amount: " + amount + ", symbol: " + symbol_name + ")";
    }
}


