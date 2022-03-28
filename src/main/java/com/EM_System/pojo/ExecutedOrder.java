package com.EM_System.pojo;

import java.sql.Timestamp;

public class ExecutedOrder {
    private int amount;
    private Double price;
    private int buy_id;
    private int sell_id;
    private Timestamp timestamp;


    public ExecutedOrder() {

    }

    public ExecutedOrder(int amount, Double price, int buy_id, int sell_id) {
        this.amount = amount;
        this.price = price;
        this.buy_id = buy_id;
        this.sell_id = sell_id;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

}
