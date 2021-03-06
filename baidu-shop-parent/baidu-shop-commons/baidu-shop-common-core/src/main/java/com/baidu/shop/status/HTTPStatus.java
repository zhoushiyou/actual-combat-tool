package com.baidu.shop.status;

/**
 * @ClassName HTTPStatus
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
public class HTTPStatus {

    public static final int OK = 200;//成功

    public static final int ERROR = 500;//失败

    public static final int OPERATION_ERROR = 5001;//操作失败

    public static final int PARAMS_VALIDATE_ERROR = 5002;//参数校验失败

    public static final int USER_PHONE_CODE = 5003;//验证码输入错误的状态

    public static final int VETIFY_ERROR = 403;//用户验证失败

    public static final int USER_PASSWORD_ERROR = 5004;//用户名或密码错误
}
