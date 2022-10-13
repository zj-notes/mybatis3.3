package com.mybatis.demo;

import com.mybatis.demo.mapper.RoleMapper;
import com.mybatis.demo.po.TblSysRole;
import com.mybatis.demo.service.CalculateService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        RoleMapper roleMapper = (RoleMapper) context.getBean("roleMapper");
        TblSysRole role = roleMapper.getRole("111");
        System.out.println("================================================================");
        System.out.println(role.toString());
        System.out.println("================================================================");

        CalculateService s = (CalculateService) context.getBean("calculateService");
        s.test();

    }
}
