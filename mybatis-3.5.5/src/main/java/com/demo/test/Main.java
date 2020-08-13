package com.demo.test;

import com.demo.mapper.RoleMapper;
import com.demo.po.Role;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // SqlSessionFactory sqlSessionFactory = null;
        // SqlSession sqlSession = null;

        DefaultSqlSessionFactory sqlSessionFactory = null;
        DefaultSqlSession sqlSession = null;

        try {
            // build 返回 DefaultSqlSessionFactory 对象
            sqlSessionFactory = (DefaultSqlSessionFactory) new SqlSessionFactoryBuilder().build(inputStream);

            // 就可以通过 SqlSessionFactory 去获取 SqlSession 对象(DefaultSqlSession)
            sqlSession = (DefaultSqlSession) sqlSessionFactory.openSession();

            // 通过 MapperProxy 动态代理对象，也就是说执行自己写的dao里面的方法的时候，其实是对应的mapperProxy在代理
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

            Role role = roleMapper.getRole(1L);

            System.out.println("result:" + role.getId() + ":" + role.getRoleName() + ":" + role.getNote());
            sqlSession.commit();
            System.out.println("================================================================");
            sqlSession.close();
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}
