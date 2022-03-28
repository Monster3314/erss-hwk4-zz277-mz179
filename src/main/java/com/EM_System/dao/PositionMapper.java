package com.EM_System.dao;

import com.EM_System.pojo.Position;
import org.apache.ibatis.annotations.Param;


public interface PositionMapper {

    Position getSpecificPosition(@Param("account_id") int account_id, @Param("symbol_name") String symbol_name);

    int createPosition(Position position);

    int editPosition(Position position);
}
