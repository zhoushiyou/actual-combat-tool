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
@EnableEurekaServer
public class ShopBasicEurekaServer {

    public static void main(String[] args) {
        System.out.println("2222");
        SpringApplication.run(ShopBasicEurekaServer.class);
    }
}
