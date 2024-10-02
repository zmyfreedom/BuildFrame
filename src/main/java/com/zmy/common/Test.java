package com.zmy.common;
import cn.hutool.json.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Test {
    private static String REGEX = "a*b";
    private static String INPUT = "aaab=aab==ab===bkkk";
    private static String REPLACE = "|";
    public static void main(String[] args) throws IOException{
        Test.ESTest();

        System.exit(0);
    }
    private static class ESUser{
        String name;
        int age;
        String sex;
        // getter and setter
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getSex() {
            return sex;
        }
        public void setSex(String sex) {
            this.sex = sex;
        }
    }
    public static void ESTest() throws IOException {
        // 创建客户端对象
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        System.out.println("连接ES成功！");

        // 高级查询
        /*
         首先创建一个SearchRequest对象，然后设置索引名称，接着创建一个SearchSourceBuilder对象，用于构建查询条件。
         创建QueryBuilder对象，设置查询条件，如查询条件为：name=zhangsan，则调用QueryBuilders.termQuery("name","zhangsan")方法。
         其他的QueryBuilder对象还有：matchAllQuery、matchQuery、boolQuery、rangeQuery、TermQueryBuilder等，
             //构建高亮字段
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color='red'>");//设置标签前缀
            highlightBuilder.postTags("</font>");//设置标签后缀
            highlightBuilder.field("name");//设置高亮字段
            //设置高亮构建对象
            searchSourceBuilder.highlighter(highlightBuilder);
            // 构建聚合
            searchSourceBuilder.aggregation(AggregationBuilders.max("聚合结果的字段名称").field("age"));，还有：term、min、avg、sum等。

         具体对象就是BoolQueryBuilder、RangeQueryBuilder等。具体使用方法可以参考官方文档。
         将QueryBuilder对象添加到SearchSourceBuilder对象中，SearchSourceBuilder.query()方法。
         SearchSourceBuilder对象，设置查询条件，如排序SearchSourceBuilder.sort()方法，分页SearchSourceBuilder.from()和SearchSourceBuilder.size()方法、分页等。
         然后，将SearchSourceBuilder对象添加到SearchRequest对象中，request.source()方法，并设置RequestOptions参数。
         然后调用client.search()方法，传入SearchRequest对象，并设置RequestOptions参数，获取SearchResponse对象。
         最后，从SearchResponse对象中获取结果response.getHits()，并进行处理。
         */
        // 创建索引- 请求对象
        GetRequest getRequest = new GetRequest("user", "1001");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("查询文档是否存在：" + getResponse.isExists());
        System.out.println("查询文档内容：" + getResponse.getSourceAsString());

        // 更新索引- 请求对象
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("user").id("1001");
        updateRequest.doc(XContentType.JSON, "sex", "女");
        // 发送请求，获取响应对象
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        // 处理响应结果
        System.out.println("更新文档是否成功：" + updateResponse.status().toString());
        System.out.println("更新文档id：" + updateResponse.getId());
        System.out.println("更新文档version：" + updateResponse.getVersion());
        System.out.println("更新文档index：" + updateResponse.getIndex());
        System.out.println("更新文档result：" + updateResponse.getResult());
        if (true) {
            return;
        }

        // 新增文档- 请求对象
        IndexRequest indexRequest = new IndexRequest();
        // 设置索引和id
        indexRequest.index("user").id("1001");
        // 创建json对象
        ESUser user = new ESUser();
        user.setName("张三");
        user.setAge(20);
        user.setSex("男");
        JSONObject userJson = new JSONObject(user);
        // 将json对象放入请求对象中
        indexRequest.source(userJson.toString(), XContentType.JSON);
        // 发送请求，获取响应对象
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        // 处理响应结果
        System.out.println("新增文档id：" + indexResponse.getId());
        System.out.println("新增文档index：" + indexResponse.getIndex());
        System.out.println("新增文档getResult：" + indexResponse.getResult());


        if (true) {
            return;
        }

        // 删除索引- 请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("user2");
        // 发送请求，获取响应对象
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        // 处理响应结果
        System.out.println("索引删除是否成功：" + deleteIndexResponse.isAcknowledged());
        if (true){
            return;
        }
        // 查询索引是否存在-请求对象
        // 查询索引-请求对象
        GetIndexRequest getIndexRequest = new GetIndexRequest("user2");
        // 发送请求，获取响应对象
        GetIndexResponse getIndexResponse = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        // 处理响应结果
        System.out.println("aliases"+getIndexResponse.getAliases());
        System.out.println("mappings"+getIndexResponse.getMappings());
        System.out.println("settings"+getIndexResponse.getSettings());
        if(true){
            return;
        }


        // 创建索引-请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("user2");
        // 发送请求，获取响应对象
        CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        // 处理响应结果
        boolean ack = response.isAcknowledged();
        System.out.println("索引创建是否成功：" + ack);
        // 关闭客户端连接
        client.close();
    }
    public static void replace(String str,String regex,String replace){
        Pattern p = Pattern.compile(REGEX);
        // 获取 matcher 对象
        Matcher m = p.matcher(INPUT);
//        System.out.println(Matcher.quoteReplacement(REPLACE));
//        System.out.println(m.replaceFirst(REPLACE));
        StringBuffer sb = new StringBuffer();
        while(m.find()){
//            System.out.println(m.group());
            System.out.println(m.start());
            System.out.println(m.end());
            //用于将匹配到的字符串替换为指定的字符串，并将结果追加到StringBuilder对象sb中
            m.appendReplacement(sb,REPLACE);
            System.out.println(sb);
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
}
