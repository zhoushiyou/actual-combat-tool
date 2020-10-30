package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PhoneValidService {

   // @ApiOperation(value = "校验手机号唯一")
   // @GetMapping(value = "user/loginCheck/{value}/{type}")
        //@PathVariable 从url地址上获取值
    Result<List<UserEntity>> checkPhone(@PathVariable(value = "value") String value , @PathVariable(value = "type") Integer type);

    //@ApiOperation(value = "给手机号发送验证码")
    //@PostMapping(value = "user/loginSendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

   // @ApiOperation(value = "校验用户输入的验证码是否正确")
   // @GetMapping(value = "user/loginCheckCode")
    Result<JSONObject> checkCode(String phone ,String code);
}
