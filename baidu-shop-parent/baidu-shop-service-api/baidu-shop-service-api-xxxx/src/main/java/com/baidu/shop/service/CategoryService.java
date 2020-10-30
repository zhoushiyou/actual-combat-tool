package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
@Api(tags = "商品分类的接口")
public interface CategoryService {

    @ApiOperation(value = "查询分类商品")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "新增商品")
    @PostMapping(value = "category/add")
    public Result<JSONObject> saveCategory(@Validated({BaiduOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "修改商品名称")
    @PutMapping(value = "category/edit")
    public Result<JSONObject> updateCategory(@Validated({BaiduOperation.Update.class})@RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "删除分类商品")
    @DeleteMapping(value = "category/delete")
    public Result<JSONObject> deleteCategory(Integer id);


    @ApiOperation(value = "通过品牌id查询分类信息")
    @GetMapping(value = "category/getCategoryByBrandId")
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId);


    @ApiOperation(value = "通过id集合查询分类信息")
    @GetMapping(value = "category/getCategoryByIdList")
    Result<List<CategoryEntity>> getCategoryByIdList(@RequestParam String cidsStr);

}
