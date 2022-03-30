package com.EM_System.pojo;

import java.sql.Timestamp;

public class Order {
    private int order_id;
    private int amount;
    private double price;
    private String symbol;
    private int account_id;
    private int state;
    private Timestamp timestamp;
    private int version;


    public Order() {

    }

    public Order(int id) {
        this.order_id = id;
    }

    public Order(int amount, Double price, String symbol, int account_id) {
        this.amount = amount;
        this.price = price;
        this.symbol = symbol;
        this.account_id = account_id;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getAccountId() {
        return account_id;
    }

    public int getOrderId() {
        return order_id;
    }

    @Override
    public String toString() {
        return "(id: " + order_id + ", account_id: " + account_id + ", amount: " + amount + ", symbol: " + symbol + ", limit: " + price + ")";
    }
}
