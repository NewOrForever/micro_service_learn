package com.example.es;

import com.example.es.model.EsProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * ClassName:SpringDataController
 * Package:com.example.es
 * Description:
 *
 * @Date:2024/1/18 16:47
 * @Author:qs@1.com
 */
@Controller
@RequestMapping("/esProduct/client")
public class EsClientController {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;


    @RequestMapping(value = "/create/{id}", method = RequestMethod.GET)
    @ResponseBody
    public IndexResponse create(@PathVariable Long id) throws IOException {
        EsProduct esProduct = new EsProduct();
        esProduct.setId(id);
        esProduct.setName("测试产品client:" + id);
        esProduct.setBrandId(1L);

        /**
         * 参考官方文档：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html
         * 参考 {@link ElasticsearchRestTemplate#save(Object)} - 该方法底层也是调用的这个方法
         * 如果要直接使用 client 的话，都可以参考官方文档和 {@link ElasticsearchRestTemplate} 的源码
         */
        Document esDocument = esProduct.getClass().getAnnotation(Document.class);
        String esProJson = new ObjectMapper().writeValueAsString(esProduct);
        IndexRequest indexRequest = new IndexRequest(esDocument.indexName()).id(id.toString()).source(esProJson, Requests.INDEX_CONTENT_TYPE);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("============>" + indexResponse.getId());
        return indexResponse;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DeleteResponse delete(@PathVariable Long id) throws IOException {
        Document esDocument = EsProduct.class.getAnnotation(Document.class);
        DeleteRequest deleteRequest = new DeleteRequest(esDocument.indexName(), id.toString());
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("============>" + deleteResponse.getId());
        return deleteResponse;
    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.GET)
    @ResponseBody
    public BulkByScrollResponse delete(@RequestParam("ids") List<Long> ids) throws IOException {
        Document esDocument = EsProduct.class.getAnnotation(Document.class);

        QueryBuilder queryBuilder = QueryBuilders.termsQuery("id", ids);

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(esDocument.indexName())
                .setQuery(queryBuilder)
                .setAbortOnVersionConflict(false)
                .setRefresh(false);

        BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);

        System.out.println("============>文档删除信息：" + bulkByScrollResponse.getStatus());
        System.out.println("============>删除文档数量：" + bulkByScrollResponse.getDeleted());

        return bulkByScrollResponse;
    }

    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public SearchResponse search(@RequestParam(required = true) String keyword,
                                 @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "name", "subTitle", "keywords");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.from(pageNum);
        searchSourceBuilder.size(pageSize);

        Document esDocument = EsProduct.class.getAnnotation(Document.class);
        SearchRequest searchRequest = new SearchRequest(new String[]{esDocument.indexName()}, searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("============>总命中数量" + searchResponse.getHits().getTotalHits());
        return searchResponse;
    }
}
