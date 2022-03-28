package com.EM_System.dao;

import com.EM_System.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    Order getMatchingSellOrder(Order order);

    Order getMatchingBuyOrder(Order order);

    Order getOrderByID(@Param("order_id") int order_id);

    int createOrder(Order order);

    int editOrder(Order order);

    int cancelOrder(@Param("order_id") int order_id);
}
