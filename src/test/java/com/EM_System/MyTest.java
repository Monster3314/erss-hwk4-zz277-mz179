package com.EM_System;
import com.EM_System.pojo.Account;
import com.EM_System.dao.AccountMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MyTest {
    @Test
    public void selectAcc() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            List<Account> accounts = mapper.getAccList();
            for (Account account : accounts) {
                System.out.println(account.toString());
            }
            sqlSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getAccByID() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            Account account = mapper.getAccByID(3);
            System.out.println(account);
            sqlSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




