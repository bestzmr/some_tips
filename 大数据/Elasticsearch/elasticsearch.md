## Maven

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





## 索引API

​	构建IndexRequest需要一下参数：

```java
IndexRequest request = new IndexRequest("posts"); 
request.id("1"); 
String jsonString = "{" +
        "\"user\":\"kimchy\"," +
        "\"postDate\":\"2013-01-30\"," +
        "\"message\":\"trying out Elasticsearch\"" +
        "}";
request.source(jsonString, XContentType.JSON);
```

​	**步骤**：

​	1.创建Index
​	2.设置document id
​	3.提供文档source字符串 or map 等方式



### 	可选参数

```java
request.routing("routing"); 

request.timeout(TimeValue.timeValueSeconds(1)); 
request.timeout("1s"); 

request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); 
request.setRefreshPolicy("wait_for");     

request.version(2); 
request.versionType(VersionType.EXTERNAL);

request.opType(DocWriteRequest.OpType.CREATE); 
request.opType("create");

request.setPipeline("pipeline");
```

### 同步调用

客户端将等待IndexResponse返回

```java
IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
```



### 异步调用

也可以以异步方式执行IndexRequest，以便client可以直接返回。用户需要指定将请求和监听器传递给异步索引方法来处理响应或潜在故障

```java
client.indexAsync(request, RequestOptions.DEFAULT, listener); 
```

异步方法不会阻塞并立即返回。一旦完成，如果执行成功完成，则使用ActionListener的onResponse方法回调；如果执行失败，则使用ActionListener的onFailure方法回调。

```java
listener = new ActionListener<IndexResponse>() {
    @Override
    public void onResponse(IndexResponse indexResponse) {
        
    }

    @Override
    public void onFailure(Exception e) {
        
    }
};
```

### IndexResponse

返回的IndexResponse允许检索有关已执行操作的信息

```java
String index = indexResponse.getIndex();
String id = indexResponse.getId();
if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
    //处理（如果需要）第一次创建文档的情况

} else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
    //处理（如果需要）文档被重写的情况，因为它已经存在

}
ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
    //处理成功碎片数小于碎片总数的情况

}
if (shardInfo.getFailed() > 0) {
    for (ReplicationResponse.ShardInfo.Failure failure :
            shardInfo.getFailures()) {
        String reason = failure.reason(); 
		//处理潜在故障
    }
}
```

如果存在版本冲突，将抛出ElasticsearchException：

```java
IndexRequest request = new IndexRequest("posts")
    .id("1")
    .source("field", "value")
    .setIfSeqNo(10L)
    .setIfPrimaryTerm(20);
try {
    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
} catch(ElasticsearchException e) {
    if (e.status() == RestStatus.CONFLICT) {
        
    }
}
```

如果opType设置为create并且已经存在具有相同索引和id的文档，则会发生上面相同的情况（版本冲突）：

```java
IndexRequest request = new IndexRequest("posts")
    .id("1")
    .source("field", "value")
    .opType(DocWriteRequest.OpType.CREATE);
try {
    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
} catch(ElasticsearchException e) {
    if (e.status() == RestStatus.CONFLICT) {
        
    }
}
```







Search API

SearchRequest常用于搜索文档、聚合、抽取相关的操作，还提供高亮显示结果文档的方法。

在最基本的形式中，我们可以向请求添加一个查询：

```java
SearchRequest searchRequest = new SearchRequest(); 
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
searchRequest.source(searchSourceBuilder); 
```

步骤：

1.创建SearchRequest

2.创建SearchSourceBuilder，大部分搜索参数都添加到SearchSourceBuilder中。它为进入搜索请求体的所有内容提供了设置器。

3.添加查询条件到SearchSourceBuilder

4.将SearchSourceBuilder添加到SearchRequest



可选参数

我们先来看看SearchRequest的一些可选参数：

```java
SearchRequest searchRequest = new SearchRequest("posts");
//指定要请求的索引
```



