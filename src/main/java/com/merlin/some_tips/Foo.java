package com.merlin.some_tips;

import java.util.ArrayList;
import java.util.List;

class Foo<T> {

    void handle(final T t) {
        System.out.println("handling " + t);
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);

        Foo<Integer> f = new Foo<>();

        list.forEach(f::handle);             // compiles fine
//        list.forEach(obj -> f.handle(obj));// compilation error

        f = new Foo<>(); // reassign f

    }
}