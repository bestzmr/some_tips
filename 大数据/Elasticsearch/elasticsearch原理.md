**什么是全文搜索引擎**？

> **[百度百科中的定义](https://baike.baidu.com/item/全文搜索引擎)**：
> 全文搜索引擎是目前广泛应用的主流搜索引擎。它的工作原理是计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。

**为什么要使用Elasticsearch而不是数据库库?**

 　　因为在我们商城中的数据，将来会非常多，所以采用以往的模糊查询，模糊查询前置配置，会放弃索引，导致商品查询是全表扫面，在百万级别的数据库中，效率非常低下，而我们使用ES做一个全文索引，我们将经常查询的商品的某些字段，比如说商品名，描述、价格还有id这些字段我们放入我们索引库里，可以提高查询速度。



**什么时候使用全文搜索引擎**？

1. 搜索的数据对象是大量的非结构化的文本数据。
2. 文件记录量达到数十万或数百万个甚至更多。
3. 支持大量基于交互式文本的查询。
4. 需求非常灵活的全文搜索查询。
5. 对高度相关的搜索结果的有特殊需求，但是没有可用的关系数据库可以满足。
6. 对不同记录类型、非文本数据操作或安全事务处理的需求相对较少的情况。



现在主流的搜索引擎大概就是：Lucene，Solr，ElasticSearch。

它们的索引建立都是根据**倒排索引**的方式生成索引，何谓倒排索引？

> 维基百科
> 倒排索引（英语：Inverted index），也常被称为反向索引、置入档案或反向档案，是一种索引方法，被用来存储在全文搜索下某个单词在一个文档或者一组文档中的存储位置的映射。它是文档检索系统中最常用的数据结构。

**Lucene、ElasticSearch、Solr的选择？**

**Lucene**

Lucene是一个Java全文搜索引擎，完全用Java编写。Lucene不是一个完整的应用程序，而是一个代码库和API，可以很容易地用于向应用程序添加搜索功能。由于Lucene的复杂性，一般很少会考虑它作为搜索的第一选择，排除一些公司需要自研搜索框架，底层需要依赖Lucene。所以这里我们重点分析 Elasticsearch 和 Solr。

ElasticSearch和Solr这两个搜索引擎都是流行的，先进的的开源搜索引擎。它们都是围绕核心底层搜索库 - Lucene构建的 - 但它们又是不同的。

- 由于**易于使用**，Elasticsearch在新开发者中更受欢迎。但是，如果您已经习惯了与Solr合作，请继续使用它，因为迁移到Elasticsearch没有特定的优势。
- 如果除了搜索文本之外还需要它来**处理分析查询**，Elasticsearch是更好的选择。
- 如果需要**分布式索引**，则需要选择Elasticsearch。对于需要良好可伸缩性和性能的云和分布式环境，Elasticsearch是更好的选择。
- 两者都有良好的**商业支持**（咨询，生产支持，整合等）
- 两者都有很好的操作工具，尽管Elasticsearch因其易于使用的API而更多地吸引了DevOps人群，因此可以围绕它创建一个更加生动的工具生态系统。
- Elasticsearch在**开源日志管理**用例中占据主导地位，许多组织在Elasticsearch中索引它们的日志以使其可搜索。虽然Solr现在也可以用于此目的，但它只是错过了这一想法。
- Solr仍然更加面向文本搜索。另一方面，Elasticsearch 通常用于过滤和分组 - 分析查询工作负载 - 而不一定是文本搜索。Elasticsearch 开发人员在 Lucene 和 Elasticsearch 级别上投入了大量精力使此类查询更高效(降低内存占用和CPU使用)。因此，对于不仅需要进行文本搜索，而且需要复杂的搜索时间聚合的应用程序，Elasticsearch是一个更好的选择。
- **Elasticsearch更容易上手**，一个下载和一个命令就可以启动一切。Solr传统上需要更多的工作和知识，但Solr最近在消除这一点上取得了巨大的进步，现在只需努力改变它的声誉。
- 在**性能方面，它们大致相同**。我说“大致”，因为没有人做过全面和无偏见的基准测试。对于95％的用例，任何一种选择在性能方面都会很好，剩下的5％需要用它们的特定数据和特定的访问模式来测试这两种解决方案。
- 从操作上讲，Elasticsearch使用起来比较简单 - 它只有一个进程。Solr在其类似Elasticsearch的完全分布式部署模式SolrCloud中依赖于Apache ZooKeeper。ZooKeeper是超级成熟，超级广泛使用等等，但它仍然是另一个活跃的部分。也就是说，如果您使用的是Hadoop，HBase，Spark，Kafka或其他一些较新的分布式软件，您可能已经在组织的某个地方运行ZooKeeper。
- 虽然Elasticsearch内置了类似ZooKeeper的组件Xen，但ZooKeeper可以更好地防止有时在Elasticsearch集群中出现的可怕的裂脑问题。公平地说，Elasticsearch开发人员已经意识到这个问题，并致力于改进Elasticsearch的这个方面。
- 如果您喜欢**监控和指标**，那么使用Elasticsearch，您将会进入天堂。这个东西比新年前夜在时代广场可以挤压的人有更多的指标！Solr暴露了关键指标，但远不及Elasticsearch那么多。

ElasticSearch中的术语有哪些？

分片：ES的“分片(shard)”机制可将一个索引内部的数据分布地存储于多个节点，它通过**将一个索引切分为多个**底层物理的Lucene索引完成**索引数据的分割存储**功能，这每一个物理的Lucene索引称为一个分片(shard)。这样的好处是可以**把一个大的索引拆分成多个，分布到不同的节点上**。降低单服务器的压力，构成分布式搜索，**提高整体检索的效率（分片数的最优值与硬件参数和数据量大小有关）。**分片的数量**只能在索引创建前指定，并且索引创建后不能更改。**

副本： 副本是一个分片的**精确复制**，每个分片可以有零个或多个副本。副本的作用一是**提高系统的容错性**，当某个节点某个分片损坏或丢失时可以从副本中恢复。二是**提高es的查询效率**，es会自动对搜索请求进行负载均衡。**特别注意的是，**根据官网信息：在Elasticsearch 6.0.0或更高版本中创建的索引**只能包含一个映射类型**。在5.x中创建的具有多种映射类型的索引将继续像在Elasticsearch 6.x中一样工作。**类型将在Elasticsearch 7.0.0中的API中弃用，并在8.0.0中完全删除。**

节点：运行了**单个实例的ES主机称为节点**，它是集群的一个成员，可以存储数据、参与集群索引及搜索操作。节点通过为其配置的ES集群名称确定其所要加入的集群。

集群： ES可以作为一个独立的单个搜索服务器。不过，一般为了处理大型数据集，实现容错和高可用性，ES可以运行在许多互相合作的服务器上。这些服务器的集合称为集群。

索引：索引是具有某些类似特征的文档集合。例如，您可以拥有店铺数据的索引，商品的一个索引以及订单数据的一个索引。

类型：类型，曾经是索引的逻辑类别/分区，允许您在同一索引中存储不同类型的文档，例如，一种类型用于用户，另一种类型用于博客帖子。

文档：文档是可以建立索引的基本信息单元。基于JSON格式进行表示。文档由一个或多个域组成，每个域拥有一个名字及一个或多个值，有多个值的域通常称为“多值域”。每个文档可以存储不同的域集，但同一类型下的文档至应该有某种程度上的相似之处。**相当于mysql表中的row**。

映射：映射是定义文档及其包含的字段如何存储和索引的过程。例如，使用映射来定义：

- - 哪些字符串字段应该被视为全文字段。
  - 哪些字段包含数字、日期或地理位置。
  - 文档中所有字段的值是否应该被索引到catch-all _all字段中。
  - 日期值的格式。
  - 用于控制动态添加字段的映射的自定义规则。

​    **每个索引都有一个映射类型，它决定了文档的索引方式。**

es与mysql的对比

![](..\..\imgs\大数据\elasticsearch\es与mysql的对比.jpg)

**倒排索引原理**

ES在建立索引的时候采用了一种叫做**倒排索引**的机制，保证每次在搜索关键词的时候能够快速定位到这个关键词所属的文档。

Inverted Index 主要包括两部分：

一个有序的数据字典 Dictionary（包括单词 Term 和它出现的频率）。

与单词 Term 对应的 Postings（即存在这个单词的文件）。

当我们搜索的时候，首先将搜索的内容分解，然后在字典里找到对应 Term，从而查找到与搜索相关的文件内容。

Lucene在对文档建立索引的时候，会给词典的所有的元素排好序，在搜索的时候直接根据二分查找的方法进行筛选就能够快速找到数据。

ES做的要更深一点，ES希望把这个词典“**搬进**”内存，直接从内存读取数据不就比从磁盘读数据要快很多吗！问题在于对于海量的数据，索引的空间消耗十分巨大，直接搬进来肯定不合适，所以需要进一步的处理，建立词典索引（term index）。通过词典索引可以直接找到搜索词在词典中的大致位置，然后从磁盘中取出词典数据再进行查找。所以大致的结构图就变成了这样：

![](..\..\imgs\大数据\elasticsearch\term-index.jpg)

term index不需要存下所有的term，而仅仅是他们的一些前缀与Term Dictionary的block之间的映射关系，再结合FST(Finite State Transducers)的压缩技术，可以使term index缓存到内存中。从term index查到对应的term dictionary的block位置之后，再去磁盘上找term，大大减少了磁盘随机读的次数。有限状态转换器（Finite State Transducers）相当于是一个Trie前缀树，可以直接根据前缀就找到对应的term在词典中的位置。

**es写入一个数据的过程？**

集群上的每个节点都是`coordinating node`（**协调节点**），协调节点表明这个节点可以做**路由**。比如**节点1**接收到了请求，但发现这个请求的数据应该是由**节点2**处理（因为主分片在**节点2**上），所以会把请求转发到**节点2**上。

- coodinate（**协调**）节点通过hash算法可以计算出是在哪个主分片上，然后**路由到对应的节点**
- `shard = hash(document_id) % (num_of_primary_shards)`

路由到对应的节点以及对应的主分片时，会做以下的事：

1. 将数据写到内存缓存区
2. 然后将数据写到translog缓存区
3. 每隔**1s**数据从buffer中refresh到FileSystemCache中，生成segment文件，一旦生成segment文件，就能通过索引查询到了
4. refresh完，memory buffer就清空了。
5. 每隔**5s**中，translog 从buffer flush到磁盘中
6. 定期/定量从FileSystemCache中,结合translog内容`flush index`到磁盘中。

解释一下：

- Elasticsearch会把数据先写入内存缓冲区，然后每隔**1s**刷新到文件系统缓存区（当数据被刷新到文件系统缓冲区以后，数据才可以被检索到）。所以：Elasticsearch写入的数据需要**1s**才能查询到
- 为了防止节点宕机，内存中的数据丢失，Elasticsearch会另写一份数据到**日志文件**上，但最开始的还是写到内存缓冲区，每隔**5s**才会将缓冲区的刷到磁盘中。所以：Elasticsearch某个节点如果挂了，可能会造成有**5s**的数据丢失。
- 等到磁盘上的translog文件大到一定程度或者超过了30分钟，会触发**commit**操作，将内存中的segement文件异步刷到磁盘中，完成持久化操作。

说白了就是：写内存缓冲区（**定时**去生成segement，生成translog），能够**让数据能被索引、被持久化**。最后通过commit完成一次的持久化。

等主分片写完了以后，会将数据并行发送到副本集节点上，等到所有的节点写入成功就返回**ack**给协调节点，协调节点返回**ack**给客户端，完成一次的写入。

**写数据底层原理**

1）document先写入导内存buffer中，同时写translog日志

2)）[https://www.elastic.co/guide/cn/elasticsearch/guide/current/near-real-time.html](https://link.zhihu.com/?target=https%3A//www.elastic.co/guide/cn/elasticsearch/guide/current/near-real-time.html)

refresh操作所以近实时搜索：**写入和打开一个新段(**一个追加的倒排索引**)的轻量的过程叫做 \*refresh\*** 。**每隔一秒钟**把buffer中的数据**创建一个新的segment，**这里**新段会被先写入到文件系统缓存**--这一步代价会比较低，稍后再被刷新到磁盘--这一步代价比较高。不过**只要文件已经在缓存中， 就可以像其它文件一样被打开和读取**了，内存buffer被清空。此时，新segment 中的文件就**可以被搜索**了，这就意味着document从被写入到可以被搜索需要一秒种，如果要更改这个属性，可以执行以下操作

PUT /my_index
{
"settings": {
"**refresh_interval**": "30s"
}
}
3）[https://www.elastic.co/guide/cn/elasticsearch/guide/current/translog.html](https://link.zhihu.com/?target=https%3A//www.elastic.co/guide/cn/elasticsearch/guide/current/translog.html)

flush操作导致持久化变更：**执行一个提交并且截断 translog 的行为在 Elasticsearch 被称作一次** ***flush**。*刷新（refresh）完成后, 缓存被清空但是事务日志不会。translog日志也会越来越多，当translog日志大小大于一个阀值时候或30分钟，会出发flush操作。

- 所有在内存缓冲区的文档都被写入一个新的段。
- 缓冲区被清空。
- 一个提交点被写入硬盘。（表明有哪些segment commit了）
- 文件系统缓存通过 `fsync` 到磁盘。
- 老的 translog 被删除。

分片每30分钟被自动刷新（flush），或者在 translog 太大的时候也会刷新。也**可以用_flush命令手动执行**。

**translog每隔5秒会被写入磁盘（所以如果这5s，数据在cache而且log没持久化会丢失）**。在一次增删改操作之后translog只有在replica和primary shard都成功才会成功，如果要提高操作速度，可以设置成异步的

PUT /my_index
{
"settings": {
"index.translog.durability": "async" ,

"index.translog.sync_interval":"5s"
}
}

所以总结是有三个批次操作，一秒做一次refresh保证近实时搜索，5秒做一次translog持久化保证数据未持久化前留底，30分钟做一次数据持久化。

2.基于translog和commit point的数据恢复

在磁盘上会有一个上次持久化的commit point，translog上有一个commit point，根据这两个commit point，会把translog中的变更记录进行回放，重新执行之前的操作

3.不变形下的删除和更新原理

[https://www.elastic.co/guide/cn/elasticsearch/guide/current/dynamic-indices.html#deletes-and-updates](https://link.zhihu.com/?target=https%3A//www.elastic.co/guide/cn/elasticsearch/guide/current/dynamic-indices.html%23deletes-and-updates)

一个文档被 “删除” 时，它实际上只是在 `.del` 文件中被 *标记* 删除。一个被标记删除的文档仍然可以被查询匹配到， 但它会在最终结果被返回前从结果集中移除。

文档更新也是类似的操作方式：当一个文档被更新时，旧版本文档被标记删除，文档的新版本被索引到一个新的段中。 可能两个版本的文档都会被一个查询匹配到，但被删除的那个旧版本文档在结果集返回前就已经被移除。

段合并的时候会将那些旧的已删除文档 从文件系统中清除。 被删除的文档（或被更新文档的旧版本）不会被拷贝到新的大段中。

4.merge操作，段合并

[https://www.elastic.co/guide/cn/elasticsearch/guide/current/merge-process.html](https://link.zhihu.com/?target=https%3A//www.elastic.co/guide/cn/elasticsearch/guide/current/merge-process.html)

由于每秒会把buffer刷到segment中，所以segment会很多，为了防止这种情况出现，es内部会不断把一些相似大小的segment合并，并且物理删除del的segment。

当然也可以手动执行

POST /my_index/_optimize?max_num_segments=1，尽量不要手动执行，让它自动默认执行就可以了

5.当你正在建立一个大的新索引时（相当于直接全部写入buffer，先不refresh，写完再refresh），可以先关闭自动刷新，待开始使用该索引时，再把它们调回来：

```text
PUT /my_logs/_settings
{ "refresh_interval": -1 } 

PUT /my_logs/_settings
{ "refresh_interval": "1s" } 
```

**es 搜索数据过程[是指search?search和普通docid get的背后逻辑不一样？]**

es 最强大的是做全文检索，就是比如你有三条数据：

- `java真好玩儿啊`
- `java好难学啊`
- `j2ee特别牛`

你根据 `java` 关键词来搜索，将包含 `java`的 `document` 给搜索出来。es 就会给你返回：java真好玩儿啊，java好难学啊。

- 客户端发送请求到一个 `coordinate node`。
- 协调节点将搜索请求转发到所有的 shard 对应的 `primary shard` 或 `replica shard`，都可以。
- query phase：每个 shard 将自己的搜索结果（其实就是一些 `doc id`）返回给协调节点，由协调节点进行数据的合并、排序、分页等操作，产出最终结果。
- fetch phase：接着由协调节点根据 `doc id` 去各个节点上拉取实际的 `document` 数据，最终返回给客户端。

当一个搜索请求被发送到一个节点Node，这个节点就变成了协调节点。这个节点的工作是向
所有相关的分片广播搜索请求并且把它们的响应整合成一个全局的有序结果集。这个结果集
会被返回给客户端。

**第一步是向索引里的每个节点的分片副本广播请求。就像document的 GET 请求一样，搜索请
求可以被每个分片的原本或任意副本处理。这就是更多的副本（当结合更多的硬件时）如何
提高搜索的吞吐量的方法。对于后续请求，协调节点会轮询所有的分片副本以分摊负载。**

每一个分片在本地执行查询和建立一个长度为 from+size 的有序优先队列——这个长度意味
着它自己的结果数量就足够满足全局的请求要求。分片返回一个轻量级的结果列表给协调节
点。只包含documentID值和排序需要用到的值，例如 _score 。
协调节点将这些分片级的结果合并到自己的有序优先队列里。这个就代表了最终的全局有序
结果集。到这里，查询阶段结束。

假设你有一个100个分片的索引。当一个请求在集群上执行时会发生什么呢？

1. 这个搜索的请求会被发送到一个节点

2. 接收到这个请求的节点，将这个查询广播到这个索引的每个分片上（可能是主分片，也可能是复本分片）

3. 每个分片执行这个搜索查询并返回结果

4. 结果在通道节点上合并、排序并返回给用户

**文档打分机制**

**数据类型有哪些**？

![](..\..\imgs\大数据\elasticsearch\数据类型.jpg)























总结：

- 反向索引又叫倒排索引，是根据文章内容中的关键字建立索引。
- 搜索引擎原理就是建立反向索引。
- Elasticsearch 在 Lucene 的基础上进行封装，实现了分布式搜索引擎。
- Elasticsearch 中的索引、类型和文档的概念比较重要，类似于 MySQL 中的数据库、表和行。
- Elasticsearch 也是 Master-slave 架构，也实现了数据的分片和备份。
- Elasticsearch 一个典型应用就是 ELK 日志分析系统。





