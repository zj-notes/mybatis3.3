/**
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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

      // sqlSession = (DefaultSqlSession) sqlSessionFactory.openSession(ExecutorType.BATCH, false); // 开启批处理，自动提交设置为 false


      // 通过 MapperProxy 动态代理对象，也就是说执行自己写的dao里面的方法的时候，其实是对应的mapperProxy在代理
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

      TblSysRole role = roleMapper.getRole("111");

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
