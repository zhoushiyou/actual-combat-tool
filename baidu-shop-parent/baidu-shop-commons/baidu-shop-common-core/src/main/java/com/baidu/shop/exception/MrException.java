package com.baidu.shop.exception;

/**
 * @ClassName MrException
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/10
 * @Version V1.0
 **/
public class MrException extends RuntimeException{

    private Integer code ;
    private String msg;

    public MrException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
