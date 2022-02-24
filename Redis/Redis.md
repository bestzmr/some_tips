### 什么是Redis？它主要用来干什么的？

Redis，英文全称是**Remote Dictionary Server**（远程字典服务），是一个开源的使用ANSI C语言编写、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。

与MySQL数据库不同的是，Redis的数据是存在内存中的。它的读写速度非常快，每秒可以处理超过10万次读写操作。因此redis被**广泛应用于缓存**，另外，Redis也经常用来做分布式锁。除此之外，Redis支持事务、持久化、LUA 脚本、LRU 驱动事件、多种集群方案。



### Redis使用场景?

秒杀的库存扣减，APP首页的访问流量高峰等等，都很容易把数据库打崩，所以引入了缓存中间件，目前市面上比较常用的缓存中间件有 **Redis** 和 **Memcached** 不过中和考虑了他们的优缺点，最后选择了Redis



### Redis的数据结构?

**String(字符串)**、**Hash(哈希)**、**List(列表)**、**Set(集合)**、**SortedSet(有序集合)**。（如果你是Redis中高级用户，而且你要在这次面试中突出你和其他候选人的不同，还需要加上下面几种数据结构**HyperLogLog、Geo[Geospatial]、Bitmap、Pub/Sub**。如果你还想加分，那你说还玩过**Redis Module**，像**BloomFilter[布隆过滤器]，RedisSearch，Redis-ML**）

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/770a0fe087fa4a7ca8b9866a3aa25675~tplv-k3u1fbpfcp-watermark.awebp)

#### String（字符串）

- 简介:String是Redis最基础的数据结构类型，它是二进制安全的，可以存储图片或者序列化的对象，值最大存储为512M
- 简单使用举例: `set key value`、`get key`等
- 应用场景：共享session、分布式锁，计数器、限流。
- 内部编码有3种，`int（8字节长整型）/embstr（小于等于39字节字符串）/raw（大于39个字节字符串）`

C语言的字符串是`char[]`实现的，而Redis使用**SDS（simple dynamic string）** 封装，sds源码如下：

```
struct sdshdr{
  unsigned int len; // 标记buf的长度
  unsigned int free; //标记buf中未使用的元素个数
  char buf[]; // 存放元素的坑
}
复制代码
```

SDS 结构图如下： ![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/7095fec177e74ff783b96c2be450b24e~tplv-k3u1fbpfcp-watermark.awebp)

Redis为什么选择**SDS**结构，而C语言原生的` char[]`不香吗？

> 举例其中一点，SDS中，O(1)时间复杂度，就可以获取字符串长度；而C 字符串，需要遍历整个字符串，时间复杂度为O(n)



#### Hash（哈希）

- 简介：在Redis中，哈希类型是指v（值）本身又是一个键值对（k-v）结构
- 简单使用举例：`hset key field value` 、`hget key field`
- 内部编码：`ziplist（压缩列表）` 、`hashtable（哈希表）`
- 应用场景：缓存用户信息等。
- **注意点**：如果开发使用hgetall，哈希元素比较多的话，可能导致Redis阻塞，可以使用hscan。而如果只是获取部分field，建议使用hmget。

#### List（列表）

- 简介：列表（list）类型是用来存储多个有序的字符串，一个列表最多可以存储2^32-1个元素。
- 简单实用举例：` lpush  key  value [value ...]` 、`lrange key start end`
- 内部编码：ziplist（压缩列表）、linkedlist（链表）
- 应用场景： 消息队列，文章列表,

一图看懂list类型的插入与弹出： ![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/359938c65085430a96657dd11a090e3c~tplv-k3u1fbpfcp-watermark.awebp)

list应用场景参考以下：

> - lpush+lpop=Stack（栈）
> - lpush+rpop=Queue（队列）
> - lpsh+ltrim=Capped Collection（有限集合）
> - lpush+brpop=Message Queue（消息队列）

#### Set（集合）

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/8a73581873cc49b18025644c5e4a0d47~tplv-k3u1fbpfcp-watermark.awebp)

- 简介：集合（set）类型也是用来保存多个的字符串元素，但是不允许重复元素
- 简单使用举例：`sadd key element [element ...]`、`smembers key`
- 内部编码：`intset（整数集合）`、`hashtable（哈希表）`
- **注意点**：smembers和lrange、hgetall都属于比较重的命令，如果元素过多存在阻塞Redis的可能性，可以使用sscan来完成。
- 应用场景： 用户标签,生成随机数抽奖、社交需求。

#### 有序集合（zset）

- 简介：已排序的字符串集合，同时元素不能重复
- 简单格式举例：`zadd key score member [score member ...]`，`zrank key member`
- 底层内部编码：`ziplist（压缩列表）`、`skiplist（跳跃表）`
- 应用场景：排行榜，社交需求（如用户点赞）。

