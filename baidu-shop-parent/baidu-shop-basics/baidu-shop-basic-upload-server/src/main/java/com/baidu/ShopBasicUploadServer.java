package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName ShopBasicUploadServer
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/1
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class ShopBasicUploadServer {
    public static void main(String[] args) {
        SpringApplication.run(ShopBasicUploadServer.class);
    }
}


