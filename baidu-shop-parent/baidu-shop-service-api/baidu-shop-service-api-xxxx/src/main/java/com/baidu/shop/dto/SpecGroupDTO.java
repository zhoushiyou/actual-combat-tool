package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.validate.group.BaiduOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName SpecGroupDTO
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/3
 * @Version V1.0
 **/
@ApiModel(value = "规格组传输数据DTO")
@Data
public class SpecGroupDTO extends BaseDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键id不能为null",groups = {BaiduOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "商品分类id",example = "1")
    @NotNull(message = "商品分类id不能为null",groups = {BaiduOperation.Add.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组的名称")
    @NotEmpty(message = "规格组的名称不能为空",groups = {BaiduOperation.Add.class})
    private String name;

    @ApiModelProperty(hidden = true)
    private List<SpecParamEntity> specParams;

}
