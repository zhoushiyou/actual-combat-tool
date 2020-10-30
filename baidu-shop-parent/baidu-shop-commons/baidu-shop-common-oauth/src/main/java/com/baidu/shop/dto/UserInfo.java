package com.baidu.shop.dto;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName UserInfo
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Integer id;

    private String username;


}
