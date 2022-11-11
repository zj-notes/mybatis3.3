package com.demo.main;

import com.demo.mapper.RoleMapper;
import com.demo.po.TblSysRole;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParameterHandlerTest {
    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultSqlSessionFactory sqlSessionFactory = null;
        DefaultSqlSession sqlSession = null;

        try {
            sqlSessionFactory = (DefaultSqlSessionFactory) new SqlSessionFactoryBuilder().build(inputStream);
            // 就可以通过 SqlSessionFactory 去获取 SqlSession 对象(DefaultSqlSession)，autoCommit = true
            sqlSession = (DefaultSqlSession) sqlSessionFactory.openSession(true);
            // 通过 MapperProxy 动态代理对象，也就是说执行自己写的dao里面的方法的时候，其实是对应的mapperProxy在代理
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

//            TblSysRole role1 = roleMapper.getRole("admin111");
//            TblSysRole role2 = roleMapper.getRoleByParam("admin111");
//            TblSysRole role3 = roleMapper.getRoleByIdAndName("admin111", "admin");
            TblSysRole role4 = roleMapper.getRoleByIdAndNameByParam("admin111", "admin");

//            Map<String, String> map = new HashMap<>();
//            map.put("id", "admin111");
//            map.put("roleName", "admin");
//            TblSysRole role5 = roleMapper.getRoleByIdAndNameByMap(map);

//            TblSysRole bean = new TblSysRole();
//            bean.setId("admin111");
//            bean.setRoleName("admin");
//            TblSysRole role6 = roleMapper.getRoleByBean(bean);
//            TblSysRole role7 = roleMapper.getRoleByBean2(bean);

        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}
