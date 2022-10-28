package com.demo.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class DruidTest {
    private static DruidDataSource dataSource;

    public static void main(String[] args) {
        DruidPooledConnection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        dataSource = getDataSource();
        conn = getConnection();
        try {
            String sql = "insert into tbl_sys_role (id, role_name, role_code, system_code) values (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            String id = UUID.randomUUID().toString().replace("-", "");  // 41a68e9895b0437b9d35792c85565a96
            pstmt.setString(1, id);
            pstmt.setString(2, "管理员" + id);
            pstmt.setString(3, "admin" + id);
            pstmt.setString(4, "gims");

            conn.setAutoCommit(false);
            pstmt.executeUpdate();
            conn.commit();

            pstmt = conn.prepareStatement("select * from tbl_sys_role where id = ?");
            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static DruidDataSource getDataSource() {
        DruidDataSource dataSource = null;
        Properties prop = new Properties();
        InputStream is = DruidTest.class.getResourceAsStream("/druid.properties");
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    public static DruidPooledConnection getConnection() {
        DruidPooledConnection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
