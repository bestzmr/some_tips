## CountdownLatch 使用场景

顾名思义CountdownLatch可以当做一个计数器来使用,比如某线程需要等待其他几个线程都执行过某个时间节点后才能继续执行 我们来模拟一个场景,某公司一共有十个人,门卫要等十个人都来上班以后,才可以休息,代码实现如下

```text
public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            //lambda中只能只用final的变量
            final int times = i;
            new Thread(() -> {
                try {
                    System.out.println("子线程" + Thread.currentThread().getName() + "正在赶路");
                    Thread.sleep(1000 * times);
                    System.out.println("子线程" + Thread.currentThread().getName() + "到公司了");
                    //调用latch的countDown方法使计数器-1
                    latch.countDown();
                    System.out.println("子线程" + Thread.currentThread().getName() + "开始工作");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }


        try {
            System.out.println("门卫等待员工上班中...");
            //主线程阻塞等待计数器归零
            latch.await();
            System.out.println("员工都来了,门卫去休息了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

运行后结果如下

```text
子线程Thread-0正在赶路
子线程Thread-2正在赶路
子线程Thread-0到公司了
子线程Thread-0开始工作
子线程Thread-1正在赶路
门卫等待员工上班中...
子线程Thread-4正在赶路
子线程Thread-9正在赶路
子线程Thread-5正在赶路
子线程Thread-6正在赶路
子线程Thread-7正在赶路
子线程Thread-8正在赶路
子线程Thread-3正在赶路
子线程Thread-1到公司了
子线程Thread-1开始工作
子线程Thread-2到公司了
子线程Thread-2开始工作
子线程Thread-3到公司了
子线程Thread-3开始工作
子线程Thread-4到公司了
子线程Thread-4开始工作
子线程Thread-5到公司了
子线程Thread-5开始工作
子线程Thread-6到公司了
子线程Thread-6开始工作
子线程Thread-7到公司了
子线程Thread-7开始工作
子线程Thread-8到公司了
子线程Thread-8开始工作
子线程Thread-9到公司了
子线程Thread-9开始工作
员工都来了,门卫去休息了
```

可以看到子线程并没有因为调用latch.countDown而阻塞,会继续进行该做的工作,只是通知计数器-1,即完成了我们如上说的场景,只需要在所有进程都进行到某一节点后才会执行被阻塞的进程.如果我们想要多个线程在同一时间进行就要用到CyclicBarrier了



## CyclicBarrier 使用场景

我们重新模拟一个新的场景,就用已经被说烂的跑步场景吧,十名运动员各自准备比赛,需要等待所有运动员都准备好以后,裁判才能说开始然后所有运动员一起跑,代码实现如下

```text
public static void main(String[] args) {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10,()->{
            System.out.println("所有人都准备好了裁判开始了");
        });
        for (int i = 0; i < 10; i++) {
            //lambda中只能只用final的变量
            final int times = i;
            new Thread(() -> {
                try {
                    System.out.println("子线程" + Thread.currentThread().getName() + "正在准备");
                    Thread.sleep(1000 * times);
                    System.out.println("子线程" + Thread.currentThread().getName() + "准备好了");
                    cyclicBarrier.await();
                    System.out.println("子线程" + Thread.currentThread().getName() + "开始跑了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }


    }
```

执行结果如下

```text
子线程Thread-0正在准备
子线程Thread-2正在准备
子线程Thread-1正在准备
子线程Thread-3正在准备
子线程Thread-4正在准备
子线程Thread-0准备好了
子线程Thread-5正在准备
子线程Thread-6正在准备
子线程Thread-7正在准备
子线程Thread-8正在准备
子线程Thread-9正在准备
子线程Thread-1准备好了
子线程Thread-2准备好了
子线程Thread-3准备好了
子线程Thread-4准备好了
子线程Thread-5准备好了
子线程Thread-6准备好了
子线程Thread-7准备好了
子线程Thread-8准备好了
子线程Thread-9准备好了
所有人都准备好了裁判开始了
子线程Thread-9开始跑了
子线程Thread-0开始跑了
子线程Thread-2开始跑了
子线程Thread-1开始跑了
子线程Thread-7开始跑了
子线程Thread-6开始跑了
子线程Thread-5开始跑了
子线程Thread-4开始跑了
子线程Thread-3开始跑了
子线程Thread-8开始跑了
```

可以看到所有线程在其他线程没有准备好之前都在被阻塞中,等到所有线程都准备好了才继续执行 我们在创建CyclicBarrier对象时传入了一个方法,当调用CyclicBarrier的await方法后,当前线程会被阻塞等到所有线程都调用了await方法后 调用传入CyclicBarrier的方法,然后让所有的被阻塞的线程一起运行







## CountdownLatch 底层实现

我们先来看看CountdownLatch的构造方法

```text
public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }
```

首先保证了count一定要大于零,然后初始化了一个Sync对象,在看看这个Sync对象是个什么

```text
private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
```

Sync是CountdownLatch的静态内部类,继承了AbstractQueuedSynchronizer(即AQS,提供了一种实现阻塞锁和一系列依赖FIFO等待队列的同步器的工具,回头单讲)抽象类, 在Sync的构造方法中,调用了setState方法,可以视作初始化了一个标记来记录当前计数器的数量

我们来看CountdownLatch的两个核心方法,await和countdown,先来看await

```text
public void await() throws InterruptedException {
        //可以视作将线程阻塞
        sync.acquireSharedInterruptibly(1);
    }
```

await调用的是AQS的方法,可以视作阻塞线程,具体实现在分析AQS的章节中展开 再来看看countdown方法

```text
public void countDown() {
        sync.releaseShared(1);
    }
```

调用了sync的一个方法,再来看看这个方法的实现

```text
public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
```

再来看这个tryReleaseShared方法

```text
protected boolean tryReleaseShared(int releases) {
            for (;;) {
                //获取标记位
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                //用cas的方式更新标记位
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
```

可以看到在调用tryReleaseShared实际上是将标记位-1并且返回标记位是否为0,如果标记位为0 那么调用的doReleaseShared可以视作将阻塞的线程放行,这样整个的流程就通了

## CyclicBarrier 底层实现

老规矩先看构造方法

```text
public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }
```

这边传入了两个对象简单的记录了一下存值,我们直接查看一下关键的await方法

```text
public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return dowait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen
        }
    }
```

再来看dowait的实现

```text
/** The lock for guarding barrier entry */
    private final ReentrantLock lock = new ReentrantLock();
    /** Condition to wait on until tripped */
    private final Condition trip = lock.newCondition();
    /** 省略部分代码 **/
private int dowait(boolean timed, long nanos)
        throws InterruptedException, BrokenBarrierException,
               TimeoutException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Generation g = generation;
            //判断是否被打断
            if (g.broken)
                throw new BrokenBarrierException();

            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }
            //将计数器-1 即在构造方法中赋值的count
            int index = --count;
            if (index == 0) {  // tripped
            //如果所有的线程都执行完毕即count=0时
                boolean ranAction = false;
                try {
                    //执行传入的方法
                    final Runnable command = barrierCommand;
                    if (command != null)
                        command.run();
                    ranAction = true;
                    //唤醒所有线程
                    nextGeneration();
                    return 0;
                } finally {
                    if (!ranAction)
                        breakBarrier();
                }
            }

            //如果count没有到0那么阻塞当前线程
            for (;;) {
                try {
                    if (!timed)
                        trip.await();
                    else if (nanos > 0L)
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        // We're about to finish waiting even if we had not
                        // been interrupted, so this interrupt is deemed to
                        // "belong" to subsequent execution.
                        Thread.currentThread().interrupt();
                    }
                }

                if (g.broken)
                    throw new BrokenBarrierException();

                if (g != generation)
                    return index;

                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }
```

从代码中可以看到,CyclicBarrier是利用Lock的condition方法来进行线程的阻塞和唤醒,类似Object.wait()和notifyAll()在count不为0时阻塞,在count=0时唤醒所有线程