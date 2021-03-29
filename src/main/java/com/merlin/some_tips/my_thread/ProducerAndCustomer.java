package com.merlin.some_tips.my_thread;

public class ProducerAndCustomer {
    static int objectNum = 0;

    public static void main(String[] args) throws InterruptedException {
        ProducerAndCustomer pac = new ProducerAndCustomer();
        Thread t1 = new Thread(new Producer());
        Thread t2 = new Thread(new Customer());
        t1.start();
        t2.start();
    }

    static class Customer implements Runnable {


        @Override
        public void run() {

            while (true) {
                synchronized (ProducerAndCustomer.class) {
                    if (objectNum > 0) {
                        objectNum--;
                        System.out.println("消费一个水果....剩余的水果数量是：" + objectNum);
                        ProducerAndCustomer.class.notify();
                    } else {
                        try {
                            ProducerAndCustomer.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }
    }

    static class Producer implements Runnable {

        @Override
        public void run() {
            synchronized (ProducerAndCustomer.class) {
                while (true) {
                    if (objectNum < 20) {

                        objectNum++;
                        System.out.println("生产了一个水果....水果数量为：" + objectNum);
                        ProducerAndCustomer.class.notify();
                    } else {
                        try {
                            ProducerAndCustomer.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
}