### Redis 的三种特殊数据类型?

- Geo：Redis3.2推出的，地理位置定位，用于存储地理位置信息，并对存储的信息进行操作。
- HyperLogLog：用来做基数统计算法的数据结构，如统计网站的UV。
- Bitmaps ：用一个比特位来映射某个元素的状态，在Redis中，它的底层是基于字符串类型实现的，可以把bitmaps成作一个以比特位为单位的数组

### 如果有大量的key需要设置同一时间过期，一般需要注意什么？ 

如果大量的key过期时间设置的过于集中，到过期的那个时间点，**Redis**可能会出现短暂的卡顿现象。严重的话会出现缓存雪崩，我们一般需要在时间上加一个随机值，使得过期时间分散一些。

**电商首页经常会使用定时任务刷新缓存，可能大量的数据失效时间都十分集中，如果失效时间一样，又刚好在失效的时间点大量用户涌入，就有可能造成缓存雪崩**

### 什么是缓存击穿、缓存穿透、缓存雪崩？



## 专题：Redis为什么这么快？

![Redis为什么这么快](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/ce8bd1c23e4347d1b382ecd78b87912c~tplv-k3u1fbpfcp-watermark.awebp)

### 1 基于内存存储实现

我们都知道内存读写是比在磁盘快很多的，Redis基于内存存储实现的数据库，相对于数据存在磁盘的MySQL数据库，省去磁盘I/O的消耗。

### 2 高效的数据结构

我们知道，Mysql索引为了提高效率，选择了B+树的数据结构。其实合理的数据结构，就是可以让你的应用/程序更快。先看下Redis的数据结构&内部编码图：

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/a63c298d256b436ea36112c6aecc7404~tplv-k3u1fbpfcp-watermark.awebp)

#### SDS简单动态字符串

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/3b08c79d7af94507be1975bc1bd32b9f~tplv-k3u1fbpfcp-watermark.awebp)

> - 字符串长度处理：Redis获取字符串长度，时间复杂度为O(1)，而C语言中，需要从头开始遍历，复杂度为O（n）;
> - 空间预分配：字符串修改越频繁的话，内存分配越频繁，就会消耗性能，而SDS修改和空间扩充，会额外分配未使用的空间，减少性能损耗。
> - 惰性空间释放：SDS 缩短时，不是回收多余的内存空间，而是free记录下多余的空间，后续有变更，直接使用free中记录的空间，减少分配。
> - 二进制安全：Redis可以存储一些二进制数据，在C语言中字符串遇到'\0'会结束，而 SDS中标志字符串结束的是len属性。

#### 字典

Redis 作为 K-V 型内存数据库，所有的键值就是用字典来存储。字典就是哈希表，比如HashMap，通过key就可以直接获取到对应的value。而哈希表的特性，在O（1）时间复杂度就可以获得对应的值。

#### 跳跃表

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/0b62d59ffbd945e18f6dfcbf650a6eed~tplv-k3u1fbpfcp-watermark.awebp)

> - 跳跃表是Redis特有的数据结构，就是在链表的基础上，增加多级索引提升查找效率。
> - 跳跃表支持平均 O（logN）,最坏 O（N）复杂度的节点查找，还可以通过顺序性操作批量处理节点。

### 3 合理的数据编码

Redis 支持多种数据数据类型，每种基本类型，可能对多种数据结构。什么时候,使用什么样数据结构，使用什么样编码，是redis设计者总结优化的结果。

> - String：如果存储数字的话，是用int类型的编码;如果存储非数字，小于等于39字节的字符串，是embstr；大于39个字节，则是raw编码。
> - List：如果列表的元素个数小于512个，列表每个元素的值都小于64字节（默认），使用ziplist编码，否则使用linkedlist编码
> - Hash：哈希类型元素个数小于512个，所有值小于64字节的话，使用ziplist编码,否则使用hashtable编码。
> - Set：如果集合中的元素都是整数且元素个数小于512个，使用intset编码，否则使用hashtable编码。
> - Zset：当有序集合的元素个数小于128个，每个元素的值小于64字节时，使用ziplist编码，否则使用skiplist（跳跃表）编码

### 4 合理的线程模型

**I/O 多路复用**

![I/O 多路复用](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/a2bfa718845848d58f41567fd2dbca6b~tplv-k3u1fbpfcp-watermark.awebp)

> 多路I/O复用技术可以让单个线程高效的处理多个连接请求，而Redis使用用epoll作为I/O多路复用技术的实现。并且，Redis自身的事件处理模型将epoll中的连接、读写、关闭都转换为事件，不在网络I/O上浪费过多的时间。

什么是I/O多路复用？

