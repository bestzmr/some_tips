**InheritableThreadLocal及其局限性**
Jdk提供了InheritableThreadLocal类，用于在父子线程间传递线程变量（ThreadLocal），实现原理就是在Thread类保存名为inheritableThreadLocals的成员属性（以InheritableThreadLocal对象为Key的ThreadLocalMap），并在初始化创建子线程时，将父线程的inheritableThreadLocals赋给子线程，这部分逻辑在Thread.init()方法内。

InheritableThreadLocal这种方式在线程只被创建和使用一次时是有效的，但对于使用线程池的场景下，由于线程被复用，初始化一次后，后续使用并不会走这个ThreadLocal传递的流程，导致后续提交的任务并不会继承到父线程的线程变量，同时，还会获取到当前任务线程被之前几次任务所修改变量值。


在使用线程池等会池化复用线程的执行组件情况下，提供`ThreadLocal`值的传递功能，解决异步执行时上下文传递的问题。

`JDK`的InheritableThreadLocal类可以完成父线程到子线程的值传递。但对于使用线程池等会池化复用线程的执行组件的情况，线程由线程池创建好，并且线程是池化起来反复使用的；这时父子线程关系的`ThreadLocal`值传递已经没有意义，应用需要的实际上是把 **任务提交给线程池时**的`ThreadLocal`值传递到 **任务执行时**。

TransmittableThreadLocal需求场景

`ThreadLocal`的需求场景即`TransmittableThreadLocal`的潜在需求场景，如果你的业务需要『在使用线程池等会池化复用线程的执行组件情况下传递`ThreadLocal`值』则是`TransmittableThreadLocal`目标场景。

下面是几个典型场景例子。

1. 分布式跟踪系统 或 全链路压测（即链路打标）
2. 日志收集记录系统上下文
3. `Session`级`Cache`
4. 应用容器或上层框架跨应用代码给下层`SDK`传递信息



demo

```java
/**
 * ttl测试
 *
 * @author zhangyunhe
 * @date 2020-04-23 12:47
 */
public class Test {

    // 1. 初始化一个TransmittableThreadLocal，这个是继承了InheritableThreadLocal的
    static TransmittableThreadLocal<String> local = new TransmittableThreadLocal<>();

    // 初始化一个长度为1的线程池
    static ExecutorService poolExecutor = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Test test = new Test();
        test.test();
    }
    private void test() throws ExecutionException, InterruptedException {

        // 设置初始值
        local.set("天王老子");
        //！！！！ 注意：这个地方的Task是使用了TtlRunnable包装的
        Future future = poolExecutor.submit(TtlRunnable.get(new Task("任务1")));
        future.get();

        Future future2 = poolExecutor.submit(TtlRunnable.get(new Task("任务2")));
        future2.get();
      
        System.out.println("父线程的值："+local.get());
        poolExecutor.shutdown();
    }

    class Task implements Runnable{

        String str;
        Task(String str){
            this.str = str;
        }
        @Override
        public void run() {
            // 获取值
            System.out.println(Thread.currentThread().getName()+":"+local.get());
            // 重新设置一波
            local.set(str);
        }
    }
}

```

### 主要使用方式：

#### 1.直接使用

同ThreadLocal：父线程使用TransmittableThreadLocal保存变量，子线程get取出。

#### 2.提交线程池使用

##### 1.增强Runnable或Callable

使用TtlRunnable.get()或TtlCallable.get()
提交线程池之后，在run()内取出变量
增强线程池

##### 2.使用TtlExecutors.getTtlExecutor()或getTtlExecutorService()、getTtlScheduledExecutorService()获取装饰后的线程池

使用线程池提交普通任务
在run()方法内取出变量（任务子线程）
装饰线程池其实本质也是装饰Runnable，只是将这个逻辑移到了ExecutorServiceTtlWrapper.submit()方法内，对所有提交的Runnable都进行包装：

![](..\imgs\Java基础\transmittableThreadLocal使用方式.jpg)

### 核心原理分析

根据TransmittableThreadLocal的使用流程，其核心逻辑可以分成三个部分：设置线程变量 -> 构建TtlRunnable -> 提交线程池运行

#### 1.设置线程变量

当调用TransmittableThreadLocal.set()设置变量值时，除了会通过调用super.set()（ThreadLocal）设置当前线程变量外，还会执行addThisToHolder()方法：

![](..\imgs\Java基础\transmittableThreadLocal原理1.jpg)

* TransmittableThreadLocal内部维护了一个静态的线程变量holder，保存的是以TransmittableThreadLocal对象为Key的Map（这个map的值永远是null，也就是当做Set使用的）

* holder保存了当前线程下的所有TTL线程变量
* 设值时向获取holder传入this，保存发起set()操作的TransmittableThreadLocal对象

#### 2.构建TtlRunnable对象

构建TtlRunnable对象时，会保存原Runnable对象引用，用于后续run()方法中业务代码的执行。另外还会调用TransmittableThreadLocal.Transmitter.capture()方法，缓存当前主线程的线程变量：

![](..\imgs\Java基础\transmittableThreadLocal原理2.jpg)

* 这里实际上就是对第一步在holder中保存的ThreadLocal对象进行遍历，保存其变量值

* 此时原本通过ThreadLocal保存的和Thread绑定的线程变量，就复制了一份到TtlRunnable对象中了

#### 3.在子线程中读取变量

当TtlRunnable对象被提交到线程池执行时，调用TtlRunnable.run()：

> 注意此时已处于任务子线程环境中
>

![](..\imgs\Java基础\transmittableThreadLocal原理3.jpg)

这里会从Runnable对象取出缓存的线程变量captured，然后进行后续流程：

##### (1)前序处理

TransmittableThreadLocal.Transmitter.replay()：

![](..\imgs\Java基础\transmittableThreadLocal原理4.jpg)

* 将缓存的父线程变量值设置到当前任务线程（子线程）的ThreadLocal内，并将父线程的线程变量备份

##### (2)执行run()方法，读取变量值

由于上一步已经将从父线程复制的线程变量都设置到当前子线程的ThreadLocal中，因此run()方法中直接通过ThreadLocal.get()即可读取继承自父线程的变量值。

##### (3)后续处理

TransmittableThreadLocal.Transmitter.restore()：
![](..\imgs\Java基础\transmittableThreadLocal原理5.jpg)

将run()执行前获取的备份，设置到当前线程中去，恢复run()执行过程中可能导致的变化，避免对后续复用此线程的任务产生影响
整个流程可参考官方给出的时序图帮助理解：

![](..\imgs\Java基础\transmittableThreadLocal原理6.jpg)

### 四、总结

首先，从使用上来看，不管是修饰Runnable还是修饰线程池，本质都是将Runnable增强为TtlRunnable。

而从实现线程变量传递的原理上来看，TTL做的实际上就是将原本与Thread绑定的线程变量，缓存一份到TtlRunnable对象中，在执行子线程任务前，将对象中缓存的变量值设置到子线程的ThreadLocal中以供run()方法的代码使用，然后执行完后，又恢复现场，保证不会对复用线程产生影响。
