package com.EM_System.pojo;

import java.sql.Date;
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

    public Order(int id, int amount, Double price, String symbol, int account_id) {
        this.order_id = id;
        this.amount = amount;
        this.price = price;
        this.symbol = symbol;
        this.account_id = account_id;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

}
