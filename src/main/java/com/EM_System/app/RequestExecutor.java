package com.EM_System.app;

import com.EM_System.dao.AccountMapper;
import com.EM_System.dao.PositionMapper;
import com.EM_System.pojo.Account;
import com.EM_System.pojo.Order;
import com.EM_System.pojo.Position;
import com.EM_System.pojo.Result;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RequestExecutor {

    private AccountMapper accountMapper;
    private PositionMapper positionMapper;


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

//    public Result executeCancel(Order order) {
//        int accountId = order.getAccountId();
//        Account account = accountMapper.getAccByID(accountId);
//
//    }
//
//    public Result executeQuery(Order order) {
//
//    }


}
