package com.merlin.some_tips.my_thread;

public class ThreadLocalTest {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static ThreadLocal<String> threadLocal2 = new ThreadLocal<>();
    public static ThreadLocal<String> threadLocal3 = new ThreadLocal<>();
    public static void main(String[] args) {
        threadLocal.set("value1");
        threadLocal2.set("value2");
        threadLocal3.set("value3");
    }
}
