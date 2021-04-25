在AQS中组织了一个叫做**Node**的数据结构（内部类）

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
- **nextWaiter**：该变量类型为Node对象，表示等待condition条件的Node节点