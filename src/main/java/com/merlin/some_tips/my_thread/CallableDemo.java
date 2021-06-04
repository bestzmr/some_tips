package com.merlin.some_tips.my_thread;

import com.merlin.some_tips.entity.Person;

import java.util.concurrent.*;

/**
 * @author: Merlin
 * @time: 2021/6/4 16:55
 */
public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Person person = new Person();
        person.setAge(22);
        person.setName("Neko");
        person.setGender("男");
        MyCallableThread<Person> thread = new MyCallableThread<>(person);
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        Future future = executorService.submit(thread);
        Person per = (Person) future.get();
        System.out.println(per.getName()+" 性别："+per.getGender());

        executorService.shutdown();
    }
}

class MyCallableThread<T> implements Callable<T> {

    T obj;

    public MyCallableThread(T person) {
        if (person != null) {
            obj = person;
        }
    }
    @Override
    public T call() throws Exception {
        if (obj != null) {
            Person p = (Person) obj;
            System.out.println(p.getName());
        }
        return obj;
    }
}

