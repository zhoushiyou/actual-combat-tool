package com.baidu.shop.entity;

import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName BankCardEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Data
@Table(name = "t_card")
public class BankCardEntity {

    @Id
    private Long id;

    private Long userId;

    private Long cardNumber;

    private Date createTime;

}
