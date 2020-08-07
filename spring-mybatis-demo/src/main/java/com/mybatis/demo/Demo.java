package com.mybatis.demo;

import com.mybatis.demo.mapper.RoleMapper;
import com.mybatis.demo.po.Role;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("ApplicationContext.xml");
        RoleMapper roleMapper = context.getBean(RoleMapper.class);
        Role role = roleMapper.getRole(1L);
        System.out.println("================================================================");
        System.out.println(role.toString());
        System.out.println("================================================================");

    }
}
