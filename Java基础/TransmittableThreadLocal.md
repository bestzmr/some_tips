在使用线程池等会池化复用线程的执行组件情况下，提供`ThreadLocal`值的传递功能，解决异步执行时上下文传递的问题。

`JDK`的InheritableThreadLocal类可以完成父线程到子线程的值传递。但对于使用线程池等会池化复用线程的执行组件的情况，线程由线程池创建好，并且线程是池化起来反复使用的；这时父子线程关系的`ThreadLocal`值传递已经没有意义，应用需要的实际上是把 **任务提交给线程池时**的`ThreadLocal`值传递到 **任务执行时**。

TransmittableThreadLocal需求场景

`ThreadLocal`的需求场景即`TransmittableThreadLocal`的潜在需求场景，如果你的业务需要『在使用线程池等会池化复用线程的执行组件情况下传递`ThreadLocal`值』则是`TransmittableThreadLocal`目标场景。

下面是几个典型场景例子。

1. 分布式跟踪系统 或 全链路压测（即链路打标）
2. 日志收集记录系统上下文
3. `Session`级`Cache`
4. 应用容器或上层框架跨应用代码给下层`SDK`传递信息