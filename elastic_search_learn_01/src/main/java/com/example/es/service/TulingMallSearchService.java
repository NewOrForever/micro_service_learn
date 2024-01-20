package com.example.es.service;


import com.example.es.model.ESRequestParam;
import com.example.es.model.ESResponseResult;
import org.elasticsearch.action.search.SearchResponse;

/**
 * @author 白起老师
 */
public interface TulingMallSearchService {


    /**
     * @param param 检索的所有参数
     * @return  返回检索的结果，里面包含页面需要的所有信息
     */
    ESResponseResult search(ESRequestParam param);
    SearchResponse searchResonse(ESRequestParam param);


}


