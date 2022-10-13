/**
 *    Copyright 2009-2020 the original author or authors.
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
package com.demo.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({String.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyStringHandler implements TypeHandler<String> {
    private Logger log = Logger.getLogger(MyStringHandler.class);

    @Override
    public String getResult(ResultSet rs, String colName) throws SQLException {
        log.info("使用我的TypeHandler,ResultSet列名获取字符串");
        return rs.getString(colName);
    }

    @Override
    public String getResult(ResultSet rs, int index) throws SQLException {
        log.info("使用我的TypeHandler,ResultSet下标获取字符串");
        return rs.getString(index);
    }

    @Override
    public String getResult(CallableStatement cs, int index) throws SQLException {
        log.info("使用我的TypeHandler,CallableStatement下标获取字符串");
        return cs.getString(index);
    }

    @Override
    public void setParameter(PreparedStatement ps, int index, String value, JdbcType arg3) throws SQLException {
        log.info("使用我的TypeHandler");
        ps.setString(index, value);
    }

}
