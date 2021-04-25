- AQS就是JDK中为“线程同步”提供的一套基础工具类（网上其他帖子叫做“框架”我觉得这个就说的太大了，其实就是一个类而已（不算它衍生出来的子类）国内存在一些翻译会导致很多初学者很难理解，比如classloader的parents被翻译为“双亲”，简直败笔，此处默哀3秒钟），因此AQS就成了非常重要的一个知识点，因为基于它可以写出JAVA中的很多“锁”类。比如此文要分析的ReentrantLock，它就是基于AQS而形成了一个“**可重入锁**”

  **ReentrantLock**

  它是一个“可重入”锁。

  > **什么是“可重入”？**

  **简单地讲**就是：“同一个线程对于已经获得到的锁，可以多次继续申请到该锁的使用权”

  **正经地讲**就是：假如访问一个资源A需要获得其锁lock，如果之前没有其他线程获取该锁，那么当前线程就获锁成功，此时该线程对该锁后续所有“请求”都将立即得到“获锁成功”的返回，即同一个线程可以多次成功的获取到之前获得的锁。“可重入”可以解释成“同一个线程可多次获取”。

  我们来看一个使用ReentrantLock的例子，来一步步地理解和学习

  ```java
  //未使用ReentrantLock进行多线程累加操作
  public class ReentrantLockForIncrease {
      static int cnt = 0;
      public static void main(String[] args) {
           Runnable r = new Runnable() {
              @Override
              public void run() {
                  int n = 10000;
                  while(n>0){
                      cnt++;
                      n--;
                  }
              }
          };
          Thread t1  = new Thread(r);
          Thread t2  = new Thread(r);
          Thread t3  = new Thread(r);
          Thread t4  = new Thread(r);
          Thread t5  = new Thread(r);
          t1.start();
          t2.start();
          t3.start();
          t4.start();
          t5.start();
  
          try {
              //等待足够长的时间 确保上述线程均执行完毕
              Thread.sleep(10000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          System.out.println(cnt);
      }
  
  }
  
  //输出的结果会小于50000
  ```

  

  ```java
  //使用ReentrantLock的多线程累加操作
  public class ReentrantLockForIncrease {
      //初始化ReentrantLock
      public static ReentrantLock reentrantLock = new ReentrantLock();
      static int cnt = 0;
      public static void main(String[] args) {
           Runnable r = new Runnable() {
              @Override
              public void run() {
                  //加锁
                  reentrantLock.lock();
                  int n = 10000;
                  while(n>0){
                      cnt++;
                      n--;
                  }
                  //执行完毕后释放锁
                  reentrantLock.unlock();
              }
          };
          Thread t1  = new Thread(r);
          Thread t2  = new Thread(r);
          Thread t3  = new Thread(r);
          Thread t4  = new Thread(r);
          Thread t5  = new Thread(r);
          t1.start();
          t2.start();
          t3.start();
          t4.start();
          t5.start();
  
          try {
              //等待足够长的时间 确保上述线程均执行完毕
              Thread.sleep(10000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          System.out.println(cnt);
      }
  
  }
  //输出结果将和预想中的一致:50000
  ```

  通过上述的例子,你可以在心里暂时把它看成“**就是一把普通的锁**”(其他细节慢慢会讨论)，而作为一把锁,当然要有锁的基本特性:

  - **加锁**
  - **解锁**

  

  至于什么指纹解锁、人脸解锁、虹膜解锁啥的，其实也逃不出“加锁”、“解锁”，只不过是在这两个基本动作上做了一些个性化。

  *同理，ReentrantLock也不过就是在锁的基本特性上加了一些“可重入”、“公平”、“非公平”等特性。因此我们只要弄清楚基本锁是如何“加锁”和“解锁”，以及ReentrantLock如何实现“可重入”、“公平”和“非公平”，也就达到了对这个内容的理解和学习的目的。*

  总结一下学习要点（大纲）：

  - **基本锁的特性**

  - - **加锁**
    - **解锁**

  - **ReentrantLock的补充特性**

  - - **可重入**
    - **公平**
    - **非公平**

  接下去就按照大纲去梳理，就OK了

  

  ## **理解ReentrantLock的主要方法**

  可以看出ReentrantLock对象实现了Lock接口

  ![](..\imgs\Java基础\ReentrantLock类结构.jpg)

而Lock接口的主要方法有以下几个

![](..\imgs\Java基础\Lock接口包含的基本方法.jpg)

Lock对象只是一个接口，上述方法具体的实现其实都在ReentrantLock中，因此我们只要在ReentrantLock对象中查看具体实现去理解锁的“加锁”和“解锁”操作是如何做的

**ReentrantLock的主要方法**

![](..\imgs\Java基础\ReentrantLock的主要方法.jpg)

**其中加锁方法即为lock()，解锁方法即为unLock()**

