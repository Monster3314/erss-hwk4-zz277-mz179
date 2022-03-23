package com.EM_System.pojo;

public class Account {
    private int id;
    private Double balance;
    private int version;

    public Account() {

    }

    public Account(int id, Double balance, int version) {
        this.id = id;
        this.balance = balance;
        this.version = version;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance='" + balance + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}


