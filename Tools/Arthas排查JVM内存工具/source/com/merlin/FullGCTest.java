package com.merlin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FullGCTest {


    //模拟银行卡的类
    private static class CardInfo {
        //小农的银行卡信息记录
        BigDecimal price = new BigDecimal(10000000.0);
        String name = "牧小农";
        int age = 18;
        Date birthdate = new Date();

        public void m() {
        }
    }

    //线程池 定时线程池
    //50个，然后设置 拒绝策略
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void main(String[] args) throws Exception {
        executor.setMaximumPoolSize(50);

        for (; ; ) {
            modelFit();
            Thread.sleep(100);
        }
    }

    /**
     * 对银行卡进行风险评估
     */
    private static void modelFit() {
        List<CardInfo> taskList = getAllCardInfo();
        //拿出每一个信息出来
        taskList.forEach(info -> {
            // do something
            executor.scheduleWithFixedDelay(() -> {
                //调用M方法
                info.m();

            }, 2, 3, TimeUnit.SECONDS);
        });
    }

    private static List<CardInfo> getAllCardInfo() {
        List<CardInfo> taskList = new ArrayList<>();
        //每次查询100张卡出来
        for (int i = 0; i < 100; i++) {
            CardInfo ci = new CardInfo();
            taskList.add(ci);
        }

        return taskList;
    }
}