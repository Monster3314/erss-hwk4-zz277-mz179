package com.EM_System.pojo;

public class TransactionsRequestItem {
    public static final short ORDER = 1;
    public static final short QUERY = 2;
    public static final short CANCEL = 3;

    int orderId;
    Order order;
    short type;
    int accountId;

    public TransactionsRequestItem(short type, int orderId, int accountId) {
        this.orderId = orderId;
        this.type = type;
        this.accountId = accountId;
    }

    public TransactionsRequestItem(short type, Order order, int accountId) {
        this.order = order;
        this.type = type;
        this.accountId = accountId;
    }

    public int getOrderId() {
        return orderId;
    }

    public Order getOrder() {
        return order;
    }

    public short getType() {
        return type;
    }

    public int getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
