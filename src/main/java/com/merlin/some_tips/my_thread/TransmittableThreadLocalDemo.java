package com.merlin.some_tips.my_thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransmittableThreadLocalDemo extends Thread {
    public static TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
    public static InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
    public static ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));
    public static void main(String[] args) throws InterruptedException {
        transmittableThreadLocal.set("hello world");
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("sub: " + transmittableThreadLocal.get());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("sub2: " + transmittableThreadLocal.get());
            }
        });
//        new TransmittableThreadLocalDemo().start();
        Thread.sleep(1000);
        transmittableThreadLocal.set("xzxxc");
//        new TransmittableThreadLocalDemo().start();
//        transmittableThreadLocal.set("hello world");
//        Thread.sleep(10);
//        new TransmittableThreadLocalDemo().start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        transmittableThreadLocal.set("hello world2");
    }

    @Override
    public void run() {
        System.out.println("sub: " + transmittableThreadLocal.get());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sub2: " + transmittableThreadLocal.get());

    }
}
