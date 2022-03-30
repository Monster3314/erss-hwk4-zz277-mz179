package com.EM_System.pojo;

import com.EM_System.app.RequestExecutor;

import java.util.ArrayList;

public class TransactionsRequest implements Request{
    private final ArrayList<Order> orders;
    private final ArrayList<Order> queries;
    private final ArrayList<Order> cancels;

    public TransactionsRequest(ArrayList<Order> orders, ArrayList<Order> queries, ArrayList<Order> cancels) {
        this.orders = orders;
        this.queries = queries;
        this.cancels = cancels;
    }

    @Override
    public String toString() {
        return "orders: " + orders + "\npositions: " + queries + "\ncancels" + cancels;
    }

    @Override
    public ArrayList<Result> exec(RequestExecutor executor) {
        return new ArrayList<>();
    }
}
