package com.merlin.some_tips.my_algorithm.queue;

/**
 * @author merlin
 * @date 2022/3/4 5:13 下午
 */
public class CircleQueue {
    String[] items;
    int n = 0;
    int head;
    int tail;

    public CircleQueue(int capacity) {
        this.n = capacity;
        items = new String[capacity];
        head = 0;
        tail = 0;

    }

    public String get() {
        if (head == tail) {
            return null;
        }
        String item = items[head];
        head = (head + 1) % n;
        return item;
    }

    public boolean put(String data) {
        if ((tail + 1) % n == head) {
            return false;
        }
        if (tail == n) {
            tail = 0;
        }
        items[tail] = data;
        tail = (tail + 1) % n;
        return true;

    }
}
