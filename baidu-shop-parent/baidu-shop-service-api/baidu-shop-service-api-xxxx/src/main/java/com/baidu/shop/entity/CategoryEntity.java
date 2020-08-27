package com.baidu.shop.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName CategoryEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
@ApiModel(value = "分类实体类")
@Data
@Table(name = "tb_category")
public class CategoryEntity {

    @Id
    @ApiModelProperty(value = "分类主键" ,example = "1")
    private Integer id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "父级分类id" ,example = "0")
    private Integer parentId;

    @ApiModelProperty(value = "是否为父节点，0:否 1:是",example = "1")
    private Integer isParent;

    @ApiModelProperty(value = "排序,排序指数，越小越靠前",example = "1")
    private Integer sort;
}
