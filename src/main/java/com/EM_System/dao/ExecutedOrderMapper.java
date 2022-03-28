package com.EM_System.dao;

import com.EM_System.pojo.ExecutedOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExecutedOrderMapper {

    List<ExecutedOrder> getExcutedOrderByOrderID(@Param("order_id") int order_id);

    int createExecutedOrder(ExecutedOrder executed_order);
}