这两个方法在源码中的实现如下

```java
//加锁
public void lock() {
    sync.lock();
}

//释放锁
public void unlock() {
    sync.release(1);
}
```

从上述可以知道这两个方法实际上是操作了一个叫做**sync**的对象，调用该对象的**lock**和**release**操作来实现

> sync是什么东西？

我拷了一段ReentrantLock类的源码片段

```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    private final Sync sync;
}
```

可以看出，**sync**是ReentrantLock中的一个**私有的成员变**量，且**类型是Sync对象**

> Sync是什么类？做啥的？是什么时候初始化的？

不着急，我们先看简单的“sync是在什么时候初始化的”

在源码中，只有在2个构造函数的地方对sync对象做了初始化，可分别初始化为NonfairSync和FairSync

```java
/** 所有锁操作都是基于这个字段 */
private final Sync sync;
/**
 * 通过该构造函数创建额ReentrantLock是一个非公平锁
 */
public ReentrantLock() {
    sync = new NonfairSync();
}
/**
 * 如果入参为true，则创建公平的ReentrantLock；
 * 否则，创建非公平锁
 */
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```

这两个对象（NonfairSync和NonfairSync）也是ReentrantLock的内部类

![](..\imgs\Java基础\NonfairSync和FairSync类继承结构.jpg)

从上图可以看书**FairSync和NonFairSync在类结构上完全一样且均继承于Sync**

而Sync对象的继承关系如下

![](..\imgs\Java基础\ReentrantLock的内部类Sync实现于AQS类.jpg)

以上我们可以得出这么一个初步结论

> **ReentrantLock实现了Lock接口,操作其成员变量sync这个AQS的子类,来完成锁的相关功能。而sync这个成员变量有2种形态：**NonfairSync和FairSync

ReentrantLock的构造函数中，默认的无参构造函数将会把Sync对象创建为NonfairSync对象，这是一个“非公平锁”；而另一个构造函数ReentrantLock(boolean fair)传入参数为true时将会把Sync对象创建为“公平锁”FairSync

**FairSync、NonfairSync、Sync之间的关系**

在上文中我们提到ReentrantLock的lock操作是调用sync的lock方法。而sync有2种形态，那么我们可以分别对比一下NonfairSync的lock方法和FairSync的lock方法有什么异同。

NoFairSync的lock()方法的执行时序图

![](..\imgs\Java基础\NoFairSync的lock()方法执行时序图.jpg)

FairSync的lock()方法的执行时序图

![](..\imgs\Java基础\FairSync的lock()方法的执行时序图.jpg)

通过对比NofairSync和FairSync的lock方法时序图可以看出两者的操作基本上是大同小异。FairSync在tryAquire方法中，**当判断到锁状态字段state == 0 时，不会立马将当前线程设置为该锁的占用线程，而是去判断是在此线程之前是否有其他线程在等待这个锁（执行hasQueuedPredecessors()方法）**，如果是的话，则该线程会加入到等待队列中，进行排队（FIFO，先进先出的排队形式）。这也就是为什么FairSync可以让线程之间公平获得该锁。

NoFairSync的tryAquire方法中，没有判断是否有在此之前的排队线程，而是直接进行获锁操作，因此多个线程之间同时争用一把锁的时候，谁先获取到就变得随机了，很有可能线程A比线程B更早等待这把锁，但是B却获取到了锁，A继续等待（这种现象叫做：**线程饥饿**）

到此，我们已经大致理解了ReentrantLock是如何做到不同线程如何“公平”和“非公平”获锁。

> 线程之间是什么时候知道要排队的，如何排队的？排队的线程什么时候能获得到锁？排队的线程怎么感知到“锁空闲”？

我们一个个解答上面的疑问

## ***线程是什么时候排队的？***

我们可以猜想一下，应该在获锁的时候，无法成功获取到该锁，然后进行排队等待。是不是这样的呢？

