package com.mr.controller;

import com.mr.entity.Student;
import com.mr.entity.StudentEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @ClassName ThymeleafController
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/14
 * @Version V1.0
 **/
@Controller
public class ThymeleafController {

    @GetMapping(value = "stu")
    public String test(ModelMap map){
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setCode("001");
        studentEntity.setPass("857");
        studentEntity.setAge(20);
        studentEntity.setColor("<font style='color:blue'>蓝色</font>");
        map.put("stu",studentEntity);
        return "test";
    }

    @GetMapping(value = "student")
    public String list(ModelMap map){
        Student s1 =  new Student("01","111",10,"red");
        Student s2 =  new Student("02","222",25,"blue");
        Student s3 =  new Student("03","333",20,"yellow");
        Student s4 =  new Student("04","444",29,"pink");

        map.put("stuList", Arrays.asList(s1,s2,s3,s4));
        return "list";
    }
}
