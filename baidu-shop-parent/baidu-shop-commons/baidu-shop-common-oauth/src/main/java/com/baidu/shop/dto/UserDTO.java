package com.baidu.shop.dto;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/13
 * @Version V1.0
 **/
@ApiModel(value = "用户手机号登录DTO")
@Data
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    private Integer id;

    @ApiModelProperty(value = "账户")
    private String username;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "手机号",hidden = true)
    private Date created;


}
