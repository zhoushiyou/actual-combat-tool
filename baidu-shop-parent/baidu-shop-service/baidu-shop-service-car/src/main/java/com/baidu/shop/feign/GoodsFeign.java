package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/20
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service",contextId = "GoodsService")
public interface GoodsFeign extends GoodsService {
}
