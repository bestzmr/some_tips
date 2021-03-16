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

