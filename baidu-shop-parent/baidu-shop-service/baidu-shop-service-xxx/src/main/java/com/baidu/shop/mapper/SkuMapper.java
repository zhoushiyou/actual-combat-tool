package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {

    @Select(value = "select sk.*,sk.own_spec as ownSpec,st.stock " +
            "from tb_sku sk,tb_stock st where sk.id = st.sku_id and sk.spu_id =#{spuId}")
    List<SkuDTO> getSkuBySpuId(Integer spuId);
}
