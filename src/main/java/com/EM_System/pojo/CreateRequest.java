package com.EM_System.pojo;

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
}
