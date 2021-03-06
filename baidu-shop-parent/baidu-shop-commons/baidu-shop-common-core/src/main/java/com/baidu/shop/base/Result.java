package com.baidu.shop.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.CORBA.INTERNAL;

/**
 * @ClassName Result
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
@Data
@NoArgsConstructor  //为类提供一个无参的构造方法
public class Result<T> {

    private Integer code;//返回码

    private String message;//返回消息

    private T data;//返回数据

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = (T) data;
    }
}
