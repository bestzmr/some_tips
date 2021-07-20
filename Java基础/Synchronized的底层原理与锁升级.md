## Synchronized的使用原理

1、Java对象头

首先，我们要知道对象在内存中的布局：

已知对象是存放在堆内存中的，对象大致可以分为三个部分，分别是对象头、实例变量和填充字节。

对象头主要是由MarkWord和Klass Point(类型指针)组成，其中Klass Point是是对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例，Mark Word用于存储对象自身的运行时数据。如果对象是数组对象，那么对象头占用3个字宽（Word），如果对象是非数组对象，那么对象头占用2个字宽。（1word = 2 Byte = 16 bit）
实例变量存储的是对象的属性信息，包括父类的属性信息，按照4字节对齐
填充字符，因为虚拟机要求对象字节必须是8字节的整数倍，填充字符就是用于凑齐这个整数倍的

![](..\imgs\Java基础\对象在内存中的结构.jpg)

通过第一部分可以知道，Synchronized不论是修饰方法还是代码块，都是通过持有修饰对象的锁来实现同步，那么Synchronized锁对象是存在哪里的呢？答案是存在锁对象的对象头的MarkWord中。那么MarkWord在对象头中到底长什么样，也就是它到底存储了什么呢？

在32位的虚拟机中：
![](..\imgs\Java基础\在32位的对象头.jpg)

在64位的虚拟机中：

![](..\imgs\Java基础\在64位的对象头.jpg)

上图中的偏向锁和轻量级锁都是在java6以后对锁机制进行优化时引进的，下文的锁升级部分会具体讲解，Synchronized关键字对应的是重量级锁，接下来对重量级锁在Hotspot JVM中的实现锁讲解。

2、Synchronized在JVM中的实现原理

重量级锁对应的锁标志位是10，存储了指向重量级监视器锁的指针，在Hotspot中，对象的监视器（monitor）锁对象由ObjectMonitor

实现（C++），其跟同步相关的数据结构如下：

```java
ObjectMonitor() {
    _count        = 0; //用来记录该对象被线程获取锁的次数
    _waiters      = 0;
    _recursions   = 0; //锁的重入次数
    _owner        = NULL; //指向持有ObjectMonitor对象的线程 
    _WaitSet      = NULL; //处于wait状态的线程，会被加入到_WaitSet
    _WaitSetLock  = 0 ;
    _EntryList    = NULL ; //处于等待锁block状态的线程，会被加入到该列表
  }
```

光看这些数据结构对监视器锁的工作机制还是一头雾水，那么我们首先看一下线程在获取锁的几个状态的转换：

![](..\imgs\Java基础\线程在获取锁时的状态转换.jpg)

线程的生命周期存在5个状态，start、running、waiting、blocking和dead

对于一个synchronized修饰的方法(代码块)来说：

当多个线程同时访问该方法，那么这些线程会先被放进_EntryList队列，此时线程处于blocking状态
当一个线程获取到了实例对象的监视器（monitor）锁，那么就可以进入running状态，执行方法，此时，ObjectMonitor对象的_owner指向当前线程，_count加1表示当前对象锁被一个线程获取
当running状态的线程调用wait()方法，那么当前线程释放monitor对象，进入waiting状态，ObjectMonitor对象的_owner变为null，_count减1，同时线程进入_WaitSet队列，直到有线程调用notify()方法唤醒该线程，则该线程重新获取monitor对象进入_Owner区
如果当前线程执行完毕，那么也释放monitor对象，进入waiting状态，ObjectMonitor对象的_owner变为null，_count减1
那么Synchronized修饰的代码块/方法如何获取monitor对象的呢？

在JVM规范里可以看到，不管是方法同步还是代码块同步都是基于进入和退出monitor对象来实现，然而二者在具体实现上又存在很大的区别。通过javap对class字节码文件反编译可以得到反编译后的代码。

（1）Synchronized修饰代码块：

Synchronized代码块同步在需要同步的代码块开始的位置插入monitorentry指令，在同步结束的位置或者异常出现的位置插入monitorexit指令；JVM要保证monitorentry和monitorexit都是成对出现的，任何对象都有一个monitor与之对应，当这个对象的monitor被持有以后，它将处于锁定状态。

例如，同步代码块如下：

```java
public class SyncCodeBlock {
   public int i;
   public void syncTask(){
       synchronized (this){
           i++;
       }
   }
}
```

对同步代码块编译后的class字节码文件反编译，结果如下（仅保留方法部分的反编译内容）：

```java
  public void syncTask();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=3, locals=3, args_size=1
         0: aload_0
         1: dup
         2: astore_1
         3: monitorenter  //注意此处，进入同步方法
         4: aload_0
         5: dup
         6: getfield      #2             // Field i:I
         9: iconst_1
        10: iadd
        11: putfield      #2            // Field i:I
        14: aload_1
        15: monitorexit   //注意此处，退出同步方法
        16: goto          24
        19: astore_2
        20: aload_1
        21: monitorexit //注意此处，退出同步方法
        22: aload_2
        23: athrow
        24: return
      Exception table:
      //省略其他字节码.......
```

