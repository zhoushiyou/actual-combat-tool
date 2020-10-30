package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.BaiduOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌的操作接口")
public interface BrandService {

    @ApiOperation(value = "查询品牌信息")
    @GetMapping(value = "brand/list")
    public Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);


    @ApiOperation(value = "新增品牌信息")
    @PostMapping(value = "brand/add")
    public Result<JSONObject> save(@Validated({BaiduOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌信息")
    @PutMapping(value = "brand/add")
    public Result<JSONObject> update(@Validated({BaiduOperation.Update.class}) @RequestBody BrandDTO brandDTO);


    @ApiOperation(value = "删除品牌信息")
    @DeleteMapping(value = "brand/delete")
    public Result<JSONObject> deleteBrand(Integer brandId);

    @ApiOperation(value = "通过分类id查询品牌信息")
    @GetMapping(value = "brand/getBrandByCategory")
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid);

    @ApiOperation(value = "通过id查询品牌信息")
    @GetMapping(value = "brand/getBrandByIdList")
    Result<List<BrandEntity>> getBrandByIdList(@RequestParam String brandIdsStr);
}
