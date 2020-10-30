package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName LogEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Data
@Table(name = "t_log")
public class LogEntity implements Serializable {

    @Id
    private Long id;//主键id
    private String type;
    private Date operationDate;
    private Long userId;
    private String ip;
    private String userCode;
    private String description;
    private String model;
}
