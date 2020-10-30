package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@ApiModel(value = "品牌DTO")
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "主键id不能为null",groups = {BaiduOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "品牌名称不能为null",groups = {BaiduOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "品牌的首字母")
    private Character letter;

    @ApiModelProperty(value = "分类品牌名称")
    @NotEmpty(message = "分类品牌名称不能为null",groups = {BaiduOperation.Add.class})
    private String category;

}