可以看出同步方法块在进入代码块时插入了monitorentry语句，在退出代码块时插入了monitorexit语句，为了保证不论是正常执行完毕（第15行）还是异常跳出代码块（第21行）都能执行monitorexit语句，因此会出现两句monitorexit语句。

（2）Synchronized修饰方法：

Synchronized方法同步不再是通过插入monitorentry和monitorexit指令实现，而是由方法调用指令来读取运行时常量池中的ACC_SYNCHRONIZED标志隐式实现的，如果方法表结构（method_info Structure）中的ACC_SYNCHRONIZED标志被设置，那么线程在执行方法前会先去获取对象的monitor对象，如果获取成功则执行方法代码，执行完毕后释放monitor对象，如果monitor对象已经被其它线程获取，那么当前线程被阻塞。

同步方法代码如下：

```java
public class SyncMethod {
   public int i;
   public synchronized void syncTask(){
           i++;
   }
}
```

对同步方法编译后的class字节码反编译，结果如下（仅保留方法部分的反编译内容）：

```java
public synchronized void syncTask();
    descriptor: ()V
    //方法标识ACC_PUBLIC代表public修饰，ACC_SYNCHRONIZED指明该方法为同步方法
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=3, locals=1, args_size=1
         0: aload_0
         1: dup
         2: getfield      #2                  // Field i:I
         5: iconst_1
         6: iadd
         7: putfield      #2                  // Field i:I
        10: return
      LineNumberTable:
        line 12: 0
        line 13: 10
}
```

可以看出方法开始和结束的地方都没有出现monitorentry和monitorexit指令，但是出现的ACC_SYNCHRONIZED标志位。



## 锁的优化

1、锁升级
锁的4中状态：无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态（级别从低到高）

（1）偏向锁：

为什么要引入偏向锁？

因为经过HotSpot的作者大量的研究发现，大多数时候是不存在锁竞争的，常常是一个线程多次获得同一个锁，因此如果每次都要竞争锁会增大很多没有必要付出的代价，为了降低获取锁的代价，才引入的偏向锁。

偏向锁的升级

当线程1访问代码块并获取锁对象时，会在java对象头和栈帧中记录偏向的锁的threadID，因为偏向锁不会主动释放锁，因此以后线程1再次获取锁的时候，需要比较当前线程的threadID和Java对象头中的threadID是否一致，如果一致（还是线程1获取锁对象），则无需使用CAS来加锁、解锁；如果不一致（其他线程，如线程2要竞争锁对象，而偏向锁不会主动释放因此还是存储的线程1的threadID），那么需要查看Java对象头中记录的线程1是否存活，如果没有存活，那么锁对象被重置为无锁状态，其它线程（线程2）可以竞争将其设置为偏向锁；如果存活，那么立刻查找该线程（线程1）的栈帧信息，如果还是需要继续持有这个锁对象，那么暂停当前线程1，撤销偏向锁，升级为轻量级锁，如果线程1 不再使用该锁对象，那么将锁对象状态设为无锁状态，重新偏向新的线程。

偏向锁的取消：

偏向锁是默认开启的，而且开始时间一般是比应用程序启动慢几秒，如果不想有这个延迟，那么可以使用-XX:BiasedLockingStartUpDelay=0；

如果不想要偏向锁，那么可以通过-XX:-UseBiasedLocking = false来设置；
（2）轻量级锁

为什么要引入轻量级锁？

轻量级锁考虑的是竞争锁对象的线程不多，而且线程持有锁的时间也不长的情景。因为阻塞线程需要CPU从用户态转到内核态，代价较大，如果刚刚阻塞不久这个锁就被释放了，那这个代价就有点得不偿失了，因此这个时候就干脆不阻塞这个线程，让它自旋这等待锁释放。

轻量级锁什么时候升级为重量级锁？

线程1获取轻量级锁时会先把锁对象的对象头MarkWord复制一份到线程1的栈帧中创建的用于存储锁记录的空间（称为DisplacedMarkWord），然后使用CAS把对象头中的内容替换为线程1存储的锁记录（DisplacedMarkWord）的地址；

如果在线程1复制对象头的同时（在线程1CAS之前），线程2也准备获取锁，复制了对象头到线程2的锁记录空间中，但是在线程2CAS的时候，发现线程1已经把对象头换了，线程2的CAS失败，那么线程2就尝试使用自旋锁来等待线程1释放锁。

但是如果自旋的时间太长也不行，因为自旋是要消耗CPU的，因此自旋的次数是有限制的，比如10次或者100次，如果自旋次数到了线程1还没有释放锁，或者线程1还在执行，线程2还在自旋等待，这时又有一个线程3过来竞争这个锁对象，那么这个时候轻量级锁就会膨胀为重量级锁。重量级锁把除了拥有锁的线程都阻塞，防止CPU空转。

 

*注意：为了避免无用的自旋，轻量级锁一旦膨胀为重量级锁就不会再降级为轻量级锁了；偏向锁升级为轻量级锁也不能再降级为偏向锁。一句话就是锁可以升级不可以降级，但是偏向锁状态可以被重置为无锁状态。

（3）这几种锁的优缺点（偏向锁、轻量级锁、重量级锁）
![](..\imgs\Java基础\三种锁的优缺点.jpg)