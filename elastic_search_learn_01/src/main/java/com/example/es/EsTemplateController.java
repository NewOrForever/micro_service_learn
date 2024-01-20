package com.example.es;

import com.example.es.model.EsProduct;
import com.example.es.service.SpringDataService;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/esProduct/template")
public class EsTemplateController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @RequestMapping(value = "/create/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String create(@PathVariable Long id) {
        EsProduct esProduct = new EsProduct();
        esProduct.setId(id);
        esProduct.setName("测试产品template:" + id);
        esProduct.setBrandId(1L);

        EsProduct pro = elasticsearchRestTemplate.save(esProduct);
        return pro.toString();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@PathVariable Long id) {
        EsProduct esProduct = new EsProduct();
        esProduct.setId(id);
        return elasticsearchRestTemplate.delete(esProduct);
    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@RequestParam("ids") List<Long> ids) {

//        Query query = new NativeSearchQueryBuilder().withQuery(idsQueryBuilder).build();
//
//        operations.delete(query, getEntityClass(), indexCoordinates);


        Criteria criteria = Criteria.where("id").in(ids);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        elasticsearchRestTemplate.delete(criteriaQuery, EsProduct.class, elasticsearchRestTemplate.getIndexCoordinatesFor(EsProduct.class));
        return "success";
    }


    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(required = true) String keyword,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "name", "subTitle", "keywords");
        Query query = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();

        SearchHits<EsProduct> result = elasticsearchRestTemplate.search(query, EsProduct.class);
        result.getSearchHits().forEach(item -> {
            System.out.println(item.getContent());
        });
        return "success";
    }
}
