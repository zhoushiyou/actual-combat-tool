package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@Validated({BaiduOperation.Add.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验用户名或手机号唯一")
    @GetMapping(value = "user/check/{value}/{type}")
    //@PathVariable 从url地址上获取值
    Result<List<UserEntity>> checkUserNameOrPhone(@PathVariable(value = "value") String value , @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验用户输入的验证码是否正确")
    @GetMapping(value = "user/checkCode")
    Result<JSONObject> checkCode(String phone ,String code);
}
