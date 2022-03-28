package com.EM_System.dao;

import com.EM_System.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    List<Order> getOrderList();

    Order getOrderByID(@Param("order_id") int order_id);

    int createOrder(Order order);

    int editOrder(Order order);
}
