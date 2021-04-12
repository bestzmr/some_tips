Maven

需要引入elasticsearch和elasticsearch-rest-high-level-client

```xml
	<dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>7.12.0</version>
    </dependency>
    <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-high-level-client</artifactId>
      <version>7.12.0</version>
    </dependency>
```

一个RestHighLevelClient需要通过REST low-level client builder 来构建完成。

```java
RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));
```



high-level client内部执行的是low-level client，low-level client内部维护了一个连接池和一些线程，所以在关闭high-level client实际上关闭的是low-level client并且low-level client内部会释放那些资源，在api中可以通过close方法进行关闭low-level client。

```java
client.close();
```

