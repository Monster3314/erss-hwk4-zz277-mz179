package com.EM_System.pojo;

import com.EM_System.app.RequestExecutor;

import java.util.ArrayList;

public interface Request {
    ArrayList<Result> exec(RequestExecutor executor);
}
