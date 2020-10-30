package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> getSpecGroup(SpecGroupDTO specGroupDTO) {

        //通过分类id查询数据
        Example example = new Example(SpecGroupEntity.class);
        if(ObjectUtil.isNotNull(specGroupDTO.getCid())) example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());

        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {

        SpecGroupEntity specGroupEntity = BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class);
        specGroupMapper.insertSelective(specGroupEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> update(SpecGroupDTO specGroupDTO) {

        SpecGroupEntity specGroupEntity = BaiduBeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class);
        specGroupMapper.updateByPrimaryKeySelective(specGroupEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> delete(Integer id) {

        //下面的操作是判断要删除的数据是否为规格组  是：不可删
        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);
        if(list.size() > 0){
            return this.setResultError("此信息为规格组并且有规格参数，不可删除!");
        }

        //删除
        specGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecParamEntity>> getSpecParam(SpecParamDTO specParamDTO) {

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();

        // 通过规格组的id对规格参数的查询
        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())) {
            criteria.andEqualTo("groupId",specParamDTO.getGroupId());
        }

        //通过分类id对规格参数的查询
        if(ObjectUtil.isNotNull(specParamDTO.getCid())){
            criteria.andEqualTo("cid",specParamDTO.getCid());
        }

        //查询有搜索属性的
        if(ObjectUtil.isNotNull(specParamDTO.getSearching())){
            criteria.andEqualTo("searching",specParamDTO.getSearching());
        }
        //判断是否是特有规格
        if(ObjectUtil.isNotNull(specParamDTO.getGeneric())){
            criteria.andEqualTo("generic",specParamDTO.getGeneric());
        }

        criteria.andEqualTo("groupId",specParamDTO.getGroupId());
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);
        return this.setResultSuccess(list);

    }

    @Transactional
    @Override
    public Result<JSONObject> specParamAdd(SpecParamDTO specParamDTO) {
        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> specParamUpdate(SpecParamDTO specParamDTO) {
        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> specParamDelete(Integer id) {
        specParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<SpecParamEntity> getSpecParamBySkuId(Integer k) {
        SpecParamEntity specParamEntity = specParamMapper.selectByPrimaryKey(k);
        return this.setResultSuccess(specParamEntity);
    }
}
