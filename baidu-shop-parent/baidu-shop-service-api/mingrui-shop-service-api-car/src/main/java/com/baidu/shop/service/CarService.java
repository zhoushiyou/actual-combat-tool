package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName CarService
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/19
 * @Version V1.0
 **/
@Api(tags = "购物车")
public interface CarService {

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "car/addCar")
    Result<JSONObject> addCar(@RequestBody Car car ,@CookieValue("MRSHOP_TOKEN") String token);

    @ApiOperation(value = "合并购物车")
    @PostMapping(value = "car/mergeCar")
    Result<JSONObject> mergeCar(@RequestBody String clientCarList,@CookieValue("MRSHOP_TOKEN") String token);

    @ApiOperation(value = "获取用户当前购物车的数据")
    @GetMapping(value = "car/getUserGoodsCar")
    Result<List<Car>> getUserGoodsCar(@CookieValue("MRSHOP_TOKEN") String token);


    @ApiOperation(value = "修改商品在购物车中的数量")
    @GetMapping(value = "car/userNumUpdate")
    Result<JSONObject> userNumUpdate(Long skuId,Integer type,@CookieValue("MRSHOP_TOKEN") String token);


}
