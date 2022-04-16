package com.EM_System.pojo;

import com.EM_System.app.RequestExecutor;

import java.util.ArrayList;

public class CreateRequest implements Request{
    private final ArrayList<CreateRequestItem> requestItems;

    public CreateRequest(ArrayList<CreateRequestItem> requestItems) {
        this.requestItems = requestItems;
    }

    @Override
    public String toString() {
        return "requests: " + requestItems;
    }

    @Override
    public ArrayList<Result> exec(RequestExecutor executor) {
        ArrayList<Result> results = new ArrayList<>();
        for (CreateRequestItem item : requestItems) {
            if (item == null) {
                results.add(new Result());
            }
            else {
                results.add(item.exec(executor));
            }
        }
        return results;
    }
}
