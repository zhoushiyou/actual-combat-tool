package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/3
 * @Version V1.0
 **/
@Api(tags = "规格的接口")
public interface SpecificationService {

    @ApiOperation(value = "规格组的查询")
    @GetMapping(value = "specgroup/list")
    Result<List<SpecGroupEntity>> getSpecGroup(@SpringQueryMap SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格组的新增")
    @PostMapping(value = "specgroup/save")
    Result<JSONObject> save(@Validated({BaiduOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);


    @ApiOperation(value = "规格组的修改")
    @PutMapping(value = "specgroup/save")
    Result<JSONObject> update(@Validated({BaiduOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "规格组的删除")
    @DeleteMapping(value = "specgroup/delete")
    Result<JSONObject> delete(Integer id);

    @ApiOperation(value = "通过规格组id与分类id对规格参数的查询")
    @GetMapping(value = "specparam/getSpecParam")
    Result<List<SpecParamEntity>> getSpecParam(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "规格参数的新增")
    @PostMapping(value = "specparam/specParamAdd")
    Result<JSONObject> specParamAdd(@Validated({BaiduOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);


    @ApiOperation(value = "规格参数的修改")
    @PutMapping(value = "specparam/specParamAdd")
    Result<JSONObject> specParamUpdate(@Validated({BaiduOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);


    @ApiOperation(value = "规格参数的删除")
    @DeleteMapping(value = "specparam/specParamDelete")
    Result<JSONObject> specParamDelete(Integer id);


    @ApiOperation(value = "通过skuId对特有规格的查询")
    @GetMapping(value = "aaaaaa")
    Result<SpecParamEntity> getSpecParamBySkuId(@RequestParam Integer k);
}
