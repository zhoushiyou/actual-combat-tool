package com.baidu.shop.dto;

import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName bankCardDTO
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Data
@ApiModel(value = "银行卡DTO")
public class BankCardDTO {

    @Id
    @ApiModelProperty(value = "银行卡id",example = "1")
    @NotNull(message = "银行卡id不能为null",groups = {BaiduOperation.Add.class})
    private Long id;

    @ApiModelProperty(value = "用户id",example = "1")
    @NotNull(message = "用户id不能为null",groups = {BaiduOperation.Add.class})
    private Long userId;

    @ApiModelProperty(value = "银行卡号",example = "1")
    @NotNull(message = "银行卡号不能为null",groups = {BaiduOperation.Add.class})
    private Long cardNumber;

    @ApiModelProperty(value = "绑定银行卡的时间")
    private Date createTime;

}