> - I/O ：网络 I/O
> - 多路 ：多个网络连接
> - 复用：复用同一个线程。
> - IO多路复用其实就是一种同步IO模型，它实现了一个线程可以监视多个文件句柄；一旦某个文件句柄就绪，就能够通知应用程序进行相应的读写操作；而没有文件句柄就绪时,就会阻塞应用程序，交出cpu。

**单线程模型**

- Redis是单线程模型的，而单线程避免了CPU不必要的上下文切换和竞争锁的消耗。也正因为是单线程，如果某个命令执行过长（如hgetall命令），会造成阻塞。Redis是面向快速执行场景的数据库。，所以要慎用如smembers和lrange、hgetall等命令。
- Redis 6.0 引入了多线程提速，它的执行命令操作内存的仍然是个单线程。

### 5 虚拟内存机制

Redis直接自己构建了VM机制 ，不会像一般的系统会调用系统函数处理，会浪费一定的时间去移动和请求。

**Redis的虚拟内存机制是啥呢？**

> 虚拟内存机制就是暂时把不经常访问的数据(冷数据)从内存交换到磁盘中，从而腾出宝贵的内存空间用于其它需要访问的数据(热数据)。通过VM功能可以实现冷热数据分离，使热数据仍在内存中、冷数据保存到磁盘。这样就可以避免因为内存不足而造成访问速度下降的问题。







### 布隆过滤器是什么？

应对**缓存穿透**问题，我们可以使用**布隆过滤器**。布隆过滤器是什么呢？

布隆过滤器是一种占用空间很小的数据结构，它由一个很长的二进制向量和一组Hash映射函数组成，它用于检索一个元素是否在一个集合中，空间效率和查询时间都比一般的算法要好的多，缺点是有一定的误识别率和删除困难。

**布隆过滤器原理是？** 假设我们有个集合A，A中有n个元素。利用**k个哈希散列**函数，将A中的每个元素**映射**到一个长度为a位的数组B中的不同位置上，这些位置上的二进制数均设置为1。如果待检查的元素，经过这k个哈希散列函数的映射后，发现其k个位置上的二进制数**全部为1**，这个元素很可能属于集合A，反之，**一定不属于集合A**。

来看个简单例子吧，假设集合A有3个元素，分别为{**d1,d2,d3**}。有1个哈希函数，为**Hash1**。现在将A的每个元素映射到长度为16位数组B。

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/6815a98b8d854141b8067595cf29b8f6~tplv-k3u1fbpfcp-watermark.awebp)

我们现在把d1映射过来，假设Hash1（d1）= 2，我们就把数组B中，下标为2的格子改成1，如下：

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/d692aaeb90494cbca21d262af6a04c07~tplv-k3u1fbpfcp-watermark.awebp)

我们现在把**d2**也映射过来，假设Hash1（d2）= 5，我们把数组B中，下标为5的格子也改成1，如下：

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/39e67c860a3a427894a0cada168b6ea2~tplv-k3u1fbpfcp-watermark.awebp)

接着我们把**d3**也映射过来，假设Hash1（d3）也等于 2，它也是把下标为2的格子标1：

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/ffcb53c8e59c43f6bc7be0261dae5e31~tplv-k3u1fbpfcp-watermark.awebp)

因此，我们要确认一个元素dn是否在集合A里，我们只要算出Hash1（dn）得到的索引下标，只要是0，那就表示这个元素**不在集合A**，如果索引下标是1呢？那该元素**可能**是A中的某一个元素。因为你看，d1和d3得到的下标值，都可能是1，还可能是其他别的数映射的，布隆过滤器是存在这个**缺点**的：会存在**hash碰撞**导致的假阳性，判断存在误差。

如何**减少这种误差**呢？

- 搞多几个哈希函数映射，降低哈希碰撞的概率
- 同时增加B数组的bit长度，可以增大hash函数生成的数据的范围，也可以降低哈希碰撞的概率

我们又增加一个Hash2**哈希映射**函数，假设Hash2（d1）=6,Hash2（d3）=8,它俩不就不冲突了嘛，如下：

![img](https://gitee.com/bestzmr/blog-resources/raw/master/resources/imgs/e3a0ac0218ed4121a15fc8cc0b9d52f4~tplv-k3u1fbpfcp-watermark.awebp)

即使存在误差，我们可以发现，布隆过滤器并**没有存放完整的数据**，它只是运用一系列哈希映射函数计算出位置，然后填充二进制向量。如果**数量很大的话**，布隆过滤器通过极少的错误率，换取了存储空间的极大节省，还是挺划算的。

目前布隆过滤器已经有相应实现的开源类库啦，如**Google的Guava类库**，Twitter的 Algebird 类库，信手拈来即可，或者基于Redis自带的Bitmaps自行实现设计也是可以的。

### 



### 延时双删？

