package com.yang.service;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yang.utils.ES_Const;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 业务编写
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 1、解析数据放入 ES 索引中
    public Boolean parseContent(String index) throws Exception{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");

        for (int i = 0; i < 2; i++) {
            String data = readJson.read_Json(ES_Const.Data_Path+i+".json");
            bulkRequest.add(
                    new IndexRequest(index)
                            .source(data, XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    // 2、获取数据实现搜索功能
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
//        if(pageNo < 0){
//            pageNo = 0;
//        }

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(ES_Const.ES_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 排序
//        sourceBuilder.sort(new FieldSortBuilder("PUB_TIME").order(SortOrder.DESC));

        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("POLICY_TITLE", keyword);
        // match
//        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("POLICY_TITLE", keyword)
//                .fuzziness(Fuzziness.AUTO);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    // 3、搜索高亮功能
    public List<Map<String, Object>> searchHighLightPage(String keyword, int pageNo, int pageSize) throws IOException {

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("zhengce");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("POLICY_TITLE", keyword);
        // match
//        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("POLICY_TITLE", keyword)
//                .fuzziness(Fuzziness.AUTO);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("POLICY_TITLE");
        highlightBuilder.requireFieldMatch(false); // 多个高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {

            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("POLICY_TITLE");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();  // 原来的结果
            // 解析高亮的字段，将原来的字段替换为高亮的字段
            if(title != null){
                Text[] fragments = title.fragments();
                String new_title = "";
                for (Text text : fragments) {
                    new_title += text;
                }
                sourceAsMap.put("POLICY_TITLE", new_title);
            }

            list.add(sourceAsMap);
        }
        return list;
    }

    // 4、 多条件搜索
    public List<Map<String, Object>> multiSearchPage(String keyword, int pageNo, int pageSize, String grade, String type, String province) throws IOException {

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(ES_Const.ES_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 排序
//        sourceBuilder.sort(new FieldSortBuilder("PUB_TIME").order(SortOrder.DESC));

        // 精准匹配
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("POLICY_TITLE", keyword));
        boolQueryBuilder.must(QueryBuilders.termQuery("POLICY_GRADE", grade));
        boolQueryBuilder.must(QueryBuilders.termQuery("POLICY_TYPE", type));
        boolQueryBuilder.must(QueryBuilders.termQuery("PROVINCE", province));
        sourceBuilder.query(boolQueryBuilder);

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }
}


