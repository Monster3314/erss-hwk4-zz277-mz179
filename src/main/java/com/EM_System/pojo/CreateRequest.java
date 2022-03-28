package com.EM_System.pojo;

import java.util.ArrayList;

public class CreateRequest implements Request{
    private final ArrayList<Account> accounts;
    private final ArrayList<Position> positions;

    public CreateRequest(ArrayList<Account> accounts, ArrayList<Position> positions) {
        this.accounts = accounts;
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "accounts: " + accounts + "\npositions: " + positions;
    }
}
