package com.baidu.shop.dto;

import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/13
 * @Version V1.0
 **/
@ApiModel(value = "用户DTO")
@Data
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {BaiduOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "账户")
    @NotNull(message = "账户不能为空",groups = {BaiduOperation.Add.class})
    private String username;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空", groups = {BaiduOperation.Add.class})
    private String password;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空", groups = {BaiduOperation.Add.class})
    private String phone;

    @ApiModelProperty(value = "手机号",hidden = true)
    private Date created;

    @ApiModelProperty(value = "手机号",hidden = true)
    private String salt;

}
