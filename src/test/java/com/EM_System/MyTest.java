//package com.EM_System;
//import com.EM_System.pojo.Account;
//import com.EM_System.dao.AccountMapper;
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//
//public class MyTest {
//    @Test
//    public void selectAcc() {
//        try {
//            String resource = "mybatis-config.xml";
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession(true);
//            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
//            List<Account> accounts;
//            try{
//                accounts = mapper.getAccList();
//            }catch (Exception e){
//                sqlSession.rollback();
//                accounts = null;
//            }finally {
//                sqlSession.commit();
//                sqlSession.close();
//            }
//            for (Account account : accounts) {
//                System.out.println(account.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void getAccByID() {
//        try {
//            String resource = "mybatis-config.xml";
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession(true);
//            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
//            Account account = mapper.getAccByID(3);
//            System.out.println(account);
//            sqlSession.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void addAcc() {
//        try {
//            String resource = "mybatis-config.xml";
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession(true);
//            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
//            Account ac = new Account(4, 5.00);
//            int acId = mapper.addAcc(ac);
//            System.out.println(acId);
//            sqlSession.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//
//
//
