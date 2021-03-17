package com.merlin.some_tips.my_thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransmittableThreadLocalTest {

    // 1. 初始化一个TransmittableThreadLocal，这个是继承了InheritableThreadLocal的
    static TransmittableThreadLocal<String> local = new TransmittableThreadLocal<>();

    // 初始化一个长度为1的线程池
    static ExecutorService poolExecutor = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        TransmittableThreadLocalTest test = new TransmittableThreadLocalTest();
        test.test();
    }

    private void test() throws ExecutionException, InterruptedException {

        // 设置初始值
        local.set("天王老子");
        //！！！！ 注意：这个地方的Task是使用了TtlRunnable包装的
        Future future = poolExecutor.submit(TtlRunnable.get(new Task("任务1")));
        future.get();
        local.set("天王老子2");
        Future future2 = poolExecutor.submit(TtlRunnable.get(new Task("任务2")));
        future2.get();

        System.out.println("父线程的值：" + local.get());
        poolExecutor.shutdown();
    }

    class Task implements Runnable {

        String str;

        Task(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            // 获取值
            System.out.println(Thread.currentThread().getName() + ":" + local.get());
            System.out.println(str);
            // 重新设置一波
            local.set(str);
            System.out.println(local.get());
        }
    }
}