```java
searchRequest.routing("routing");//设置路由参数
```





大多数控制搜索行为的选项都可以在SearchSourceBuilder上设置，它或多或少地包含Rest API的搜索请求体中的选项。

```java
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy")); 
sourceBuilder.from(0); //设置搜索索引的开始位置，默认为0
sourceBuilder.size(5); //确定要返回的搜索命中数。默认为10
sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 设置超时时间，用于控制允许搜索的时间。
```





QueryBuilder 

搜索查询是使用QueryBuilder对象创建的。对于Elasticsearch的查询DSL支持的每种搜索查询类型，都在QueryBuilder可以找到

可以使用其构造函数创建QueryBuilder：

```java
MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("user", "kimchy"); 
```

创建与字段“user”上的文本“kimchy”匹配的全文匹配查询。

一旦创建，QueryBuilder对象将提供方法来配置它创建的搜索查询的选项：

```java
matchQueryBuilder.fuzziness(Fuzziness.AUTO); //开启模糊查询
matchQueryBuilder.prefixLength(3); //在匹配查询上设置前缀长度选项
matchQueryBuilder.maxExpansions(10); //设置最大扩展选项以控制查询的模糊过程
```

还可以使用QueryBuilders工具类创建QueryBuilder对象。此类提供了可用于使用流式编程的方式创建QueryBuilder对象的方法：

```java
QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("user", "kimchy")
                                                .fuzziness(Fuzziness.AUTO)
                                                .prefixLength(3)
                                                .maxExpansions(10);
```

无论用什么方法来创建QueryBuilder，QueryBuilder对象都必须添加到SearchSourceBuilderQueryBuilder对象都必须添加到SearchSourceBuilder



SortBuilder

SearchSourceBuilder允许添加一个或多个SortBuilder实例。有四种特殊的实现（Field、Score、GeoDistance和ScriptSortBuilder）。

```java
sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); //根据_score字段降序排序（默认）
sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC));  //根据_id字段升序
```



Source Filter

默认情况下，搜索请求返回文档_source的内容，但与Rest API一样，您可以覆盖此行为。例如，您可以完全关闭 _source 检索：

```java
sourceBuilder.fetchSource(false);
```

该方法还接受一个或多个通配符模式的数组，以控制以更细粒度的方式包含或排除哪些字段：

```java
String[] includeFields = new String[] {"title", "innerObject.*"};
String[] excludeFields = new String[] {"user"};
sourceBuilder.fetchSource(includeFields, excludeFields);
```



高亮显示

高亮显示搜索结果可以通过在SearchSourceBuilder上设置HighlightBuilder来实现。通过添加一个或多个HighlightBuilder.Field到HighlightBuilder，可以为每个字段定义不同的高亮显示

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
HighlightBuilder highlightBuilder = new HighlightBuilder(); 
HighlightBuilder.Field highlightTitle =
        new HighlightBuilder.Field("title"); 
highlightTitle.highlighterType("unified");  
highlightBuilder.field(highlightTitle);  
HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
highlightBuilder.field(highlightUser);
searchSourceBuilder.highlighter(highlightBuilder);
```



聚合

通过首先创建适当的AggregationBuilder，然后在SearchSourceBuilder上设置它，可以将聚合添加到搜索中。在下面的示例中，我们在公司名称上创建一个术语聚合，并在公司员工的平均年龄上创建一个子聚合：

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_company")
        .field("company.keyword");
aggregation.subAggregation(AggregationBuilders.avg("average_age")
        .field("age"));
searchSourceBuilder.aggregation(aggregation);
```



Suggestion

要向搜索请求添加Suggestion，请使用SuggestionBuilder实现之一，该实现可以从SuggestionBuilders工厂类轻松访问。Suggestion 生成器需要添加到顶级的SuggestBuilder中，SuggestBuilder本身可以在SearchSourceBuilder上设置。

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
SuggestionBuilder termSuggestionBuilder =
    SuggestBuilders.termSuggestion("user").text("kmichy"); 