源码贴上来！

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```

各位客官，看到了没，acquireQueued(**addWaiter(Node.EXCLUSIVE)**, arg)

这个**addWaiter(Node.EXCLUSIVE)**就是请求排队的，通过上面的时序图（图8，9）可知该动作是在FairSync或者NoFairSync调用AQS的aquire(1)方法时触发的，而且由该方法中的if块可知，只有当if中的tryAcquire为false的时候（也就是获锁失败的时候），才会执行后续的acquireQueued方法

## ***线程是如何排队的？***

想要知道如何排队，那也就是去理解**addWaiter(Node.EXCLUSIVE)**这个方法具体是如何实现的

而在看源码解析前，各位同学可以思考一下，如果是你来实现一个队列来对线程进行排队和管理，你需要关心什么信息呢？

1. **线程**，肯定要知道我是哪个线程（因为连哪个线程都不知道，你还排啥队，管理个球球？）
2. **队列中线程状态**，既然知道是哪一个线程，肯定还要知道线程当前处在什么状态，是已经取消了“获锁”请求，还是在“”等待中”，或者说“即将得到锁”
3. **前驱和后继线程**，因为是一个等待队列，那么也就需要知道当前线程前面的是哪个线程，当前线程后面的是哪个线程（因为当前线程释放锁以后，理当立马通知后继线程去获取锁）



而上述的数据，在AQS中被组织到了一个叫做**Node**的数据结构（内部类）中

<img src="..\imgs\Java基础\Node类的内部结构.jpg" style="zoom:50%;" />

线程的2种等待模式：

- **SHARED**：表示线程以共享的模式等待锁（如ReadLock）
- **EXCLUSIVE**：表示线程以互斥的模式等待锁（如ReentrantLock），互斥就是一把锁只能由一个线程持有，不能同时存在多个线程使用同一个锁



线程在队列中的状态枚举：

- **CANCELLED**：值为1，表示线程的获锁请求已经“取消”
- **SIGNAL**：值为-1，表示该线程一切都准备好了,就等待锁空闲出来给我
- **CONDITION**：值为-2，表示线程等待某一个条件（Condition）被满足
- **PROPAGATE**：值为-3，当线程处在“SHARED”模式时，该字段才会被使用上（在后续讲共享锁的时候再细聊）

**初始化Node对象时，默认为0**

成员变量：

- **waitStatus**：该int变量表示线程在队列中的状态，其值就是上述提到的CANCELLED、SIGNAL、CONDITION、PROPAGATE
- **prev**：该变量类型为Node对象，表示该节点的前一个Node节点（前驱）
- **next**：该变量类型为Node对象，表示该节点的后一个Node节点（后继）
- **thread**：该变量类型为Thread对象，表示该节点的代表的线程
- **nextWaiter**：该变量类型为Node对象，表示等待condition条件的Node节点（暂时不用管它，不影响我们理解主要知识点）



解释了Node的数据结构，那么我用几张图来表示多线程竞争下ReentrantLock锁时是如何排队的

1. 初始状态（也就是锁未被任何线程占用的时候）线程A申请锁
   **此时，成功获取到锁，无排队线程**
2. 线程B申请该锁，且上一个线程未释放

![](..\imgs\Java基础\第一个进入排队的线程.jpg)

这里需要关注的是Head节点，这个节点是一个空的Node节点，不存储任何线程相关的信息

3. 线程C申请该锁，且占有该锁的线程未释放

<img src="..\imgs\Java基础\第二个进入排队的线程.jpg" style="zoom: 50%;" />

4.线程D申请该锁，且占有该锁的线程未释放

![](..\imgs\Java基础\第三个进入排队的线程.jpg)

通过以上几幅图，就可以大致了解该队列是链表的形式组织不同Node（每一个Node代表一个线程）之间的先后顺序。Tips: **强烈建议没有学习过“数据结构”的同学先去学习一下数据结构！框架和花哨的知识点千变万化层出不穷，唯有底层的计算机原理是共通和基本不变的**

## ***等待中的线程如何感知到锁空闲并获得锁？***

上文我们提到acquireQueued(**addWaiter(Node.EXCLUSIVE)**, arg)中的addWaiter(Node.EXCLUSIVE)方法是对**获锁失败的线程**放入到队列中排队等待，而该方法的外层方法**acquireQueued()**就是对已经排队中的线程进行“获锁”操作

简单地讲：就是一个线程获取锁失败了，被放到了线程等待队列中，而acquireQueued方法就是把放入队列中的这个线程不断进行“获锁”,直到它**“成功获锁”**或者“**不再需要锁（如被中断）**”

这个方法的主要流程

![](..\imgs\Java基础\排队中的Node获锁流程.jpg)

这里需要注意的是：紫色箭头所在的流程实际上是一个**“while循环”\***，跳出该循环的**唯一出**口就是**“p是head节点，并且当前线程获锁成功”**

为什么要这个条件呢？因为这个条件满足就代表**“这个线程是排队线程中的最前面的节点（线程）了**”

再提一句，别嫌啰嗦：“不管**公平**还是**非公平**模式下，ReentrantLock对于排队中的线程都能保证，排在前面的一定比排在后面的线程优先获得锁”**但是，这里有个但是**，非公平模式**不保证**“队列中的第一个线程一定就比新来的（未加入到队列）的线程优先获锁”因为队列中的第一个线程尝试获得锁时，可能刚好来了一个线程也要获取锁，而这个刚来的线程都还未加入到等待队列，此时两个线程同时随机竞争，很有可能，队列中的第一个线程竞争失败（而该线程等待的时间其实比这个刚来的线程等待时间要久）。拗口吗？哈哈，好好理解一下。我尽力了。

这里就有小伙伴问了:“如果就是那么不恰巧,就是不符合这个唯一跳出循环的条件”,那就一直在循环里面空跑了吗!那CPU使用率不就会飙升?!



流程图里有一个步骤“判断当前线程是否需要被阻塞”，如果是的话，就

“**阻塞**线程”！

“**阻塞**线程”！

“**阻塞**线程”！

当线程被阻塞了，也就没有循环什么事情了（阻塞的线程将会让出CPU资源，该线程不会被CPU运行）。直到下次被唤醒，该线程才会继续进行循环体内的操作

重点来了！**“什么时候线程需要被阻塞呢？”**

我们来看一下这个判断的执行流程

![](..\imgs\Java基础\AQS判断线程是否应该被阻塞.jpg)

问题来了:

> 流程图中“CAS设置pred节点状态为SIGNAL“并表示该线程“不应该”被阻塞,那么该线程就会继续在上述提到的**“while循环”\***一直空跑吗?

其实认真看的同学应该就能知道,While循环中必须要这个node符合“**它就是该队列中最早的Node并且获锁成功**”才会跳出While循环体,如果不是,则会继续执行到**“判断这个线程是否应该被阻塞”**,此时,原本状态不是SIGNAL的线程,因为在上一次**“判断这个线程是否应该被阻塞”**这个方法时被设置成了SIGNAL,那么第二次执行这个判断时,就会被成功阻塞。也就不会出现“空跑”的情况

综上所述呢，只要有其他线程因为释放了锁，那么“线程等待队列中的第一个Node节点就可以成功获取到锁（如果没有队列外的线程同时竞争这个锁）”

## **解锁**

在上一个知识点我提到

> 只要有其他线程因为释放了锁，那么“线程等待队列中的第一个Node节点就可以成功获取到锁（如果没有队列外的线程同时竞争这个锁）”

实际上这并不是一个很准确的结论，因为“线程等待队列”中的第一个Node节点在其他线程未释放锁时，因为获取不到锁，那么也会被“阻塞”

这个时候，实际上所有在等待队列中的Node节点里代表的线程都是处于“阻塞”状态。

> 那什么时候唤醒这些阻塞的线程呢？

哈哈，既然申请锁的时候会导致线程在得不到锁时被“阻塞”

那么，肯定就是其他线程在**释放锁时“唤醒”**被阻塞着的线程去“拿锁”。

ReentrantLock中的源码走一个

```java
public void unlock() {
    sync.release(1);
}

