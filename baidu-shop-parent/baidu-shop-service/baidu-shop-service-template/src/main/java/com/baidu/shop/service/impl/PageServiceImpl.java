package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Service
public class PageServiceImpl implements PageService {

  //  @Autowired
    private GoodsFeign goodsFeign;

   // @Autowired
    private BrandFeign brandFeign;

  //  @Autowired
    private CategoryFeign categoryFeign;

   // @Autowired
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        Map<String, Object> map = new HashMap<>();
        //查询spu信息
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        if(spuResult.getCode() == HTTPStatus.OK){
            if(spuResult.getData().size() == 1){
                //spu信息
                SpuDTO spuInfo = spuResult.getData().get(0);
                map.put("spuInfo",spuInfo);
                //spudetail信息
                Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
                if(spuDetailResult.getCode() == HTTPStatus.OK){
                    SpuDetailEntity spuDetailList = spuDetailResult.getData();
                    map.put("spuDetailSpec",spuDetailList);
                }

                //品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);
                if(brandInfoResult.getCode() == HTTPStatus.OK){
                    PageInfo<BrandEntity> data = brandInfoResult.getData();
                    List<BrandEntity> brandList = data.getList();
                    if(brandList.size() == 1){
                        map.put("brandInfo",brandList.get(0));
                    }
                }

                //分类信息
                Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(
                        String.join(
                        ",",
                        Arrays.asList(spuInfo.getCid1()+""
                        ,spuInfo.getCid2()+""
                        ,spuInfo.getCid3()+"")
                    )
                );
                if(categoryResult.getCode() ==HTTPStatus.OK){
                    map.put("categoryInfo",categoryResult.getData());
                }

                //sku信息
                Result<List<SkuDTO>> skuBySpuResult = goodsFeign.getSkuBySpuId(spuDTO.getId());
                List<Long> priceList = new ArrayList<>();
                if(skuBySpuResult.getCode() == HTTPStatus.OK){
                    List<SkuDTO> skuList = skuBySpuResult.getData();
                    skuList.stream().forEach(price -> priceList.add(price.getPrice().longValue()));
                    map.put("skuInfo",skuList);
                }

                //规格组和规格参数
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3()); //通过cid3去查询
                Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroup(specGroupDTO);
                if(specGroupResult.getCode() == HTTPStatus.OK){
                    List<SpecGroupEntity> specGroupInfo = specGroupResult.getData();

                    List<SpecGroupDTO> groupsParamList = specGroupInfo.stream().map(specGroup -> {
                        SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                        //规格参数 -- 通用参数
                        SpecParamDTO specParamDTO = new SpecParamDTO();
                        specParamDTO.setGroupId(specGroup.getId());
                        specParamDTO.setGeneric(true);
                        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParam(specParamDTO);
                        if (specParamResult.getCode() == HTTPStatus.OK) {
                            sgd.setSpecParams(specParamResult.getData());
                        }
                        return sgd;
                    }).collect(Collectors.toList());
                    map.put("groupsParam",groupsParamList);
                }


                //特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3()); //通过cid3查询特有规格
                specParamDTO.setGeneric(false); //是否是通用属性
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParam(specParamDTO);
                if(specParamResult.getCode() == HTTPStatus.OK){
                    //将数据转化为map方便页面操作
                    Map<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream()
                            .forEach(spec -> specMap.put(spec.getId(),spec.getName()));
                    map.put("specParamMap",specMap);

                }
            }
        }
        return map;
    }

}
