package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date()); //同步当前时间
        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(value) && ObjectUtil.isNotNull(type)){
            if(type == UserConstant.USER_TYPE_USERNAME){
                criteria.andEqualTo("username",value);
            }else if(type == UserConstant.USER_TYPE_PHONE){
                criteria.andEqualTo("phone",value);
            }
        }
        List<UserEntity> userEntities = userMapper.selectByExample(example);

        return this.setResultSuccess(userEntities);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //随机生成6位验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
        //因为发送信息是有条数限制的,所以我们不再使用
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);
        //将验证码放到redis缓存中
        redisRepository.set(UserConstant.USER_PHONE_PRE +userDTO.getPhone(),code);
        //设置验证码在redis中的失效时间,也就是说超过了120秒,验证码就失效了
        redisRepository.expire(UserConstant.USER_PHONE_PRE + userDTO.getPhone(),120);

        log.debug("手机号:{} 发送验证码:{}",userDTO.getPhone(),code);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> checkCode(String phone, String code) {
        String s = redisRepository.get(UserConstant.USER_PHONE_PRE + phone);
        if(!code.equals(s)) return this.setResultError(HTTPStatus.USER_PHONE_CODE,"验证码输入错误");
        return this.setResultSuccess();
    }
}
