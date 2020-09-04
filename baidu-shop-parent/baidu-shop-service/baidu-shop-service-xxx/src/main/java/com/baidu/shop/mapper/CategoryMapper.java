package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName CategoryMapper
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
public interface CategoryMapper extends Mapper<CategoryEntity> {

    @Select(value = "select c.id,c.name from tb_category c where c.id in (select cb.category_id from tb_category_brand cb where cb.brand_id=#{brandId})")
    List<CategoryEntity> getCategoryByBrandId(Integer brandId);

}
