package com.baidu.shop.entity;

import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName UserSiteEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/23
 * @Version V1.0
 **/
@Data
@Table(name = "t_site")
@ApiModel(value = "用户收货地址Entity")
public class UserSiteEntity {

    @Id
    @ApiModelProperty(value = "收货地址id",example = "1")
    @NotNull(message = "收货地址不能为null",groups = {BaiduOperation.Add.class})
    private Long siteId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "收货人姓名")
    @NotEmpty(message = "姓名不能为null",groups = {BaiduOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号不能为null",groups = {BaiduOperation.Add.class})
    private String phone;

    @ApiModelProperty(value = "收货地址")
    @NotEmpty(message = "收货地址不能为null",groups = {BaiduOperation.Add.class})
    private String address;

    @ApiModelProperty(value = "详细地址,县镇")
    @NotEmpty(message = "详细地址,县镇不能为null",groups = {BaiduOperation.Add.class})
    private String addressAlias;

    @ApiModelProperty(value = "邮编")
    private String postcode;

}
