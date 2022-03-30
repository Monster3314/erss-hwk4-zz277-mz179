package com.EM_System.app;

import com.EM_System.dao.AccountMapper;
import com.EM_System.dao.OrderMapper;
import com.EM_System.dao.PositionMapper;
import com.EM_System.pojo.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RequestExecutor {

    private final AccountMapper accountMapper;
    private final PositionMapper positionMapper;
    private final OrderMapper orderMapper;

    public RequestExecutor() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        this.accountMapper = sqlSession.getMapper(AccountMapper.class);
        this.positionMapper = sqlSession.getMapper(PositionMapper.class);
        this.orderMapper = sqlSession.getMapper(OrderMapper.class);
    }

    public Result executeCreate(Account account) {
        int numRow = accountMapper.addAcc(account);
        String id = String.valueOf(account.getAccountId());
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        attr.put("id", id);
        // TODO: check valid balance
        if (numRow == 0) {
            return new Result("error", attr, "Account already exists", new ArrayList<>());
        }
        else {
            return new Result("created", attr, null, new ArrayList<>());
        }
    }

    public Result executeCreate(Position position) {
        int id = position.getAccountId();
        Account account = accountMapper.getAccByID(id);
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        attr.put("sym", position.getSymbol());
        attr.put("id", String.valueOf(id));
        if (account == null) {
            return new Result("error", attr, "Account does not exist", new ArrayList<>());
        }
        // TODO: check valid position amount
        addPosition(position);
        return new Result("created", attr, null, new ArrayList<>());
    }

    private void addPosition(Position position) {
        while (true) {
            Position oldPosition = positionMapper.getSpecificPosition(position.getAccountId(), position.getSymbol());
            if (oldPosition == null) {
                int numRow = positionMapper.createPosition(position);
                if (numRow == 1) {
                    return;
                }
                continue;
            }
            oldPosition.addAmount(position.getAmount());
            int numRow = positionMapper.editPosition(oldPosition);
            if (numRow == 1) {
                return ;
            }
        }
    }

    public Result executeCancel(int orderId) {
        Order order = orderMapper.getOrderByID(orderId);
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        attr.put("id", String.valueOf(orderId));
        if (order == null) {
            return new Result("error", attr, "Transaction does not exist or it is not open", new ArrayList<>());
        }
        int accountId = order.getAccountId();
        Account account = accountMapper.getAccByID(accountId);
        double refund;
        return new Result("error", new LinkedHashMap<>(), "Transaction does not exist or it is not open", new ArrayList<>());
    }

    private boolean tryAddBalance(int accountId, double amount) {
        while (true) {
            Account account = accountMapper.getAccByID(accountId);
            double balance = account.getBalance();
            if (balance + amount < 0) {
                return false;
            }
            account.addBalance(amount);
            int numRow = accountMapper.updateAcc(account);
            if (numRow == 1) {
                return true;
            }
        }
    }

    public Result executeQuery(int orderId) {
        return new Result("error", new LinkedHashMap<>(), "Transaction does not exist or it is not open", new ArrayList<>());
    }

    public Result executeOrder(Order order) {
        return new Result("error", new LinkedHashMap<>(), "Transaction does not exist or it is not open", new ArrayList<>());
    }


}
