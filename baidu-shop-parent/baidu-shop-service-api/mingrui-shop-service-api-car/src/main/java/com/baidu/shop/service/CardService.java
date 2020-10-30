package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BankCardDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName CardService
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Api(tags = "银行卡操作接口")
public interface CardService {

    @ApiOperation(value = "查询银行卡")
    @GetMapping(value = "card/getBankCard")
    Result<List<BankCardDTO>> getBankCard(@RequestBody BankCardDTO bankCardDTO, @CookieValue(value = "MRSHOP_TOKEN")String token);


    @ApiOperation(value = "新增银行卡")
    @PostMapping(value = "card/addBankCard")
    Result<JSONObject> addBankCard(@RequestBody BankCardDTO bankCardDTO, @CookieValue(value = "MRSHOP_TOKEN")String token);


}
