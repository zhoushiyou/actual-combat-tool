package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.component.MrRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService{

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Autowired
    private MrRabbitMQ mrRabbitMQ;

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());

        //构建条件查询
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(spuDTO)){
            //按spuId查询
            if(ObjectUtil.isNotNull(spuDTO.getId())){
                criteria.andEqualTo("id",spuDTO.getId());
            }
            //按名称模糊查询
            if (StringUtil.isNotEmpty(spuDTO.getTitle()))
                criteria.andLike("title","%" + spuDTO.getTitle() + "%");
            //查看上下架 如果值为2的话不进行拼接，默认查询所有
            if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
                criteria.andEqualTo("saleable",spuDTO.getSaleable());
            //排序
            if(StringUtil.isNotEmpty(spuDTO.getSort()))
                example.setOrderByClause(spuDTO.getOrderByClause());
        }

        List<SpuEntity> list = spuMapper.selectByExample(example);

        //展示分类与品牌的名称
        List<SpuDTO> spuList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //通过分类id查询品牌名称
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            //查询分类名称  自定义方法(可减少查询语句)
            String categoryName = categoryMapper.getCategoryName(spuDTO1.getCid1(),spuDTO1.getCid2(),spuDTO1.getCid3());

            spuDTO1.setCategoryName(categoryName);

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,pageInfo.getTotal()+"",spuList);
    }

    //商品入库
    @Override
    public Result<JSONObject> save(SpuDTO spuDTO) {
        Integer spuId = this.saveGoodsTransactional(spuDTO);
        //在新增完成之后将消息发送到消息队列
        mrRabbitMQ.send(spuId + "", MqMessageConstant.SPU_ROUT_KEY_SAVE);

        return this.setResultSuccess();
    }
    //这里涉及到事务的提交问题
    @Transactional
    public Integer saveGoodsTransactional(SpuDTO spuDTO){
        Date date = new Date();

        //新增spu数据
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);//是否上架  1：true
        spuEntity.setValid(1); //是否有效 1：true
        spuEntity.setCreateTime(date); //创建时间
        spuEntity.setLastUpdateTime(date);//最后修改时间
        spuMapper.insertSelective(spuEntity);

        //新增spu大字段数据
        Integer spuId = spuEntity.getId();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuId);
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku和库存数据
        this.saveSkuAndStock(spuDTO.getSkus(),date,spuId);

        return spuEntity.getId();
    }


    //通过spuId查询Detail信息并回显
    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }
    //通过spuId查询sku信息并回显
    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkuBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    //修改方法
    @Override
    public Result<JSONObject> edit(SpuDTO spuDTO) {
        this.editGoodsTransactional(spuDTO);

        //在修改完成之后将消息发送到消息队列
        mrRabbitMQ.send(spuDTO.getId()+"",MqMessageConstant.SPU_ROUT_KEY_UPDATE);

        return this.setResultSuccess();
    }
    @Transactional
    public void editGoodsTransactional(SpuDTO spuDTO){
        //修改spu信息
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date); //最后修改时间
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail信息
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));

        //修改sku信息
        //先通过spuId查询出sku信息并删除
        //后新增数据
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuDTO.getId());

        //调用封装方法  删除SkuAndStock
        this.delSkuAndStock(spuDTO.getId());

        //调用封装方法  新增sku stock信息
        this.saveSkuAndStock(spuDTO.getSkus(),date,spuDTO.getId());
    }

    //封装 sku与库存信息方法  用List<SkuDTO> skus替换 SpuDTO spuDTO(因为spuDTO.getSkus()的返回类型就是List<> )
    private void saveSkuAndStock(List<SkuDTO> skus,Date date,Integer spuId){
        skus.stream().forEach(skuDto -> {
            //保存sku数据
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDto, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //保存库存stock信息
            StockEntity stockEntity = BaiduBeanUtil.copyProperties(skuDto, StockEntity.class);
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDto.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    //封装修改和删除共同的代码
    private void delSkuAndStock(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<Long> skuIdList = skuMapper.selectByExample(example)
                .stream()
                .map(sku -> sku.getId())
                .collect(Collectors.toList());
        if(skuIdList.size() > 0){
            skuMapper.deleteByIdList(skuIdList);//删除sku信息
            stockMapper.deleteByIdList(skuIdList);// 删除skuDTO中的stock
        }
    }

    //商品删除
    @Override
    public Result<JSONObject> delete(Integer spuId) {
        this.delGoodsTransactional(spuId);

        //在修改完成之后将消息发送到消息队列
        mrRabbitMQ.send(spuId+"",MqMessageConstant.SPU_ROUT_KEY_DELETE);

       return this.setResultSuccess();
    }
    @Transactional
    public void delGoodsTransactional(Integer spuId){
        //删除spu信息
        spuMapper.deleteByPrimaryKey(spuId);
        //删除Detail大字段信息
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //删除SkuAndStock
        this.delSkuAndStock(spuId);
    }

    //商品上下架
    @Transactional
    @Override
    public Result<JSONObject> upOrDown(Integer spuId) {

        //查询当前选中商品的信息
        SpuEntity spuEntity = spuMapper.selectByPrimaryKey(spuId);

        if(ObjectUtil.isNotNull(spuEntity)){
            if(spuEntity.getSaleable() == 1){
                spuEntity.setSaleable(0); //修改状态-->下架
            }else{
                spuEntity.setSaleable(1); //修改状态-->上架
            }
            spuMapper.updateByPrimaryKeySelective(spuEntity);
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<SkuEntity> getskuByskuId(Long skuId) {
        SkuEntity skuEntity = skuMapper.selectByPrimaryKey(skuId);
        return this.setResultSuccess(skuEntity);
    }
}
