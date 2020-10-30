package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @ClassName ShopBasicZuulServer
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/28
 * @Version V1.0
 **/
@SpringBootApplication  //开启自动配置
@EnableEurekaClient  //声明当前服务需要一个eureka的客户端，并将服务注册到eureka注册中心
@EnableZuulProxy  //启用网关代理
public class ShopBasicZuulServer {

    public static void main(String[] args) {
        SpringApplication.run(ShopBasicZuulServer.class);
    }
}
