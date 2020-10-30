package com.baidu.shop.service.impl;

import com.baidu.shop.annotation.Log;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.CarConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {
        //将json字符串转化为json对象
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);
        //将json对象属性为clientCarList的数据取出来,并转化为List集合
        List<Car> carList = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.get("clientCarList").toString(), Car.class);

        //遍历新增到购物车
        carList.stream().forEach(car ->{
            this.addCar(car,token);
        });

        return this.setResultSuccess();
    }

    @Log(operDesc = "商品入车",operModul = "car",operType = "post")
    @Override
    public Result<JSONObject> addCar(Car car,String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过userid和skuid从redis中获取商品数据
            Car redisCar = redisRepository.getHash(CarConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    car.getSkuId() + "", Car.class);

            Car saveCar = null;
            log.debug("通过key:{}, skuid:{} 获取到的数据为:{}",
                    CarConstant.USER_GOODS_CAR_PRE+userInfo.getId(),car.getSkuId(),redisCar);

            if(ObjectUtil.isNotNull(redisCar)){
                //原来的用户购物车中已有当前要添加到购物车的商品
                redisCar.setNum(car.getNum() + redisCar.getNum());
                saveCar = redisCar;
                log.debug("当前用户购物车中已有将要新增的商品，重新设置num:{}",redisCar.getNum());

            }else{//当前用户购物车中没有将要新增的商品
                //调用feign,通过skuId查询sku信息
                Result<SkuEntity> skuResult = goodsFeign.getskuByskuId(car.getSkuId());
                if(skuResult.getCode() == HTTPStatus.OK){
                    SkuEntity skuEntity = skuResult.getData();
                    car.setTitle(skuEntity.getTitle());
                    //判断image为不为空
                    car.setImage(StringUtil.isNotEmpty(skuEntity.getImages()) ?
                            skuEntity.getImages().split(",")[0]:"");

                    Map<String, String> skuMap = JSONUtil.toMapValueString(skuEntity.getOwnSpec());
                    Map<String, String> hashMap = new HashMap<>();
                    skuMap.forEach((k,v) ->{
                        Result<SpecParamEntity> t = specificationFeign.getSpecParamBySkuId(Integer.valueOf(k));
                        if(ObjectUtil.isNotNull(t)){
                            hashMap.put(t.getData().getName(),v);
                        }
                    });
                    car.setOwnSpec(JSONUtil.toJsonString(hashMap));
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));
                    car.setUserId(userInfo.getId());
                    saveCar = car;
//                      redisRepository.setHash(CarConstant.USER_GOODS_CAR_PRE + skuEntity.getId(),car.getSkuId() + "", JSONUtil.toJsonString(car));
                    log.debug("新增商品到购物车redis,KEY:{}, skuId:{}, car:{}",
                            CarConstant.USER_GOODS_CAR_PRE+skuEntity.getId(),car.getSkuId(),JSONUtil.toJsonString(car));
                }
            }
            redisRepository.setHash(CarConstant.USER_GOODS_CAR_PRE+userInfo.getId(),
                    car.getSkuId()+"",JSONUtil.toJsonString(saveCar));
            log.debug("新增数据到redis成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    //若用户已经登录的话,购物车里商品的信息从redis中获取
    @Override
    public Result<List<Car>> getUserGoodsCar(String token) {

        List<Car> list = new ArrayList<>();
        try {
            //获取当前登录用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //通过用户id从redis获取购物车数据
            Map<String, String> goodsCarMap = redisRepository.getHash(CarConstant.USER_GOODS_CAR_PRE + userInfo.getId());
            goodsCarMap.forEach((key,value) ->{
                list.add(JSONUtil.toBean(value,Car.class));
            });

            return this.setResultSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("获取信息错误");
    }

    @Override
    public Result<JSONObject> userNumUpdate(Long skuId, Integer type, String token) {

        //获取当前登录的用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car redisCar = redisRepository.getHash(CarConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    skuId + "", Car.class);

            redisCar.setNum(type == CarConstant.USER_GOODS_CAR_NUM ? redisCar.getNum() +1 : redisCar.getNum() -1);

            redisRepository.setHash(CarConstant.USER_GOODS_CAR_PRE+userInfo.getId(),skuId + "",JSONUtil.toJsonString(redisCar));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }
}
