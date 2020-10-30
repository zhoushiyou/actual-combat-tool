package com.baidu.shop.entity;

/**
 * @ClassName SpecGroupEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/3
 * @Version V1.0
 **/

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spec_group")
@Data
public class SpecGroupEntity {

    @Id
    private Integer id;

    private Integer cid;

    private String name;
}
