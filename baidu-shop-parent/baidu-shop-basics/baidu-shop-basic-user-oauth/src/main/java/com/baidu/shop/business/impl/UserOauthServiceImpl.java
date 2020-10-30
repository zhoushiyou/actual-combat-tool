package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName UserOauthServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Service
@Slf4j
public class UserOauthServiceImpl extends BaseApiService implements UserOauthService {

    @Resource
    private UserOauthMapper userOauthMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public String login(UserEntity userEntity, JwtConfig jwtConfig) {

        String token = null;

        Example example = new Example(UserEntity.class);
        example.createCriteria().andEqualTo("username",userEntity.getUsername());
        example.createCriteria().andEqualTo("phone",userEntity.getPhone());

        if(StringUtil.isNotEmpty(userEntity.getUsername())) {
            List<UserEntity> userList = userOauthMapper.selectByExample(example);
            if (userList.size() == 1) {
                UserEntity entity = userList.get(0);
                //比较密码
                if (BCryptUtil.checkpw(userEntity.getPassword(), entity.getPassword())) {
                    //若密码比较成功的话
                    //创建token
                    try {
                        token = JwtUtils.generateToken(new UserInfo(entity.getId(), entity.getUsername())
                                , jwtConfig.getPrivateKey(), jwtConfig.getExpire());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return token;
    }

    @Override
    public Result<JSONObject> loginSendValidCode(UserDTO userDTO) {

        //随机生成6位数的验证码
        String s =  (int)(((Math.random() * 9) + 1 ) * 100000) + "";

        //将验证码放到redis缓存中
        redisRepository.set(UserConstant.USER_PHONE_PRE + userDTO.getPhone(),s);
        //设置验证码失效时间
        redisRepository.expire(UserConstant.USER_PHONE_PRE + userDTO.getPhone(),60);
        //因为我们使用的真实发送信息是有条数限制的,所以我们把验证码打印出来
        log.debug("手机号:{} 验证码:{}",userDTO.getPhone(),s);
        return this.setResultSuccess();
    }

}
