package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.exception.MrException;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    //控制事务  防止业务操作中出现错误，如果出现错误的话，事务回滚回到最原始的状态，避免操作失误
    @Transactional
    @Override
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {

        //强制修改id,可以减少一次查询和判断
        //这个更新操作是必须要执行的，可以提高效率
        CategoryEntity categoryEntity1 = new CategoryEntity();
        //通过前台传输的parentId与数据库查询的当前节点id对比校验
        categoryEntity1.setId(categoryEntity.getParentId());
        categoryEntity1.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(categoryEntity1);

        categoryMapper.insertSelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> updateCategory(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Transactional   //防止代码中途出现错误，进行事务回滚
    @Override
    public Result<JSONObject> deleteCategory(Integer id) {

        //判断当前选中的节点id是否存在,
        CategoryEntity entity = categoryMapper.selectByPrimaryKey(id);
        if(entity == null) {
            return this.setResultError("当前id不存在");
        }
        //选中的id存在的话，判断是否为父级id
        if(entity.getIsParent() == 1){
            return this.setResultError("当前节点为父级节点id,不可删除");
        }

        //构建条件查询，通过当前删除节点的id查询数据, 查询当前父节点下有几条子节点
        Example example = new Example(CategoryEntity.class); //注意导包  属于通用Mapper
        example.createCriteria().andEqualTo("parentId",entity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        //调用封装的方法
        this.deleteCategoryAndGroupAndBrandAndGoods(id);

        //如果查询出来的数据只有要删除的这一条
        //那么就把当前父级节点的状态改为0
        if(list.size() == 1){
            CategoryEntity parentStaticId = new CategoryEntity();
            parentStaticId.setId(entity.getParentId());
            parentStaticId.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(parentStaticId); //执行修改操作
        }

        categoryMapper.deleteByPrimaryKey(id);  //彻底删除
        return this.setResultSuccess();
    }

    //封装分类与规格组、品牌、商品的删除
    private void deleteCategoryAndGroupAndBrandAndGoods(Integer id){
        //若分类商品与品牌绑定关系的话，则不可删除
        Example example1 = new Example(CategoryBrandEntity.class);
        example1.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrandEntity> categoryBrandEntities = categoryBrandMapper.selectByExample(example1);
        if (!categoryBrandEntities.isEmpty()) throw new MrException(601,"当前分类已与品牌绑定关系，不可删！");

        //若分类商品与规格组绑定关系，则不可删
        Example example2 = new Example(SpecGroupEntity.class);
        example2.createCriteria().andEqualTo("cid",id);
        List<SpecGroupEntity> categoryGroup = specGroupMapper.selectByExample(example2);
        if(ObjectUtil.isNotNull(categoryGroup)) throw new MrException(602,"当前分类与规格组绑定关系，不可删除");

        //若分类已在商品中展示的话 -->不可删  可以不进行判断，因为它已与品牌、规格绑定关系
//        Example example = new Example(SpuEntity.class);
//        example.createCriteria().andEqualTo("cid3",id);
//        List<SpuEntity> list = spuMapper.selectByExample(example);
//        if(list.size() > 0){
//            throw new MrException(603,"分类已在商品中展示,不可删！！！！！");
//        }

    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId) {
        //这里调用的方法需要手写在CategoryMapper中
        List<CategoryEntity> brand= categoryMapper.getCategoryByBrandId(brandId);
        return this.setResultSuccess(brand);
    }

    //通过cid3查询分类信息
    @Override
    public Result<List<CategoryEntity>> getCategoryByIdList(String cidsStr) {
        List<Integer> cidList = Arrays.asList(cidsStr.split(","))
                .stream()
                .map(cidStr -> Integer.parseInt(cidStr))
                .collect(Collectors.toList());

        List<CategoryEntity> list = categoryMapper.selectByIdList(cidList);

        return this.setResultSuccess(list);
    }

}