SuggestBuilder suggestBuilder = new SuggestBuilder();
suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder); 
searchSourceBuilder.suggest(suggestBuilder);
```



Profiling Queries and Aggregations

可用于分析特定搜索请求的查询和聚合的执行情况。要使用它，必须在SearchSourceBuilder上将配置文件标志设置为true：

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
searchSourceBuilder.profile(true);
```

执行SearchRequest后，相应的SearchResponse将包含分析结果。



同步执行搜索

```java
SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
```



异步执行搜索

```java
ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
    @Override
    public void onResponse(SearchResponse searchResponse) {
        
    }

    @Override
    public void onFailure(Exception e) {
        
    }
};
client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener); 
```



SearchResponse

执行搜索返回的SearchResponse提供有关搜索执行本身的详细信息以及对返回文档的访问。首先，有关于请求执行本身的有用信息，如HTTP状态码、执行时间或请求是提前终止还是超时：

```java
RestStatus status = searchResponse.status();
TimeValue took = searchResponse.getTook();
Boolean terminatedEarly = searchResponse.isTerminatedEarly();
boolean timedOut = searchResponse.isTimedOut();
```



其次，响应还通过提供受搜索影响的碎片总数以及成功碎片与失败碎片的统计信息，提供关于碎片级别执行的信息。还可以通过迭代ShardSearchFailures的数组来处理可能的故障，如以下示例所示：

```java
int totalShards = searchResponse.getTotalShards();
int successfulShards = searchResponse.getSuccessfulShards();
int failedShards = searchResponse.getFailedShards();
for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
    // failures should be handled here
}
```



SearchHits

要访问返回的文档，首先需要获取响应中包含的SearchHits：

```java
SearchHits hits = searchResponse.getHits();
```

SearchHits提供有关所有命中的全局信息，如总点击数或最大分数：

```java
TotalHits totalHits = hits.getTotalHits();
// the total number of hits, must be interpreted in the context of totalHits.relation
long numHits = totalHits.value;
// whether the number of hits is accurate (EQUAL_TO) or a lower bound of the total (GREATER_THAN_OR_EQUAL_TO)
TotalHits.Relation relation = totalHits.relation;
float maxScore = hits.getMaxScore();
```

嵌套在SearchHits中的是可以迭代出一个个SearchHit：

```java
SearchHit[] searchHits = hits.getHits();
for (SearchHit hit : searchHits) {
    // do something with the SearchHit
}
```

SearchHit提供对索引、文档ID和每次搜索命中分数等基本信息的访问：

```java
String index = hit.getIndex();
String id = hit.getId();
float score = hit.getScore();
```

此外，它还允许您以简单的JSON字符串或键/值对映射的形式返回文档源。在此映射中，常规字段以键/值对的形式返回。多值字段作为对象列表返回，嵌套对象作为另一个键/值映射返回。下面这些情况需要相应地强制转换：

```java
String sourceAsString = hit.getSourceAsString();
Map<String, Object> sourceAsMap = hit.getSourceAsMap();
String documentTitle = (String) sourceAsMap.get("title");
List<Object> users = (List<Object>) sourceAsMap.get("user");
Map<String, Object> innerObject =
        (Map<String, Object>) sourceAsMap.get("innerObject");
```

高亮

如果请求时设置了高亮，可以从结果中的每个SearchHit检索突出显示的文本片段。

```java
SearchHits hits = searchResponse.getHits();
for (SearchHit hit : hits.getHits()) {
    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
    HighlightField highlight = highlightFields.get("title"); 
    Text[] fragments = highlight.fragments();  
    String fragmentString = fragments[0].string();
}
```



聚合

可以从SearchResponse检索聚合，方法是首先获取聚合树的根，即Aggregations对象，然后按名称获取聚合。

