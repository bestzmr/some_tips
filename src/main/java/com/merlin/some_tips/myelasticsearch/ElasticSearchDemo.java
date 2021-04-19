package com.merlin.some_tips.myelasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.*;
import java.util.*;

/**
 * elasticsearch demo
 *
 * @author zhaoqiang
 */
public class ElasticSearchDemo {
    public static final String JSONFILE = "D:\\ProjectData\\idea-workspace\\elasticsearch_demo\\src\\main\\java\\org\\example\\0f7c3886-302c-3b72-92a2-42d73501a465.json";


    /**
     * 获得一个高级client
     *
     * @return RestHighLevelClient
     */
    public static RestHighLevelClient getRestHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    /**
     * 从文件中获取json字符串
     *
     * @param fileName 文件绝对路径
     * @return json string
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 1.创建Index
     * 2.设置document id
     * 3.设置文档source字符串
     *
     * @return IndexRequest
     */
    public static IndexRequest createRequestIndex() {
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = readJsonFile(JSONFILE);
        request.source(jsonString, XContentType.JSON);
        return request;
    }

    /**
     * 异步调用索引创建方法
     */
    public static void execIndexRequest() {
        final IndexRequest indexRequest = createRequestIndex();
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                String index = indexResponse.getIndex();
                String id = indexResponse.getId();
                System.out.println("索引: " + index + " id: " + id);
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    //处理第一次创建文档的情况
                    System.out.println("第一次创建索引： "+index + " id: " + id);

                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    //处理文档被重写的情况，因为它已经存在
                    System.out.println("文档被重写");

                }
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    //处理成功碎片数小于碎片总数的情况
                    System.out.println("成功碎片数小于碎片总数");
                    System.out.println("分片总数：" + shardInfo.getTotal());
                    System.out.println("分片成功数：" + shardInfo.getSuccessful());
                }
//                if (shardInfo.getFailed() > 0) {
//                    for (ReplicationResponse.ShardInfo.Failure failure :
//                            shardInfo.getFailures()) {
//                        String reason = failure.reason();
//                        //处理潜在故障
//                        System.out.println("出现故障");
//                    }
//                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        getRestHighLevelClient().indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
        System.out.println("end");
    }

    /**
     * 用来处理搜索结果，转换成链表
     *
     * @param builder
     * @return
     */
    public static List<String> listSearchResult(SearchSourceBuilder builder) {
        // 提交查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(builder);
        RestHighLevelClient client = getRestHighLevelClient();

        // 获得response
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 从response中获得结果
        List<String> list = new LinkedList<>();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            list.add(next.getSourceAsString());
        }
        return list;
    }


    public static List<String> multiSearch(Map<String, Object> mustMap, int length) {
        // 根据多个条件 生成 boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 循环添加多个条件
        for (Map.Entry<String, Object> entry : mustMap.entrySet()) {
            boolQueryBuilder.must(QueryBuilders
                    .matchQuery(entry.getKey(), entry.getValue()));
        }

        // boolQueryBuilder生效
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(length);

        // 其中listSearchResult是自己编写的方法，以供多中查询方式使用。
        return listSearchResult(searchSourceBuilder);
    }

    /**
     * 根据单个属性查询
     *
     * @param key
     * @param value
     * @param length
     * @return
     */
    public static List<String> simpleSearch(String key, Object value, int length) {
        // 使用上面已经编写好的方法
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return multiSearch(map, length);
    }

    /**
     * 根据时间段去查询
     *
     * @param length
     * @return
     */
    public static List<String> searchByDate(Date from, Date to, int length) {
        // 生成builder
        RangeQueryBuilder rangeQueryBuilder =
                QueryBuilders.rangeQuery("date").from(from).to(to);

        /// boolQueryBuilder生效
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(rangeQueryBuilder);
        searchSourceBuilder.size(length);

        return listSearchResult(searchSourceBuilder);
    }

    /**
     * 删除es的整个数据库
     *
     * @param id
     * @return
     * @throws IOException
     */
//    public static boolean delete() throws IOException {
//        RestHighLevelClient client = getRestHighLevelClient();
//        DeleteIndexRequest request =
//                new DeleteIndexRequest(ElasticSearchUtil.ES_NAME);
//        client.indices().delete(request, RequestOptions.DEFAULT);
//        return true;
//    }
    public static void main(String[] args) {
       execIndexRequest();

    }

}
