package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.misc.FpUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public Result<JSONObject> delHTMLBySpuId(Integer spuId) {
        File file = new File(staticHTMLPath + File.separator + spuId + ".html");

        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {


        //也就是说我们现在可以创建上下文了
        Map<String, Object> map = this.getPageInfoBySpuId(spuId);

        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);

        //main-->主线程
        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");

            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {  //若不关流的话，它会一直占用本地资源
            writer.close();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        //获取所有的spu数据
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(new SpuDTO());
        if (spuInfoResult.getCode() == HTTPStatus.OK) {
            List<SpuDTO> spuDTOList = spuInfoResult.getData();
            spuDTOList.stream().forEach(spuDTO -> createStaticHTMLTemplate(spuDTO.getId()));
        }
        return this.setResultSuccess();
    }

    private Map<String, Object> getPageInfoBySpuId(Integer spuId) {

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
                SpuDetailEntity spuDetailEntity = this.spudetailEntity(spuId);
                map.put("spuDetailSpec",spuDetailEntity);

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
                List<CategoryEntity> categoryEntityList = this.categoryList(spuInfo);
                map.put("categoryInfo",categoryEntityList);

                //sku信息
                List<SkuDTO> skuInfo = this.skusList(spuId);
                map.put("skuInfo",skuInfo);

                //规格组和规格参数
                List<SpecGroupDTO> specGroupDTOList = this.specialGroupSpecInfo(spuInfo);
                map.put("groupsParam",specGroupDTOList);

                //特有规格参数
                Map<Integer, String> specialSpecList = this.specialSpecInfo(spuInfo);
                map.put("specParamMap",specialSpecList);
            }
        }
        return map;
    }

    //spudetail信息
    private SpuDetailEntity spudetailEntity(Integer spuId){
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
        if(spuDetailResult.getCode() == HTTPStatus.OK){
            SpuDetailEntity spuDetailList = spuDetailResult.getData();
            return spuDetailList;
        }
        return null;
    }

//    //品牌信息  有毛病
//    private List<BrandEntity> brandList(SpuDTO spuInfo){
//        //品牌信息
//        BrandDTO brandDTO = new BrandDTO();
//        brandDTO.setId(spuInfo.getBrandId());
//        Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);
//        if(brandInfoResult.getCode() == HTTPStatus.OK){
//            PageInfo<BrandEntity> data = brandInfoResult.getData();
//            List<BrandEntity> brandList = data.getList();
//            if(brandList.size() == 1){
//                return brandList;
//            }
//        }
//        return null;
//    }

    //分类信息
    private List<CategoryEntity> categoryList(SpuDTO spuInfo){
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(
                String.join(
                        ",",
                        Arrays.asList(spuInfo.getCid1()+""
                                ,spuInfo.getCid2()+""
                                ,spuInfo.getCid3()+"")
                )
        );
        if(categoryResult.getCode() ==HTTPStatus.OK){
           return categoryResult.getData();
        }
        return null;
    }

    //sku信息
    private List<SkuDTO> skusList(Integer spuId){
        Result<List<SkuDTO>> skuBySpuResult = goodsFeign.getSkuBySpuId(spuId);
        List<Long> priceList = new ArrayList<>();
        if(skuBySpuResult.getCode() == HTTPStatus.OK){
            List<SkuDTO> skuList = skuBySpuResult.getData();
            skuList.stream().forEach(price -> priceList.add(price.getPrice().longValue()));
            return skuList;
        }
        return null;
    }

    //规格组和规格参数
    private List<SpecGroupDTO> specialGroupSpecInfo(SpuDTO spuInfo){
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
            return groupsParamList;

        }
        return null;
    }

    //特有规格参数
    private Map<Integer,String> specialSpecInfo(SpuDTO spuInfo){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuInfo.getCid3()); //通过cid3查询特有规格
        specParamDTO.setGeneric(false); //是否是通用属性
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParam(specParamDTO);
        if(specParamResult.getCode() == HTTPStatus.OK){
            //将数据转化为map方便页面操作
            Map<Integer, String> specMap = new HashMap<>();
            specParamResult.getData().stream()
                    .forEach(spec -> specMap.put(spec.getId(),spec.getName()));
            return specMap;

        }
        return null;
    }
}
