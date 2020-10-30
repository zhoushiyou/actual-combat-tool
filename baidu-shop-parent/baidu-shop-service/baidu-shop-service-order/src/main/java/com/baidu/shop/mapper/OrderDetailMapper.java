package com.baidu.shop.mapper;

import com.baidu.shop.entity.OrderDetailEntity;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

//一对多可能会需要到批量操作
public interface OrderDetailMapper extends Mapper<OrderDetailEntity>, InsertListMapper<OrderDetailEntity> {
}
