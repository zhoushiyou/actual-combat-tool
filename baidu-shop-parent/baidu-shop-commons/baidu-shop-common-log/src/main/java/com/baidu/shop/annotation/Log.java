package com.baidu.shop.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Log
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Target(ElementType.METHOD)//注解放置的目标位置即方法级别
@Retention(RetentionPolicy.RUNTIME)//注解在哪个阶段执行
@Documented//生成文档
public @interface  Log {
    // String value() default "";
    String operModul() default ""; // 操作模块

    String operType() default "";  // 操作类型

    String operDesc() default "";  // 操作说明
}
