package com.EM_System.dao;

import com.EM_System.pojo.Position;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PositionMapper {

    List<Position> getPositionByID(@Param("account_id") int account_id);

    int createPosition(Position position);

    int editPosition(Position position);
}
