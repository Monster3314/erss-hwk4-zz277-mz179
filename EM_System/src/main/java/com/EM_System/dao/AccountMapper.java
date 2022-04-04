package com.EM_System.dao;
import com.EM_System.pojo.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper {

    List<Account> getAccList();

    Account getAccByID(@Param("account_id") int account_id);

    int deleteAcc(@Param("account_id") int account_id);

    int updateAcc(Account account);

    int addAcc(Account account);

}



