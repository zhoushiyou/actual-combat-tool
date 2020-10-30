package com.baidu.shop.controller;

import com.baidu.shop.service.PageService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item") 我们不使用这些业务,因为需要去数据库多次查询,时间长
public class PageController {

    //@Autowired
    private PageService pageService;

    //{ spuId }不把代码写死
    //@GetMapping(value = "{spuId}.html")
    public String test(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap){

        Map<String,Object> map = pageService.getPageInfoBySpuId(spuId);
        modelMap.putAll(map);
        return "item";
    }

}
