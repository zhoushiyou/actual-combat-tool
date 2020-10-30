package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.BankCardDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.BankCardEntity;
import com.baidu.shop.mapper.CardMapper;
import com.baidu.shop.service.CardService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BankCardServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@RestController
public class BankCardServiceImpl extends BaseApiService implements CardService {

    @Resource
    private CardMapper cardMapper;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Result<List<BankCardDTO>> getBankCard(BankCardDTO bankCardDTO, String token) {

        //获取当前登录用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            if(userInfo == null){
                throw new RuntimeException("用户状态异常");
            }
            Example example = new Example(BankCardDTO.class);
            example.createCriteria().andEqualTo("userId",userInfo.getId());
            List<BankCardEntity> list = cardMapper.selectByExample(example);
            return this.setResultSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("无银行卡");
    }

    @Override
    public Result<JSONObject> addBankCard(BankCardDTO bankCardDTO, String token) {

        //获取当前登录用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            if(userInfo == null){
                throw new RuntimeException("用户状态异常");
            }
            BankCardEntity bankCardEntity = BaiduBeanUtil.copyProperties(bankCardDTO, BankCardEntity.class);
            if(bankCardEntity.getId() != null){
                cardMapper.updateByPrimaryKeySelective(bankCardEntity);
            }else{
                bankCardEntity.setUserId(Long.valueOf(userInfo.getId()));
                bankCardEntity.setCardNumber(bankCardDTO.getCardNumber());
                bankCardEntity.setCreateTime(bankCardDTO.getCreateTime());
                cardMapper.insertSelective(bankCardEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }
}
