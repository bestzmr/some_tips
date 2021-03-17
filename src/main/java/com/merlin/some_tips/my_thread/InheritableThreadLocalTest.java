package com.merlin.some_tips.my_thread;

import java.util.concurrent.*;

public class InheritableThreadLocalTest extends Thread{
    public static InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static void main(String[] args) throws InterruptedException {
        inheritableThreadLocal.set("hello world");
        executorService.submit(new InheritableThreadLocalTest("thread1"));
        inheritableThreadLocal.set("hello world2");
        executorService.submit(new InheritableThreadLocalTest("thread1-2"));
        executorService.shutdown();
//        inheritableThreadLocal.set("hello");
//        threadLocal.set("world");
//        threadLocal.set("java");
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        FutureTask<String> futureTask = new FutureTask<>(new CallableTest());
//        Future future = futureTask;
//
//        new InheritableThreadLocalTest().start();
//        Thread.sleep(10);
//        inheritableThreadLocal.set("who guess");//第二次赋值，对已经初始化的线程不能得到该值，只能得到初始化时的值
////        System.out.println("par inheritableThreadLocal: "+inheritableThreadLocal.get());
////        new InheritableThreadLocalTest("thread2").start();
//        System.out.println("par inheritableThreadLocal: "+inheritableThreadLocal.get());
////        System.out.println("threadLocal: "+threadLocal.get());
    }

    public InheritableThreadLocalTest() {

    }
    String name;
    public InheritableThreadLocalTest(String name) {
        super(name);
        this.name = name;
    }
    @Override
    public void run() {
//        System.out.println("sub inheritableThreadLocal: "+inheritableThreadLocal.get());
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("sub2 inheritableThreadLocal: "+inheritableThreadLocal.get());
//        inheritableThreadLocal.set("hello world");//子线程对InheritableThreadLocal赋值，父线程不可见
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("inheritableThreadLocal: "+inheritableThreadLocal.get());
//        System.out.println("threadLocal: "+threadLocal.get());
        System.out.println(Thread.currentThread().getName() + ": " + inheritableThreadLocal.get());
        inheritableThreadLocal.set(name);
    }
}

class CallableTest implements Callable<String> {
    @Override
    public String call() throws Exception {
        return null;
    }
}