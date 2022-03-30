package com.EM_System.pojo;

public class TransactionsRequestItem {
    public static final short ORDER = 1;
    public static final short QUERY = 2;
    public static final short CANCEL = 3;

    int orderId;
    Order order;
    short type;

    public TransactionsRequestItem(short type, int orderId) {
        this.orderId = orderId;
        this.type = type;
    }

    public TransactionsRequestItem(short type, Order order) {
        this.order = order;
        this.type = type;
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

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
