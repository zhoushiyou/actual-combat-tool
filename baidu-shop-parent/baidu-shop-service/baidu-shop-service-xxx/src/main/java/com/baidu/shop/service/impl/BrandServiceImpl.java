package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序  条件查询
        Example example = new Example(BrandEntity.class);

        if(ObjectUtil.isNotNull(brandDTO)){
            //根据id查询
            if(ObjectUtil.isNotNull(brandDTO.getId())){
                example.createCriteria().andEqualTo("id",brandDTO.getId());
            }
            //条件查询
            if(StringUtil.isNotEmpty(brandDTO.getName())){
                example.createCriteria().andLike("name","%" + brandDTO.getName() + "%");
            }
            //排序方式
            if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());
        }

        List<BrandEntity> list = brandMapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JSONObject> save(BrandDTO brandDTO) {

        BrandEntity brandEntity =  BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);

//        //获得到品牌的名称
//        String name = brandDTO.getName();
//        char c = name.charAt(0);//获得第一个字符
 //        String s = String.valueOf(c); //转化为String类型
//        //将第一个字符转化为pinyinUtil
//        String upperCase = PinyinUtil.getUpperCase(s, PinyinUtil.TO_FIRST_CHAR_PINYIN);
//        //获得拼音的首字母
//        char c1 = upperCase.charAt(0);
//        //将首字母转化为大写
//        brandEntity.setLetter(c1);

        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0))
                ,PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        //新增CategoryAndBrand数据
        this.CategoryAndBrand(brandDTO,brandEntity);


        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> update(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);

        //获得品牌的名称并截取只要第一个字符转化为String类型
        //通过PinyinUtil.getUpperCase()将第一个字符转化为通过PinyinUtil，并使用charAt(0)获得字符的首字母
        //brandEntity.setLetter()将字母转换为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(
                String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //执行修改操作
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //通过brandID删除中间表的数据
       this.deleteBrandAndCategory(brandEntity.getId());

       //新增CategoryAndBrand数据
        this.CategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    //封装新增关系数据
    private void CategoryAndBrand(BrandDTO brandDTO,BrandEntity brandEntity){
        if(brandDTO.getCategory().contains(",")){

            //通过split方法分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用JDK1,8的stream
            //使用map函数返回一个新的数据   map返回得到的是一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(id ->{
                        CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                        categoryBrandEntity.setCategoryId(StringUtil.toInteger(id));
                        categoryBrandEntity.setBrandId(brandEntity.getId());
                        return categoryBrandEntity;
                    }).collect(Collectors.toList());

            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);

        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteBrand(Integer brandId) {
        //判断商品中是否有品牌 是：当前品牌不可删
        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId",brandId);
        List<SpuEntity> list = spuMapper.selectByExample(example);
        if(list.size() > 0){
          return this.setResultError("当前品牌已在商品中！不可删除！");
        }

        //删除Brand表中的信息
        brandMapper.deleteByPrimaryKey(brandId);

        //删除关系表
        this.deleteBrandAndCategory(brandId);

        return this.setResultSuccess();
    }

    //封装删除关系表语法
    private void deleteBrandAndCategory(Integer brandId){
        //删除CategoryBrand表的数据
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",brandId);
        categoryBrandMapper.deleteByExample(example);
    }

    //通过分类id查询品牌信息
    @Override
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid) {

        List<BrandEntity> list = brandMapper.getBrandByCategory(cid);

        return this.setResultSuccess(list);
    }

    //通过id查询品牌的信息
    @Override
    public Result<List<BrandEntity>> getBrandByIdList(String brandIdsStr) {
        List<Integer> brandIdList = Arrays.asList(brandIdsStr.split(","))
                .stream().map(brandIdStr -> Integer.parseInt(brandIdStr))
                .collect(Collectors.toList());
        List<BrandEntity> list = brandMapper.selectByIdList(brandIdList);

        return this.setResultSuccess(list);
    }
}
