package com.demo.main;

import com.demo.mapper.RoleMapper;
import com.demo.po.TblSysRole;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================");
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
            /**
             * 具体可以分为以下几步：
             * 1、加载配置文件到 Configuration
             * 2、构建 SQlSessionFactory
             * 3、打开 SqlSession 会话
             * 4、Executor 开始处理请求
             * 5、SqlSource 解析Sql
             * 6、StatementHandler 执行sql
             * 7、parameterHandler 设置参数
             * 8、StatementHandler 执行Sql
             * 9、ResultSetHandler 处理结果集
             */
            // build 返回 DefaultSqlSessionFactory 对象
            sqlSessionFactory = (DefaultSqlSessionFactory) new SqlSessionFactoryBuilder().build(inputStream);

            // 就可以通过 SqlSessionFactory 去获取 SqlSession 对象(DefaultSqlSession)
            sqlSession = (DefaultSqlSession) sqlSessionFactory.openSession();

            // 通过 MapperProxy 动态代理对象，也就是说执行自己写的dao里面的方法的时候，其实是对应的mapperProxy在代理
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

//            roleMapper.insertRole(new Role());
            TblSysRole role = roleMapper.getRole("111");     // 是代理对象

            System.out.println("result:" + role.getId() + ":" + role.getRoleName() + ":" + role.getRoleCode());
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
