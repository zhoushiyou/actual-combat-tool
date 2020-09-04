package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName ShopBasicEurekaServer
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaServer  //声明当前是一个服务中心
public class ShopBasicEurekaServer {

    public static void main(String[] args) {
        SpringApplication.run(ShopBasicEurekaServer.class);
    }
}
