package com.danertu.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadUtil {
    private static ExecutorService executorService;

    public static void execute(Runnable task){
        if(executorService==null) {
            executorService = Executors.newFixedThreadPool(10);
        }
        executorService.execute(task);
    }
}
