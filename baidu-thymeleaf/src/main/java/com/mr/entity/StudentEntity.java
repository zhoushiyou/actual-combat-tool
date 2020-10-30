package com.mr.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName StudentEntity
 * @Description: TODO
 * @Author zsy
 * @Date 2020/9/14
 * @Version V1.0
 **/
@Data
@ToString
public class StudentEntity {

    private String code;
    private String pass;
    private Integer age;
    private String color;



}
