package com.example.es.service.impl;


import com.example.es.model.ESRequestParam;
import com.example.es.model.ESResponseResult;
import com.example.es.model.EsProduct;
import com.example.es.service.TulingMallSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 白起老师
 */
@Service(value="tulingMallSearchService")
public class TulingMallSearchServiceImpl implements TulingMallSearchService {

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**************************图灵商城搜索*****************************/
    @Override
    public ESResponseResult search(ESRequestParam param) {

        try {
            //1、构建检索对象-封装请求相关参数信息
            SearchRequest searchRequest = startBuildRequestParam(param);

            //2、进行检索操作
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("response:" + response);
            //3、分析响应数据，封装成指定的格式
            ESResponseResult responseResult = startBuildResponseResult(response, param);
            return responseResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SearchResponse searchResonse(ESRequestParam param) {

        try {
            //1、构建检索对象-封装请求相关参数信息
            SearchRequest searchRequest = startBuildRequestParam(param);

            //2、进行检索操作
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 封装请求参数信息
     * 关键字查询、根据属性、分类、品牌、价格区间、是否有库存等进行过滤、分页、高亮、以及聚合统计品牌分类属性
     */
    private SearchRequest startBuildRequestParam(ESRequestParam param) {
        /**
         * {
         *   "query": {
         *     "bool": {
         *       "must": [
         *         {
         *           "multi_match": {
         *             "query": "手机",
         *             "fields": ["name", "subTitle", "keywords"]
         *           }
         *         }
         *       ],
         *       "filter": [
         *         {
         *           "term": {
         *             "hasStock": true
         *           }
         *         },
         *         {
         *           "range": {
         *             "price": {
         *               "gte": "1",
         *               "lte": "5000"
         *             }
         *           }
         *         },
         *         {
         *           "nested": {
         *             "path": "attrs",
         *             "query": {
         *               "bool": {
         *                 "must": [
         *                   {
         *                     "match": {
         *                       "attrs.attrId": 1
         *                     }
         *                   },
         *                   {
         *                     "match": {
         *                       "attrs.attrName": "cpu"
         *                     }
         *                   }
         *                 ]
         *               }
         *             }
         *           }
         *         }
         *       ]
         *     }
         *   },
         *   "sort": [
         *     {
         *       "salecount": {
         *         "order": "desc"
         *       }
         *     }
         *   ],
         *   "aggs": {
         *     "brand_agg": {
         *       "terms": {
         *         "field": "brandId",
         *         "size": 10
         *       },
         *       "aggs": {
         *         "brand_name_agg": {
         *           "terms": {
         *             "field": "brandName"
         *           }
         *         },
         *         "brand_img_agg": {
         *           "terms": {
         *             "field": "brandImg"
         *           }
         *         }
         *       }
         *     },
         *     "category_agg": {
         *       "terms": {
         *         "field": "categoryId",
         *         "size": 10
         *       },
         *       "aggs": {
         *         "category_name_agg": {
         *           "terms": {
         *           "field": "categoryName"
         *           }
         *         }
         *       }
         *     },
         *     "attr_agg": {
         *       "nested": {
         *         "path": "attrs"
         *       },
         *       "aggs": {
         *         "attr_id_agg": {
         *           "terms": {
         *             "field": "attrs.attrId"
         *           },
         *           "aggs": {
         *             "attr_name_agg": {
         *               "terms": {
         *                 "field": "attrs.attrName"
         *               }
         *             },
         *             "attr_value_agg": {
         *               "terms": {
         *                 "field": "attrs.attrValue"
         *               }
         *             }
         *           }
         *         }
         *       }
         *     }
         *   },
         *   "highlight": {
         *     "pre_tags": [
         *       "<b style='color:red'>"
         *     ],
         *     "post_tags": [
         *       "</b>"
         *     ],
         *     "fields": {
         *       "*": {}
         *     }
         *   }
         * }
         */

        // 1. bool query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1.1 must
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(param.getKeyword(), "name", "subTitle", "keywords"));
        }
        // 1.2 filter
        // 1.2.1 hasStock
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        // 1.2.2 price
        if (!StringUtils.isEmpty(param.getPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            String[] prices = param.getPrice().split("_");
            if (prices.length == 1) {
                if (param.getPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(prices[0]);
                } else {
                    rangeQueryBuilder.gte(prices[0]);
                }
            } else {
                rangeQueryBuilder.gte(prices[0]).lte(prices[1]);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        // 1.2.3 attrs
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] attrArr = attr.split("_");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrArr[0]));
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrName", attrArr[1]));
                boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None));
            }
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        // 1.3 sort
        if (!StringUtils.isEmpty(param.getSort())) {
            String[] sortArr = param.getSort().split("_");
            SortOrder order = sortArr[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(sortArr[0], order);
        }
        // 1.4 aggs
        // 1.4.1 brand_agg
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(10);
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName"));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg"));
        searchSourceBuilder.aggregation(brandAgg);
        // 1.4.2 category_agg
        AggregationBuilders.terms("category_agg").field("categoryId").size(10)
                .subAggregation(AggregationBuilders.terms("category_name_agg").field("categoryName"));
        // 1.4.3 attr_agg
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        nestedAggregationBuilder.subAggregation(attrIdAgg);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));
        searchSourceBuilder.aggregation(nestedAggregationBuilder);
        // 1.5 highlight
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            highlightBuilder.field("*");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 1.6 from、size
        searchSourceBuilder.from((param.getPageNum() - 1) * 10);
        searchSourceBuilder.size(10);

        System.out.println("构建的DSL 语句：" + searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("product_db");
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }


    /**
     * 封装查询到的结果信息
     * 关键字查询、根据属性、分类、品牌、价格区间、是否有库存等进行过滤、分页、高亮、以及聚合统计品牌分类属性
     */
    private ESResponseResult startBuildResponseResult(SearchResponse response, ESRequestParam param) throws JsonProcessingException {
        // 1.hits
        SearchHits hits = response.getHits();
        List<EsProduct> products = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 1.1 source
            String source = hit.getSourceAsString();
            EsProduct esProduct = objectMapper.readValue(source, EsProduct.class);
            // 1.2 highlight
            if (!StringUtils.isEmpty(param.getKeyword())) {
                HighlightField highlightField = hit.getHighlightFields().get("name");
                String name = highlightField != null ? highlightField.getFragments()[0].toString() : esProduct.getName();
                esProduct.setName(name);
            }
            products.add(esProduct);
        }
        // 2.aggs
        Aggregations aggregations = response.getAggregations();
        // 2.1 brand_agg
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
        List<ESResponseResult.BrandVo> brandVos = new ArrayList<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            ESResponseResult.BrandVo brandVo = new ESResponseResult.BrandVo();
            // 2.1.1 brandId
            Long brandId = bucket.getKeyAsNumber().longValue();
            // 2.1.2 brandName
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            // 2.1.3 brandImg
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();

            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        // 2.2 attr_agg
        ParsedNested attrAgg = aggregations.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<ESResponseResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            ESResponseResult.AttrVo attrVo = new ESResponseResult.AttrVo();
            // 2.2.1 attrId
            Long attrId = bucket.getKeyAsNumber().longValue();
            // 2.2.2 attrName
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 2.2.3 attrValue
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());

            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }

        ESResponseResult esResponseResult = new ESResponseResult();
        esResponseResult.setProducts(products);
        esResponseResult.setBrands(brandVos);
        esResponseResult.setAttrs(attrVos);

        // page
        esResponseResult.setPageNum(param.getPageNum());
        esResponseResult.setTotal(hits.getTotalHits().value);
        esResponseResult.setTotalPages((int) Math.ceil((double) hits.getTotalHits().value / 10));
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= esResponseResult.getTotalPages(); i++) {
            pageNavs.add(i);
        }
        esResponseResult.setPageNavs(pageNavs);

        return esResponseResult;
    }
}