public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```

其中，unparkSuccessor(h)方法就是“唤醒操作”，主要流程如代码所示

1. 尝试释放当前线程持有的锁
2. 如果成功释放，那么去唤醒头结点的后继节点（因为头节点head是不保存线程信息的节点，仅仅是因为数据结构设计上的需要，在数据结构上，这种做法往往叫做“空头节点链表”。对应的就有“非空头结点链表”）



unparkSuccessor(h)的执行流程源码解析

```java
private void unparkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);

    //如果head节点的下一个节点它是null或者已经被cancelled了（status>0）
    //那么就从队列的尾巴往前找，找到一个最前面的并且状态不是cancelled的线程
    //至于为什么要从后往前找，不是从前往后找，谁能跟我说一下，这点我也不知道为什么
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    //将找到的队列中符合条件的第一个线程“唤醒”
    if (s != null)
        LockSupport.unpark(s.thread);
}
```



至此，申请锁的“阻塞”和释放锁的“唤醒”操作中**“队列什么时候进行排队”**、**“如何排队”**、**“什么时候移除队列”**、**“何时阻塞线程”**、**“何时唤醒线程”**基本都已经解释清楚了。

## **如何实现可重入**

在讲解lock()方法的图8、图9中，我们有提到**加锁**操作会对**state字段进行+1操作**

这里需要注意到AQS中很多内部变量的修饰符都是采用的volitale,然后配合**CAS操作**来保证AQS本身的线程安全(因为AQS自己线程安全,基于它的衍生类才能更好地保证线程安全),这里的state字段就是AQS类中的一个用volitale修饰的int变量

state字段初始化时,值为0。表示目前没有任何线程持有该锁。当一个线程每次获得该锁时，值就会在原来的基础上加1，多次获锁就会多次加1（指同一个线程），这里就是可重入。因为可以同一个线程多次获锁，只是对这个字段的值在原来基础上加1; 相反unlock操作也就是解锁操作，实际是是调用AQS的release操作，而每执行一次这个操作，就会对state字段在原来的基础上减1，当state==0的时候就表示当前线程已经完全释放了该锁。那么就会如上文提到的那样去调用“唤醒”动作，去把在“线程等待队列中的线程”叫醒

为了加深对个AQS的大致工作流程的理解，我对AQS重点的几个内容画了一个粗略的流程图

![](..\imgs\Java基础\AQS.jpg)

