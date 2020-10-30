package com.baidu.shop.mapper;

import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName CategoryMapper
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
public interface CategoryMapper extends Mapper<CategoryEntity>, SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "select c.id,c.name from tb_category c where c.id in(select cb.category_id from tb_category_brand cb where cb.brand_id=#{brandId})")
    List<CategoryEntity> getCategoryByBrandId(Integer brandId);

    //自定义方法，查询分类名称
    @Select(value = "select GROUP_CONCAT(name SEPARATOR '/')as categoryName from tb_category c where id in(#{cid1},#{cid2},#{cid3})")
    String getCategoryName(Integer cid1, Integer cid2, Integer cid3);

}
