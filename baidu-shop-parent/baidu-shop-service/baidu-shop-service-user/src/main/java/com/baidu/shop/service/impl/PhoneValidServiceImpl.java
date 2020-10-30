package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.PhoneValidService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PhoneValidServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/15
 * @Version V1.0
 **/
//@RestController
@Slf4j
public class PhoneValidServiceImpl extends BaseApiService implements PhoneValidService {

   // @Resource
    private UserMapper userMapper;

   // @Autowired
    private RedisRepository redisRepository;

  @Override
    public Result<List<UserEntity>> checkPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(value) && ObjectUtil.isNotNull(type)){
            if(type == UserConstant.USER_TYPE_PHONE){
                criteria.andEqualTo("phone",value);
            }
        }
        List<UserEntity> list = userMapper.selectByExample(example);
        return this.setResultSuccess(list);
    }

  @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //随机生成6位的验证码
        String s = (int)(((Math.random() * 9 ) + 1) * 100000) + "";
        //将验证码放到redis缓存中
        redisRepository.set(UserConstant.USER_PHONE_PRE + userDTO.getPhone(),s);
        //设置验证码在redis缓存中的失效时间
        redisRepository.expire(UserConstant.USER_PHONE_PRE+ userDTO.getPhone(),60);
        //因为我们使用的真实发送信息是有条数限制的,所以我们把验证码打印出来
        log.debug("手机号:{} 验证码:{}",userDTO.getPhone(),s);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> checkCode(String phone, String code) {

        String s = redisRepository.get(UserConstant.USER_PHONE_PRE + phone);
        if(!code.equals(s)){
            return this.setResultError(HTTPStatus.USER_PHONE_CODE,"验证码输入错误");
        }


        return this.setResultSuccess();
    }
}
