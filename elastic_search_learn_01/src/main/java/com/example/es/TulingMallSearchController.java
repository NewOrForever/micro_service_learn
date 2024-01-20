package com.example.es;

/*
@author 图灵学院-白起老师
*/

import com.alibaba.fastjson2.JSON;
import com.example.common.utils.R;
import com.example.es.model.ESRequestParam;
import com.example.es.model.ESResponseResult;
import com.example.es.service.TulingMallSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class TulingMallSearchController {

    @Autowired
    private TulingMallSearchService tulingMallSearchService;

    /**
     * 自动将页面提交过来的所有请求参数封装成我们指定的对象
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/search/test")
    public String searchTest(ESRequestParam param, HttpServletResponse response) throws IOException {

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResponse searchResult = tulingMallSearchService.searchResonse(param);
        System.out.println(searchResult);

        ObjectMapper objectMapper = new ObjectMapper();
        Aggregations aggregations = searchResult.getAggregations();
        ParsedNested attrAgg = aggregations.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        Terms.Bucket bucket = attrIdAgg.getBuckets().get(0);
        ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
        Terms.Bucket bucket1 = attrNameAgg.getBuckets().get(0);

        /**
         * @see org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms.ParsedBucket#getKeyAsNumber()
         * key 如果是字符串中文，json 序列化会出现问题
         */
        String s = objectMapper.writeValueAsString(attrNameAgg);

        return "success";
    }


    @RequestMapping(value = "/searchList")
    public R listPage(ESRequestParam param) {
        //1、根据传递来的页面的查询参数，去es中检索商品
        ESResponseResult searchResult = tulingMallSearchService.search(param);
        return R.ok(searchResult);
    }

}
