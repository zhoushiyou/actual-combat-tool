package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName StockEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/8
 * @Version V1.0
 **/
@Table(name = "tb_stock")
@Data
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skuId;

    private Integer seckillStock;

    private Integer seckillTotal;

    private Integer stock;

}
