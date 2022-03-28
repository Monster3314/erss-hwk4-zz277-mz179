package com.EM_System.dao;

import com.EM_System.pojo.Position;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PositionMapper {

    Position getSpecificPosition(@Param("account_id") int account_id, String symbol_name);

    int createPosition(Position position);

    int editPosition(Position position);
}
