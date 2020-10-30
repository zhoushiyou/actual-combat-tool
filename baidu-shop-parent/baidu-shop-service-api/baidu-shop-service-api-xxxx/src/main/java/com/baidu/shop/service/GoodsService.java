package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品的接口")
public interface GoodsService {

    @ApiOperation(value = "商品的查询方法")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(@SpringQueryMap SpuDTO spuDTO);

    @ApiOperation(value = "商品入库")
    @PostMapping(value = "goods/save")
    Result<JSONObject> save(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuId查询Detail信息")
    @GetMapping(value = "goods/getSpuDetailBySpuId")
    Result<SpuDetailEntity> getSpuDetailBySpuId(@RequestParam Integer spuId);

    @ApiOperation(value = "通过spuId查询sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(@RequestParam Integer spuId);

    @ApiOperation(value = "商品修改")
    @PutMapping(value = "goods/save")
    Result<JSONObject> edit(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "商品删除")
    @DeleteMapping(value = "goods/delete")
    Result<JSONObject> delete(Integer spuId);

    @ApiOperation(value = "商品上下架")
    @PutMapping(value = "goods/upOrDown")
    Result<JSONObject> upOrDown(@RequestParam Integer spuId);

    @ApiOperation(value = "通过skuId查询sku信息")
    @GetMapping(value = "goods/getskuByskuId")
    Result<SkuEntity> getskuByskuId(@RequestParam Long skuId);


}
