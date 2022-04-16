package com.EM_System.pojo;

import java.sql.Timestamp;

public class Order {
    private int order_id;
    private int amount;
    private double price;
    private String symbol_name;
    private int account_id;
    private String state;
    private Timestamp timestamp;
    private int version;


    public Order() {

    }

    public Order(int id) {
        this.order_id = id;
    }

    public Order(int amount, Double price, String symbol_name, int account_id) {
        this.amount = amount;
        this.price = price;
        this.symbol_name = symbol_name;
        this.account_id = account_id;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getAccountId() {
        return account_id;
    }

    public int getOrderId() {
        return order_id;
    }

    public void cancelOrder() {
        this.state = "canceled";
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public void execOrder(int amount) {
        this.amount += amount;
        if (this.amount == 0) {
            //TODO: state should be 2 if available
            this.state = "executed";
        }
    }

    public int getAmount() {
        return amount;
    }

    public String getState() {
        return state;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol_Name() {
        return symbol_name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "(id: " + order_id + ", account_id: " + account_id + ", amount: " + amount + ", symbol: " + symbol_name + ", limit: " + price + ")";
    }
}
