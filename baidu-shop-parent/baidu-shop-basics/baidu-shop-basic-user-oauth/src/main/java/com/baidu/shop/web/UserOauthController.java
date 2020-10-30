package com.baidu.shop.web;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证中心")
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService oauthService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping(value = "oauth/login")
    @ApiOperation(value = "用户登录")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity,
                                    HttpServletRequest request, HttpServletResponse response){

        String token  = oauthService.login(userEntity,jwtConfig);

        //若返回的token为null的话 --> 打印错误信息
        if(ObjectUtil.isNull(token)){
            return this.setResultError(HTTPStatus.USER_PASSWORD_ERROR,"用户名或密码错误");
        }
        //将token放到cookie中
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    //用户已登陆
    @GetMapping(value = "auth/verify")
    public Result<UserInfo> verifyUser(@CookieValue(value = "MRSHOP_TOKEN") String token,
                                       HttpServletRequest request,HttpServletResponse response){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //刷新token,当用户对网页进行操作时,就刷新token--> 登录状态就又刷新30分钟过期了
            token = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
            //将token放到cookie中
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token, jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {//如果有异常的话,说明token有问题
            e.printStackTrace();
            return this.setResultError(HTTPStatus.VETIFY_ERROR,"用户失效");
        }
    }

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/loginSendValidCode")
    public  Result<JSONObject> loginSendValidCode(@RequestBody UserDTO userDTO){
        Result<JSONObject> result = oauthService.loginSendValidCode(userDTO);

        return result;
    }


}