```java
Aggregations aggregations = searchResponse.getAggregations();
Terms byCompanyAggregation = aggregations.get("by_company"); 
Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic"); 
Avg averageAge = elasticBucket.getAggregations().get("average_age"); 
double avg = averageAge.getValue();
```

请注意，如果按名称访问聚合，则需要根据请求的聚合类型指定聚合接口，否则将引发ClassCastException：

```java
Range range = aggregations.get("by_company"); 
```



还可以将所有聚合作为由聚合名称设置关键字的映射进行访问。在这种情况下，需要显式地转换到适当的聚合接口：

```java
Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
Terms companyAggregation = (Terms) aggregationMap.get("by_company");
```

还有一些getter以列表形式返回所有顶级聚合：

```java
List<Aggregation> aggregationList = aggregations.asList();
```

最后但并非最不重要的是，您可以迭代所有聚合，然后根据其类型决定如何进一步处理它们：

```java
for (Aggregation agg : aggregations) {
    String type = agg.getType();
    if (type.equals(TermsAggregationBuilder.NAME)) {
        Bucket elasticBucket = ((Terms) agg).getBucketByKey("Elastic");
        long numberOfDocs = elasticBucket.getDocCount();
    }
}
```



Suggestions

要从SearchResponse中获取Suggestion，请使用Suggest对象作为入口点，然后检索嵌套的建议对象：

```java
Suggest suggest = searchResponse.getSuggest(); 
TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user"); 
for (TermSuggestion.Entry entry : termSuggestion.getEntries()) { 
    for (TermSuggestion.Entry.Option option : entry) { 
        String suggestText = option.getText().string();
    }
}
```



Retrieving Profiling Results

使用getProfileResults（）方法从SearchResponse检索分析结果。此方法返回一个映射，其中包含SearchRequest执行中涉及的每个shard的ProfileShardResult对象。ProfileShardResult使用唯一标识概要文件结果所对应的分片的键存储在映射中。	

下面是一个示例代码，演示如何迭代每个分片的所有分析结果：

```java
Map<String, ProfileShardResult> profilingResults =
        searchResponse.getProfileResults(); 
for (Map.Entry<String, ProfileShardResult> profilingResult : profilingResults.entrySet()) { 
    String key = profilingResult.getKey(); 
    ProfileShardResult profileShardResult = profilingResult.getValue(); 
}
```

ProfileShardResult对象本身包含一个或多个查询配置文件结果，针对基础Lucene索引执行的每个查询对应一个结果：

```java
List<QueryProfileShardResult> queryProfileShardResults =
        profileShardResult.getQueryProfileResults(); 
for (QueryProfileShardResult queryProfileResult : queryProfileShardResults) { 

}
```

每个QueryProfileShardResult都提供对详细查询树执行的访问，并作为ProfileResult对象列表返回：

```java
for (ProfileResult profileResult : queryProfileResult.getQueryResults()) { 
    String queryName = profileResult.getQueryName(); 
    long queryTimeInMillis = profileResult.getTime(); 
    List<ProfileResult> profiledChildren = profileResult.getProfiledChildren(); 
}
```

QueryProfileShardResult还允许访问Lucene收集器的配置信息：

```java
CollectorResult collectorResult = queryProfileResult.getCollectorResult();  
String collectorName = collectorResult.getName();  
Long collectorTimeInMillis = collectorResult.getTime(); 
List<CollectorResult> profiledChildren = collectorResult.getProfiledChildren();
```

QueryProfileShardResult对象以与查询树执行非常类似的方式提供对详细聚合树执行的访问：

```java
AggregationProfileShardResult aggsProfileResults =
        profileShardResult.getAggregationProfileResults(); 
for (ProfileResult profileResult : aggsProfileResults.getProfileResults()) { 
    String aggName = profileResult.getQueryName(); 
    long aggTimeInMillis = profileResult.getTime(); 
    List<ProfileResult> profiledChildren = profileResult.getProfiledChildren(); 
}
```