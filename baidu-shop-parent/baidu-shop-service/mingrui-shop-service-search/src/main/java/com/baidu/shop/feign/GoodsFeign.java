package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/16
 * @Version V1.0
 **/
//contextId = "GoodsService" 因为用到了多个feign所以需要区别一下 这个原则上来说是不需要写的，根据我们的项目来考虑
@FeignClient(contextId = "GoodsService", value = "xxx-service")
public interface GoodsFeign extends GoodsService {
}
