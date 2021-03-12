在并发编程中时常有这样一种需求：每条线程都需要存取一个同名变量，但每条线程中该变量的值均不相同。

如果是你，该如何实现上述功能？常规的思路如下： 使用一个线程共享的`Map<Thread,Object>`，Map中的key为线程对象，value即为需要存储的值。那么，我们只需要通过`map.get(Thread.currentThread())`即可获取本线程中该变量的值。

这种方式确实可以实现我们的需求，但它有何缺点呢？——答案就是：需要同步，效率低！

由于这个map对象需要被所有线程共享，因此需要加锁来保证线程安全性。当然我们可以使用`java.util.concurrent.*`包下的`ConcurrentHashMap`提高并发效率，但这种方法只能降低锁的粒度，不能从根本上避免同步锁。而JDK提供的`ThreadLocal`就能很好地解决这一问题。下面来看看ThreadLocal是如何高效地实现这一需求的。



## 如何使用ThradLocal？

在介绍ThreadLocal原理之前，首先简单介绍一下它的使用方法。

```
public class Main{
    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    
    public void start() {
        for (int i=0; i<10; i++) {
            new Thread(new Runnable(){
                @override
                public void run(){
                    threadLocal.set(i);
                    threadLocal.get();
                    threadLocal.remove();
                }
            }).start();
        }
    }
}
复制代码
```

- 首先我们需要创建一个线程共享的ThreadLocal对象，该对象用于存储Integer类型的值；
- 然后在每条线程中可以通过如下方法操作ThreadLocal：
  - `set(obj)`：向当前线程中存储数据
  - `get()`：获取当前线程中的数据
  - `remove()`：删除当前线程中的数据

ThreadLocal的使用方法非常简单，关键在于它背后的实现原理。回到上面的问题：ThreadLocal究竟是如何避免同步锁，从而保证读写的高效？



## ThradLocal原理

ThreadLocal的内部结构如下图所示：

![](..\imgs\Java基础\ThradLocal内部结构.jpg)

`ThreadLocal`并不维护`ThreadLocalMap`，并不是一个存储数据的容器，它只是相当于一个工具包，提供了操作该容器的方法，如get、set、remove等。而`ThreadLocal`内部类`ThreadLocalMap`才是存储数据的容器，并且该容器由`Thread`维护。

每一个`Thread`对象均含有一个`ThreadLocalMap`类型的成员变量`threadLocals`，它存储本线程中所有ThreadLocal对象及其对应的值。

`ThreadLocalMap`由一个个`Entry`对象构成，`Entry`的代码如下：

```
static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;

    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
}
复制代码
```

`Entry`继承自`WeakReference<ThreadLocal<?>>`，一个`Entry`由`ThreadLocal`对象和`Object`构成。由此可见，`Entry`的key是ThreadLocal对象，并且是一个弱引用。当没指向key的强引用后，该key就会被垃圾收集器回收。

那么，ThreadLocal是如何工作的呢？下面来看set和get方法。

```
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}

public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}
复制代码
```

当执行set方法时，ThreadLocal首先会获取当前线程对象，然后获取当前线程的ThreadLocalMap对象。再以当前ThreadLocal对象为key，将值存储进ThreadLocalMap对象中。

get方法执行过程类似。ThreadLocal首先会获取当前线程对象，然后获取当前线程的ThreadLocalMap对象。再以当前ThreadLocal对象为key，获取对应的value。

由于每一条线程均含有各自**私有的**ThreadLocalMap容器，这些容器相互独立互不影响，因此不会存在线程安全性问题，从而也无需使用同步机制来保证多条线程访问容器的互斥性。

**关键设计小结**

代码分析到这里，其实对于ThreadLocal的内部主要设计以及其和Thread的关系比较清楚了：

- **每个线程，是一个Thread实例，其内部拥有一个名为threadLocals的实例成员，其类型是ThreadLocal.ThreadLocalMap**
- **通过实例化ThreadLocal实例，我们可以对当前运行的线程设置一些线程私有的变量，通过调用ThreadLocal的set和get方法存取**
- **ThreadLocal本身并不是一个容器，我们存取的value实际上存储在ThreadLocalMap中，ThreadLocal只是作为TheadLocalMap的key**
- **每个线程实例都对应一个TheadLocalMap实例，我们可以在同一个线程里实例化很多个ThreadLocal来存储很多种类型的值，这些ThreadLocal实例分别作为key，对应各自的value**
- **当调用ThreadLocal的set/get进行赋值/取值操作时，首先获取当前线程的ThreadLocalMap实例，然后就像操作一个普通的map一样，进行put和get**





