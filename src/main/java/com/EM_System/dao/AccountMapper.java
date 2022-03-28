package com.EM_System.dao;
import com.EM_System.pojo.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper {

    List<Account> getAccList();

    Account getAccByID(@Param("id") int id);

    int deleteAcc(@Param("id") int id);

    int updateAcc(Account account);

    int addAcc(Account account);

}



