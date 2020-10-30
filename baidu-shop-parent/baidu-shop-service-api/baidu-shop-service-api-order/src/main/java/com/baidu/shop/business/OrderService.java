package com.baidu.shop.business;


import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.entity.UserSiteEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order/createOrder")
    Result<String> createOrder(@RequestBody OrderDTO orderDTO, @CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "根据订单id查询订单信息")
    @GetMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

    @ApiOperation(value = "获取用户的收货地址")
    @GetMapping(value = "order/getUserSite")
    Result<List<UserSiteEntity>> getUserSite(@CookieValue(value = "MRSHOP_TOKEN")String token);

    @ApiOperation(value = "新增用户的收货地址")
    @PostMapping(value = "order/addUserSite")
    Result<List<UserSiteEntity>> addUserSite(@RequestBody UserSiteEntity userSiteEntity, @CookieValue(value = "MRSHOP_TOKEN")String token);

    @ApiOperation(value = "删除用户收货地址")
    @DeleteMapping(value = "order/deleteUserSite")
    Result<JSONObject> deleteUserSite(@RequestParam(value = "id") Long id);

    @ApiOperation(value = "回显用户收货地址")
    @DeleteMapping(value = "order/queryUserSite")
    Result<List<UserSiteEntity>> queryUserSite(@RequestParam(value = "id") Long id);
}
