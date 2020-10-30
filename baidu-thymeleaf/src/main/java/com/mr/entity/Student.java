package com.mr.entity;

import lombok.Data;

/**
 * @ClassName Student
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/14
 * @Version V1.0
 **/
@Data
public class Student {

    private String code;
    private String pass;
    private Integer age;
    private String color;

    public Student(String code, String pass, Integer age, String color) {
        this.code = code;
        this.pass = pass;
        this.age = age;
        this.color = color;
    }
}
