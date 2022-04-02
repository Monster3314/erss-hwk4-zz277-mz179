package com.EM_System.app;

import com.EM_System.dao.AccountMapper;
import com.EM_System.dao.ExecutedOrderMapper;
import com.EM_System.dao.OrderMapper;
import com.EM_System.dao.PositionMapper;
import com.EM_System.pojo.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.w3c.dom.Attr;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class RequestExecutor {

    private final AccountMapper accountMapper;
    private final PositionMapper positionMapper;
    private final OrderMapper orderMapper;
    private final ExecutedOrderMapper executedOrderMapper;

    public RequestExecutor() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        this.accountMapper = sqlSession.getMapper(AccountMapper.class);
        this.positionMapper = sqlSession.getMapper(PositionMapper.class);
        this.orderMapper = sqlSession.getMapper(OrderMapper.class);
        this.executedOrderMapper = sqlSession.getMapper(ExecutedOrderMapper.class);
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

    // TODO: Authentication (orderId, accountId)
    public Result executeCancel(int orderId) {
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        attr.put("id", String.valueOf(orderId));
        Order order = orderMapper.getOrderByID(orderId);
        if (order == null) {
            return new Result("error", attr, "Transaction does not exist", new ArrayList<>());
        }
        while (true) {
            if (order.getAmount() == 0 || !order.getState().equals("open")) {
                return new Result("error", attr, "Transaction is not open", new ArrayList<>());
            }
            order.cancelOrder();
            if  (orderMapper.editOrder(order) == 1) {
                break;
            }
            else {
                order = orderMapper.getOrderByID(orderId);
            }
        }
        double price = order.getPrice();
        int accountId = order.getAccountId();
        int amount = order.getAmount();
        // TODO: check amount cannot be 0
        if (amount > 0) {
            double refund = price * amount;
            tryAddBalance(accountId, refund);
        }
        else {
            addPosition(new Position(accountId, -amount, order.getSymbol_Name()));
        }
        return new Result("canceled", attr, null, queryOrder(orderId));
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

    private ArrayList<Result> queryOrder(int orderId) {
        ArrayList<Result> results = new ArrayList<>();
        Order order = orderMapper.getOrderByID(orderId);
        if (order == null) {
            return results;
        }
        if (order.getAmount() != 0) {
            LinkedHashMap<String, String> attr = new LinkedHashMap<>();
            attr.put("shares", String.valueOf(order.getAmount()));
            if (order.getState().equals("open")) {
                results.add(new Result("open", attr, null, new ArrayList<>()));
            }
            else if (order.getState().equals("canceled")) {
                attr.put("time", String.valueOf(order.getTimestamp()));
                results.add(new Result("canceled", attr, null, new ArrayList<>()));
            }
        }
        List<ExecutedOrder> executedOrderList = executedOrderMapper.getExcutedOrderByOrderID(orderId);
        for (ExecutedOrder executedOrder : executedOrderList) {
            LinkedHashMap<String, String> attr = new LinkedHashMap<>();
            attr.put("shares", String.valueOf(executedOrder.getAmount()));
            attr.put("price", String.valueOf(executedOrder.getPrice()));
            attr.put("time", String.valueOf(executedOrder.getTimestamp()));
            results.add(new Result("executed", attr, null, new ArrayList<>()));
        }
        return results;
    }

    public Result executeQuery(int orderId) {
        ArrayList<Result> results = queryOrder(orderId);
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        attr.put("id", String.valueOf(orderId));
        if (results.isEmpty()) {
            return new Result("error", attr, "Transaction does not exist", new ArrayList<>());
        }
        else {
            return new Result("status", attr, null, results);
        }
    }

    public Result executeOrder(Order order) {
        LinkedHashMap<String, String> attr = new LinkedHashMap<>();
        int amount = order.getAmount();
        double price = order.getPrice();
        String symbol = order.getSymbol_Name();
        attr.put("sym", symbol);
        attr.put("amount", String.valueOf(amount));
        attr.put("limit", String.valueOf(price));
        int accountId = order.getAccountId();
        if (amount > 0) {
            double pay = price * amount;
            if (!tryAddBalance(accountId, -pay)) {
                return new Result("error", attr, "Insufficient balance", new ArrayList<>());
            }
        }
        else {
            if (!tryDeductPosition(accountId, -amount, symbol)) {
                return new Result("error", attr, "Insufficient position", new ArrayList<>());
            }
        }
        // TODO: createOrder() return orderId?
        int orderId = orderMapper.createOrder(order);
        attr.put("id", String.valueOf(orderId));
        tryMatchOrder(orderId);
        return new Result("opened", attr, null, new ArrayList<>());
    }

    public boolean tryDeductPosition(int accountId, int amount, String symbol) {
        Position position = positionMapper.getSpecificPosition(accountId, symbol);
        if (position == null) {
            return false;
        }
        while (true) {
            if (position.getAmount() - amount >= 0) {
                position.addAmount(-amount);
            } else {
                return false;
            }
            int numRow = positionMapper.editPosition(position);
            if (numRow == 1) {
                return true;
            }
            position = positionMapper.getSpecificPosition(accountId, symbol);
        }
    }

    private void tryMatchOrder(int orderId) {
        Order order = orderMapper.getOrderByID(orderId);
        while (order.getAmount() != 0 && order.getState().equals("open")) {
            double price = order.getPrice();
            int amount = order.getAmount();
            if (amount > 0) {
                Order matchOrder = orderMapper.getMatchingSellOrder(order);
                if (matchOrder == null || matchOrder.getPrice() > price) {
                    return;
                }
                int matchAmount = -matchOrder.getAmount();
                int execAmount = Math.min(matchAmount, amount);
                // TODO: deal with atomic, exec order begin
                order.execOrder(-execAmount);
                matchOrder.execOrder(execAmount);
                if (orderMapper.editOrder(order) == 1 && orderMapper.editOrder(matchOrder) == 1) {
                    tryAddBalance(matchOrder.getAccountId(), execAmount * (2 * price - matchOrder.getPrice()));
                    addPosition(new Position(order.getAccountId(), execAmount, order.getSymbol_Name()));
                }
            }
            else {
                Order matchOrder = orderMapper.getMatchingBuyOrder(order);
                if (matchOrder == null || matchOrder.getPrice() < price) {
                    return;
                }
                int matchAmount = matchOrder.getAmount();
                int execAmount = Math.min(matchAmount, -amount);
                // TODO: same as above
                order.execOrder(execAmount);
                matchOrder.execOrder(-execAmount);
                if (orderMapper.editOrder(order) == 1 && orderMapper.editOrder(matchOrder) == 1) {
                    tryAddBalance(order.getAccountId(), execAmount * price);
                    addPosition(new Position(matchOrder.getAccountId(), execAmount, matchOrder.getSymbol_Name()));
                }
            }
            order = orderMapper.getOrderByID(orderId);
        }
    }
}
