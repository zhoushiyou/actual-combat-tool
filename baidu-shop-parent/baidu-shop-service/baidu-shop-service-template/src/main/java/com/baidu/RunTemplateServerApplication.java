package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunTemplateServerApplication
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/23
 * @Version V1.0
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient //将服务注入到Eureka服务中心
@EnableFeignClients
public class RunTemplateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunTemplateServerApplication.class);
    }

}
