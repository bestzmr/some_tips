bean从我们使用者角度来看可以分为两种： 一 Spring启动后已经生成的可以直接用的，二 启动的时候不生成，用的时候才去生成的（专业术语叫做懒加载）。

Spring作为一个框架，有万千代码类，这些代码有的是辅助功能，有的是工具功能，那么容器的类在哪？就是BeanFactory类，其内部定义了作为容器需要提供的功能方法。

工作中常用的是DefaultListableBeanFactory

查看类图（IDEA右键类名-->Diagrams -->show Diagram ）

可以看到该类是继承了 DefaultSingletonBeanRegistry


![](..\imgs\Spring\DefaultListableBeanFactory.png)

- DefaultSingletonBeanRegistry：存放具体的bean
  - **singletonObjects**： 存放bean的名称及实例
  - **singletonFactories**： 存放bean的名称及实例创建工厂

```java
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    protected static final Object NULL_OBJECT = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    //省略下面代码
}
```

