package com.EM_System.pojo;

import com.EM_System.app.RequestExecutor;

import java.util.ArrayList;

public class TransactionsRequest implements Request{
    private final ArrayList<TransactionsRequestItem> requestItems;

    public TransactionsRequest(ArrayList<TransactionsRequestItem> requestItems) {
        this.requestItems = requestItems;
    }

    @Override
    public String toString() {
        return "requests: " + requestItems;
    }

    @Override
    public ArrayList<Result> exec(RequestExecutor executor) {
        ArrayList<Result> results = new ArrayList<>();
        for (TransactionsRequestItem item : requestItems) {
            if (item == null) {
                results.add(new Result());
                continue;
            }
            short type = item.getType();
            if (type == TransactionsRequestItem.ORDER) {
                results.add(executor.executeOrder(item.getOrder()));
            }
            else if (type == TransactionsRequestItem.QUERY) {
                results.add(executor.executeQuery(item.getOrderId()));
            }
            else if (type == TransactionsRequestItem.CANCEL) {
                results.add(executor.executeCancel(item.getOrderId(), item.getAccountId()));
            }
        }
        return results;
    }
}
