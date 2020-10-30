package com.baidu.shop.entity;

import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Table(name = "tb_brand")
@Data
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //使用主键生成策略
    private Integer id;

    private String name;

    private String image;

    private Character letter;
}
