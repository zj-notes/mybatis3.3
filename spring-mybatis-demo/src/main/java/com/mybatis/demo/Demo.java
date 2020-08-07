package com.mybatis.demo;

import com.mybatis.demo.mapper.RoleMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("ApplicationContext.xml");
        RoleMapper roleMapper = context.getBean(RoleMapper.class);
        System.out.println(roleMapper.getRole(1L));
    }
}
