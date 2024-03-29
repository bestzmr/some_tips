### Redis数据类型
![](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/F2E1EA8A8E774451BEF2BCA8D9868DBA.png)






### Reids的特点
* redis本质上是一个Key-Value类型的内存数据库,整个数据库统统加载在内存当中进行操作，定期通过异步操作把数据库数据flush到硬盘上进行保存。因为是纯内存操作，Redis的性能非常出色，每秒可以处理超过 10万次读写操作，是已知性能最快的Key-Value DB。
* Redis的出色之处不仅仅是性能，Redis最大的魅力是支持保存多种数据结构，此外单个value的最大限制是1GB,因此Redis可以用来实现很多有用的功能，比方说用他的List来做FIFO双向链表，实现一个轻量级的高性能消息队列服务，用他的Set可以做高性能的tag系统等等。
* 另外Redis也可以对存入的Key-Value设置expire时间，因此也可以被当作一 个功能加强版的memcached来用。Redis的主要缺点是数据库容量受到物理内存的限制，不能用作海量数据的高性能读写，因此Redis适合的场景主要局限在较小数据量的高性能操作和运算上。


### 使用redis有哪些好处
* 速度快，因为数据存在内存中，类似于HashMap，HashMap的优势就是查找和操作的时间复杂度都是O(1)
* 支持丰富数据类型，支持string，list，set，sorted set，hash
* 支持事务，操作都是原子性，所谓的原子性就是对数据的更改要么全部执行，要么全部不执行
* 丰富的特性：可用于缓存，消息，按key设置过期时间，过期后将会自动删除

### Redis持久化的方式
* RDB[默认的配置方式 - 开启状态]
    * RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘，实际操作过程是fork一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。
> 1.	默认开启，会按照配置的指定时间将内存中的数据快照到磁盘中，创建一个dump.rdb文件，redis启动时再恢复到内存中。
>2.	redis会单独创建fork()一个子进程，将当前父进程的数据库数据复制到子进程的内存中，然后由子进程写入到临时文件中，持久化的过程结束了，再用这个临时文件替换上次的快照文件，然后子进程退出，内存释放。
>3.	需要注意的是，每次快照持久化都会将主进程的数据库数据复制一遍，导致内存开销加倍，若此时内存不足，则会阻塞服务器运行，直到复制结束释放内存；都会将内存数据完整写入磁盘一次，所以如果数据量大的话，而且写操作频繁，必然会引起大量的磁盘I/O操作，严重影响性能，并且最后一次持久化后的数据可能会丢失；

* AOF
    * AOF持久化以日志的形式记录服务器所处理的每一个写、删除操作，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。
>0.	以日志的形式记录每个写操作（读操作不记录），只需追加文件但不可以改写文件，redis启动时会根据日志从头到尾全部执行一遍以完成数据的恢复工作。包括flushDB也会执行。
>1.	主要有两种方式触发：有写操作就写、每秒定时写（也会丢数据）。
>2.	因为AOF采用追加的方式，所以文件会越来越大，针对这个问题，新增了重写机制，就是当日志文件大到一定程度的时候，会fork出一条新进程来遍历进程内存中的数据，每条记录对应一条set语句，写到临时文件中，然后再替换到旧的日志文件（类似rdb的操作方式）。默认触发是当aof文件大小是上次重写后大小的一倍且文件大于64M时触发；
>4.	当两种方式同时开启时，数据恢复redis会优先选择AOF恢复。一般情况下，只要使用默认开启的RDB即可，因为相对于AOF，RDB便于进行数据库备份，并且恢复数据集的速度也要快很多。
>5.	开启持久化缓存机制，对性能会有一定的影响，特别是当设置的内存满了的时候，更是下降到几百reqs/s。所以如果只是用来做缓存的话，可以关掉持久化。



### 存储在Redis的数据持久化到数据库中保持数据同步问题
* 采用延时双删策略
    * 在写库前后都进行redis.del(key)操作，并且设定合理的超时时间。
* 异步更新缓存
    * MySQL binlog增量订阅消费+消息队列+增量数据更新到redis
    * 增量,指的是mysql的update、insert、delate变更数据。
    * 读取binlog后分析 ，利用消息队列,推送更新各台的redis缓存数据。


### Redis雪崩
* 数据未加载到缓存中，或者缓存同一时间大面积的失效，从而导致所有请求都去查数据库，导致数据库CPU和内存负载过高，甚至宕机。
* 雪崩的简单过程：
    * redis集群大面积故障
    * 缓存失效，但依然大量请求访问缓存服务redis
    * redis大量失效后，大量请求转向到mysql数据库
    * mysql的调用量暴增，很快就扛不住了，甚至直接宕机
    * 由于大量的应用服务依赖mysql和redis的服务，这个时候很快会演变成各服务器集群的雪崩，最后网站彻底崩溃。

* 防止雪崩
    * 缓存层设计成高可用，防止缓存大面积故障。即使个别节点、个别机器、甚至是机房宕掉，依然可以提供服务，例如 Redis Sentinel 和 Redis Cluster 都实现了高可用。
    * 可以利用ehcache等本地缓存(暂时支持)，但主要还是对源服务访问进行限流、资源隔离（熔断）、降级等。当访问量剧增、服务出现问题仍然需要保证服务还是可用的。系统可以根据一些关键数据进行自动降级,降级的最终目的是保证核心服务可用，即使是有损的。
    * Redis备份和快速预热
        * Redis数据备份和恢复
        * 快速缓存预热

### Redis穿透
* 缓存穿透是指查询一个一不存在的数据。例如：从缓存redis没有命中，需要从mysql数据库查询，查不到数据则不写入缓存，这将导致这个不存在的数据每次请求都要到数据库去查询，造成缓存穿透。
* 解决思路
    * 如果查询数据库也为空，直接设置一个默认值存放到缓存，这样第二次到缓冲中获取就有值了，而不会继续访问数据库。设置一个过期时间或者当有值的时候将缓存中的值替换掉即可。可以给key设置一些格式规则，然后查询之前先过滤掉不符合规则的Key。

### Redis应用
* 会话缓存
* 全页缓存（FPC）
* 排行榜/计数器
* 发布/订阅
