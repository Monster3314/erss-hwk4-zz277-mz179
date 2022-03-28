package com.EM_System.pojo;

import java.util.ArrayList;

public class TransactionsRequest implements Request{
    private ArrayList<Order> orders;
    private ArrayList<Order> queries;
    private ArrayList<Order> cancels;

    public TransactionsRequest(ArrayList<Order> orders, ArrayList<Order> queries, ArrayList<Order> cancels) {
        this.orders = orders;
        this.queries = queries;
        this.cancels = cancels;
    }
}
