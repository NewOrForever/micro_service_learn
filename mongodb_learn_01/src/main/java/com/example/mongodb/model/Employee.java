package com.example.mongodb.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * ClassName:Employee
 * Package:com.example.mongodb.model
 * Description:
 *
 * @Date:2022/9/21 15:02
 * @Author:qs@1.com
 */
@Data
@Document("emp")    // 对应集合中的一个文档
public class Employee {
    @Id                         // 映射我呢当中的_id
    private Integer id;
    @Field("username")
    private String name;
    @Field
    private int age;
    @Field
    private Double salary;
    @Field
    private Date birthDay;
    @Transient
    private String unSerialized;
}
