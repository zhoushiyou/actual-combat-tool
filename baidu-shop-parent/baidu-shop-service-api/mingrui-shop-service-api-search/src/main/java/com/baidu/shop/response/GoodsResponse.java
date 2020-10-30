package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsResponse
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/21
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<CategoryEntity> categoryList;

    private List<BrandEntity> brandList;

    private Map<String,List<String>> specParamValueMap;

    public GoodsResponse(Long total,Long totalPage,List<CategoryEntity> categoryList,List<BrandEntity> brandList,List<GoodsDoc> goodsDocs,Map<String,List<String>> specParamValueMap){
        super(HTTPStatus.OK,HTTPStatus.OK + "",goodsDocs);

        this.total = total;
        this.totalPage = totalPage;
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specParamValueMap = specParamValueMap;
    }

}
