package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Result<JSONObject> delData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDocs.get(0));
        return this.setResultSuccess();
    }

    //创建索引,将数据提交到es
    @Override
    public Result<JSONObject> initGoodsEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            indexOperations.createMapping();
            log.debug("创建成功");
        }

        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        //查询出来的数据是多个spu
        List<GoodsDoc> goodsDocs = new ArrayList<>();

        if (spuInfo.getCode() == HTTPStatus.OK){
            //获取spu数据
            List<SpuDTO> spuList = spuInfo.getData();

            spuList.stream().forEach(spu ->{
                //这里不能使用到 BaiduBeanUtil.copyProperties()  类型不一致(Integer/Long)
                GoodsDoc goodsDoc = new GoodsDoc();
                goodsDoc.setId(spu.getId().longValue());//转换SpuDTO里的id是Integer而GoodsDoc里的id是Long
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //通过spuId查询skuList
                Map<List<Long>, List<Map<String, Object>>> skus = this.getSkusAndPriceList(spu.getId());
                skus.forEach((key,value) ->{
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });

                //通过cid3查询规格参数
                Map<String, Object> specMap = this.getSpecMap(spu);

                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);

            });
            System.out.println(goodsDocs);
        }
        return goodsDocs;
    }

    //封装skuList
    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(Integer spuId) {
        Map<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();

        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuMap = null;

        if (skuResult.getCode() == HTTPStatus.OK) {

            List<SkuDTO> skuList = skuResult.getData();

            skuMap = skuList.stream().map(sku -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());

                priceList.add(sku.getPrice().longValue());

                return map;
            }).collect(Collectors.toList());
        }
        hashMap.put(priceList,skuMap);
        return hashMap;
    }

    //封装规格参数
    private Map<String, Object> getSpecMap(SpuDTO spuDTO){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParam(specParamDTO);
        HashMap<String, Object> specMap = new HashMap<>();

        if(specParamResult.getCode() == HTTPStatus.OK){
            //只有规格参数的id和规格参数的名字
            List<SpecParamEntity> paramList = specParamResult.getData();

            //通过spuId去查询spuDetail,detail里面有通用特殊参数的值
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuDTO.getId());
            if(spuDetailResult.getCode() == HTTPStatus.OK){
                SpuDetailEntity spuDetailInfo = spuDetailResult.getData();

                //通用规格参数的值
                String genericSpecStr  = spuDetailInfo.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpecStr);

                //特殊规格参数的值
                String specialSpecStr = spuDetailInfo.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpecStr);

                paramList.stream().forEach(param ->{
                    if(param.getGeneric()){
                        if(param.getNumeric() && param.getSearching()){
                            specMap.put(param.getName(),
                                    this.chooseSegment(genericSpecMap.get(param.getId()+ ""),
                                            param.getSegments(),param.getUnit()));
                        }else{
                            specMap.put(param.getName(),genericSpecMap.get(param.getId() + ""));
                        }
                    }else{
                        specMap.put(param.getName(),specialSpecMap.get(param.getId() + ""));
                    }
                });
            }
        }
        return specMap;
    }

    /**
     * 把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }

    //清空es中的数据
    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(indexOperations.exists()) indexOperations.delete();
        return this.setResultSuccess();
    }

    //搜索过滤
    @Override
    public GoodsResponse search(String search,Integer page,String filter) {
        if(StringUtil.isEmpty(search)) throw new RuntimeException("搜索字段不能为空");

        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(this.getSearchBuilder(search,page,filter).build() , GoodsDoc.class);

        //总条数
        long total = hits.getTotalHits();
        // 总页数
        // 1). 将查询的总条数装箱后再拆箱为double类型(因为long是长整型只要整数)/10 为总页数
        // 2). 向上取整
        // 3). 装箱后再拆箱为long
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() /10)).longValue();

        List<SearchHit<GoodsDoc>> highLightHits = ESHighLightUtil.getHighLightHit(hits.getSearchHits());

        List<GoodsDoc> goodsList = highLightHits
                .stream()
                .map(searchHit -> searchHit.getContent())
                .collect(Collectors.toList());

        //获取聚合数据
        Aggregations aggregations = hits.getAggregations();

        //调用封装的分类信息
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);
        List<CategoryEntity> categoryList = null;
        Integer hotCid = 0;
        for (Map.Entry<Integer,List<CategoryEntity>> m :map.entrySet()){
            hotCid = m.getKey();
            categoryList = m.getValue();
        }

        //通过cid去查询规格参数
        Map<String, List<String>> specParamValueMap  = this.getSpecParamInfo(hotCid, search);

        //调用封装的品牌信息
        List<BrandEntity> brandList = this.getBrandAggIdList(aggregations);

        GoodsResponse goodsResponse = new GoodsResponse(total,totalPage,categoryList,brandList,goodsList,specParamValueMap);

        return goodsResponse;
    }

    /**
     * 构建条件查询
     * @param search
     * @param page
     */
    private NativeSearchQueryBuilder getSearchBuilder(String search,Integer page,String filter){
        //多条件查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //搜索过滤
        if(StringUtil.isNotEmpty(filter) && filter.length() > 2){  //这里的长度需要 >2 因为返回的是用{}包裹的对象
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key,value) ->{
                MatchQueryBuilder matchQueryBuilder = null;
                //分类 品牌和 规格参数的查询方式不一样
                if(key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key,value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + key + ".keyword",value);
                }
                //添加过滤,它不会影响评分
                boolQueryBuilder.must(matchQueryBuilder);
            });
            queryBuilder.withFilter(boolQueryBuilder); //添加过滤条件
        }

        //对品牌、商品进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));
        //高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        return queryBuilder;
    }

    //通过分类id去查询数据
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){
        Terms cid_agg = aggregations.get("cid_agg");

        List<? extends Terms.Bucket> cidBuckets = cid_agg.getBuckets();

        List<Integer> hotCidArr = Arrays.asList(0);//热度最高的分类id
        List<Long> maxCount = Arrays.asList(0L);

        //之所以使用Map是因为需要return两个对象，也可以使用List/Set
        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        //返回是一个id的String类型集合
        List<String> cidList = cidBuckets.stream().map(cidBucket -> {
            Number keyAsNumber = cidBucket.getKeyAsNumber();

            if(cidBucket.getDocCount() > maxCount.get(0)){
                maxCount.set(0,cidBucket.getDocCount());
                hotCidArr.set(0,keyAsNumber.intValue());
            }

            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());
        //通过分类id去查询数据，并转换为String类型
        String cidsStr = String.join(",", cidList);
        Result<List<CategoryEntity>> cateResult = categoryFeign.getCategoryByIdList(cidsStr);

        map.put(hotCidArr.get(0),cateResult.getData() );

        return map;
    }

    //通过品牌id去查询数据
    private List<BrandEntity> getBrandAggIdList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<String> brandIdList = brandBuckets.stream()
                .map(brandBucket -> brandBucket.getKeyAsNumber().intValue() + "")
                .collect(Collectors.toList());
        //通过品牌id去查询数据
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIdList(String.join(",",brandIdList));

        return brandResult.getData();
    }

    /**
     * 规格参数
     * @param hotCid
     * @param search
     * @return
     */
    private Map<String, List<String>> getSpecParamInfo(Integer hotCid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);//只搜索有查询属性的规格参数
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParam(specParamDTO);

        if(specParamResult.getCode() == HTTPStatus.OK){
            List<SpecParamEntity> specParamList = specParamResult.getData();
            //构建多条件查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            //多字段查询
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            //分页必须得查询一条数据
            queryBuilder.withPageable(PageRequest.of(0,1));

            specParamList.stream().forEach(specParam ->{
                queryBuilder.addAggregation(AggregationBuilders
                        .terms(specParam.getName())
                        .field("specs." + specParam.getName() + ".keyword"));
            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = searchHits.getAggregations();//获取聚合函数结果

            specParamList.stream().forEach(specParam ->{
                Terms terms = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                List<String> valueList = buckets.stream()
                        .map(bucket -> bucket.getKeyAsString())
                        .collect(Collectors.toList());
                map.put(specParam.getName(),valueList);
            });
            return map;
        }

        return null;
    }
}
