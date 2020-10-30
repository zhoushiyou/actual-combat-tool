package com.baidu.shop.business.impl;

import com.baidu.shop.annotation.Log;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.CarConstant;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.entity.UserSiteEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.mapper.UserSiteMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/21
 * @Version V1.0
 **/
@RestController
@Slf4j
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;
    
    @Resource
    private UserSiteMapper userSiteMapper;

    @Resource
    private RedisRepository redisRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private JwtConfig jwtConfig;

    @Transactional
    @Override
    @Log(operDesc = "生成订单",operModul = "order",operType = "get")
    public Result<String> createOrder(OrderDTO orderDTO, String token) {
        //根据雪花算法生成订单id
        long orderId = idWorker.nextId();

        try {
            //获取当前登录用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            if(userInfo == null){
                throw new RuntimeException("用户状态异常");
            }
            Date date = new Date();
            OrderEntity orderEntity = new OrderEntity();

            //生成订单
            orderEntity.setOrderId(orderId);
            orderEntity.setUserId(userInfo.getId() + "");
            orderEntity.setBuyerMessage("爱死你了");//买家留言
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setPaymentType(orderDTO.getPayType());//支付类型
            orderEntity.setSourceType(1);//写死的PC端,如果项目健全了以后,这个值应该是常量
            orderEntity.setInvoiceType(1);//发票类型  写死
            orderEntity.setBuyerRate(1);//买家是否评论  1:没有
            orderEntity.setCreateTime(date);

            List<Long> longs = Arrays.asList(0L);

            //detail
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuIdStr -> {
                //lamdba就是相当于在一个类（匿名的）的方法（匿名的）里操作
                //通过skuId查询sku数据
                Car car = redisRepository.getHash(CarConstant.USER_GOODS_CAR_PRE + userInfo.getId(), skuIdStr, Car.class);
                if(car == null){
                    throw new RuntimeException("数据异常");
                }
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setOrderId(orderId);//订单id
                orderDetailEntity.setSkuId(Long.valueOf(skuIdStr));
                orderDetailEntity.setNum(car.getNum());
                orderDetailEntity.setTitle(car.getTitle());
                orderDetailEntity.setImage(car.getImage());
                orderDetailEntity.setPrice(car.getPrice());

                longs.set(0,car.getPrice() * car.getNum()+longs.get(0));

                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setActualPay(longs.get(0));//实付金额
            orderEntity.setTotalPay(longs.get(0));//总金额

            //status
            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1); //已经创建订单,但未支付
            orderStatusEntity.setCreateTime(date);

            //入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //通过用户id和skuId删除购物车中的数据
            Arrays.asList( orderDTO.getSkuIds().split(",")).forEach(skuidStr ->{
                redisRepository.delHash(CarConstant.USER_GOODS_CAR_PRE+userInfo.getId(),skuidStr);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"操作成功",orderId + "");
    }


    //通过订单id查询订单信息
    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {

        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaiduBeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderEntity.class);
        example.createCriteria().andEqualTo("orderId",orderInfo.getOrderId());

        List<OrderDetailEntity> orderDetailList = orderDetailMapper.selectByExample(example);
        orderInfo.setOrderDetailList(orderDetailList);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderInfo.getOrderId());
        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);
    }

    @Override
    public Result<List<UserSiteEntity>> getUserSite(String token) {

        List<UserSiteEntity> userSiteList =null;
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //判断用户的状态是否正常
            if(userInfo == null){
                throw new RuntimeException("用户状态异常");
            }

            Example example = new Example(UserSiteEntity.class);
            example.createCriteria().andEqualTo("userId", userInfo.getId());
            userSiteList = userSiteMapper.selectByExample(example);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(userSiteList);
    }

    @Override
    public Result<List<UserSiteEntity>> addUserSite(UserSiteEntity userSiteEntity,String token) {

        UserSiteEntity entity = new UserSiteEntity();
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            entity.setUserId(Long.valueOf(userInfo.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setSiteId(userSiteEntity.getSiteId());
        entity.setName(userSiteEntity.getName());
        entity.setPhone(userSiteEntity.getPhone());
        entity.setAddress(userSiteEntity.getAddress());
        entity.setAddressAlias(userSiteEntity.getAddressAlias());
        entity.setPostcode(userSiteEntity.getPostcode());
        userSiteMapper.insertSelective(entity);

        return this.setResultSuccess(entity);
    }

    @Override
    public Result<JSONObject> deleteUserSite(Long id) {
        Example example = new Example(UserSiteEntity.class);
        example.createCriteria().andEqualTo("siteId",id);
        if(ObjectUtil.isNotNull(example)) userSiteMapper.deleteByExample(example);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserSiteEntity>> queryUserSite(Long id) {
        return null;
    }
}
