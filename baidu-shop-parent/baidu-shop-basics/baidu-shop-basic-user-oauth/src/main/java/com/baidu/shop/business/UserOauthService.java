package com.baidu.shop.business;

import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import org.json.JSONObject;

import java.util.List;

public interface UserOauthService {
    String login(UserEntity userEntity, JwtConfig jwtConfig);


    Result<JSONObject> loginSendValidCode(UserDTO userDTO);

}