## ThreadLocal的内存泄漏分析

**实现原理**

```text
static class ThreadLocalMap {

    static class Entry extends WeakReference<ThreadLocal<?>> {
        /** The value associated with this ThreadLocal. */
        Object value;

        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
    }
    ...
   }
```

ThreadLocal的实现原理，每一个Thread维护一个ThreadLocalMap，key为使用**弱引用**的ThreadLocal实例，value为线程变量的副本。这些对象之间的引用关系如下,

![](..\imgs\Java基础\内存泄漏.jpg)

> 实心箭头表示强引用，空心箭头表示弱引用



根据上面的内存模型图我们可以知道，由于ThreadLocalMap是以弱引用的方式引用着ThreadLocal，换句话说，就是**ThreadLocal是被ThreadLocalMap以弱引用的方式关联着，因此如果ThreadLocal没有被ThreadLocalMap以外的对象引用，则在下一次GC的时候，ThreadLocal实例就会被回收，那么此时ThreadLocalMap里的一组KV的K就是null**了，因此在没有额外操作的情况下，此处的V便不会被外部访问到，而且**只要Thread实例一直存在，Thread实例就强引用着ThreadLocalMap，因此ThreadLocalMap就不会被回收，那么这里K为null的V就一直占用着内存**。

综上，发生内存泄露的条件是

- ThreadLocal实例没有被外部强引用，比如我们假设在提交到线程池的task中实例化的ThreadLocal对象，当task结束时，ThreadLocal的强引用也就结束了
- ThreadLocal实例被回收，但是在ThreadLocalMap中的V没有被任何清理机制有效清理
- 当前Thread实例一直存在，则会一直强引用着ThreadLocalMap，也就是说ThreadLocalMap也不会被GC

也就是说，如果Thread实例还在，但是ThreadLocal实例却不在了，则ThreadLocal实例作为key所关联的value无法被外部访问，却还被强引用着，因此出现了内存泄露。



## 那为什么使用弱引用而不是强引用？？

我们看看Key使用的

### key 使用强引用

当hreadLocalMap的key为强引用回收ThreadLocal时，因为ThreadLocalMap还持有ThreadLocal的强引用，如果没有手动删除，ThreadLocal不会被回收，导致Entry内存泄漏。

### key 使用弱引用

当ThreadLocalMap的key为弱引用回收ThreadLocal时，由于ThreadLocalMap持有ThreadLocal的弱引用，即使没有手动删除，ThreadLocal也会被回收。当key为null，在下一次ThreadLocalMap调用set(),get()，remove()方法的时候会被清除value值。



### ThreadLocalMap的remove()分析

在这里只分析remove()方式，其他的方法可以查看源码进行分析：

```java
private void remove(ThreadLocal<?> key) {
    //使用hash方式，计算当前ThreadLocal变量所在table数组位置
    Entry[] tab = table;
    int len = tab.length;
    int i = key.threadLocalHashCode & (len-1);
    //再次循环判断是否在为ThreadLocal变量所在table数组位置
    for (Entry e = tab[i];
         e != null;
         e = tab[i = nextIndex(i, len)]) {
        if (e.get() == key) {
            //调用WeakReference的clear方法清除对ThreadLocal的弱引用
            e.clear();
            //清理key为null的元素
            expungeStaleEntry(i);
            return;
        }
    }
}
```

再看看清理key为null的元素expungeStaleEntry(i):

```java
private int expungeStaleEntry(int staleSlot) {
    Entry[] tab = table;
    int len = tab.length;

    // 根据强引用的取消强引用关联规则，将value显式地设置成null，去除引用
    tab[staleSlot].value = null;
    tab[staleSlot] = null;
    size--;

    // 重新hash，并对table中key为null进行处理
    Entry e;
    int i;
    for (i = nextIndex(staleSlot, len);
         (e = tab[i]) != null;
         i = nextIndex(i, len)) {
        ThreadLocal<?> k = e.get();
        //对table中key为null进行处理,将value设置为null，清除value的引用
        if (k == null) {
            e.value = null;
            tab[i] = null;
            size--;
        } else {
            int h = k.threadLocalHashCode & (len - 1);
            if (h != i) {
                tab[i] = null;
                while (tab[h] != null)
                    h = nextIndex(h, len);
                tab[h] = e;
            }
        }
    }
    return i;
}
```

## ThreadLocal正确的使用方法

- 每次使用完ThreadLocal都调用它的remove()方法清除数据
- 将ThreadLocal变量定义成private static，这样就一直存在ThreadLocal的强引用，也就能保证任何时候都能通过ThreadLocal的弱引用访问到Entry的value值，进而清除掉 。